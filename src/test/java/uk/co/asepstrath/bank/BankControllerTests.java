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

    @Test
    @DisplayName("BankController GET_api()")
    public void getAccounts() throws SQLException {
        BankController bankController = new BankController(ds, log);

        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        Statement stmt = mock(Statement.class);
        when(con.createStatement()).thenReturn(stmt);
        ResultSet set = mock(ResultSet.class);
        when(stmt.executeQuery("SELECT * FROM `userAccounts`")).thenReturn(set);

        when(set.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(set.getString("Name")).thenReturn("John Smith").thenReturn("Joe Bloggs");
        when(set.getBigDecimal("Balance")).thenReturn(new BigDecimal(10)).thenReturn(new BigDecimal(25));

        String json = bankController.GET_api();
        Assertions.assertEquals("[{\"name\":\"John Smith\",\"balance\":10},{\"name\":\"Joe Bloggs\",\"balance\":25}]", json);
    }

    @Test
    @DisplayName("BankController GET_api() SQLException")
    public void getAccountsSQLException() throws SQLException {
        BankController bankController = new BankController(ds, log);
        when(ds.getConnection()).thenThrow(new SQLException());
        Assertions.assertThrows(StatusCodeException.class, bankController::GET_api);
    }
}
