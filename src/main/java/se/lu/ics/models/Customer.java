package se.lu.ics.models;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String accountNo;
    private String name;
    private String deliveryAddress;
    private List<FruitBasket> baskets = new ArrayList<>();

    public Customer(String accountNo, String name, String deliveryAddress) {
        this.accountNo = accountNo;
        this.name = name;
        this.deliveryAddress = deliveryAddress;
    }

    public String getAccountNo(){
        return accountNo;
    }

    public String getName() {
        return name;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setAccountNo(String accountNo){
        this.accountNo = accountNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<FruitBasket> getBaskets() {
        return baskets;
    }
}