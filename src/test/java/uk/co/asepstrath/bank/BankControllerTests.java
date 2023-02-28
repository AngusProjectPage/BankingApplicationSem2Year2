package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BankControllerTests {

    static DataSource ds;
    static Logger log;
    static Connection con;
    static Statement stmt;

    @BeforeAll
    public static void before() throws SQLException {
        ds = mock(DataSource.class);
        log = mock(Logger.class);
        con = mock(Connection.class);
        stmt = mock(Statement.class);

        when(ds.getConnection()).thenReturn(con);
        when(con.createStatement()).thenReturn(stmt);
    }

    @Test
    @DisplayName("Create Controller")
    public void createController() {
        BankController bankController = new BankController(ds, log);
        Assertions.assertNotNull(bankController);
    }

    @Test
    @DisplayName("View Accounts")
    public void viewAccounts() {
        BankController bankController = new BankController(ds, log);
        ModelAndView mav = bankController.viewAccounts();
        Assertions.assertNotNull(mav.getModel().get("accounts"));
        Assertions.assertNotNull(mav.getModel().get("dataOrigin"));
    }

}
