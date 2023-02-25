package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Account {
    private UUID id;
    private String name;
    private BigDecimal balance;
    private String currency;
    private String accountType;

    Account(String _name) {
        this.id = UUID.randomUUID();
        this.name = _name;
        this.balance = BigDecimal.ZERO;
        this.currency = "GBP";
        this.accountType = "Current Account";
    }

    Account(String _name, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.name = _name;
        this.balance = amount;
        this.currency = "GBP";
        this.accountType = "Current Account";
    }

    public String getUUID() { return id.toString(); }

    public String getName() {
        return name;
    }

    public double getBalance() { return this.balance.setScale(2, RoundingMode.HALF_UP).doubleValue(); }

    public String getCurrency() { return currency; }

    public String getAccountType() { return accountType; }

    public void deposit(double amount) {
        this.balance = this.balance.add(new BigDecimal(amount));
    }

    public void withdraw(double amount){
        BigDecimal amountBD = new BigDecimal(amount);
        if(amount > 0 && this.balance.compareTo(amountBD) >= 0){
            this.balance = this.balance.subtract(amountBD);
        }
        else{
            throw new ArithmeticException("Cannot withdraw amount: "+ amount + " From Account.");
        }
    }

    @Override
    public String toString() {
        return "[" + id + "] " +
                name + ": " +
                currency + balance.toString()
                + " (" + accountType + ")";
    }

}
