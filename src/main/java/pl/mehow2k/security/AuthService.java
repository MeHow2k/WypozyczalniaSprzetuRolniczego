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

    // 1. REJESTRACJA NOWEGO UŻYTKOWNIKA
    public String registerUser(RegisterRequest registerRequest) {
        // Sprawdzenie, czy email jest już zajęty (Ochrona przed duplikatami)
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Error: Email jest już zajęty!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());

        // BEZPIECZEŃSTWO: Hashowanie hasła za pomocą BCrypt przed zapisem do bazy
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // przypisanie domyślnej roli CLIENT
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Error: Rola CLIENT nie istnieje w bazie!"));

        user.setRoles(Set.of(clientRole));

        userRepository.save(user);
        return "Użytkownik zarejestrowany pomyślnie!";
    }

    // 2. LOGOWANIE I GENEROWANIE TOKENU
    public String loginUser(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // BEZPIECZEŃSTWO: Weryfikacja przesłanego hasła z hashem z bazy danych
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                // Hasła się zgadzają -> generujemy token JWT
                //map Role na stringi
                List<String> roleNames = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());
                return tokenProvider.generateToken(user.getUsername(), roleNames);
            }
        }

        // Ze względów bezpieczeństwa zwracamy ogólny komunikat, aby nie podpowiadać hakerowi,
        // czy pomylił login, czy hasło.
        throw new RuntimeException("Błędny login lub hasło!");
    }
}
