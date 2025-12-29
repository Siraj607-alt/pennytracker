package com.penny.pennytracker;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    public Expense() {}

    public Expense(User user, double amount, String category, String description, LocalDate date) {
        this.user = user;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    // GETTERS
    public Long getId() { return id; }
    public User getUser() { return user; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    // SETTERS
    public void setUser(User user) { this.user = user; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }
}
