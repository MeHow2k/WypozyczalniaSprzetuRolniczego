package pl.mehow2k.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import pl.mehow2k.models.Role;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.security.AuthService;
import pl.mehow2k.transfers.LoginRequest;
import pl.mehow2k.transfers.RegisterRequest;
import pl.mehow2k.transfers.UserInfoResponse;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


    // Endpoint rejestracji (Dostępny dla każdego)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Walidacja nazwy użytkownika
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nazwa użytkownika nie może być pusta");
        }

        // Walidacja czy hasło nie jest puste
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Hasło nie może być puste");
        }

        // Walidacja długości hasła
        if (registerRequest.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Hasło musi składać się z co najmniej 8 znaków");
        }

        // Walidacja struktury hasła
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!Pattern.matches(passwordRegex, registerRequest.getPassword())) {
            return ResponseEntity.badRequest().body("Hasło musi zawierać co najmniej jedną wielką literę, jedną cyfrę oraz jeden znak specjalny (@$!%*?&)");
        }


        try {
            String result = authService.registerUser(registerRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint logowania (Dostępny dla każdego)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.loginUser(loginRequest);

            ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", jwt)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Lax")
                    .build();

            // Pobieramy role, aby przesłać je frontendowi
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new UserInfoResponse(user.getUsername(), roleNames));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    //endpiont wylogowania
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Przy wylogowaniu wysyłamy puste ciasteczko z czasem ważności 0, aby przeglądarka je skasowała
        ResponseCookie cookie = ResponseCookie.from("jwtToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Wylogowano");
    }
}
