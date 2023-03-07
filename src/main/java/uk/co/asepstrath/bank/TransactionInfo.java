package uk.co.asepstrath.bank;

import com.github.jknack.handlebars.internal.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionInfo {

    private String id;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private int numTransactions;

    public TransactionInfo(String id, BigDecimal balanceBefore) {
        this.id = id;
        this.balanceBefore = balanceBefore;
        balanceAfter = new BigDecimal(0);
        numTransactions = 0;
    }
    public String getId() {return id;}
    public BigDecimal getBalanceBefore() {return balanceBefore;}
    public BigDecimal getBalanceAfter() {return balanceAfter;}
    public int getNumTransactions() {return numTransactions;}
    public void updateTransactionCount() {
        numTransactions++;
    }
    public void setBalance(BigDecimal balance) {
        balanceAfter = balance;
    }
}
