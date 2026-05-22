package pl.mehow2k.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mehow2k.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

