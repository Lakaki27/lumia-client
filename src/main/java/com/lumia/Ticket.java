package com.lumia;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    public ArrayList<String> articlesSumUp = new ArrayList<String>();
    public double totalPrice;

    public Ticket(List<Article> articles, double totalPrice) {
        this.totalPrice = totalPrice;

        for (Article article : articles) {
            articlesSumUp.add(String.valueOf(article.getTotalPrice()) + "€ - " + String.valueOf(article.getAmount()) + "x " + article.getName());
        }
    }

    public boolean print() {
        System.out.println(articlesSumUp.toString());
        System.out.println("Total: " + String.valueOf(totalPrice) + "€");
        return true;
    }
}
