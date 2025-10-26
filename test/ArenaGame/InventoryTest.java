package ArenaGame;

import org.junit.*;
import static org.junit.Assert.*;

public class InventoryTest {
    
    private Inventory inventory;
    
    @Before
    public void setUp() {
        inventory = new Inventory();
    }
    
    @After
    public void tearDown() {
        inventory = null;
    }

    /**
     * Test of addItem method, of class Inventory.
     */
    @Test
    public void testAddItem() {
        int initSize = inventory.size();
        inventory.addItem(new Item("Health Potion", "Restores 20 HP", "heal", 20));
        assertEquals("Inventory size should increase by 1 when an item is added to it", initSize + 1, inventory.size());
    }

    /**
     * Test of isEmpty method, of class Inventory.
     */
    @Test
    public void testIsEmpty() {
        assertTrue("Inventory generated empty before population", inventory.isEmpty());
    }

    /**
     * Test of useItem method, of class Inventory.
     */
    @Test
    public void testUseItem() {
        Item potion = new Item("Health Potion", "Restores 20 HP", "heal", 20);
        inventory.addItem(potion);
        assertFalse("Inventory should not be empty after adding an item", inventory.isEmpty());
        Item used = inventory.useItem(0);
        assertEquals("Used item should be the same one added", potion.getName(), used.getName());
        assertTrue("Inventory should now be empty after using all items", inventory.isEmpty());
    }

    /**
     * Test of display method, of class Inventory.
     */
    @Test
    public void testDisplay() {
        try {
            inventory.display();
        } catch (Exception exception) {
            fail("display() should not throw an exception if empty");
        }
    }

    /**
     * Test of useItems method, of class Inventory.
     */
    @Test
    public void testUseItemInvalidIndex() {
        Item result = inventory.useItem(10);
        assertNull("Using invalid index should return null", result);
    }
    
}
