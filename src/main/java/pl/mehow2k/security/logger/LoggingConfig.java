package pl.mehow2k.security.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class LoggingConfig {

    @Autowired
    private DatabaseLoggingHandler databaseLoggingHandler;

    @Bean
    public Logger configureLogger() {
        // Tworzymy instancję loggera z unikalną nazwą
        Logger logger = Logger.getLogger("AppLogger");

        // Ustawiamy poziom logowania
        logger.setLevel(Level.INFO);

        // Dodajemy niestandardowy handler do logowania w bazie danych
        logger.addHandler(databaseLoggingHandler);

        // dodanie handlera do logowania w konsoli
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);

        return logger;
    }
}