package br.com.projeto.biblioteca.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Sell> sells = new ArrayList<>();
    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Rent> rents = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Devolution> devolution;

    public User(){}

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Sell> getSells() {
        return sells;
    }

    public void setSells(List<Sell> sells) {
        sells.forEach(s -> s.setUser(this));
        this.sells = sells;
    }

    public List<Rent> getRents() {
        return rents;
    }

    public void setRents(List<Rent> rents) {
        rents.forEach(r -> r.setUser(this));
        this.rents = rents;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", name='" + name;
    }
}
