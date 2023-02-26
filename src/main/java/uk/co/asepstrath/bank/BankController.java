package uk.co.asepstrath.bank;

import com.google.gson.Gson;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotations.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Path("/")
public class BankController {

    private final DataSource dataSource;
    private final Logger logger;

    /*
    This constructor can take in any dependencies the controller may need to respond to a request
     */
    public BankController(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
    }

    private ArrayList<Account> getAccounts() {
        try (Connection connection = dataSource.getConnection()) {
            // Create Statement (batch of SQL Commands)
            Statement statement = connection.createStatement();
            // Perform SQL Query
            ResultSet set = statement.executeQuery("SELECT * FROM `userAccounts`");

            ArrayList<Account> Accounts = new ArrayList<Account>();
            while(set.next()) {
                Account a = new Account(set.getString("Name"), set.getBigDecimal("Balance"));
                Accounts.add(a);
            }
            // Return value
            return Accounts;
        } catch (SQLException e) {
            // If something does go wrong this will log the stack trace
            logger.error("Database Error Occurred",e);
            // And return a HTTP 500 error to the requester
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }
    }

    @GET("/api")
    public String GET_api() {
        return new Gson().toJson(getAccounts());
    }

    @GET
    public ModelAndView viewAccounts() {
        HashMap hm = new HashMap<String,Object>();
        String json = GET_api();
        ArrayList<Account> accs = new Gson().fromJson(json, ArrayList.class);
        hm.put("accounts",accs);
        return new ModelAndView("accounts.hbs", hm);
    }

    @GET ("/transactions")
    public ModelAndView viewTransactionInformaton() {
        HashMap hm = new HashMap<String,Object>();
        Transaction transactionObject = Unirest.get("https://api.asep-strath.co.uk/api/team8/transactions/1")
                .asObject(Transaction.class)
                .getBody();

        hm.put("transaction", transactionObject);
        return new ModelAndView("transactions.hbs", hm);
    }

}
