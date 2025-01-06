package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByCpfEquals(String cpf);
}
