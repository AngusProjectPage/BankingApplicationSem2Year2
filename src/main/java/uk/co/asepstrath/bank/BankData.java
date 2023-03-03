package uk.co.asepstrath.bank;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import kong.unirest.json.JSONArray;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BankData {

    private final DataSource ds;
    private final Logger log;

    public BankData(DataSource dataSource, Logger logger) {
        ds = dataSource;
        log = logger;
    }

    /**
     * Check if API is available
     */
    public boolean getAPIStatus() {
        return Unirest.get("http://api.asep-strath.co.uk/api/Team8/transactions?PageSize=0").asJson().getStatus() == 200; // success, API is online
    }

    /**
     * Get accounts from API or local database
     *
     * @return HashMap containing accounts and data origin
     */
    public HashMap<String, Object> getAccounts() {
        HashMap<String, Object> hm = new HashMap<>();

        // Check if API is available, if not, use local database
        if (getAPIStatus()) {
            hm.put("accounts", getAccountsAPI());
            hm.put("dataOrigin", "API");
        } else {
            hm.put("accounts", getAccountsSQL());
            hm.put("dataOrigin", "DB");
        }

        return hm;
    }

    /**
     * Get accounts from API
     *
     * @return ArrayList of accounts
     */
    private ArrayList<Account> getAccountsAPI() {
        HttpResponse<JsonNode> res = Unirest.get("http://api.asep-strath.co.uk/api/Team8/accounts").asJson();
        JSONArray jsonAccs = res.getBody().getArray();
        ArrayList<Account> accs = new ArrayList<>();

        for (int i = 0; i < jsonAccs.length(); i++) {
            JSONObject jsonAcc = jsonAccs.getJSONObject(i);
            accs.add(new Account(
                    jsonAcc.getString("id"),
                    jsonAcc.getString("name"),
                    BigDecimal.valueOf(jsonAcc.getDouble("balance")),
                    jsonAcc.getString("currency"),
                    jsonAcc.getString("accountType")
            ));
        }

        return accs;
    }

    /**
     * Get accounts from local database
     *
     * @return ArrayList of accounts
     */
    private ArrayList<Account> getAccountsSQL() {

        try (Connection connection = ds.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
                ArrayList<Account> accs = new ArrayList<>();
                while (rs.next())
                    accs.add(new Account(
                            rs.getString("id"),
                            rs.getString("name"),
                            BigDecimal.valueOf(rs.getDouble("balance")),
                            rs.getString("currency"),
                            rs.getString("accountType")
                    ));

                return accs;
            }
        } catch (SQLException e) {
            log.error("Error getting accounts from SQL", e);
            return null;
        }

    }

    /**
     * Get transactions from API or local database
     *
     * @return HashMap containing transactions and data origin
     */
    public HashMap<String, Object> getTransactions() {
        HashMap<String, Object> hm = new HashMap<>();

        // Check if API is available, if not, use local database
        if (getAPIStatus()) {
            ArrayList<Transaction> transactions = getTransactionsAPI();
            hm.put("transactions", transactions);
            hm.put("transactionTotal", transactions.size());
            hm.put("dataOrigin", "API");
        } else {
            ArrayList<Transaction> transactions = getTransactionsSQL();
            hm.put("transactions", transactions);
            if (transactions != null) hm.put("transactionTotal", transactions.size());
            hm.put("dataOrigin", "DB");
        }

        return hm;
    }

    /**
     * Get transactions from API
     *
     * @return ArrayList of transactions
     */
    private ArrayList<Transaction> getTransactionsAPI() {
        HttpResponse<JsonNode> res = Unirest.get("http://api.asep-strath.co.uk/api/Team8/transactions").asJson();
        JSONArray jsonTrans = res.getBody().getArray();
        ArrayList<Transaction> transactions = new ArrayList<>();

        for (int i=0; i < jsonTrans.length(); i++) {
            JSONObject jsonT = jsonTrans.getJSONObject(i);
            transactions.add(new Transaction(
                    jsonT.getString("id"),
                    jsonT.getString("depositAccount"),
                    jsonT.getString("withdrawAccount"),
                    jsonT.getString("timestamp"),
                    BigDecimal.valueOf(jsonT.getDouble("amount")),
                    jsonT.getString("currency")
            ));
        }

        return transactions;
    }

    /**
     * Get transactions from local database
     *
     * @return ArrayList of transactions
     */
    private ArrayList<Transaction> getTransactionsSQL() {
        try (Connection connection = ds.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM transactions");
                ArrayList<Transaction> transactions = new ArrayList<>();
                while (rs.next())
                    transactions.add(new Transaction(
                            rs.getString("id"),
                            rs.getString("depositAccount"),
                            rs.getString("withdrawAccount"),
                            rs.getString("timestamp"),
                            BigDecimal.valueOf(rs.getDouble("amount")),
                            rs.getString("currency")
                    ));

                return transactions;
            }
        } catch (SQLException e) {
            log.error("Error getting transactions from SQL", e);
            return null;
        }

    }

    /**
     * Sanitise SQL string
     *
     * @param s String to sanitise
     * @return Sanitised string
     */
    public String sanitiseSQL(String s) {
        return s.replace("'", "''");
    }

    /**
     * Post accounts to local database
     *
     * @param accs ArrayList of accounts
     */
    private void postAccountsSQL(ArrayList<Account> accs) {
        try (Connection connection = ds.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS accounts (id VARCHAR(36) PRIMARY KEY, name VARCHAR(255), balance DOUBLE, currency VARCHAR(3), accountType VARCHAR(255))");
                for (Account acc : accs) {
                    stmt.execute("INSERT INTO accounts (id, name, balance, currency, accountType) VALUES ('" + acc.getId() + "', '" + sanitiseSQL(acc.getName()) + "', " + acc.getBalance() + ", '" + acc.getCurrency() + "', '" + acc.getAccountType() + "')");
                }
            }
        } catch (SQLException e) {
            log.error("Error creating accounts table", e);
        }
    }

    /**
     * Post transactions to local database
     *
     * @param transactions ArrayList of transactions
     */
    private void postTransactionsSQL(ArrayList<Transaction> transactions) {
        try (Connection connection = ds.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id VARCHAR(36) PRIMARY KEY, depositAccount VARCHAR(36), withdrawAccount VARCHAR(36), timestamp VARCHAR(255), amount DOUBLE, currency VARCHAR(3))");
                for (Transaction t : transactions) {
                    stmt.execute("INSERT INTO transactions (id, depositAccount, withdrawAccount, timestamp, amount, currency) VALUES ('" + t.getId() + "', '" + t.getDepositAccount() + "', '" + t.getWithdrawAccount() + "', '" + t.getTimestamp() + "', " + t.getAmount() + ", '" + t.getCurrency() + "')");
                }
            }
        } catch (SQLException e) {
            log.error("Error creating transactions table", e);
        }
    }

    /**
     * Initialise local database
     */
    public void initialise() {
        if (!getAPIStatus()) {
            log.error("API is not available, cannot initialise database");
            return;
        }

        // Get and store accounts/transactions
        postAccountsSQL(getAccountsAPI());
        postTransactionsSQL(getTransactionsAPI());
    }

}
