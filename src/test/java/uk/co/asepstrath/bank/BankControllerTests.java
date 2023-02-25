package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import kong.unirest.HttpMethod;
import kong.unirest.MockClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import org.slf4j.Logger;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class BankControllerTests {

    static DataSource ds;
    static Logger log;

    @BeforeAll
    public static void before() {
        ds = mock(DataSource.class);
        log = mock(Logger.class);
    }

    @Test
    @DisplayName("Create Controller")
    public void createController() {
        BankController bankController = new BankController(ds, log);
        Assertions.assertNotNull(bankController);
    }

    @Test
    @DisplayName("Get Accounts")
    public void getAccounts() {
        // Setup
        BankController bankController = new BankController(ds, log);
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://api.asep-strath.co.uk/api/Team8/accounts")
            .thenReturn("[{\"id\":\"00000000-0000-0000-0000-000000000000\",\"name\":\"Homer Simpson\",\"balance\":123.45,\"currency\":\"BMD\",\"accountType\":\"Checking Account\"}]");

        ModelAndView mv = bankController.viewAccounts();
        ArrayList mva = (ArrayList) mv.getModel().get("accounts");

        // Test
        Assertions.assertNotNull(mva);
        Assertions.assertEquals(1, mva.size());
        Assertions.assertEquals(
                "[{id=00000000-0000-0000-0000-000000000000, name=Homer Simpson, balance=123.45, currency=BMD, accountType=Checking Account}]",
                mva.toString()
        );

        /*Account acc = accs.get(0);

        Assertions.assertEquals("00000000-0000-0000-0000-000000000000", acc.getUUID());
        Assertions.assertEquals("Homer Simpson", acc.getName());
        Assertions.assertEquals(123.45, acc.getBalance());
        Assertions.assertEquals("BMD", acc.getCurrency());
        Assertions.assertEquals("Checking Account", acc.getAccountType());*/
    }

}
