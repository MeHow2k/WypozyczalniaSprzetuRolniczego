package pl.mehow2k.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String timestamp;
    private String level;

    public LogRecord(String message, String timestamp) {
            this.message = message;
            this.timestamp = timestamp;
            this.level = "INFO";
    }

    public LogRecord() {}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    public String getTimestamp() {return timestamp;}
    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}
    public String getLevel() {return level; }
    public void setLevel(String level) {this.level = level;}
    public Long getId() {return id; }
}
