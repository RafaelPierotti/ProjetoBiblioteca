package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
