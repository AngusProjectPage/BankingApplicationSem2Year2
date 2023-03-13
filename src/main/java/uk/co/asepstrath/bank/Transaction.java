package uk.co.asepstrath.bank;

import com.github.jknack.handlebars.internal.lang3.StringUtils;

import java.math.BigDecimal;

public class Transaction {

    private final String id;
    private final String depositAccount;
    private final String withdrawAccount;
    private final String timestamp;
    private final BigDecimal amount;
    private final String currency;
    private final Boolean fraudulent;


    public Transaction(String _id, String _depositAccount, String _withdrawAccount, String _timestamp, BigDecimal _amount, String _currency, Boolean _fraudulent) {
        // Limit string lengths to prevent SQL injection
        this.id = StringUtils.left(_id, 36);
        this.depositAccount = StringUtils.left(_depositAccount, 36);
        this.withdrawAccount = StringUtils.left(_withdrawAccount, 36);
        this.timestamp = StringUtils.left(_timestamp, 255);
        this.amount = _amount;
        this.currency = StringUtils.left(_currency, 3);
        this.fraudulent = _fraudulent;
    }

    public String getId() { return id; }
    public String getDepositAccount() {
        return depositAccount;
    }
    public String getWithdrawAccount() { return withdrawAccount; }
    public String getTimestamp() {
        return timestamp;
    }
    public BigDecimal getAmount() {
        return this.amount;
    }
    public String getCurrency() {
        return currency;
    }
    public Boolean isFraudulent() { return fraudulent; }

}
