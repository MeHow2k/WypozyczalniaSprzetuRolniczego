package pl.mehow2k.repositories;

import pl.mehow2k.models.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {
}