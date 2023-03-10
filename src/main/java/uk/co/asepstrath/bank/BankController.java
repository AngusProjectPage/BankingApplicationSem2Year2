package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.annotations.*;

@Path("/")
public class BankController {

    private final BankData data;

    public BankController(BankData data) {
        this.data = data;
    }

    @GET
    public ModelAndView viewAccounts() {
        return new ModelAndView("accounts.hbs", data.getAccounts());
    }

    @GET ("/transactions")
    public ModelAndView viewTransactionInformation() {
        return new ModelAndView("transactions.hbs", data.getTransactions());
    }

}
