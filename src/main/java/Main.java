import app.MyApp;
import config.Config;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            MyApp app = (MyApp) ctx.getBean("myApp");
            app.run();
        }
    }
}
