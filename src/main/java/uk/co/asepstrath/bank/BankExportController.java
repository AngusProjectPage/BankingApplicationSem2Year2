package uk.co.asepstrath.bank;

import io.jooby.annotations.*;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Path("/export")
public class BankExportController {

    private String[] getHeaders(JSONArray json) {
        // Get header files for CSV; keys of JSON object
        return json.getJSONObject(0).keySet().toArray(new String[0]);
    }

    private List<List<String>> getData(JSONArray json, String[] headers) {
        // Get data for CSV; values of JSON objects
        List<List<String>> data = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            List<String> row = new ArrayList<>();
            for (String header : headers) {
                row.add(json.getJSONObject(i).get(header).toString());
            }
            data.add(row);
        }

        return data;
    }

    private ByteArrayInputStream generateCSV(JSONArray json) {
        String[] headers = getHeaders(json);
        List<List<String>> data = getData(json, headers);

        ByteArrayInputStream file; // file to return

        try
        (
            // create using try-with-resources to ensure the stream is closed
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader(headers))
        )
        {
            for (List<String> row : data) {
                csvPrinter.printRecord(row); // add row to CSV
            }
            csvPrinter.flush(); // flush to ensure all data is written to the stream
            file = new ByteArrayInputStream(out.toByteArray()); // convert to byte array
        }
        catch (IOException e) {
            throw new RuntimeException("Error while writing CSV file: " + e.getMessage());
        }

        return file;
    }


    // Routes

    @Produces("text/csv")
    @GET("/accounts")
    public ByteArrayInputStream exportAccounts() {
        HttpResponse<JsonNode> res = Unirest.get("http://localhost:8080/api/accounts").asJson();
        JSONObject jsonRes = res.getBody().getObject();
        JSONArray jsonAccs = jsonRes.getJSONArray("accounts");

        return generateCSV(jsonAccs);
    }

    @Produces("text/csv")
    @GET("/accounts/{id}")
    public ByteArrayInputStream exportAccount(@PathParam String id) {
        HttpResponse<JsonNode> res = Unirest.get("http://localhost:8080/api/accounts/" + id).asJson();
        JSONObject jsonRes = res.getBody().getObject();

        if (jsonRes.isEmpty()) {
            return new ByteArrayInputStream("Account not found!".getBytes());
        }

        JSONArray jsonAcc = jsonRes.getJSONArray("account");

        return generateCSV(jsonAcc);
    }

    @Produces("text/csv")
    @GET("/transactions")
    public ByteArrayInputStream exportTransactions() {
        HttpResponse<JsonNode> res = Unirest.get("http://localhost:8080/api/transactions").asJson();
        JSONObject jsonRes = res.getBody().getObject();

        JSONArray jsonTrans = jsonRes.getJSONArray("transactions");

        return generateCSV(jsonTrans);
    }

    @Produces("text/csv")
    @GET("/transactions/{id}")
    public ByteArrayInputStream exportTransaction(@PathParam String id) {
        HttpResponse<JsonNode> res = Unirest.get("http://localhost:8080/api/transactions/" + id).asJson();
        JSONObject jsonRes = res.getBody().getObject();

        if (jsonRes.isEmpty()) {
            return new ByteArrayInputStream("Transaction not found!".getBytes());
        }

        JSONArray jsonTran = jsonRes.getJSONArray("transaction");

        return generateCSV(jsonTran);
    }

}
