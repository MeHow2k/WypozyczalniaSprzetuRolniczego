package pl.mehow2k;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartApplication {
    public static void main(String[] args) {
        // Ta linijka uruchamia serwer Tomcat i całą aplikację
        SpringApplication.run(StartApplication.class, args);
    }
}
