package se.lu.ics.models;

public class Purchase {
    private String accountNo;
    private String basketNo;
    private String purchaseDate;

    public Purchase(String accountNo, String basketNo, String purchaseDate) {
        this.accountNo = accountNo;
        this.basketNo = basketNo;
        this.purchaseDate = purchaseDate;
    }

    public String getAccountNo(){
        return accountNo;
    }

    public void setAccountNo(String accountNo){
        this.accountNo = accountNo;
    }

    public String getBasketNo() {
        return basketNo;
    }

    public void setBasketNo(String basketNo) {
        this.basketNo = basketNo;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}