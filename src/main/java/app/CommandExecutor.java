package app;

import DTO.*;
import config.JwtInterceptor;
import exception.ApiException;
import model.Mark;
import model.Person;
import model.State;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static config.StaticConfig.COMMAND_INFO;

@Component
public class CommandExecutor {

    private RestTemplate restTemplate;
    private final HashMap<String, Consumer<String[]>> commandTable
            = new HashMap<>();
    private Instant expirationDate;

    public CommandExecutor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public State execute(String srcCommand) {
        if ((srcCommand == null) || srcCommand.equals("")) {
            System.err.println("Incorrect command");
            return State.AUTHORIZED;
        }
        String[] parsedCommand = srcCommand.split("\\s+");
        if (parsedCommand[0].equals("exit") || (expirationDate.isBefore(Instant.now()))) {
            return State.UNAUTHORIZED;
        }
        Consumer<String[]> runnableCommand = commandTable.get(parsedCommand[0]);
        if (runnableCommand == null) {
            System.err.println("Incorrect command!");
            return State.AUTHORIZED;
        }
        runnableCommand.accept(parsedCommand);
        return State.AUTHORIZED;
    }

    public void authorize(String name, String password) throws JSONException {
        restTemplate.getInterceptors().clear();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\n" +
                "    \"userName\":\"" + name + "\",\n" +
                "    \"password\":\"" + password + "\"\n" +
                "}", headers);

        ResponseEntity<String> responseEntity = null; //server request
        try {
            responseEntity = restTemplate.postForEntity("http://localhost:8080/auth/signin", request, String.class);
        } catch (ResourceAccessException e) {
            System.err.println("Server is unavailable");
        }

        JSONObject obj = new JSONObject(responseEntity.getBody()); //parsing
        String jwtToken = obj.getString("token");
        expirationDate = Instant.now().plusMillis(Long.parseLong(obj.getString("expiration")));
        restTemplate.setInterceptors(List.of(new JwtInterceptor(jwtToken)));
        System.out.println("You are authorized\n");
    }


    //CommandTable Initialization
    private <T extends Iterable<?>> void sendRequestAndDisplayList(String path,
                                                                   ParameterizedTypeReference<T> responseType) {
        RequestEntity<Void> request = RequestEntity
                .get(path).build();
        T result = restTemplate.exchange(request, responseType).getBody();
        if (result != null) {
            Iterator<?> iterator = result.iterator();
            iterator.forEachRemaining(System.out::println);
        }
    }

    private <T extends Map<?, ?>> void sendRequestAndDisplayMap(String path,
                                                                ParameterizedTypeReference<T> responseType) {
        RequestEntity<Void> request = RequestEntity
                .get(path).build();
        T result = restTemplate.exchange(request, responseType).getBody();
        if (result != null) {
            Iterator<?> iterator = result.entrySet().iterator();
            iterator.forEachRemaining(System.out::println);
        }
    }

    {
        commandTable.put("help", (args) -> System.out.println(COMMAND_INFO));
        commandTable.put("groupList", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            sendRequestAndDisplayList("http://localhost:8080/mark/group/" + args[1] + "/list",
                    new ParameterizedTypeReference<List<Mark>>() {
                    });
        });
        commandTable.put("groupAvg", (args) -> {
            sendRequestAndDisplayMap("http://localhost:8080/mark/group/avg",
                    new ParameterizedTypeReference<Map<String, Double>>() {
                    });
        });
        commandTable.put("subjectMarks", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            sendRequestAndDisplayList("http://localhost:8080/mark/subject/" + args[1] + "/list",
                    new ParameterizedTypeReference<List<Mark>>() {
                    });
        });
        commandTable.put("teacherMarks", (args) -> {
            if (args.length < 2) {
                return;
            }
            sendRequestAndDisplayList("http://localhost:8080/mark/teacher/" + args[1] + "/list",
                    new ParameterizedTypeReference<List<Mark>>() {
                    });
        });
        commandTable.put("studentMarks", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            sendRequestAndDisplayList("http://localhost:8080/mark/student/" + args[1] + "/list",
                    new ParameterizedTypeReference<List<Mark>>() {
                    });
        });
        commandTable.put("studentAvg", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            sendRequestAndDisplayMap("http://localhost:8080/mark/student/" + args[1] + "/avg",
                    new ParameterizedTypeReference<Map<String, Double>>() {
                    });
        });
        commandTable.put("subjectsAvg", (args) -> {
            sendRequestAndDisplayMap("http://localhost:8080/mark/subject/avg",
                    new ParameterizedTypeReference<Map<String, Double>>() {
                    });
        });
        commandTable.put("personGeneralAvg", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            sendRequestAndDisplayList("http://localhost:8080/person/" + args[1] + "/avg",
                    new ParameterizedTypeReference<Set<Double>>() {
                    });
        });
        commandTable.put("peopleOrderedByMark", (args) -> {
            sendRequestAndDisplayList("http://localhost:8080/person/ordered-list/by-mark",
                    new ParameterizedTypeReference<List<Person>>() {
                    });
        });
        commandTable.put("peopleFromGroup", (args) -> {
            if (args.length < 2) {
                return;
            }
            sendRequestAndDisplayList("http://localhost:8080/person/from-group/" + args[1],
                    new ParameterizedTypeReference<List<Person>>() {
                    });
        });
        commandTable.put("peopleWithMark", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            sendRequestAndDisplayList("http://localhost:8080/person/with-mark/" + args[1],
                    new ParameterizedTypeReference<List<Person>>() {
                    });
        });
        commandTable.put("teachersAvg", (args) -> sendRequestAndDisplayMap("http://localhost:8080/person/teacher/avg",
                new ParameterizedTypeReference<Map<String, Double>>() {
                }));
        commandTable.put("studentsAvg", (args) -> sendRequestAndDisplayMap("http://localhost:8080/person/student/avg",
                new ParameterizedTypeReference<Map<String, Double>>() {
                }));
        commandTable.put("addMark", (args) -> {
            if (args.length < 5) {
                throw new ApiException();
            }
            HttpEntity<MarkDTO> request = new HttpEntity<>(new MarkDTO(1,
                    args[1],
                    args[2],
                    args[3],
                    args[4]), new HttpHeaders());
            ResponseEntity<String> response = restTemplate
                    .postForEntity("http://localhost:8080/mark", request, String.class);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                System.out.println("Mark was created");
            }
        });
        commandTable.put("updateMark", (args) -> {
            if (args.length < 5) {
                throw new ApiException();
            }
            HttpEntity<MarkDTO> request = new HttpEntity<>(new MarkDTO(1,
                    args[1],
                    args[2],
                    args[3],
                    args[4]), new HttpHeaders());

            ResponseEntity<String> response = restTemplate
                    .exchange("http://localhost:8080/mark",
                            HttpMethod.PUT,
                            request,
                            String.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println("Mark was updated");
            }
        });
        commandTable.put("deleteMark", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            HttpEntity<Void> request = new HttpEntity<>(null);
            ResponseEntity<Void> response = restTemplate
                    .exchange("http://localhost:8080/mark/" + args[1],
                            HttpMethod.DELETE,
                            request,
                            Void.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println("Mark was deleted");
            }
        });
        commandTable.put("deleteWithAvgMarkLowerThan", (args) -> {
            if (args.length < 2) {
                throw new ApiException();
            }
            HttpEntity<Void> request = new HttpEntity<>(null);
            ResponseEntity<Void> response = restTemplate
                    .exchange("http://localhost:8080/person/avg-lower/" + args[1],
                            HttpMethod.DELETE,
                            request,
                            Void.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println("Students were deleted");
            }
        });
        commandTable.put("deleteTheWorstStudent", (args) -> {
            HttpEntity<Void> request = new HttpEntity<>(null);
            ResponseEntity<Void> response = restTemplate
                    .exchange("http://localhost:8080/person/worst",
                            HttpMethod.DELETE,
                            request,
                            Void.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println("Student was deleted");
            }
        });
        commandTable.put("addPerson", (args) -> {
            if (args.length < 6) {
                throw new ApiException();
            }
            HttpEntity<PersonDTO> request = new HttpEntity<>(new PersonDTO(1L,
                    args[1],
                    args[2],
                    args[3],
                    Long.parseLong(args[4]),
                    args[5].charAt(0)), new HttpHeaders());
            ResponseEntity<String> response = restTemplate
                    .postForEntity("http://localhost:8080/person", request, String.class);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                System.out.println("Person was created");
            }
        });
        commandTable.put("updatePerson", (args) -> {
            if (args.length < 6) {
                throw new ApiException();
            }
            HttpEntity<PersonDTO> request = new HttpEntity<>(new PersonDTO(1L,
                    args[1],
                    args[2],
                    args[3],
                    Long.parseLong(args[4]),
                    args[5].charAt(0)), new HttpHeaders());
            ResponseEntity<String> response = restTemplate
                    .exchange("http://localhost:8080/person", HttpMethod.PUT, request, String.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println("Person was updated");
            }
        });
    }

}
