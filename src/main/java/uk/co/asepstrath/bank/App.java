package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.json.JacksonModule;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class App extends Jooby {

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new JacksonModule());
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

        /*
        This will host any files in src/main/resources/assets on <host>/assets
         */
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        mvc(new BankController(ds, log));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);

    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");

        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `userAccounts` (`Name` varchar(255),`Balance` DECIMAL(10, 2))");

            stmt.executeUpdate("INSERT INTO userAccounts " + "VALUES ('Rachel', 50.00)");
            stmt.executeUpdate("INSERT INTO userAccounts " + "VALUES ('Monica', 100.00)");
            stmt.executeUpdate("INSERT INTO userAccounts " + "VALUES ('Phoebe', 76.00)");
            stmt.executeUpdate("INSERT INTO userAccounts " + "VALUES ('Joey', 23.90)");
            stmt.executeUpdate("INSERT INTO userAccounts " + "VALUES ('Chandler', 3.00)");
            stmt.executeUpdate("INSERT INTO userAccounts " + "VALUES ('Ross', 54.32)");


        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
