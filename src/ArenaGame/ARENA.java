package ArenaGame;

/**
 * The ARENA class is the entry point or launcher for the Arena Game.
 * It initializes the GameMenu and starts the game loop.
 * 
 * Responsibilities:
 * - Start the game by launching GameMenu
 */
public class ARENA {
     /**
     * Main method, starts the Arena game.
     */
    public static void main(String[] args) {
        GameMenu menu = new GameMenu();
        menu.run();
    }
}

