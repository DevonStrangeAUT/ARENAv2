package ArenaGame;

import java.util.Random;

/**
 * EnemyGladiator represents an AI-controlled gladiator.
 * Chooses actions automatically during its turn.
 */
public class EnemyGladiator extends Gladiator {

    private Random randNum;

    /**
     * Create a new enemy gladiator.
     *
     * @param name       the gladiator's name
     * @param health     starting health
     * @param maxHealth  maximum health
     * @param attack     attack power
     * @param defense    defense value
     * @param randNum    random generator for decisions
     */
    public EnemyGladiator(String name, int health, int maxHealth, int attack, int defense, Random randNum) {
        super(name, health, attack, defense, maxHealth);
        this.randNum = randNum;
    }

    /**
     * Enemy takes its turn by choosing to attack, guard, or taunt.
     * Decision is based on health, opponent health, or random chance.
     *
     * @param opponent the player or gladiator being fought
     */
    @Override
    public void takeTurn(Gladiator opponent) {
        // Reset guard status at the start of a turn
        setBlocking(false);

        System.out.println("\nOpponent's turn...");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int choice;

        if (health < maxHealth * 0.3) {
            choice = 1; // Guard if low health
        } else if (opponent.getHealth() < opponent.getMaxHealth() * 0.3) {
            choice = 0; // Attack if opponent is weak
        } else {
            int rand = randNum.nextInt(10); // Random decision
            if (rand < 5) {
                choice = 0; // Attack
            } else if (rand < 9) {
                choice = 1; // Guard
            } else {
                choice = 2; // Taunt
            }
        }

        // Execute chosen action
        switch (choice) {
            case 0 -> {
                System.out.println(name + " attacks.");
                opponent.takeDamage(getAttack());
                ArenaGame.BattleManager.logAction(getName(), "attacks.", getAttack());
            }
            case 1 -> {
                setBlocking(true);
                System.out.println(name + " prepares to guard your attack.");
                ArenaGame.BattleManager.logAction(getName(), "guards.", 0);
            }
            case 2 -> {
                System.out.println(name + " taunts you.");
                ArenaGame.BattleManager.logAction(getName(), "taunts.", 0);
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
