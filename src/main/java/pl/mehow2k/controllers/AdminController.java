package pl.mehow2k.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.mehow2k.models.Role;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.RoleRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.transfers.GiveRoleRequest;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    // pobranie listu userów
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/addrole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long id, @RequestBody GiveRoleRequest request) {

        //Szukamy użytkownika w bazie po ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Nie znaleziono użytkownika o ID: " + id));

        // Szukamy roli w bazie po nazwie
        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Error: Rola " + request.getRoleName() + " nie istnieje w bazie danych!"));

        // Sprawdzamy, czy użytkownik nie ma już tej roli
        if (user.getRoles().contains(role)) {
            return ResponseEntity.badRequest().body("Użytkownik posiada już tę rolę: " + request.getRoleName());
        }

        // Dodajemy rolę do kolekcji użytkownika i zapisujemy w bazie
        user.getRoles().add(role);
        userRepository.save(user);

        return ResponseEntity.ok("Pomyślnie nadano rolę " + request.getRoleName() + " użytkownikowi " + user.getUsername());
    }

    @PutMapping("/deleterole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoleFromUser(@PathVariable Long id, @RequestBody GiveRoleRequest request) {

        if ("ROLE_ADMIN".equalsIgnoreCase(request.getRoleName())) {
            return ResponseEntity.badRequest().body("Nie można odebrać roli ADMINISTRATORA.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Nie znaleziono użytkownika o ID: " + id));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Error: Rola " + request.getRoleName() + " nie istnieje w bazie danych!"));

        // Sprawdzamy, czy użytkownik m rolę, którą chcemy usunąć
        if (!user.getRoles().contains(role)) {
            return ResponseEntity.badRequest().body("Użytkownik nie posiada roli: " + request.getRoleName());
        }

        // Usuwamy rolę i zapisujemy zmiany
        user.getRoles().remove(role);
        userRepository.save(user);

        return ResponseEntity.ok("Pomyślnie odebrano rolę " + request.getRoleName() + " użytkownikowi " + user.getUsername());
    }
}