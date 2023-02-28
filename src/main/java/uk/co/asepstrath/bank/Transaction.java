package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Transaction {

    private final String id;
    private final String depositAccount;
    private final String withdrawAccount;
    private final String timestamp;
    private final BigDecimal amount;
    private final String currency;

    public Transaction(String _id, String _depositAccount, String _withdrawAccount, String _timestamp, BigDecimal _amount, String _currency) {
        this.id = _id;
        this.depositAccount = _depositAccount;
        this.withdrawAccount = _withdrawAccount;
        this.timestamp = _timestamp;
        this.amount = _amount;
        this.currency = _currency;
    }

    public String getId() {return id;}
    public String getDepositAccount() {
        return depositAccount;
    }
    public String getWithdrawAccount() { return withdrawAccount; }
    public String getTimestamp() {
        return timestamp;
    }
    public double getAmount() {
        return this.amount.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    public String getCurrency() {
        return currency;
    }

}
