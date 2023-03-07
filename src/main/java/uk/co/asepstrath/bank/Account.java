package uk.co.asepstrath.bank;

import com.github.jknack.handlebars.internal.lang3.StringUtils;

import java.math.BigDecimal;
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
        // Limit string lengths to prevent SQL injection
        this.id = StringUtils.left(_id, 36);
        this.name = StringUtils.left(_name, 255);
        this.balance = amount;
        this.currency = StringUtils.left(_currency, 3);
        this.accountType = StringUtils.left(_accountType, 255);
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public BigDecimal getBalance() { return this.balance; }

    public String getCurrency() { return currency; }

    public String getAccountType() { return accountType; }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) throws ArithmeticException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Cannot withdraw negative amount: " + amount + " From Account.");
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new ArithmeticException("Cannot withdraw amount: " + amount + " From Account.");
        }

        this.balance = this.balance.subtract(amount);
    }

    @Override
    public String toString() {
        return "[" + id + "] " +
                name + ": " +
                currency + balance.toString()
                + " (" + accountType + ")";
    }

}
