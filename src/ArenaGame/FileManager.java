package ArenaGame;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileManager handles all file operations for the game.
 * <p>
 * Responsibilities:
 * - Read and write gladiators
 * - Read and write player scores
 * - Write battle logs
 * - Clear and reset data files
 */
public class FileManager {

    private static final String GLADIATOR_FILE = "gladiators.txt";
    private static final String SCORE_FILE = "scores.txt";
    private static final String BATTLE_LOG = "battles.log";

    // ================= READ METHODS =================

    /**
     * Reads gladiators from file. 
     * If missing, defaults are created.
     *
     * @return a list of gladiators
     */
    public static List<Gladiator> readGladiators() {
        List<Gladiator> gladiators = new ArrayList<>();
        Path path = Paths.get(GLADIATOR_FILE);

        if (!Files.exists(path)) {
            System.out.println("Gladiator file missing, creating defaults.");
            return resetGladiators();
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String name = data[0].trim();
                    int health = Integer.parseInt(data[1].trim());
                    int attack = Integer.parseInt(data[3].trim());
                    int defense = Integer.parseInt(data[4].trim());
                    int maxHealth = Integer.parseInt(data[2].trim());
                    gladiators.add(new EnemyGladiator(name, health, maxHealth, attack, defense, new Random()));
                }
            }
        } catch (IOException error) {
            System.out.println("Error reading gladiators: " + error.getMessage());
        }
        return gladiators;
    }

    /**
     * Reads scores from file. 
     * Creates file if missing.
     *
     * @return map of player names to scores
     */
    public static Map<String, Integer> readScores() {
        Map<String, Integer> scores = new HashMap<>();
        Path path = Paths.get(SCORE_FILE);

        if (!Files.exists(path)) {
            try { Files.createFile(path); } 
            catch (IOException e) { System.out.println("Cannot create scores.txt"); }
            return scores;
        }

        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":");
                if (parts.length == 2) {
                    scores.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
        } catch (IOException error) {
            System.out.println("Error reading scores.txt: " + error.getMessage());
        }
        return scores;
    }

    /**
     * Loads all player names from scores.
     *
     * @return set of player names
     */
    public static Set<String> loadPlayerNames() {
        return readScores().keySet();
    }

    // ================= WRITE METHODS =================

    /**
     * Writes gladiators to file (overwrites).
     */
    public static void writeGladiators(List<Gladiator> gladiators) {
        Path path = Paths.get(GLADIATOR_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Gladiator g : gladiators) {
                writer.write(g.getName() + "," + g.getHealth() + "," + g.getMaxHealth() + "," + g.getAttack() + "," + g.getDefense());
                writer.newLine();
            }
        } catch (IOException error) {
            System.out.println("Error writing gladiators.txt: " + error.getMessage());
        }
    }

    /**
     * Writes scores to file (overwrites).
     */
    public static void writeScores(Map<String, Integer> scores) {
        Path path = Paths.get(SCORE_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException error) {
            System.out.println("Error writing scores.txt: " + error.getMessage());
        }
    }

    /**
     * Appends a battle log entry to file with timestamp.
     */
    public static void writeBattleLog(String logEntry) {
        Path path = Paths.get(BATTLE_LOG);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("[" + new Date() + "] " + logEntry);
            writer.newLine();
        } catch (IOException error) {
            System.out.println("Error writing battles.log: " + error.getMessage());
        }
    }

    // ================= CLEAR METHODS =================

    /**
     * Clears all scores from scores.txt.
     */
    public static void clearScores() {
        Path path = Paths.get(SCORE_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            // file truncated
        } catch (IOException error) {
            System.out.println("Could not clear scores.txt: " + error.getMessage());
        }
    }

    /**
     * Clears all logs from battles.log.
     */
    public static void clearBattleLog() {
        Path path = Paths.get(BATTLE_LOG);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            // file truncated
        } catch (IOException error) {
            System.out.println("Could not clear battles.log: " + error.getMessage());
        }
    }

    // ================= RESET METHODS =================

    /**
     * Resets gladiators file with default values.
     *
     * @return list of default gladiators
     */
    public static List<Gladiator> resetGladiators() {
        List<Gladiator> defaults = new ArrayList<>();
        defaults.add(new EnemyGladiator("Spartacus", 100, 100, 35, 5, new Random()));
        defaults.add(new EnemyGladiator("Maximus", 120, 120, 45, 10, new Random()));
        defaults.add(new EnemyGladiator("Commodus", 90, 90, 30, 3, new Random()));
        writeGladiators(defaults);
        return defaults;
    }
}
