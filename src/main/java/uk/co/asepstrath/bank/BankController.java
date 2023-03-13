package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.annotations.*;

@Path("/")
public class BankController {

    private final BankData data;

    public BankController(BankData data) {
        this.data = data;
    }

    @GET("/accounts")
    public ModelAndView viewAccounts(@QueryParam String id) {
       if(id != null) {
           return new ModelAndView("account.hbs", data.getAccountTransactionInfo(id));
        } else {
           return new ModelAndView("accounts.hbs", data.getAccounts());
        }
    }

    @GET ("/transactions")
    public ModelAndView viewTransactionInformation() {
        return new ModelAndView("transactions.hbs", data.getTransactions());
    }



}
