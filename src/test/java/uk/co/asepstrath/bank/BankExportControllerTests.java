package uk.co.asepstrath.bank;

import com.google.gson.Gson;
import kong.unirest.HttpMethod;
import kong.unirest.MockClient;
import kong.unirest.json.JSONArray;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class BankExportControllerTests {

    static BankExportController bankExportController;

    @BeforeAll
    static void beforeAll() {
        bankExportController = new BankExportController();
    }

    @Test
    @DisplayName("Generate CSV Headers")
    void getHeaders() {
        Account a = new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account");
        Transaction t = new Transaction("99999999-9999-9999-9999-999999999999", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "2020-01-01T00:00:00Z", BigDecimal.valueOf(123.45), "GBP", false);

        String[] accountHeaders = {"id", "name", "balance", "currency", "accountType"};
        String[] transactionHeaders = {"id", "depositAccount", "withdrawAccount", "timestamp", "amount", "currency", "fraudulent"};

        String[] expAccountHeaders = bankExportController.getHeaders(new JSONArray("[" + new Gson().toJson(a) + "]"));
        String[] expTransactionHeaders = bankExportController.getHeaders(new JSONArray("[" + new Gson().toJson(t) + "]"));

        Assertions.assertArrayEquals(accountHeaders, expAccountHeaders);
        Assertions.assertArrayEquals(transactionHeaders, expTransactionHeaders);
    }

    @Test
    @DisplayName("Generate CSV Data")
    void getData() {
        ArrayList<Account> as = new ArrayList<>();
        as.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        as.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        ArrayList<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("99999999-9999-9999-9999-999999999999", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "2020-01-01T00:00:00Z", BigDecimal.valueOf(123.45), "GBP", false));

        String[] accountHeaders = {"id", "name", "balance", "currency", "accountType"};
        String[] transactionHeaders = {"id", "depositAccount", "withdrawAccount", "timestamp", "amount", "currency"};

        List<List<String>> expAccountData = bankExportController.getData(new JSONArray(new Gson().toJson(as.toArray())), accountHeaders);
        List<List<String>> expTransactionData = bankExportController.getData(new JSONArray(new Gson().toJson(ts.toArray())), transactionHeaders);

        Assertions.assertEquals(2, expAccountData.size());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", expAccountData.get(0).get(0));
        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", expAccountData.get(1).get(0));

        Assertions.assertEquals(1, expTransactionData.size());
        Assertions.assertEquals("99999999-9999-9999-9999-999999999999", expTransactionData.get(0).get(0));
    }

    @Test
    @DisplayName("Generate CSV")
    void generateCSV() {
        ArrayList<Account> as = new ArrayList<>();
        as.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        as.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        Assertions.assertDoesNotThrow(() -> bankExportController.generateCSV(new JSONArray(new Gson().toJson(as.toArray()))));
    }

    @Test
    @DisplayName("Export Accounts")
    void exportAccounts() {
        ArrayList<Account> as = new ArrayList<>();
        as.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        as.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://localhost:8080/api/accounts").thenReturn("{\"accounts\": " + new Gson().toJson(as.toArray()) + "}");

        Assertions.assertDoesNotThrow(() -> bankExportController.exportAccounts());

        MockClient.clear();
    }

    @Test
    @DisplayName("Export Transactions")
    void exportTransactions() {
        ArrayList<Transaction> ts = new ArrayList<>();
        ts.add(new Transaction("99999999-9999-9999-9999-999999999999", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "2020-01-01T00:00:00Z", BigDecimal.valueOf(123.45), "GBP", false));

        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://localhost:8080/api/transactions").thenReturn("{\"transactions\": " + new Gson().toJson(ts.toArray()) + "}");

        Assertions.assertDoesNotThrow(() -> bankExportController.exportTransactions());

        MockClient.clear();
    }

    @Test
    @DisplayName("Export Account")
    void exportAccount() {
        Account a = new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account");

        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://localhost:8080/api/accounts/00000000-0000-0000-0000-000000000000").thenReturn("{\"account\": " + new Gson().toJson(a) + "}");

        Assertions.assertNotEquals(new ByteArrayInputStream("Account not found!".getBytes()), bankExportController.exportAccount("00000000-0000-0000-0000-000000000000"));

        MockClient.clear();
    }

    @Test
    @DisplayName("Export Transaction")
    void exportTransaction() {
        Transaction t = new Transaction("99999999-9999-9999-9999-999999999999", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "2020-01-01T00:00:00Z", BigDecimal.valueOf(123.45), "GBP", false);

        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://localhost:8080/api/transactions/99999999-9999-9999-9999-999999999999").thenReturn("{\"transaction\": " + new Gson().toJson(t) + "}");

        Assertions.assertNotEquals(new ByteArrayInputStream("Transaction not found!".getBytes()), bankExportController.exportTransaction("99999999-9999-9999-9999-999999999999"));

        MockClient.clear();
    }
}
