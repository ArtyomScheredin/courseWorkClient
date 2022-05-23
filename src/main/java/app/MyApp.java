package app;

import model.State;
import exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.BufferedReader;
import java.io.IOException;

import static config.StaticConfig.COMMAND_INFO;

@Component
public class MyApp {
    private State state = State.INITIALISED;
    private final BufferedReader reader;

    private final CommandExecutor executor;

    @Autowired
    public MyApp(BufferedReader reader, CommandExecutor executor) {
        this.reader = reader;
        this.executor = executor;
    }

    public void run() {
        System.out.println(COMMAND_INFO);
        try {
            while (!state.equals(State.FINISHED)) {
                switch (state) {
                    case INITIALISED -> initialize();
                    case UNAUTHORIZED -> authorize();
                    case AUTHORIZED -> executeCommands();
                    default -> throw new ApiException();
                }
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private void executeCommands() throws IOException {
        String command = reader.readLine();
        if ((command == null) || command.equals("")) {
            System.err.println("Incorrect command");
            state = State.AUTHORIZED;
            return;
        }
        state = executor.execute(command);
    }

    private void authorize() throws IOException, JSONException {
        System.out.println("name:");
        String name = reader.readLine();
        System.out.println("password:");
        String password = reader.readLine();

        try {
            executor.authorize(name, password);
        } catch (HttpClientErrorException e) {
            System.err.println("Incorrect credentials. Try again!");
            return;
        }
        state = State.AUTHORIZED;
    }

    public void initialize() throws IOException {
        System.out.println("Welcome!\n Firstly, you should authorize yourself.");
        state = State.UNAUTHORIZED;
    }
}
