package uk.co.asepstrath.bank;

import io.jooby.annotations.*;

@Path("/api")
public class BankAPIController {

    private final BankData data;

    public BankAPIController(BankData data) {
        this.data = data;
    }

    @Produces("application/json")
    @GET
    public String getRoot() {
        return "{}"; // Returns empty 200 OK
    }

    @Produces("application/json")
    @GET("/accounts")
    public String getAccounts() {
        return "[]"; // TODO: Implement (returns list of all accounts)
    }

    @Produces("application/json")
    @GET("/accounts/{id}")
    public String getAccount(@PathParam String id) {
        return "{}"; // TODO: Implement (returns account matching id & all relevant transactions)
    }

    @Produces("application/json")
    @GET("/transactions")
    public String getTransactions() {
        return "[]"; // TODO: Implement (returns list of all transactions)
    }

    @Produces("application/json")
    @GET("/transactions/{id}")
    public String getTransaction(@PathParam String id) {
        return "{}"; // TODO: Implement (returns transaction matching id)
    }

}
