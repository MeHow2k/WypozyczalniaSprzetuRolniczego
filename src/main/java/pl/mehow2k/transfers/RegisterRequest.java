package pl.mehow2k.transfers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Nazwa użytkownika nie może być pusta")
    private String username;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 8, message = "Hasło musi składać się z co najmniej 8 znaków")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Hasło musi zawierać co najmniej jedną wielką literę, jedną cyfrę oraz jeden znak specjalny (@$!%*?&)"
    )
    private String password;


    // Gettery i Settery
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}