package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton utility class for managing MySQL JDBC connections.
 *
 * Update DB_URL, DB_USER, and DB_PASSWORD to match your MySQL setup
 * before running the application.
 */
public class DBConnection {

    // ---------------------------------------------------------------
    // Configuration — change these to match your MySQL installation
    // ---------------------------------------------------------------
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/online_voting_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER     = "root";       // your MySQL username
    private static final String DB_PASSWORD = "root";       // your MySQL password

    // Admin credentials (simple in-app check; not stored in DB)
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";

    // Singleton connection instance
    private static Connection connection = null;

    // Private constructor — prevents instantiation
    private DBConnection() {}

    /**
     * Returns a singleton Connection object.
     * Creates a new connection if one does not already exist or is closed.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java.jar to classpath.", e);
        }
        return connection;
    }

    /**
     * Closes the database connection gracefully.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            }
        }
    }
}
