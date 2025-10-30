package ArenaGame;

import java.util.Scanner;

/**
 * PlayerGladiator represents the human-controlled gladiator.
 * <p>
 * Allows the player to choose actions each turn and manage an inventory.
 */
public class PlayerGladiator extends Gladiator {

    private Scanner scanner;
    private Inventory inventory;

    /**
     * Create a player gladiator with default items.
     *
     * @param name       player name
     * @param health     starting health
     * @param maxHealth  maximum health
     * @param attack     attack power
     * @param defense    defense value
     * @param scanner    scanner for player input
     */
    public PlayerGladiator(String name, int health, int maxHealth, int attack, int defense, Scanner scanner) {
        super(name, health, attack, defense, maxHealth);
        this.scanner = scanner;
        this.inventory = new Inventory();
        // Add some starter items
        inventory.addItem(new Item("Health Potion", "Restores 20 HP", "heal", 20));
        inventory.addItem(new Item("Berserk Potion", "Increases attack by 5", "buff", 5));
    }

    /**
     * @return the player's inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Reads player input choice.
     *
     * @return number entered by player, or -1 if invalid
     */
    private int getChoice() {
        System.out.print("> ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Executes the player's turn.
     * Player can attack, guard, use an item, taunt, or exit.
     *
     * @param opponent the enemy gladiator
     */
    @Override
    public void takeTurn(Gladiator opponent) {
        setBlocking(false);

        boolean turnCompleted = false;

        while (!turnCompleted) {
            System.out.println("\nYour turn. Select an action:");
            System.out.println("1. Attack.");
            System.out.println("2. Guard.");
            System.out.println("3. Use item.");
            System.out.println("4. Taunt.");
            System.out.println("5. Exit");

            int combatChoice = getChoice();

            switch (combatChoice) {
                case 1 -> {
                    System.out.println("You attack.");
                    opponent.takeDamage(getAttack());
                    ArenaGame.BattleManager.logAction(getName(), "attacks.", getAttack());
                    turnCompleted = true;
                }
                case 2 -> {
                    System.out.println("You guard.");
                    setBlocking(true);
                    ArenaGame.BattleManager.logAction(getName(), "guards.", 0);
                    turnCompleted = true;
                }
                case 3 -> {
                    if (inventory.isEmpty()) {
                        System.out.println("You have no items to use!");
                    } else {
                        inventory.display();

                        int itemChoice;
                        do {
                            System.out.print("Select an item (1-" + inventory.size() + "): ");
                            itemChoice = getChoice() - 1;
                        } while (itemChoice < 0 || itemChoice >= inventory.size());

                        Item selectedItem = inventory.useItem(itemChoice);
                        useItem(selectedItem);
                        ArenaGame.BattleManager.logAction(getName(), "uses an item.", 0);
                        turnCompleted = true;
                    }
                }
                case 4 -> {
                    System.out.println("You taunt.");
                    ArenaGame.BattleManager.logAction(getName(), "taunts.", 0);
                    turnCompleted = true;
                }
                case 5 -> {
                    GameMenu.quit();
                    turnCompleted = true;
                }
                default -> System.out.println("Invalid choice. Please enter a number between 1-5.");
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Applies effects of an item (heal, buff, etc.).
     *
     * @param item the item being used
     */
    public void useItem(Item item) {
        switch (item.getName()) {
            case "Health Potion" -> {
                int healedAmount = Math.min(item.getValue(), maxHealth - getHealth());
                setHealth(getHealth() + healedAmount);
                System.out.println(getName() + " was healed for " + healedAmount + " HP.");
            }
            case "Berserk Potion" -> {
                setAttack(getAttack() + item.getValue());
                System.out.println(getName() + "'s attack was increased by " + item.getValue() + ".");
            }
            default -> System.out.println("You fail to use an item.");
        }
    }
}
