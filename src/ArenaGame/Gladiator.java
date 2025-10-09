package ArenaGame;

/**
 * Abstract base class for all gladiators.
 * <p>
 * Stores common stats (health, attack, defense), 
 * manages damage calculation and blocking, 
 * and defines the abstract turn-taking method.
 */
public abstract class Gladiator {

    protected String name;
    protected int health;
    protected int maxHealth;
    protected int attack;
    protected int defense;
    protected boolean isBlocking;

    /**
     * Create a new gladiator with stats.
     *
     * @param name       gladiator name
     * @param health     current health
     * @param attack     attack power
     * @param defense    defense value
     * @param maxHealth  maximum health
     */
    public Gladiator(String name, int health, int attack, int defense, int maxHealth) {
        this.name = name;
        this.health = health;
        this.maxHealth = maxHealth;
        this.attack = attack;
        this.defense = defense;
    }

    /**
     * Checks if the gladiator is still alive.
     *
     * @return true if health > 0
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Apply damage to the gladiator.
     * Damage is reduced by defense and blocking.
     *
     * @param damage raw damage before reductions
     */
    public void takeDamage(int damage) {
        double damageMultiplier = 1 - ((double) defense / (defense + 50));
        int totalDamage = (int) Math.round(damage * damageMultiplier);

        if (isBlocking()) {
            totalDamage = (int) Math.ceil(totalDamage / 2.0);
        }

        totalDamage = Math.max(totalDamage, 1); // always at least 1 damage
        health -= totalDamage;

        System.out.println(name + " has taken " + totalDamage + " damage. Remaining Health = " + Math.max(health, 0));
    }

    /**
     * Enable or disable blocking.
     */
    public void setBlocking(boolean status) {
        isBlocking = status;
    }

    /**
     * Check if gladiator is blocking.
     *
     * @return true if blocking
     */
    public boolean isBlocking() {
        return isBlocking;
    }

    // ===== GETTERS =====

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }

    // ===== SETTERS =====

    public void setHealth(int health) { this.health = health; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }

    /**
     * Each gladiator must define its own behavior for a turn.
     *
     * @param opponent the opposing gladiator
     */
    public abstract void takeTurn(Gladiator opponent);
}
