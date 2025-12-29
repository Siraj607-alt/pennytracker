package com.penny.pennytracker;

import jakarta.persistence.*;

@Entity
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double targetAmount;
    private double savedAmount;

    @ManyToOne
    private User user;

    // constructors
    public SavingsGoal() {}

    public SavingsGoal(String name, double targetAmount, User user) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = 0;
        this.user = user;
    }

    // getters & setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getSavedAmount() { return savedAmount; }
    public User getUser() { return user; }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }
}
