package br.com.projeto.biblioteca.repository;

import br.com.projeto.biblioteca.model.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.quantity = b.quantity + :quantity WHERE b.id = :id")
    void addBook(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.quantity = b.quantity - :quantity WHERE b.id = :id")
    void sellBook(@Param("id") Long id, @Param("quantity") Integer quantity);

    Optional<Book> findByIdEquals(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Book b Set b.quantity = b.quantity - 1 WHERE b.id = :id")
    void rentBook(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Book b Set b.quantity = b.quantity + 1 WHERE b.id = :id")
    void devolutionBook(@Param("id") Long id);
}