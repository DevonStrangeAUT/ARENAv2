package ArenaGame.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The DatabaseManager class handles the connection and management of an
 * embedded database.
 *
 * Responsibilities: - Create and manage the connection to the database - Create
 * a database if one doesn't exist (and let the user know) - Provide methods for
 * closing or altering the database connection
 *
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:derby:arenaDB;create=true";

    private static final String USERNAME = "ARENA";
    private static final String PASSWORD = "ARENA";

    /**
     * Private constructor method to prevent direct instantiation. Creates the
     * connection when the instance is initialized.
     */
    private DatabaseManager() {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Successfully connected to " + DB_URL);
        } catch (ClassNotFoundException exception) {
            System.err.println("Derby driver not found: " + exception.getMessage());
        } catch (SQLException exception) {
            System.err.println("Failed to connect to the database: " + exception.getMessage());
        }
    }

    /**
     * Provides access to the DatabaseManager instance
     *
     * @return the DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Returns the current active connection object.
     *
     * @return active JDBC connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Safely closes connection between client and DB
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to database closed.");
            }
        } catch (SQLException exception) {
            System.err.println("Failed to close the database connection: " + exception.getMessage());
        }
    }
}
