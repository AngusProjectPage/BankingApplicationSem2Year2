package uk.co.asepstrath.bank;

public class Transactions {
    private String id;
    private String depositAccount;
    private String timestamp;
    private double amount;
    private String currency;


    public String getId() {return id;}
    public String getDepositAccount() {
        return depositAccount;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public double getCurrentBalance() {
        return amount;
    }
    public String getCurrency() {
        return currency;
    }

}
