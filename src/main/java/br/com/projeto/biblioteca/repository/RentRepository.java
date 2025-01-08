package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentRepository extends JpaRepository<Rent, Long> {
}
