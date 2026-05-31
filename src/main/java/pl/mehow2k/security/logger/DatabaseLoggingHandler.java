package pl.mehow2k.security.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.mehow2k.models.LogRecord;
import pl.mehow2k.repositories.LogRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;

@Component
public class DatabaseLoggingHandler extends Handler {

    private final LogRepository logRecordRepository;

    @Autowired
    public DatabaseLoggingHandler(LogRepository logRecordRepository) {
        this.logRecordRepository = logRecordRepository;
    }


    @Override
    public void publish(java.util.logging.LogRecord record) {
        // Sprawdzenie, czy log jest logowalny (czy ma odpowiedni poziom logowania)
        if (!isLoggable(record)) {
            return;
        }
        // Pobranie poziomu logowania
        String locLevel = record.getLevel().getName();

        // Tworzymy obiekt logu do zapisania w bazie danych
        LogRecord logRecord = new LogRecord();
        logRecord.setMessage(record.getMessage());

        // Utworzenie formatu dla daty i godziny
        LocalDateTime localData = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localData.format(formatter); // Formatowanie daty i godziny
        logRecord.setTimestamp(formattedDateTime);

        // Ustawienie poziomu logu
        logRecord.setLevel(locLevel);

        // Zapisanie logu w bazie danych
        logRecordRepository.save(logRecord);
    }

    @Override
    public void flush() { }
    @Override
    public void close() throws SecurityException { }
}
