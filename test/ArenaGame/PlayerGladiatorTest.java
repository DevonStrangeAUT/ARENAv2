package ArenaGame;

import org.junit.*;
import static org.junit.Assert.*;

public class PlayerGladiatorTest {

    private PlayerGladiator playerGladiator;
    private Gladiator enemyGladiator;

    @Before
    public void setUp() {
        playerGladiator = new PlayerGladiator("testPlayer", 100, 100, 30, 5, new java.util.Scanner(System.in));
        enemyGladiator = new EnemyGladiator("testEnemy", 100, 100, 30, 5, new java.util.Random());
    }

    @After
    public void tearDown() {
        playerGladiator = null;
        enemyGladiator = null;
    }

    /**
     * Test of getInventory method, of class PlayerGladiator.
     */
    @Test
    public void testGetInventory() {
        System.out.println("getInventory");
        assertNotNull("Inventory should be created in constructor method", playerGladiator.getInventory());
        assertFalse("Inventory should contain default items", playerGladiator.getInventory().isEmpty());
    }

    /**
     * Test of takeDamage method, of class PlayerGladiator.
     */
    @Test
    public void testDamageReducesHP() {
        System.out.println("takeDamage");
        int initHP = playerGladiator.getHealth();
        playerGladiator.takeDamage(20);
        assertTrue("Health should decrease by takeDamage value", playerGladiator.getHealth() < initHP);
    }

    /**
     * Test of useItem method, of class PlayerGladiator.
     */
    @Test
    public void testUseHealthPotionHeals() {
        System.out.println("useItem - item = Health Potion");
        playerGladiator.setHealth(50);
        Item potion = new Item("Health Potion", "Restores 20 HP", "heal", 20);
        playerGladiator.useItem(potion);
        assertTrue("Players health should increase by 20 after using Health Potion", playerGladiator.getHealth() > 50);
    }

    /**
     * Test of takeDamage method, of class PlayerGladiator.
     */
    @Test
    public void testGuardHalfDamage() {
        System.out.println("guard");
        playerGladiator.setBlocking(true);
        int initHP = playerGladiator.getHealth();
        playerGladiator.takeDamage(20);
        assertTrue("Guard should half damage recieved", playerGladiator.getHealth() > initHP - 20);
    }

    /**
     * Test of isAlive method, of class PlayerGladiator.
     */
    @Test
    public void testIsAlive() {
        System.out.println("isAlive");
        playerGladiator.setHealth(0);
        assertFalse("Players with 0 HP should return false", playerGladiator.isAlive());
    }

}
