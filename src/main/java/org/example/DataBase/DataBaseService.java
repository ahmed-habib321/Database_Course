package org.example.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



/**
 * Provides high-level database operations.
 * <p>
 * This service class allows executing SQL queries, retrieving
 * table data, and displaying query results in a readable format.
 * It relies on {@link DataBase_Module} to manage database
 * connections.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 * {@code
 *  DataBaseService service = new DataBaseService(dbModule);
 *  service.setQuery("INSERT INTO users VALUES (1, 'Ahmed')")
 *         .ExecuteQuery();
 *  Map<String, ArrayList<Object>> table = service.RetrieveAllData("users");
 *  service.displayTable(table);}
 * </pre>
 * </p>
 *
 * @author Ahmed
 */
public class DataBaseService {
    /** Database connection manager. */
    private DataBase_Module DBM;
    /** SQL statement used to execute queries. */
    private PreparedStatement Order;
    /** SQL query string to be executed. */
    private String Query;
    /** Result set returned from SELECT queries. */
    private ResultSet RS;

    /**
     * Constructs a {@code DataBaseService} using an existing
     * {@link DataBase_Module}.
     *
     * @param DBM database module responsible for managing connections
     */
    public DataBaseService(DataBase_Module DBM) {
        this.DBM = DBM;
    }

    /**
     * Executes the currently set SQL query.
     * <p>
     * The method establishes a database connection,
     * executes the query, and then closes the connection.
     * </p>
     *
     * <p>
     * This method is suitable for queries such as:
     * INSERT, UPDATE, DELETE, or DDL statements.
     * </p>
     */
    public void executeQuery(){
        DBM.EstablishConnection();
        try {
            Order = DBM.getConnection().prepareStatement(Query);
            Order.execute();
            System.out.println("Query executed successfully.");
            Order.close();
        } catch (SQLException e) {
            System.out.println("Error executing query : " + e.getMessage());
        }finally {
            DBM.DestroyConnection();
        }
    }

    /**
     * Sets the SQL query to be executed.
     *
     * @param query SQL query string
     * @return current {@code DataBaseService} instance
     *         (allows method chaining)
     */
    public DataBaseService setQuery(String query) {
        this.Query = query;
        return this;
    }

    /**
     * Retrieves all data from the specified database table.
     * <p>
     * The returned data structure maps each column name
     * to a list of values belonging to that column.
     * </p>
     *
     * @param TableName name of the table to retrieve data from
     * @return a map where keys are column names and values are
     *         lists containing column data
     */
    public Map<String , ArrayList<Object>> retrieveAllData(String TableName) {
        Map<String , ArrayList<Object>> output = new HashMap<>();
        DBM.EstablishConnection();
        Query = "select * from \""+TableName+"\";";
        try{
            Order = DBM.getConnection().prepareStatement(Query);
            RS = Order.executeQuery();
            ResultSetMetaData RSMD = RS.getMetaData();
            for (int i = 1; i <= RSMD.getColumnCount() ; i++) {
                output.put(RSMD.getColumnName(i) , new ArrayList<>());
            }
            Set<String> keys = output.keySet();
            while (RS.next()){
                for (String key : keys) {
                    output.get(key).add(RS.getString(key));
                }
            }
            Order.close();
            RS.close();
            System.out.println("Data retrieved successfully from table: " + TableName);
        } catch (SQLException e) {
            System.out.printf("Error retrieving data from table %s : %s%n", TableName,e.getMessage());
        }finally {
            DBM.DestroyConnection();
        }
        return output;
    }

    /**
     * Displays table data in a formatted text-based layout.
     * <p>
     * The table structure is printed using column headers
     * followed by rows of data.
     * </p>
     *
     * @param Table a map representing table data, where
     *              keys are column names and values are
     *              lists of column values
     */
    public void displayTable(Map<String , ArrayList<Object>> Table){
        if (Table.isEmpty()) {
            System.out.println("Table is empty.");
            return;
        }
        int rows = Table.values().iterator().next().size();
        Set<String> columns  = Table.keySet();

        StringBuilder builder = new StringBuilder();

        for (String col : columns) {
            builder.append(col).append(" | ");
        }
        builder.append("\n");

        // Rows
        for (int i = 0; i < rows; i++) {
            for (String col : columns) {
                builder.append(Table.get(col).get(i))
                        .append(" | ");
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }
}
