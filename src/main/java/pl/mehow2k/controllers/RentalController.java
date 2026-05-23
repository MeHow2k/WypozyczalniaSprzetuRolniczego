package pl.mehow2k.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.mehow2k.models.Machine;
import pl.mehow2k.models.Reservation;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.MachineRepository;
import pl.mehow2k.repositories.ReservationRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.transfers.ReservationRequest;

import java.util.List;

    @RestController
    @RequestMapping("/api/rental")
    public class RentalController {

        @Autowired
        private MachineRepository machineRepository;

        @Autowired
        private ReservationRepository reservationRepository;

        @Autowired
        private UserRepository userRepository;

//        // Wstrzykujemy tajny klucz zewnętrznego API z pliku konfiguracyjnego
//        @Value("${weather.api.key}")
//        private String weatherApiKey;

        //Przeglądanie sprzętu DLA NIEZALOGOWANYCH I ZALOGOWANYCH
        @GetMapping("/machines")
        public List<Machine> getAllMachines() {
            return machineRepository.findAll();
        }

        // Rezerwacja maszyn
        @PostMapping("/reserve")
        @PreAuthorize("hasRole('CLIENT')")
        public ResponseEntity<?> reserveMachine(@RequestBody ReservationRequest request) {
            // Pobieramy login aktualnie zalogowanego użytkownika z kontekstu bezpieczeństwa JWT
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(currentUsername).orElseThrow();

            Machine machine = machineRepository.findById(request.getMachineId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono maszyny"));

            if (!machine.isAvailable()) {
                return ResponseEntity.badRequest().body("Ta maszyna jest aktualnie niedostępna.");
            }

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setMachine(machine);
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus("PENDING");

            reservationRepository.save(reservation);
            return ResponseEntity.ok("Rezerwacja została złożona i oczekuje na weryfikację pracownika.");
        }

        // Akceptacja rezerwacji prez pracownika (zarządzanie statusem)
        @PutMapping("/staff/approve/{id}")
        @PreAuthorize("hasRole('STAFF')")
        public ResponseEntity<?> approveReservation(@PathVariable Long id) {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji"));

            reservation.setStatus("APPROVED");
            reservationRepository.save(reservation);
            return ResponseEntity.ok("Rezerwacja zaakceptowana pomyślnie.");
        }

        // Dodawanie nowego sprzętu - admin
        @PostMapping("/admin/machines")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> addMachine(@RequestBody Machine machine) {
            machineRepository.save(machine);
            return ResponseEntity.ok("Nowy sprzęt rolniczy dodany do bazy danych.");
        }

//        // 5. ZEWNĘTRZNE API (Zabezpieczone kluczem na backendzie)
//        // Użytkownik pyta naszą aplikację, czy pogoda pozwala na pracę w polu.
//        // Nasz backend podczepia pod to ukryty klucz i odpytuje zewnętrzne API.
//        @GetMapping("/weather-check")
//        @PreAuthorize("hasAnyRole('USER', 'EMPLOYEE', 'ADMIN')")
//        public ResponseEntity<String> checkWeatherForHarvest() {
//            // W prawdziwym projekcie użyłbyś tu RestTemplate lub WebClient do wykonania żądania HTTP:
//            // String url = "https://api.openweathermap.org/data/2.5/weather?q=Warsaw&appid=" + weatherApiKey;
//
//            // Na potrzeby projektu zwracamy symulację, która dowodzi bezpiecznego użycia klucza na backendzie
//            return ResponseEntity.ok("Autoryzacja zewnętrznego API powiodła się. [Użyty klucz na backendzie: " + weatherApiKey.substring(0, 4) + "****]. Pogoda sprzyja zbiorom.");
//        }
    }


