package ArenaGame.database;

import java.sql.*;
import java.util.*;

/**
 * BattleLogDAO handles and parses all battle log operations. (In v2 txt/log
 * files are a legacy feature and data is stored in a DB)
 *
 * Responsibilities: - Create the BATTLE_LOGS table if one is not present -
 * Insert and append entries - Retrieve and clear table on request
 */
public class BattleLogDAO {

    private final Connection connection;

    /**
     * Starting constructor method initializes DB connection and generates table
     * (if needed)
     */
    public BattleLogDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
        noTable();
    }

    /**
     * Create the table if needed
     */
    private void noTable() {
        String sql = """
                     CREATE TABLE BATTLE_LOGS (
                     LOG_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                     TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     PLAYER_NAME VARCHAR(50) NOT NULL,
                     ENEMY_NAME VARCHAR(50) NOT NULL,
                     RESULT VARCHAR(20) NOT NULL
                     )
                     """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("BATTLE_LOGS table successfully created.");
        } catch (SQLException exception) {
            if (!"X0Y32".equals(exception.getSQLState())) {
                System.err.println("Failed to create BATTLE_LOGS table: " + exception.getMessage());
            }
        }
    }

    // ========== WRITE METHODS ==========
    /**
     * Adds a new entry to BATTLE_LOG or appends
     *
     * @param playerName name of the player
     * @param enemyName name of the enemy
     * @param result battle outcome ("Win" or "Loss")
     */
    public void addBattleLog(String playerName, String enemyName, String result) {
        String sql = "INSERT INTO BATTLE_LOGS (PLAYER_NAME, ENEMY_NAME, RESULT) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.setString(2, enemyName);
            statement.setString(3, result);
            statement.executeUpdate();
            System.out.println("Battle log updated: " + playerName + " against " + enemyName + " ended in " + result);
        } catch (SQLException exception) {
            System.err.println("Failed to add to battle log: " + exception.getMessage());
        }
    }

    public void clearLogs() {
        String sql = "DELETE FROM BATTLE_LOGS";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            System.out.println("Successfully cleared battle logs.");
        } catch (SQLException exception) {
            System.err.println("Failed to clear battle logs: " + exception.getMessage());
        }
    }

    // ========== READ METHODS ==========
    /**
     * Pulls battle logs on request.
     *
     * @return List of formatted log entries as strings
     */
    public List<String> getLogs() {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT TIMESTAMP, PLAYER_NAME, ENEMY_NAME, RESULT FROM BATTLE_LOGS ORDER BY LOG_ID DESC";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Timestamp timeStamp = resultSet.getTimestamp("TIMESTAMP");
                String playerName = resultSet.getString("PLAYER_NAME");
                String enemyName = resultSet.getString("ENEMY_NAME");
                String result = resultSet.getString("RESULT");

                logs.add(String.format("[%s] %s vs %s → %s",
                        timeStamp.toLocalDateTime().withNano(0),
                        playerName, enemyName, result));
            }
        } catch (SQLException exception) {
            System.err.println("Failed to retrieve battle logs: " + exception.getMessage());
        }
        return logs;
    }

    /**
     * Counts num stored battle logs.
     *
     * @return number of logs present in table
     */
    public int numLogs() {
        String sql = "SELECT COUNT(*) FROM BATTLE_LOGS";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            System.err.println("Failed to get log count: " + exception.getMessage());
        }
        return 0;
    }

}
