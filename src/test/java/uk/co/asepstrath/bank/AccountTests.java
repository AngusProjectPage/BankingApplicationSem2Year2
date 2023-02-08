package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Test
    @DisplayName("Test 0")
    public void createAccount(){
        Account a = new Account();
        Assertions.assertNotNull(a);
    }

    @Test
    @DisplayName("Test 1 : Start Simple")
    public void ZeroStart(){
        Account a = new Account();
        Assertions.assertEquals(0, a.getBalance());
    }

    @Test
    @DisplayName("Test 2: Adding Funds")
    public void AddingFunds(){
        Account a = new Account(20);
        a.deposit(50);
        Assertions.assertEquals(70,a.getBalance());
    }

    @Test
    @DisplayName("Test 3: Spending Spree")
    public void SubtractFunds(){
        Account a = new Account(40);
        a.withdraw(20);
        Assertions.assertEquals(20,a.getBalance());
    }

    @Test
    @DisplayName("Test 4: No OverDraft")
    public void NoOverDraft(){
        Account a = new Account(30);
        Assertions.assertThrows(ArithmeticException.class,()-> a.withdraw(100));
    }

    @Test
    @DisplayName("Test 5: Super Saving")
    public void SuperSaving(){
        Account a = new Account(20);
        for(int i=0; i<5; i++){
            a.deposit(10);
        }
        for(int j=0;j<3; j++){
            a.withdraw(20);
        }
        Assertions.assertEquals(10,a.getBalance());
    }

    @Test
    @DisplayName("Test 6: Taking Care of Pennies")
    public void DepositWithPennies(){
        Account a = new Account(5.45);
        a.deposit(17.56);
        Assertions.assertEquals(23.01,a.getBalance());

    }



}
