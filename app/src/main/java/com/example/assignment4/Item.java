package com.example.assignment4;

public class Item {
    private String id;         // Firestore document ID (or random ID if not using Firestore's auto ID)
    private String name;
    private int quantity;
    private double price;

    // Firestore requires an empty constructor
    public Item() {}

    // Constructor without ID (will be set later)
    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getter and setter for the Firestore document ID (or random ID)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for the item name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for the quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and setter for the price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
