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
    }

    @GET
    public ModelAndView viewAccounts() {
        return new ModelAndView("accounts.hbs", data.getAccounts());
    }

}
