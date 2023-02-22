package uk.co.asepstrath.bank;

import io.jooby.exception.StatusCodeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BankControllerTests {

    static DataSource ds;
    static Logger log;

    @BeforeAll
    public static void before() {
        ds = mock(DataSource.class);
        log = mock(Logger.class);
    }

    @Test
    @DisplayName("BankController Constructor")
    public void createBankController() {
        BankController bankController = new BankController(ds, log);
        Assertions.assertNotNull(bankController);
    }

}
