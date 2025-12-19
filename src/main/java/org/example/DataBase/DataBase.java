package org.example.DataBase;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Represents database configuration information.
 * <p>
 * This class loads database connection details from a properties file
 * and constructs a JDBC connection URL automatically.
 * </p>
 *
 * <p>
 * Expected properties file location:
 * <pre>
 * src/main/java/org/example/DataBase/DBinfo.properties
 * </pre>
 * </p>
 *
 * Required properties:
 * <ul>
 *     <li>DBMS</li>
 *     <li>hostname</li>
 *     <li>Port</li>
 *     <li>DatabaseName</li>
 *     <li>User</li>
 *     <li>Password</li>
 * </ul>
 *
 * Example JDBC URL format:
 * <pre>
 * jdbc:{DBMS}://{hostname}:{Port}/{DatabaseName}
 * </pre>
 *
 * @author Ahmed
 */
@Getter
@Setter
public class DataBase {
    /** Database Management System (e.g., mysql, postgresql). */
    private String DBMS;
    /** Hostname or IP address of the database server. */
    private String hostname;
    /** Port number on which the database server is running. */
    private String Port;
    /** Database Management System username or root. */
    private String User;
    /** Database Management System password. */
    private String Password;
    /** Name of the database. */
    private String DatabaseName;
    /** Fully constructed JDBC connection URL. */
    private String Url;

    /**
     * Constructs a {@code DataBase} object by loading configuration
     * values from a properties file.
     *
     * @throws IOException if the properties file cannot be found
     *                     or read successfully
     */
    public DataBase() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("src\\main\\resources\\DBinfo.properties"));

        this.DBMS = prop.getProperty("DBMS");
        this.hostname = prop.getProperty("hostname");
        this.Port = prop.getProperty("Port");
        this.DatabaseName = prop.getProperty("DatabaseName");
        this.Url = "jdbc:%s://%s:%s/%s".formatted(this.DBMS, this.hostname, this.Port, this.DatabaseName);
        this.User = prop.getProperty("User");
        this.Password = prop.getProperty("Password");
    }


}
