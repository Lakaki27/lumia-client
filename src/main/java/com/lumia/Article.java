package com.lumia;

public class Article {
    private int amount;
    private String name;
    private double unitPrice;

    public Article(int amount, String name, double unitPrice) {
        this.amount = amount;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        String returnedName = name;

        if ( name.length()>20 ) {
            returnedName = returnedName.substring(0,20).concat("...");
        }

        return returnedName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return unitPrice * amount;
    }
}
