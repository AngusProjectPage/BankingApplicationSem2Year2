package uk.co.asepstrath.bank;

import kong.unirest.*;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;

import org.mockito.Mockito;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;

class BankDataTests {

    static DataSource ds;
    static Logger log;
    static MockClient mc;
    static Connection conn;
    static Statement stmt;
    static PreparedStatement pstmt;
    static ResultSet rs;

    @BeforeEach
    void before() {
        ds = mock(DataSource.class);
        log = mock(Logger.class);
        mc = MockClient.register();
        conn = mock(Connection.class);
        stmt = mock(Statement.class);
        pstmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
    }

    @Test
    @DisplayName("Create Data")
    void createData() {
        BankData bankData = new BankData(ds, log);
        Assertions.assertNotNull(bankData);
    }

    // Accounts

    @Test
    @DisplayName("Get Accounts from API")
    void getAPIAccounts() {
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
        Assertions.assertEquals(BigDecimal.valueOf(123.45), r.get(0).getBalance());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());
        Assertions.assertEquals("Current Account", r.get(0).getAccountType());

        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(1).getId());
        Assertions.assertEquals("Peter Griffin", r.get(1).getName());
        Assertions.assertEquals(BigDecimal.valueOf(678.9), r.get(1).getBalance());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
        Assertions.assertEquals("Investment Account", r.get(1).getAccountType());

        MockClient.clear();
    }

    @Test
    @DisplayName("Get Accounts from SQL")
    void getSQLAccounts() throws SQLException {
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
        Assertions.assertEquals(BigDecimal.valueOf(123.45), r.get(0).getBalance());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());
        Assertions.assertEquals("Current Account", r.get(0).getAccountType());

        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(1).getId());
        Assertions.assertEquals("Peter Griffin", r.get(1).getName());
        Assertions.assertEquals(BigDecimal.valueOf(678.9), r.get(1).getBalance());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
        Assertions.assertEquals("Investment Account", r.get(1).getAccountType());


    }

    @Test
    @DisplayName("Get Accounts SQL Exception")
    void getSQLAccountsException() throws SQLException {
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
    void storeAccounts() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(conn.prepareStatement(Mockito.anyString())).thenReturn(pstmt);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        accounts.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        bankData.storeAccountsSQL(accounts);

        // Assertions
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute(Mockito.anyString()); // Create table

        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(1, "00000000-0000-0000-0000-000000000000");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(2, "Homer Simpson");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setDouble(3, 123.45);
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(4, "GBP");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(5, "Current Account");

        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(1, "11111111-1111-1111-1111-111111111111");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(2, "Peter Griffin");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setDouble(3, 678.9);
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(4, "USD");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(5, "Investment Account");

        Mockito.verify(pstmt, Mockito.times(2)).executeUpdate();
    }

    // Transactions

    @Test
    @DisplayName("Get Transactions from API")
    void getAPITransactions() {
        BankData bankData = new BankData(ds, log);
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://api.asep-strath.co.uk/api/Team8/transactions").thenReturn("[" +
                "{\"id\":\"99999999-9999-9999-9999-999999999999\",\"depositAccount\":\"00000000-0000-0000-0000-000000000000\",\"withdrawAccount\":\"11111111-1111-1111-1111-111111111111\",\"timestamp\":\"2020-01-01T00:00:00Z\",\"amount\":123.45,\"currency\":\"GBP\"}," +
                "{\"id\":\"88888888-8888-8888-8888-888888888888\",\"depositAccount\":\"22222222-2222-2222-2222-222222222222\",\"withdrawAccount\":\"33333333-3333-3333-3333-333333333333\",\"timestamp\":\"2020-01-01T00:00:00Z\",\"amount\":678.9,\"currency\":\"USD\"}" +
                "]");

        mock.expect(HttpMethod.GET, "http://api.asep-strath.co.uk/api/Team8/fraud").thenReturn("[\"88888888-8888-8888-8888-888888888888\"]");

        ArrayList<Transaction> r = bankData.getTransactionsAPI();

        // Assertions
        Assertions.assertEquals(2, r.size());

        Assertions.assertEquals("99999999-9999-9999-9999-999999999999", r.get(0).getId());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", r.get(0).getDepositAccount());
        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(0).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(0).getTimestamp());
        Assertions.assertEquals(BigDecimal.valueOf(123.45), r.get(0).getAmount());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());
        Assertions.assertFalse(r.get(0).isFraudulent());

        Assertions.assertEquals("88888888-8888-8888-8888-888888888888", r.get(1).getId());
        Assertions.assertEquals("22222222-2222-2222-2222-222222222222", r.get(1).getDepositAccount());
        Assertions.assertEquals("33333333-3333-3333-3333-333333333333", r.get(1).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(1).getTimestamp());
        Assertions.assertEquals(BigDecimal.valueOf(678.9), r.get(1).getAmount());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
        Assertions.assertTrue(r.get(1).isFraudulent());

        MockClient.clear();
    }

    @Test
    @DisplayName("Get Transactions from SQL")
    void getSQLTransactions() throws SQLException {
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
        Mockito.when(rs.getBoolean("fraudulent")).thenReturn(false).thenReturn(true);

        ArrayList<Transaction> r = bankData.getTransactionsSQL();

        // Assertions
        Assertions.assertEquals(2, r.size());

        Assertions.assertEquals("99999999-9999-9999-9999-999999999999", r.get(0).getId());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", r.get(0).getDepositAccount());
        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", r.get(0).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(0).getTimestamp());
        Assertions.assertEquals(BigDecimal.valueOf(123.45), r.get(0).getAmount());
        Assertions.assertEquals("GBP", r.get(0).getCurrency());
        Assertions.assertFalse(r.get(0).isFraudulent());

        Assertions.assertEquals("88888888-8888-8888-8888-888888888888", r.get(1).getId());
        Assertions.assertEquals("22222222-2222-2222-2222-222222222222", r.get(1).getDepositAccount());
        Assertions.assertEquals("33333333-3333-3333-3333-333333333333", r.get(1).getWithdrawAccount());
        Assertions.assertEquals("2020-01-01T00:00:00Z", r.get(1).getTimestamp());
        Assertions.assertEquals(BigDecimal.valueOf(678.9), r.get(1).getAmount());
        Assertions.assertEquals("USD", r.get(1).getCurrency());
        Assertions.assertTrue(r.get(1).isFraudulent());

    }

    @Test
    @DisplayName("Get Transactions SQL Exception")
    void getSQLTransactionsException() throws SQLException {
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
    void storeTransactions() throws SQLException {
        BankData bankData = new BankData(ds, log);

        Mockito.when(ds.getConnection()).thenReturn(conn);
        Mockito.when(conn.createStatement()).thenReturn(stmt);
        Mockito.when(conn.prepareStatement(Mockito.anyString())).thenReturn(pstmt);

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("99999999-9999-9999-9999-999999999999", "00000000-0000-0000-0000-000000000000", "11111111-1111-1111-1111-111111111111", "2020-01-01T00:00:00Z", BigDecimal.valueOf(123.45), "GBP", false));
        transactions.add(new Transaction("88888888-8888-8888-8888-888888888888", "22222222-2222-2222-2222-222222222222", "33333333-3333-3333-3333-333333333333", "2020-01-01T00:00:00Z", BigDecimal.valueOf(678.9), "USD", true));

        bankData.storeTransactionsSQL(transactions);

        // Assertions
        Mockito.verify(stmt, Mockito.atLeastOnce()).execute(Mockito.anyString()); // Create table

        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(1, "99999999-9999-9999-9999-999999999999");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(2, "00000000-0000-0000-0000-000000000000");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(3, "11111111-1111-1111-1111-111111111111");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(4, "2020-01-01T00:00:00Z");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setDouble(5, 123.45);
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(6, "GBP");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setBoolean(7, false);

        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(1, "88888888-8888-8888-8888-888888888888");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(2, "22222222-2222-2222-2222-222222222222");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(3, "33333333-3333-3333-3333-333333333333");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(4, "2020-01-01T00:00:00Z");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setDouble(5, 678.9);
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setString(6, "USD");
        Mockito.verify(pstmt, Mockito.atLeastOnce()).setBoolean(7, true);

        Mockito.verify(pstmt, Mockito.times(2)).executeUpdate();
    }

    @Test
    @DisplayName("Applying Account Transactions")
    void applyingTransactions() {
        BankData bankData = new BankData(ds, log);
        Account acc1 = new Account("123456789", "John Michaels", BigDecimal.valueOf(2500.20), "GDP", "Savings account");
        Account acc2 = new Account("987654321", "Mary Howard", BigDecimal.valueOf(4020.46), "TND", "ISA");
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(acc1);
        accounts.add(acc2);

        Transaction trans1 = new Transaction("88448822", "123456789", "987654321", "2022-01-08T17:03:14.185726", BigDecimal.valueOf(844.52), "GDP", false);
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(trans1);

        TransactionInfo transInfo1 = new TransactionInfo(accounts.get(0).getId(), accounts.get(0).getBalance());
        TransactionInfo transInfo2 = new TransactionInfo(accounts.get(1).getId(), accounts.get(1).getBalance());
        ArrayList<TransactionInfo> transactionInfos = new ArrayList<>();
        transactionInfos.add(transInfo1);
        transactionInfos.add(transInfo2);


        ArrayList<TransactionInfo> finalTransactionInfo = bankData.applyAllTransactions(accounts, transactions, transactionInfos);
        TransactionInfo transaction1 = finalTransactionInfo.get(0);
        TransactionInfo transaction2 = finalTransactionInfo.get(1);

        // Regular transaction
        Assertions.assertEquals("123456789", transaction1.getId());
        Assertions.assertEquals("987654321", transaction2.getId());
        Assertions.assertEquals(BigDecimal.valueOf(2500.20), transaction1.getBalanceBefore());
        Assertions.assertEquals(BigDecimal.valueOf(4020.46), transaction2.getBalanceBefore());
        Assertions.assertEquals(BigDecimal.valueOf(3344.72), transaction1.getBalanceAfter());
        Assertions.assertEquals(BigDecimal.valueOf(3175.94), transaction2.getBalanceAfter());
        Assertions.assertEquals(1, transaction1.getNumTransactions());
        Assertions.assertEquals(1, transaction2.getNumTransactions());
        Assertions.assertEquals(0, transaction1.getFailedTransactions());
        Assertions.assertEquals(0, transaction2.getFailedTransactions());

        // Fraudulent transaction
        Transaction trans2 = new Transaction("99112233", "123456789", "987654321", "2023-01-08T17:03:14.185726", BigDecimal.valueOf(1), "GDP", true);
        transactions.add(trans2);
        transInfo1 = new TransactionInfo(accounts.get(0).getId(), accounts.get(0).getBalance());
        transInfo2 = new TransactionInfo(accounts.get(1).getId(), accounts.get(1).getBalance());
        ArrayList<TransactionInfo> transactionInfos2 = new ArrayList<>();
        transactionInfos2.add(transInfo1);
        transactionInfos2.add(transInfo2);

        ArrayList<TransactionInfo> finalTransactionInfo2 = bankData.applyAllTransactions(accounts, transactions, transactionInfos2);

        transaction1 = finalTransactionInfo2.get(0);
        transaction2 = finalTransactionInfo2.get(1);

        Assertions.assertEquals("123456789", transaction1.getId());
        Assertions.assertEquals("987654321", transaction2.getId());
        Assertions.assertEquals(BigDecimal.valueOf(3344.72), transaction1.getBalanceBefore());
        Assertions.assertEquals(BigDecimal.valueOf(3175.94), transaction2.getBalanceBefore());
        Assertions.assertEquals(BigDecimal.valueOf(4189.24), transaction1.getBalanceAfter());
        Assertions.assertEquals(BigDecimal.valueOf(2331.42), transaction2.getBalanceAfter());
        Assertions.assertEquals(1, transaction1.getNumTransactions());
        Assertions.assertEquals(1, transaction2.getNumTransactions());
        Assertions.assertEquals(1, transaction1.getFailedTransactions());
        Assertions.assertEquals(1, transaction2.getFailedTransactions());

    }

    @Test
    @DisplayName("Get Account Transactions")
    void gettingAccountTransactions() throws SQLException {
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
        Mockito.when(rs.getBoolean("fraudulent")).thenReturn(false).thenReturn(true);

        ArrayList<Transaction> transactions = bankData.getAccountTransactions("00000000-0000-0000-0000-000000000000");

        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals("99999999-9999-9999-9999-999999999999", transactions.get(0).getId());
    }

    @Test
    @DisplayName("Initialise Transaction Info")
    void initialiseTransactionInfo() {
        BankData bankData = new BankData(ds, log);

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("00000000-0000-0000-0000-000000000000", "Homer Simpson", BigDecimal.valueOf(123.45), "GBP", "Current Account"));
        accounts.add(new Account("11111111-1111-1111-1111-111111111111", "Peter Griffin", BigDecimal.valueOf(678.9), "USD", "Investment Account"));

        ArrayList<TransactionInfo> transactionInfos = bankData.initialiseTransactionInfo(accounts);

        Assertions.assertEquals(2, transactionInfos.size());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", transactionInfos.get(0).getId());
        Assertions.assertEquals("11111111-1111-1111-1111-111111111111", transactionInfos.get(1).getId());
    }

}
