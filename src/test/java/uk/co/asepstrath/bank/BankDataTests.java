package uk.co.asepstrath.bank;

import kong.unirest.*;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;

import org.mockito.Mockito;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class BankDataTests {

    static DataSource ds;
    static Logger log;
    static MockClient mc;
    static Connection conn;
    static Statement stmt;
    static ResultSet rs;

    @BeforeEach
    public void before() {
        ds = mock(DataSource.class);
        log = mock(Logger.class);
        mc = MockClient.register();
        conn = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
    }

    @Test
    @DisplayName("Create Data")
    public void createData() {
        BankData bankData = new BankData(ds, log);
        Assertions.assertNotNull(bankData);
    }

    // Accounts

    @Test
    @DisplayName("Get Accounts from API")
    public void getAPIAccounts() {
        BankData bankData = new BankData(ds, log);
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://api.asep-strath.co.uk/api/Team8/accounts").thenReturn("[" +
                "{\"id\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"Homer Simpson\",\"balance\":123.45,\"currency\":\"GBP\",\"accountType\":\"Current Account\"}," +
                "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"name\":\"Peter Griffin\",\"balance\":678.9,\"currency\":\"USD\",\"accountType\":\"Investment Account\"}" +
        "]");

        ArrayList<Account> r = bankData.getAccountsAPI();

        // Assertions
        Assertions.assertEquals(2, r.size());

        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", r.get(0).getId());
        Assertions.assertEquals("Homer Simpson", r.get(0).getName());
        Assertions.assertEquals(123.45, r.get(0).getBalance());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());
        Assertions.assertEquals("Current Account", r.get(0).getAccountType());

        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(1).getId());
        Assertions.assertEquals("Peter Griffin", r.get(1).getName());
        Assertions.assertEquals(678.9, r.get(1).getBalance());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
        Assertions.assertEquals("Investment Account", r.get(1).getAccountType());
    }

    @Test
    @DisplayName("Get Accounts from SQL")
    public void getSQLAccounts() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery(Mockito.anyString())).thenReturn(rs);

        Mockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(rs.getString("id")).thenReturn("00000000-0000-0000-0000-000000000000").thenReturn("11111111-1111-1111-1111-111111111111");
        Mockito.when(rs.getString("name")).thenReturn("Homer Simpson").thenReturn("Peter Griffin");
        Mockito.when(rs.getDouble("balance")).thenReturn(123.45).thenReturn(678.9);
        Mockito.when(rs.getString("currency")).thenReturn("GBP").thenReturn("USD");
        Mockito.when(rs.getString("accountType")).thenReturn("Current Account").thenReturn("Investment Account");

        ArrayList<Account> r = bankData.getAccountsSQL();

        // Assertions
        Assertions.assertEquals(2, r.size());

        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", r.get(0).getId());
        Assertions.assertEquals("Homer Simpson", r.get(0).getName());
        Assertions.assertEquals(123.45, r.get(0).getBalance());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());
        Assertions.assertEquals("Current Account", r.get(0).getAccountType());

        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(1).getId());
        Assertions.assertEquals("Peter Griffin", r.get(1).getName());
        Assertions.assertEquals(678.9, r.get(1).getBalance());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
        Assertions.assertEquals("Investment Account", r.get(1).getAccountType());
    }

    @Test
    @DisplayName("Get Accounts SQL Exception")
    public void getSQLAccountsException() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery(Mockito.anyString())).thenThrow(new SQLException());

        ArrayList<Account> r = bankData.getAccountsSQL();

        // Assertions
        Assertions.assertEquals(0, r.size());
    }

    @Test
    @DisplayName("Store Accounts in DB")
    public void storeAccounts() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeUpdate(Mockito.anyString())).thenReturn(1);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        accounts.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        bankData.storeAccountsSQL(accounts);

        // Assertions
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute(Mockito.anyString()); // Create table
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute("INSERT INTO accounts (id, name, balance, currency, accountType) VALUES ('00000000-0000-0000-0000-000000000000', 'Homer Simpson', 123.45, 'GBP', 'Current Account')");
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute("INSERT INTO accounts (id, name, balance, currency, accountType) VALUES ('11111111-1111-1111-1111-111111111111', 'Peter Griffin', 678.9, 'USD', 'Investment Account')");
    }

    // Transactions

    @Test
    @DisplayName("Get Transactions from API")
    public void getAPITransactions() {
        BankData bankData = new BankData(ds, log);
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://api.asep-strath.co.uk/api/Team8/transactions").thenReturn("[" +
                "{\"id\":\"99999999-9999-9999-9999-999999999999\",\"depositAccount\":\"00000000-0000-0000-0000-000000000000\",\"withdrawAccount\":\"11111111-1111-1111-1111-111111111111\",\"timestamp\":\"2020-01-01T00:00:00Z\",\"amount\":123.45,\"currency\":\"GBP\"}," +
                "{\"id\":\"88888888-8888-8888-8888-888888888888\",\"depositAccount\":\"22222222-2222-2222-2222-222222222222\",\"withdrawAccount\":\"33333333-3333-3333-3333-333333333333\",\"timestamp\":\"2020-01-01T00:00:00Z\",\"amount\":678.9,\"currency\":\"USD\"}" +
                "]");

        ArrayList<Transaction> r = bankData.getTransactionsAPI();

        // Assertions
        Assertions.assertEquals(2, r.size());

        Assertions.assertEquals("99999999-9999-9999-9999-999999999999", r.get(0).getId());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", r.get(0).getDepositAccount());
        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(0).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(0).getTimestamp());
        Assertions.assertEquals(123.45, r.get(0).getAmount());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());

        Assertions.assertEquals("88888888-8888-8888-8888-888888888888", r.get(1).getId());
        Assertions.assertEquals("22222222-2222-2222-2222-222222222222", r.get(1).getDepositAccount());
        Assertions.assertEquals("33333333-3333-3333-3333-333333333333", r.get(1).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(1).getTimestamp());
        Assertions.assertEquals(678.9, r.get(1).getAmount());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
    }

    @Test
    @DisplayName("Get Transactions from SQL")
    public void getSQLTransactions() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery(Mockito.anyString())).thenReturn(rs);

        Mockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(rs.getString("id")).thenReturn("99999999-9999-9999-9999-999999999999").thenReturn("88888888-8888-8888-8888-888888888888");
        Mockito.when(rs.getString("depositAccount")).thenReturn("00000000-0000-0000-0000-000000000000").thenReturn("22222222-2222-2222-2222-222222222222");
        Mockito.when(rs.getString("withdrawAccount")).thenReturn("11111111-1111-1111-1111-111111111111").thenReturn("33333333-3333-3333-3333-333333333333");
        Mockito.when(rs.getString("timestamp")).thenReturn("2020-01-01T00:00:00Z").thenReturn("2020-01-01T00:00:00Z");
        Mockito.when(rs.getDouble("amount")).thenReturn(123.45).thenReturn(678.9);
        Mockito.when(rs.getString("currency")).thenReturn("GBP").thenReturn("USD");

        ArrayList<Transaction> r = bankData.getTransactionsSQL();

        // Assertions
        Assertions.assertEquals(2, r.size());

        Assertions.assertEquals("99999999-9999-9999-9999-999999999999", r.get(0).getId());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", r.get(0).getDepositAccount());
        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(0).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(0).getTimestamp());
        Assertions.assertEquals(123.45, r.get(0).getAmount());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());

        Assertions.assertEquals("88888888-8888-8888-8888-888888888888", r.get(1).getId());
        Assertions.assertEquals("22222222-2222-2222-2222-222222222222", r.get(1).getDepositAccount());
        Assertions.assertEquals("33333333-3333-3333-3333-333333333333", r.get(1).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(1).getTimestamp());
        Assertions.assertEquals(678.9, r.get(1).getAmount());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
    }

    @Test
    @DisplayName("Get Transactions SQL Exception")
    public void getSQLTransactionsException() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeQuery(Mockito.anyString())).thenThrow(new SQLException());

        ArrayList<Transaction> r = bankData.getTransactionsSQL();

        // Assertions
        Assertions.assertEquals(0, r.size());
    }

    @Test
    @DisplayName("Store Transactions in DB")
    public void storeTransactions() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(stmt.executeUpdate(Mockito.anyString())).thenReturn(1);

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("99999999-9999-9999-9999-999999999999", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "2020-01-01T00:00:00Z", BigDecimal.valueOf(123.45), "GBP"));
        transactions.add(new Transaction("88888888-8888-8888-8888-888888888888", "22222222-2222-2222-2222-222222222222", "33333333-3333-3333-3333-333333333333", "2020-01-01T00:00:00Z", BigDecimal.valueOf(678.9), "USD"));

        bankData.storeTransactionsSQL(transactions);

        // Assertions
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute(Mockito.anyString()); // Create table
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute("INSERT INTO transactions (id, depositAccount, withdrawAccount, timestamp, amount, currency) VALUES ('99999999-9999-9999-9999-999999999999', '00000000-0000-0000-0000-000000000000', '11111111-1111-1111-1111-111111111111', '2020-01-01T00:00:00Z', 123.45, 'GBP')");
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute("INSERT INTO transactions (id, depositAccount, withdrawAccount, timestamp, amount, currency) VALUES ('88888888-8888-8888-8888-888888888888', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', '2020-01-01T00:00:00Z', 678.9, 'USD')");
    }

    @Test
    @DisplayName("Sanitise SQL")
    public void sanitiseSQL() {
        BankData bankData = new BankData(ds, log);
        String s = "'";
        Assertions.assertEquals("''", bankData.sanitiseSQL(s));
    }

}
