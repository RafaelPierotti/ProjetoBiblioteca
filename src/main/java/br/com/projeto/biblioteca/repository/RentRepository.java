package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Book;
import br.com.projeto.biblioteca.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Long> {

    Optional<Rent> findByIdEquals(Long id);

    @Query("SELECT b.id FROM Rent r JOIN r.books b WHERE r.id = :rentId")
    List<Long> findBookIdsByRentId(@Param("rentId") Long rentId);
}
