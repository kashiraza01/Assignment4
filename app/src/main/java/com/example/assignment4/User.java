package com.example.assignment4;

public class User {
    private String name;

    public User() {
        // Default constructor jo required hai by Firebase
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
