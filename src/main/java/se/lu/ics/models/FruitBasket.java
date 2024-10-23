package se.lu.ics.models;

import java.util.ArrayList;
import java.util.List;

public class FruitBasket {
    private String basketNo;
    private String name;
    private double price;
    private List<Customer> customers = new ArrayList<>();

    public FruitBasket(String basketNo, String name, double price) {
        this.basketNo = basketNo;
        this.name = name;
        this.price = price;
    }

    public String getBasketNo(){
        return basketNo;
    }

    public void setBasketNo(String basketNo){
        this.basketNo = basketNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}