package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Account {
    private final String id;
    private final String name;
    private BigDecimal balance;
    private final String currency;
    private final String accountType;

    Account() { this(UUID.randomUUID().toString(), "Default Account", BigDecimal.ZERO, "GBP", "Current Account"); }

    Account(String _name, BigDecimal _balance) { this(UUID.randomUUID().toString(), _name, _balance, "GBP", "Current Account"); }

    Account (String _id, String _name, BigDecimal amount, String _currency, String _accountType) {
        this.id = _id;
        this.name = _name;
        this.balance = amount;
        this.currency = _currency;
        this.accountType = _accountType;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public BigDecimal getBalance() { return this.balance.setScale(2, RoundingMode.HALF_UP).doubleValue(); }

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
