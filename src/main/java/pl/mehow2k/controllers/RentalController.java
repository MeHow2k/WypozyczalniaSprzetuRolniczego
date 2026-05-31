package pl.mehow2k.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.mehow2k.models.Machine;
import pl.mehow2k.models.Reservation;
import pl.mehow2k.models.ReservationStatus;
import pl.mehow2k.models.User;
import pl.mehow2k.repositories.MachineRepository;
import pl.mehow2k.repositories.ReservationRepository;
import pl.mehow2k.repositories.UserRepository;
import pl.mehow2k.transfers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
    @RequestMapping("/api/rental")
    public class RentalController {

        @Autowired
        private MachineRepository machineRepository;

        @Autowired
        private ReservationRepository reservationRepository;

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private Logger logger;

        //Do obliczania ceny za trase
        // Wstrzykujemy klucz zewnętrznego API z pliku konfiguracyjnego wraz z wspolrzednymi firmy i ceną/km
        @Value("${routing.api.key}")
        private String apiKey;

        @Value("${routing.depot.latitude}")
        private double depotLat;

        @Value("${routing.depot.longitude}")
        private double depotLon;

        @Value("${routing.cost.per.km}")
        private double costPerKm;

        //Przeglądanie sprzętu DLA NIEZALOGOWANYCH I ZALOGOWANYCH
        @GetMapping("/machines")
        public List<Machine> getAllMachines() {
            return machineRepository.findAll();
        }

        // Rezerwacja maszyn przez klienta
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

            LocalDate today = LocalDate.now();
            LocalDate maxFutureDate = today.plusYears(1); // Maksymalnie 1 rok w przód

            //Blokada dat z przeszłości
            if (request.getStartDate().isBefore(today)) {
                return ResponseEntity.badRequest().body("Nie można zarezerwować maszyny z datą wsteczną.");
            }

            //Blokada rezerwacji zbyt wcześnie (np.user próbuje rezerwować na za 2 lata)
            if (request.getStartDate().isAfter(maxFutureDate) || request.getEndDate().isAfter(maxFutureDate)) {
                return ResponseEntity.badRequest().body("Nie można rezerwować sprzętu z wyprzedzeniem większym niż rok.");
            }

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setMachine(machine);
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus(ReservationStatus.PENDING);

            reservationRepository.save(reservation);
            //log
            String logString = String.format(
                    "REZERWACJE: Użytkownik [%s] (ID: %d) wysłał żądanie rezerwacji maszyny %s (ID: %d) na czas od %s do %s",
                    currentUsername, user.getId(), machine.getName(),machine.getId(), reservation.getStartDate().toString(),reservation.getEndDate().toString()
            );
            logger.info(logString);

            return ResponseEntity.ok("Rezerwacja " + machine.getName() + " została złożona i oczekuje na weryfikację pracownika.");
        }

        //pobranie rezerwacji klienta
        @GetMapping("/my-reservations")
        @PreAuthorize("hasRole('CLIENT')")
        public ResponseEntity<List<UserReservationResponse>> getUserReservations() {

            // pobieramy username
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            // szukamy usera w bazie
            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Error: Nie znaleziono zalogowanego użytkownika."));

            // szukanie rezerwacji dla id usera
            List<Reservation> reservations = reservationRepository.findByUserId(user.getId());

            // Mapowanie na DTO
            List<UserReservationResponse> responseList = reservations.stream()
                    .map(r -> new UserReservationResponse(
                            r.getId(),
                            r.getMachine() != null ? r.getMachine().getName() : "Usunięta maszyna",
                            r.getMachine() != null ? r.getMachine().getCategory() : "Brak",
                            r.getStartDate(),
                            r.getEndDate(),
                            r.getStatus()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);
        }

        //pobranie wszystkich rezerwacji
        @GetMapping("/reservations")
        @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
        public ResponseEntity<List<Reservation>> getAllReservations() {
            return ResponseEntity.ok(reservationRepository.findAll());
        }

        //zmiana statusu rezerwacji
        @PutMapping("/updatestatus/{id}")
        @PreAuthorize("hasRole('STAFF')")
        public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, @RequestBody ReservationStatusRequest request) {

            //pobranie prascownika wykonujacego request
            String staffUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            // Walidacja czy DTO nie jest puste
            if (request.getStatus() == null) {
                return ResponseEntity.badRequest().body("Error: Status rezerwacji nie może być pusty!");
            }

            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji."));

            String reservationOriginalStatus = String.valueOf(reservation.getStatus());

            if (!reservation.getMachine().isAvailable() && reservation.getStatus()==ReservationStatus.PENDING) {
                // Jeśli maszyna jest niedostępna, a ktoś próbuje ustawić status INNY niż REJECTED
                if (request.getStatus() != ReservationStatus.REJECTED) {
                    return ResponseEntity.badRequest()
                            .body("Nie można zatwierdzić rezerwacji: Maszyna jest obecnie niedostępna.");
                }
            }
            // Zmieniamy status (np. na APPROVED lub REJECTED)
            reservation.setStatus(request.getStatus());

            //jesli zaakceptowano, maszyna staje sie niedostepna
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

            //log
            String logString = String.format(
                    "REZERWACJE: Pracownik [%s] zmienił status rezerwacji (ID: %d) użytkownika %s (ID: %d) z %s na %s",
                    staffUsername, reservation.getId(), reservation.getUser().getUsername(),reservation.getUser().getId(), reservationOriginalStatus, request.getStatus()
            );
            logger.info(logString);

            return ResponseEntity.ok("Zmieniono status rezerwacji o id: "+id+" na: " + request.getStatus());
        }

        //za pomocą API OpenRouteService, klient moze obliczyć koszt dojazdu maszyny lawetą
        @PostMapping("/calculate-transport")
        @PreAuthorize("hasRole('CLIENT')")
        public ResponseEntity<?> calculateTransportByAddress(@RequestBody TransportRequest request) {

            if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Adres nie może być pusty!");
            }

            RestTemplate restTemplate = new RestTemplate();

            //Zmiana adresu na wspolrzednie:
            // Szukamy lokalizacji w Polsce (boundary.country=POL)
            String geocodeUrl = String.format(
                    "https://api.openrouteservice.org/geocode/search?api_key=%s&text=%s&boundary.country=POL&size=1",
                    apiKey, request.getAddress()
            );

            double clientLon;
            double clientLat;

            try {
                ResponseEntity<JsonNode> geocodeResponse = restTemplate.getForEntity(geocodeUrl, JsonNode.class);
                JsonNode coordinatesNode = geocodeResponse.getBody()
                        .path("features")
                        .get(0)
                        .path("geometry")
                        .path("coordinates");

                if (coordinatesNode.isMissingNode()) {
                    return ResponseEntity.badRequest().body("Nie znaleziono podanej miejscowości w Polsce. Sprawdź literówki.");
                }

                // OpenRouteService w geocode zwraca tablicę: [longitude, latitude]
                clientLon = coordinatesNode.get(0).asDouble();
                clientLat = coordinatesNode.get(1).asDouble();

            } catch (Exception e) {
                System.out.println("GEOCODING ERROR: " + e.getMessage());
                return ResponseEntity.status(502).body("Błąd serwera lokalizacji podczas rozpoznawania adresu.");
            }

            // kalkulacja trasy w oparciu o wspołrzedzne
            String routeUrl = String.format(
                    "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%s,%s&end=%s,%s",
                    apiKey, depotLon, depotLat, clientLon, clientLat
            );

            try {
                ResponseEntity<JsonNode> routeResponse = restTemplate.getForEntity(routeUrl, JsonNode.class);

                double distanceInMeters = routeResponse.getBody()
                        .path("features")
                        .get(0)
                        .path("properties")
                        .path("summary")
                        .path("distance")
                        .asDouble();

                double distanceKm = Math.round((distanceInMeters / 1000.0) * 100.0) / 100.0;
                double totalCost = Math.round((distanceKm * costPerKm) * 100.0) / 100.0;

                return ResponseEntity.ok(new TransportResponse(distanceKm, totalCost));

            } catch (Exception e) {
                return ResponseEntity.status(502).body("Nie udało się wyznaczyć trasy drogowej do tej miejscowości.");
            }
        }

    }


