package com.penny.pennytracker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String profession;
    private Integer monthlyIncome;
    private String goal;
    private double walletBalance;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    // Correct getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Correct setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    // getters
    public String getProfession() { return profession; }
    public Integer getMonthlyIncome() { return monthlyIncome; }
    public String getGoal() { return goal; }
    public double getWalletBalance() { return walletBalance; }

    // setters
    public void setProfession(String profession) { this.profession = profession; }
    public void setMonthlyIncome(Integer monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public void setGoal(String goal) { this.goal = goal; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }




}
