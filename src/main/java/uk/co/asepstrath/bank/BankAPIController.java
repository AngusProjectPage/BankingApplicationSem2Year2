package uk.co.asepstrath.bank;

import com.google.gson.Gson;
import io.jooby.annotations.*;

@Path("/api")
public class BankAPIController {

    private final BankData data;

    public BankAPIController(BankData data) {
        this.data = data;
    }

    @Produces("application/json")
    @GET("/")
    public String getRoot() {
        return "{\"res\": \"OK!\"}"; // Returns empty 200 OK
    }

    @Produces("application/json")
    @GET("/accounts")
    public String getAccounts() {
        return new Gson().toJson(data.getAccounts());
    }

    @Produces("application/json")
    @GET("/accounts/{id}")
    public String getAccount(@PathParam String id) {
        return new Gson().toJson(data.getAccount(id));
    }

    @Produces("application/json")
    @GET("/transactions")
    public String getTransactions() {
        return new Gson().toJson(data.getTransactions());
    }

    @Produces("application/json")
    @GET("/transactions/{id}")
    public String getTransaction(@PathParam String id) {
        return new Gson().toJson(data.getTransaction(id));
    }

}
