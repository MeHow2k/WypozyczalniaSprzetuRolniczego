package pl.mehow2k.repositories;

import pl.mehow2k.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserUsername(String username); // Do wyciągania rezerwacji konkretnego użytkownika
    List<Reservation> findByUserId(Long userId);
}