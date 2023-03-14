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
    public ModelAndView viewAccounts() {
       return new ModelAndView("accounts.hbs", data.getAccounts());
    }

    @GET("/accounts/{id}")
    public ModelAndView viewAccount(@PathParam String id) {
        return new ModelAndView("account.hbs", data.getAccount(id));
    }

    @GET ("/transactions")
    public ModelAndView viewTransactions() {
        return new ModelAndView("transactions.hbs", data.getTransactions());
    }

    @GET ("/transactions/{id}")
    public ModelAndView viewTransaction(@PathParam String id) {
        return new ModelAndView("transaction.hbs", data.getTransaction(id));
    }

    @GET ("/home")
    public ModelAndView viewHome () {
        return new ModelAndView("home.hbs");
    }

}
