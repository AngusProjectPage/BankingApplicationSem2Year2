package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.annotations.*;
import org.slf4j.Logger;

import javax.sql.DataSource;

@Path("/")
public class BankController {

    private final BankData data;

    public BankController(DataSource ds, Logger log) {
        data = new BankData(ds, log);
        data.initialise();
    }

    @GET("/accounts")
    public ModelAndView viewAccounts(@QueryParam String id) {
       if(id != null) {
           return new ModelAndView("account.hbs", data.getAccount(id));
        } else {
           return new ModelAndView("accounts.hbs", data.getAccounts());
        }
    }

    @GET ("/transactions")
    public ModelAndView viewTransactionInformation() {
        return new ModelAndView("transactions.hbs", data.getTransactions());
    }



}
