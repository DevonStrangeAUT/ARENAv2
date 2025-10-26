package ArenaGame.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Quick self-test for DatabaseManager connection.
 */
public class TestDatabaseConnection {

    public static void main(String[] args) {
        System.out.println("Testing Derby database connection...");

        // Try to get a connection from DatabaseManager
        Connection connection = DatabaseManager.getInstance().getConnection();

        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection successful.");
                System.out.println("Database: " + connection.getMetaData().getURL());
                System.out.println("Driver: " + connection.getMetaData().getDriverName());
            } else {
                System.err.println("Connection is null or closed.");
            }
        } catch (SQLException exception) {
            System.err.println("SQLException: " + exception.getMessage());
        }

        // Close connection when done
        DatabaseManager.getInstance().closeConnection();
    }
}
