package pl.mehow2k.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mehow2k.models.Role;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.RoleRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.security.AuthService;
import pl.mehow2k.transfers.JWTResponse;
import pl.mehow2k.transfers.LoginRequest;
import pl.mehow2k.transfers.RegisterRequest;

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

            // Pobieramy role, aby przesłać je frontendowi do konfiguracji widoku (UX)
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .toList();

            return ResponseEntity.ok(new JWTResponse(jwt, user.getUsername(), roleNames));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
