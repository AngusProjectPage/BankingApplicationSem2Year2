package uk.co.asepstrath.bank;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

class BankAPIControllerTests {

    static BankData data;
    static BankAPIController bankAPIController;

    @BeforeAll
    static void beforeAll() {
        data = Mockito.mock(BankData.class);
        bankAPIController = new BankAPIController(data);
    }

    @Test
    @DisplayName("Get root")
    void getRoot() {
        Assertions.assertNotNull(bankAPIController.getRoot());
    }

    @Test
    @DisplayName("Get accounts")
    void getAccounts() {
        HashMap<String, Object> hm = new HashMap<>();
        ArrayList<Account> as = new ArrayList<>();
        as.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        as.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        hm.put("dataOrigin", "Mock");
        hm.put("accounts", as);

        Mockito.when(data.getAccounts()).thenReturn(hm);

        String jsonHM = new Gson().toJson(hm);
        Assertions.assertEquals(jsonHM, bankAPIController.getAccounts());
    }

    @Test
    @DisplayName("Get account")
    void getAccount() {
        HashMap<String, Object> hm = new HashMap<>();
        Account a = new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account");
        ArrayList<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "...", BigDecimal.valueOf(123.45), "GBP", false));

        hm.put("dataOrigin", "Mock");
        hm.put("account", a);
        hm.put("transactions", ts);

        Mockito.when(data.getAccount("00000000-0000-0000-0000-000000000000")).thenReturn(hm);

        String jsonHM = new Gson().toJson(hm);
        Assertions.assertEquals(jsonHM, bankAPIController.getAccount("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    @DisplayName("Get transactions")
    void getTransactions() {
        HashMap<String, Object> hm = new HashMap<>();
        ArrayList<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "...", BigDecimal.valueOf(123.45), "GBP", false));
        ts.add(new Transaction("11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "...", BigDecimal.valueOf(678.9), "USD", false));

        hm.put("dataOrigin", "Mock");
        hm.put("transactions", ts);
        hm.put("transactionTotal", ts.size());

        Mockito.when(data.getTransactions()).thenReturn(hm);

        String jsonHM = new Gson().toJson(hm);
        Assertions.assertEquals(jsonHM, bankAPIController.getTransactions());
    }

    @Test
    @DisplayName("Get transaction")
    void getTransaction() {
        HashMap<String, Object> hm = new HashMap<>();
        Transaction t = new Transaction("00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "...", BigDecimal.valueOf(123.45), "GBP", false);

        hm.put("dataOrigin", "Mock");
        hm.put("transaction", t);

        Mockito.when(data.getTransaction("00000000-0000-0000-0000-000000000000")).thenReturn(hm);

        String jsonHM = new Gson().toJson(hm);
        Assertions.assertEquals(jsonHM, bankAPIController.getTransaction("00000000-0000-0000-0000-000000000000"));
    }

}
