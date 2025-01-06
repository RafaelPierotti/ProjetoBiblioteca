package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameEquals(String name);
}
