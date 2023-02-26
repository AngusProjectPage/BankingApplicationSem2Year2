package uk.co.asepstrath.bank;

public class Transaction {
    private String id;
    private String depositAccount;
    private String timestamp;
    private double amount;
    private String currency;

    public String getCurrency() {
        return currency;
    }
    public double getAmount() {
        return amount;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public String getDepositAccount() {
        return depositAccount;
    }

    public String getId() {
        return id;
    }
}
