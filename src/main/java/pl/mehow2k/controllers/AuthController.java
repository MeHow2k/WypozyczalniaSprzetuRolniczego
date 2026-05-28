package pl.mehow2k.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import pl.mehow2k.models.Role;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.RoleRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.security.AuthService;
import pl.mehow2k.transfers.JWTResponse;
import pl.mehow2k.transfers.LoginRequest;
import pl.mehow2k.transfers.RegisterRequest;
import pl.mehow2k.transfers.UserInfoResponse;

import java.util.ArrayList;
import java.util.List;

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
                    .path("/api")           // Ciasteczko będzie wysyłane tylko do endpointów zaczynających się od /api
                    .maxAge(24 * 60 * 60)   // Czas życia: 24 godziny (zgodnie z tokenem)
                    .httpOnly(true)         // KLUCZOWE: JS nie ma dostępu do ciasteczka (Ochrona przed XSS!)
                    .secure(false)          // W produkcji dajemy TRUE (wymaga HTTPS). Na localhost zostawiamy false.
                    .sameSite("Lax")        // Ochrona przed CSRF potem na STRICT!!
                    .build();

            // Pobieramy role, aby przesłać je frontendowi do konfiguracji widoku (UX)
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new UserInfoResponse(user.getUsername(), roleNames));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }@PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Przy wylogowaniu wysyłamy puste ciasteczko z czasem ważności 0, aby przeglądarka je skasowała
        ResponseCookie cookie = ResponseCookie.from("jwtToken", "")
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Wylogowano");
    }
}
