package br.com.projeto.biblioteca.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Client client;
    @ManyToOne
    private User user;
    @ManyToMany
    @JoinTable(
            name = "rent_books",
            joinColumns = @JoinColumn(name = "rent_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books = new ArrayList<>();

    public Rent(){}

    public Rent(Client client, User user, List<Book> books) {
        this.client = client;
        this.user = user;
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", client=" + client +
                ", user=" + user +
                ", books=" + books;
    }
}
