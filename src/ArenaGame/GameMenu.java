package ArenaGame;

import java.util.*;

/**
 * GameMenu handles displaying menus, user interactions with those menus and
 * handles processing input for the ARENA game.
 *
 * Responsibilities: - Displays main menu on launch - Start battles when option
 * is selected - View/reset scores - View/reset battle logs - Reset gladiators
 * to defaults - Maintain game loop
 */
public class GameMenu {

    private Scanner scanner;
    private Map<String, Integer> scores;
    private List<Gladiator> gladiators;
    private String playerName;
    public static boolean running = true;

    /**
     * Initializes the game menu, loads scores and reads gladiators.
     */
    public GameMenu() {
        this.scanner = new Scanner(System.in);
        this.scores = FileManager.readScores();
        this.gladiators = FileManager.readGladiators();
    }

    /**
     * Game loop, show menu until player exits the game.
     */
    public void run() {
        playIntro();

        playerName = getPlayerName();

        if (!scores.containsKey(playerName)) {
            scores.put(playerName, 0);
        }

        while (running) {
            displayMenu();
            int playerChoice = getChoice(1, 7);  // 1–7 are valid menu options

            switch (playerChoice) {
                case 1 ->
                    startBattle();
                case 2 ->
                    viewScores();
                case 3 ->
                    viewBattleLog();
                case 4 -> {
                    FileManager.clearScores();
                    scores.clear();
                    System.out.println("Scores cleared.");
                    if (!scores.containsKey(playerName)) {
                        scores.put(playerName, 0);
                    }
                }
                case 5 -> {
                    FileManager.resetGladiators();
                    gladiators = FileManager.readGladiators();
                    System.out.println("Gladiators reset to defaults.");
                }
                case 6 -> {
                    FileManager.clearBattleLog();
                    System.out.println("Battle Log cleared.");
                }
                case 7 ->
                    quit();

                default ->
                    System.out.println("Enter a valid choice.");
            }
        }

        FileManager.writeScores(scores);
        playOutro();
        scanner.close();
    }

    /*
    *   Gets the player's name using a scanner, and validates that the name can be used
     */
    private String getPlayerName() {
        String name;
        while (true) {
            System.out.print("Who dares enter the ARENA?: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
            } else if (name.length() > 20) {
                System.out.println("Name too long. Max 20 characters.");
            } else if (!name.matches("[a-zA-Z0-9_]+")) {
                System.out.println("Only letters, numbers, and underscores allowed.");
            } else {
                break;
            }
        }
        return name;
    }

    /**
     * Displays the main menu options.
     */
    private void displayMenu() {
        System.out.println("\n===== ARENA MENU =====");
        System.out.println("1. Enter Battle");
        System.out.println("2. View Scores");
        System.out.println("3. View Battle Logs");
        System.out.println("4. Reset Scores");
        System.out.println("5. Reset Gladiators");
        System.out.println("6. Reset Battle Log");
        System.out.println("7. Exit");
        System.out.print("> ");
    }

    /**
     * Reads integer input from the player.
     */
    private int getChoice(int min, int max) {
        int choice = -1;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    break;
                }
                System.out.print("Enter a valid number between " + min + " and " + max + ": ");
            } catch (NumberFormatException error) {
                System.out.print("Invalid input type. Please enter a number between " + min + " and " + max + ": ");
            }
        }
        return choice;
    }

    /**
     * Starts a battle between the player and a random enemy gladiator.
     */
    private void startBattle() {
        if (gladiators.isEmpty()) {
            System.out.println("No enemies await...");
            return;
        }
        Random rand = new Random();
        Gladiator enemy = gladiators.get(rand.nextInt(gladiators.size()));
        PlayerGladiator player = new PlayerGladiator(playerName, 100, 100, 50, 5, scanner);

        BattleManager battle = new BattleManager(player, enemy);
        boolean playerWon = battle.startBattle();

        if (playerWon) {
            int newScore = scores.getOrDefault(playerName, 0) + 1;
            scores.put(playerName, newScore);
            FileManager.writeScores(scores);
        }

        String result = playerWon ? playerName + " defeated gladiator: " + enemy.getName()
                : playerName + " was defeated by gladiator: " + enemy.getName();
        FileManager.writeBattleLog(result);
    }

    /**
     * Displays all player scores.
     */
    private void viewScores() {
        System.out.println("\n===== Player Scores =====");
        if (scores.isEmpty()) {
            System.out.println("Could not read scores.txt");
        } else {
            scores.forEach((name, score) -> System.out.println(name + ": " + score));
        }
    }

    /**
     * Displays the battle log.
     */
    private void viewBattleLog() {
        System.out.println("\n===== Battle Log =====");
        try (Scanner logReader = new Scanner(new java.io.File("battles.log"))) {
            if (!logReader.hasNextLine()) {
                System.out.println("Not available at this time...");
            }
            while (logReader.hasNextLine()) {
                System.out.println(logReader.nextLine());
            }
        } catch (Exception error) {
            System.out.println("Could not read battle.log as no entries are available.");
        }
    }

    /**
     * Displays the game intro.
     */
    private void playIntro() {
        String[] title = {
            "    _   ___ ___ _  _    _   ",
            "   /_\\ | _ \\ __| \\| |  /_\\  ",
            "  / _ \\|   / _|| .` | / _ \\ ",
            " /_/ \\_\\_|_\\___|_|\\_\\/_/ \\_\\",
            "                            "
        };

        System.out.println();

        try {
            for (String line : title) {
                System.out.println(line);
                Thread.sleep(250); // pause 150ms between lines
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nWelcome to the ARENA, warrior!");
    }

    /**
     * Displays the game outro.
     */
    private void playOutro() {
        System.out.println("Thank you for playing!");
    }

    public static void quit() {
        running = false;
    }
}
