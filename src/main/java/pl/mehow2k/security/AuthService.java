package pl.mehow2k.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.mehow2k.models.Role;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.RoleRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.transfers.LoginRequest;
import pl.mehow2k.transfers.RegisterRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTTokenProvider tokenProvider;

    // REJESTRACJA NOWEGO UŻYTKOWNIKA
    public String registerUser(RegisterRequest registerRequest) {
        // Sprawdzenie, czy username jest już zajęty
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Login jest już zajęty!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());

        // Hashowanie hasła za pomocą BCrypt przed zapisem do bazy
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // przypisanie domyślnej roli CLIENT
        Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Error: Rola CLIENT nie istnieje w bazie!"));

        user.setRoles(Set.of(clientRole));

        userRepository.save(user);
        return "Użytkownik zarejestrowany pomyślnie!";
    }

    //LOGOWANIE I GENEROWANIE TOKENU
    public String loginUser(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Weryfikacja przesłanego hasła z hashem z bazy danych
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Hasła się zgadzają -> generujemy token JWT
                //map Role na stringi
                List<String> roleNames = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
                return tokenProvider.generateToken(user.getUsername(), roleNames);
            }
        }
        throw new RuntimeException("Błędny login lub hasło!");
    }
}
