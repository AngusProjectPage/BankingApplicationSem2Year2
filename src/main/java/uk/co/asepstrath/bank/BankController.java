package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.annotations.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;

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
        HttpResponse<ArrayList> res = Unirest.get("http://api.asep-strath.co.uk/api/Team8/accounts").asObject(ArrayList.class);
        return (ArrayList<Account>) res.getBody();
    }

    @GET
    public ModelAndView viewAccounts() {
        HashMap<String, Object> hm = new HashMap<>();
        ArrayList<Account> accs = getAccounts();
        hm.put("accounts",accs);
        return new ModelAndView("accounts.hbs", hm);
    }

}
