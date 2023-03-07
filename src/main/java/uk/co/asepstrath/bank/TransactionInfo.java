package uk.co.asepstrath.bank;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionInfo {

    private String id;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private int numTransactions;
    private int failedTransactions;

    public TransactionInfo(String id, BigDecimal balanceBefore) {
        this.id = id;
        this.balanceBefore = balanceBefore;
        balanceAfter = new BigDecimal(0);
        numTransactions = 0;
        failedTransactions = 0;
    }
    public String getId() {return id;}
    public BigDecimal getBalanceBefore() {return balanceBefore.setScale(2, RoundingMode.HALF_UP);}
    public BigDecimal getBalanceAfter() {return balanceAfter.setScale(2, RoundingMode.HALF_UP);}
    public int getNumTransactions() {return numTransactions;}
    public int getFailedTransactions() {return failedTransactions;}
    public void updateTransactionCount() {
        numTransactions++;
    }
    public void updateFailedTransactionCount() {
        failedTransactions++;
    }
    public void setBalance(BigDecimal balance) {
        balanceAfter = balance;
    }
}
