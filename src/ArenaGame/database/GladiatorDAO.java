package ArenaGame.database;

import ArenaGame.EnemyGladiator;
import ArenaGame.Gladiator;
import java.sql.*;
import java.util.*;

/**
 * GladiatorDAO handles database changes for enemy gladiators
 *
 * Responsibilities: 
 * - Create and manage GLADIATORS table 
 * - Add and retrieve records 
 * - Initialize default GLADIATORS table if it is not present
 */
public class GladiatorDAO {

    private final Connection connection;

    /**
     * Constructor method initializes connection and ensures 
     * GLADIATORS table exists
     */
    public GladiatorDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
        noTable();
        initializeDefaults();
    }

    /**
     * Creates the GLADIATORS table if it is not detected on run
     */
    private void noTable() {
        String sql = """
                     CREATE TABLE GLADIATORS (
                        GLADIATOR_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        NAME VARCHAR(50) UNIQUE NOT NULL,
                        HEALTH INT NOT NULL,
                        MAX_HEALTH INT NOT NULL,
                        ATTACK INT NOT NULL,
                        DEFENSE INT NOT NULL
                     )
                     """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("GLADIATORS table created.");
        } catch (SQLException exception) {
            if (!"X0Y32".equals(exception.getSQLState())) {
                System.err.println("Error creating GLADIATORS table: " + exception.getMessage());
            }
        }
    }

    // ========== WRITE METHODS ==========
    /**
     * Adds a gladiator to the database
     */
    public void addGladiator(String name, int health, int maxHealth, int attack, int defense) {
        String sql = "INSERT INTO GLADIATORS (NAME, HEALTH, MAX_HEALTH, ATTACK, DEFENSE) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, health);
            statement.setInt(3, maxHealth);
            statement.setInt(4, attack);
            statement.setInt(5, defense);
            statement.executeUpdate();
            System.out.println("Added gladiator: " + name);
        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                System.out.println("Gladiator already exists: " + name);
            } else {
                System.err.println("Error adding gladiator: " + exception.getMessage());
            }
        }
    }

    /**
     * Updates a gladiator's stats
     */
    public void updateGladiator(String name, int health, int attack, int defense) {
        String sql = "UPDATE GLADIATORS SET HEALTH=?, ATTACK=?, DEFENSE=?, WHERE NAME=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, health);
            statement.setInt(2, attack);
            statement.setInt(3, defense);
            statement.setString(4, name);
            statement.executeUpdate();
            System.out.println("Updated gladiators: " + name);
        } catch (SQLException exception) {
            System.err.println("Error updating gladiators: " + exception.getMessage());
        }
    }

    /**
     * Deletes gladiators from table
     */
    public void resetGladiators() {
        String sql = "DELETE FROM GLADIATORS";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            System.out.println("Gladiator table cleared.");
            initializeDefaults();
        } catch (SQLException exception) {
            System.err.println("Error resetting gladiators: " + exception.getMessage());
        }
    }

    // ========== READ METHODS ==========
    /**
     * Returns all gladiators as EnemyGladiator objects
     */
    public List<Gladiator> getAllGladiators() {
        List<Gladiator> gladiators = new ArrayList<>();
        String sql = "SELECT * FROM GLADIATORS";
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                gladiators.add(new EnemyGladiator(
                        rs.getString("NAME"),
                        rs.getInt("HEALTH"),
                        rs.getInt("MAX_HEALTH"),
                        rs.getInt("ATTACK"),
                        rs.getInt("DEFENSE"),
                        new Random()
                ));
            }
        } catch (SQLException exception) {
            System.err.println("Error retrieving gladiators: " + exception.getMessage());
        }
        return gladiators;
    }

    /**
     * Retrieves a random gladiator from the database
     */
    public Gladiator getRandomGladiator() {
        List<Gladiator> all = getAllGladiators();
        if (all.isEmpty()) {
            return null;
        }
        return all.get(new Random().nextInt(all.size()));
    }

    /**
     * Ensure default gladiators exist
     */
    private void initializeDefaults() {
        if (getAllGladiators().isEmpty()) {
            System.out.println("No gladiators found - reverting to defaults.");
            addGladiator("Spartacus", 100, 100, 35, 5);
            addGladiator("Maximus", 120, 120, 45, 10);
            addGladiator("Commodus", 90, 90, 30, 3);
        }
    }
}
