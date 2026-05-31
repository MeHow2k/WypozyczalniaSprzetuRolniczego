package pl.mehow2k.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mehow2k.models.LogRecord;

import java.util.List;

public interface LogRepository extends JpaRepository<LogRecord,Long> {

    List<LogRecord> findAllByOrderByIdDesc();
}
