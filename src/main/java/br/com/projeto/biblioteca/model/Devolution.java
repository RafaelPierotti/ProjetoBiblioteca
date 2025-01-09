package br.com.projeto.biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "devolutions")
public class Devolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Rent rent;
    private String devolutionDate;
    private Double totalFees;

    public Devolution(){}

    public Devolution(User user, Rent rent, String devolutionDate, Double totalFees) {
        this.user = user;
        this.rent = rent;
        this.devolutionDate = devolutionDate;
        this.totalFees = totalFees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(Double totalFees) {
        this.totalFees = totalFees;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    @Override
    public String toString() {
        return "user=" + user +
                ", rent=" + rent +
                ", devolutionDate='" + devolutionDate;
    }

    public String getDevolutionDate() {
        return devolutionDate;
    }

    public void setDevolutionDate(String devolutionDate) {
        this.devolutionDate = devolutionDate;
    }
}
