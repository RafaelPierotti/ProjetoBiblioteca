package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Sell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellRepository extends JpaRepository<Sell, Long> {
}
