package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Devolution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevolutionRepository extends JpaRepository<Devolution, Long> {
}
