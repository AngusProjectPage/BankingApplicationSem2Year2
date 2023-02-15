package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

public class Account {
    private String name;
    private BigDecimal balance;

    Account(String _name) {
        this.name = _name;
        this.balance = BigDecimal.ZERO;
    }

    Account(String _name, BigDecimal amount){
        this.name = _name;
        this.balance = amount;
    }


    public void deposit(double amount) {
        this.balance = this.balance.add(new BigDecimal(amount));
    }

    public double getBalance() {

        return this.balance.setScale(2, RoundingMode.HALF_UP).doubleValue();
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
        return name + ": " + balance.toString();
    }

}
