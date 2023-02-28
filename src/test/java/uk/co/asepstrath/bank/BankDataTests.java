package uk.co.asepstrath.bank;

import kong.unirest.MockClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import org.slf4j.Logger;

import static org.mockito.Mockito.mock;

public class BankDataTests {

    static DataSource ds;
    static Logger log;
    static MockClient mc;

    @BeforeAll
    public static void before() {
        ds = mock(DataSource.class);
        log = mock(Logger.class);
        mc = MockClient.register();
    }

    @Test
    @DisplayName("Create Data")
    public void createData() {
        BankData bankData = new BankData(ds, log);
        Assertions.assertNotNull(bankData);
    }

    // Tests for other methods; how to test API calls? Issue with HTTPResponse<JsonNode>

    @Test
    @DisplayName("Sanitise SQL")
    public void sanitiseSQL() {
        BankData bankData = new BankData(ds, log);
        String s = "'";
        Assertions.assertEquals(bankData.sanitiseSQL(s), "''");
    }

}
