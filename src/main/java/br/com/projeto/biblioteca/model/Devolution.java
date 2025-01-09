package br.com.projeto.biblioteca.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

public class Devolution {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Rent rent;
    private String devolutionDate;
    private Double totalFees;

    public Devolution(User user, Rent rent, String devolutionDate, Double totalFees) {
        this.user = user;
        this.rent = rent;
        this.devolutionDate = devolutionDate;
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
