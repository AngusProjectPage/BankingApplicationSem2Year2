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

        initialise();
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
     * Get accounts from API or local database
     *
     * @return HashMap containing accounts and data origin
     */
    public HashMap<String, Object> getAccounts() {
        HashMap<String, Object> hm = new HashMap<>();

        // Check if API is available, if not, use local database
        if (Unirest.get("http://api.asep-strath.co.uk/api/Team8/").asJson().getStatus() == 404) {
            hm.put("accounts", getAccountsAPI());
            hm.put("dataOrigin", "Data pulled from http://api.asep-strath.co.uk/api/Team8/");
        } else {
            hm.put("accounts", getAccountsSQL());
            hm.put("dataOrigin", "Data pulled from local database, API is not available");
        }

        return hm;
    }

    /**
     * Sanitise SQL string
     *
     * @param s String to sanitise
     * @return Sanitised string
     */
    private String sanitiseSQL(String s) {
        return s.replace("'", "''");
    }

    /**
     * Initialise local database
     */
    public void initialise() {
        // API will return 404 if it is available
        if (Unirest.get("http://api.asep-strath.co.uk/api/Team8/").asJson().getStatus() != 404) {
            log.error("API is not available, cannot initialise database");
            return;
        }

        // Get accounts from API
        ArrayList<Account> accs = getAccountsAPI();

        // Create accounts table and insert accounts
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

}
