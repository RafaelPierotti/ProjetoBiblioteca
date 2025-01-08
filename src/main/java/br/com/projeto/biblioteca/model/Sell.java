package br.com.projeto.biblioteca.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Sell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double total;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @ManyToOne
    private User user;
    @ManyToMany
    @JoinTable(
            name = "sell_books",
            joinColumns = @JoinColumn(name = "sell_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books = new ArrayList<>();

    public Sell(){}

    public Sell(Double total, Integer quantity, PaymentMethod paymentMethod, User user, List<Book> books) {
        this.total = total;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.user = user;
        this.books = books;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", total=" + total +
                ", quantity=" + quantity +
                ", paymentMethod=" + paymentMethod +
                ", user=" + user +
                ", books=" + books;
    }
}
