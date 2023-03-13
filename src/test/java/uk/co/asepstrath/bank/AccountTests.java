package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AccountTests {

    @Test
    @DisplayName("Create Account")
    void createAccount(){
        Account a = new Account();
        Assertions.assertNotNull(a);
    }

    @Test
    @DisplayName("Value Initialisation")
    void ZeroStart(){
        Account a = new Account();
        Assertions.assertNotNull(a.getId());
        Assertions.assertEquals("Default Account", a.getName());
        Assertions.assertEquals(BigDecimal.ZERO, a.getBalance());
        Assertions.assertEquals("GBP", a.getCurrency());
        Assertions.assertEquals("Current Account", a.getAccountType());
    }

    @Test
    @DisplayName("Adding Funds")
    void AddingFunds(){
        Account a = new Account("John Smith", BigDecimal.valueOf(20));
        a.deposit(BigDecimal.valueOf(50));
        Assertions.assertEquals(BigDecimal.valueOf(70),a.getBalance());
    }

    @Test
    @DisplayName("Withdrawing Funds")
    void SubtractFunds(){
        Account a = new Account("John Smith", BigDecimal.valueOf(40));
        a.withdraw(BigDecimal.valueOf(20));
        Assertions.assertEquals(BigDecimal.valueOf(20),a.getBalance());
    }

    @Test
    @DisplayName("Throw Overdraft")
    void NoOverDraft(){
        Account a = new Account("John Smith", BigDecimal.valueOf(30));
        Assertions.assertThrows(ArithmeticException.class,()-> a.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    @DisplayName("Deposit/Withdraw Calculation")
    void SuperSaving(){
        Account a = new Account("John Smith", BigDecimal.valueOf(20));
        for(int i=0; i<5; i++){
            a.deposit(BigDecimal.valueOf(10));
        }
        for(int j=0;j<3; j++){
            a.withdraw(BigDecimal.valueOf(20));
        }
        Assertions.assertEquals(BigDecimal.valueOf(10),a.getBalance());
    }

    @Test
    @DisplayName("Decimal Balance")
    void DepositWithPennies(){
        Account a = new Account("John Smith", BigDecimal.valueOf(5.45));
        a.deposit(BigDecimal.valueOf(17.56));
        Assertions.assertEquals(BigDecimal.valueOf(23.01),a.getBalance());
    }

}
