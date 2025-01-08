package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByNameContainingIgnoreCase(String name);

    Optional<Client> findByCpfEquals(String cpf);

    Optional<Client> findByIdEquals(Long id);
}
