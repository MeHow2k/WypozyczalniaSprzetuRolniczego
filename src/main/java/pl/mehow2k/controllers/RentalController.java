package pl.mehow2k.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.mehow2k.models.Machine;
import pl.mehow2k.models.Reservation;
import pl.mehow2k.models.ReservationStatus;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.MachineRepository;
import pl.mehow2k.repositories.ReservationRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.transfers.ReservationRequest;
import pl.mehow2k.transfers.ReservationStatusRequest;

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
            // Pobieramy login aktualnie zalogowanego użytkownika z cookie
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono zalogowanego użytkownika."));

            Machine machine = machineRepository.findById(request.getMachineId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono maszyny"));

            if (!machine.isAvailable()) {
                return ResponseEntity.badRequest().body("Ta maszyna jest aktualnie niedostępna.");
            }

            if (request.getStartDate().isAfter(request.getEndDate())) {
                return ResponseEntity.badRequest().body("Data rozpoczęcia nie może być późniejsza niż data zakończenia.");
            }

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setMachine(machine);
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus(ReservationStatus.PENDING);

            reservationRepository.save(reservation);
            return ResponseEntity.ok("Rezerwacja " + machine.getName() + " została złożona i oczekuje na weryfikację pracownika.");
        }

        @GetMapping("/reservations")
        @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
        public ResponseEntity<List<Reservation>> getAllReservations() {
            return ResponseEntity.ok(reservationRepository.findAll());
        }

        @PutMapping("/updatestatus/{id}")
        @PreAuthorize("hasRole('STAFF')")
        public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, @RequestBody ReservationStatusRequest request) {

            // Walidacja czy DTO nie jest puste
            if (request.getStatus() == null) {
                return ResponseEntity.badRequest().body("Error: Status rezerwacji nie może być pusty!");
            }

            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji."));

            // Zmieniamy status (np. na APPROVED lub REJECTED)
            reservation.setStatus(request.getStatus());

            if (request.getStatus() == ReservationStatus.APPROVED) {
                Machine machine = reservation.getMachine();
                machine.setAvailable(false);
                machineRepository.save(machine);
            }

            // Jeśli zamówienie zostało sfinalizowane lub odrzucone maszyna znowu dostepna

            if (request.getStatus() == ReservationStatus.COMPLETED || request.getStatus() == ReservationStatus.REJECTED) {
                Machine machine = reservation.getMachine();
                machine.setAvailable(true);
                machineRepository.save(machine);
            }

            reservationRepository.save(reservation);
            return ResponseEntity.ok("Zmieniono status rezerwacji o id: "+id+" na: " + request.getStatus());
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
        @GetMapping("/weather-check")
        @PreAuthorize("hasAnyRole('CLIENT', 'STAFF', 'ADMIN')")
        public ResponseEntity<String> checkWeatherForHarvest() {
            // W prawdziwym projekcie użyłbyś tu RestTemplate lub WebClient do wykonania żądania HTTP:
            // String url = "https://api.openweathermap.org/data/2.5/weather?q=Warsaw&appid=" + weatherApiKey;

            // Na potrzeby projektu zwracamy symulację, która dowodzi bezpiecznego użycia klucza na backendzie
            return ResponseEntity.ok("Autoryzacja zewnętrznego API powiodła się. Pogoda sprzyja zbiorom.");
        }
    }


