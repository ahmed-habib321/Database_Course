package org.example.DataBase;

import lombok.Getter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Handles database connection management.
 * <p>
 * This class is responsible for establishing and destroying
 * a JDBC connection using configuration data provided by
 * the {@link DataBase} class.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 * DataBase_Module dbModule = new DataBase_Module();
 * dbModule.EstablishCons();
 * Connection conn = dbModule.getCons();
 * // use connection
 * dbModule.DestroyCons();
 * </pre>
 * </p>
 *
 * @author Ahmed
 */
public class DataBase_Module {
    /**
     * Active JDBC database connection.
     * <p>
     * Use {@link #getConnection()} to access the connection.
     * </p>
     */
    @Getter
    private Connection connection;
    /**
     * Database configuration loader.
     * <p>
     * Reads connection details from the properties file
     * via the {@link DataBase} class.
     * </p>
     */
    private DataBase database = new DataBase();

    /**
     * Creates a {@code DataBase_Module} instance.
     *
     * @throws IOException if database configuration properties
     *                     cannot be loaded
     */
    public DataBase_Module() throws IOException {
    }

    /**
     * Establishes a connection to the database using JDBC.
     * <p>
     * Connection parameters (URL, username, password)
     * are retrieved from the {@link DataBase} object.
     * </p>
     * <p>
     * If the connection fails, the error message is printed
     * to the console.
     */
    public void EstablishConnection() {
        try {
            connection = DriverManager.getConnection(
                    database.getUrl(),
                    database.getUser(),
                    database.getPassword());
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to establish database connection ->" + e.getMessage());
        }
    }

    /**
     * Closes the active database connection.
     * <p>
     * If the connection is already closed or invalid,
     * an error message will be printed.
     * </p>
     */
    public void DestroyConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed successfully.");
                }
            } catch (SQLException e) {
                System.out.println("Error while closing database connection -> " + e.getMessage());
            }
        }
    }
}
