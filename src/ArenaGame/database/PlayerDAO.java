package ArenaGame.database;

import java.sql.*;
import java.util.*;

/**
 * PlayerDAO (direct access object) manages all database methods and functions
 * related to player data.
 *
 * Responsibilities: - Create PLAYERS table in arenaDB if one does not exist -
 * Insert new players into the table - Retrieve and update player scores as
 * needed - Fetch player records when requested
 */
public class PlayerDAO {

    private final Connection connection;

    /**
     * A Constructor method that initializes database connection and checks
     * tables exist in DB.
     */
    public PlayerDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
        noTable();
    }

    /**
     * Create PLAYERS table if no table exists.
     */
    private void noTable() {
        String sql = """
                     CREATE TABLE PLAYERS (
                     PLAYER_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                     NAME VARCHAR(50) UNIQUE NOT NULL,
                     SCORE INT DEFAULT 0
                     )
                     """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("PLAYERS table created.");
        } catch (SQLException exception) {
            if (!"X0Y32".equals(exception.getSQLState())) { // Table already exists error
                System.err.println("Failed to create PLAYERS table: " + exception.getMessage());
            }
        }
    }

    // ========== WRITE METHODS ==========
    /**
     * Inputs a player to the DB if they do not currently exist
     *
     * @param name player name
     */
    public void addPlayer(String name) {
        String sql = "INSERT INTO PLAYERS (NAME) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
            System.out.println("Player: " + name + " successfully added to database.");
        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                System.out.println("Pkayer already present in database.");
            } else {
                System.err.println("Error adding player to database: " + exception.getMessage());
            }
        }
    }

    /**
     * Updates player scores
     *
     * @param name player name
     * @param newScore new score value
     */
    public void updateScore(String name, int newScore) {
        String sql = "UPDATE PLAYERS SET SCORE = ? WHERE NAME = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newScore);
            statement.setString(2, name);
            statement.executeUpdate();
            System.out.println("Updated " + name + " to score " + newScore);
        } catch (SQLException exception) {
            System.err.println("Error updating score for: " + name + ": " + exception.getMessage());
        }
    }

    /**
     * Resets scores in PLAYER to zero
     */
    public void resetScores() {
        String sql = "UPDATE PLAYERS SET SCORE = 0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            System.out.println("Player scores reset.");
        } catch (SQLException exception) {
            System.err.println("Error resetting scores: " + exception.getMessage());
        }
    }

    // ========== READ METHODS ==========
    /**
     * Getter for player score
     *
     * @param name player name
     * @return player score or -1 if not score found
     */
    public int getScore(String name) {
        String sql = "SELECT SCORE FROM PLAYERS WHERE NAME = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("SCORE");
            }
        } catch (SQLException exception) {
            System.err.println("Error retrieving score for " + name + ": " + exception.getMessage());
        }
        return -1;
    }

    /**
     * Check if a player exists
     *
     * @param name player name
     * @return true if player exists
     */
    public boolean playerExists(String name) {
        String sql = "SELECT 1 FROM PLAYERS WHERE NAME = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException exception) {
            System.err.println("Error checking if player exists: " + exception.getMessage());
        }
        return false;
    }

    /**
     * Retrieves players and their scores.
     *
     * @return Map of player names and scores
     */
    public Map<String, Integer> getAllPlayers() {
        Map<String, Integer> players = new LinkedHashMap<>();
        String sql = "SELECT NAME, SCORE FROM PLAYERS ORDER BY SCORE DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.put(rs.getString("NAME"), rs.getInt("SCORE"));
            }
        } catch (SQLException exception) {
            System.err.println("Error retrieving players: " + exception.getMessage());
        }
        return players;
    }
    
    
    
}
