package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account {
    private BigDecimal balance;

    Account(){
        this.balance = new BigDecimal(0);
    }

    Account(double amount){
        this.balance = new BigDecimal(amount);
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

}
