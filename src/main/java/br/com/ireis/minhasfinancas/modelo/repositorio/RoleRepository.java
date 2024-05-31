package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
