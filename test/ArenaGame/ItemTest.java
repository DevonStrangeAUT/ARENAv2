package ArenaGame;

import org.junit.*;
import static org.junit.Assert.*;

public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        item = new Item("Health Potion", "Restores 20 HP", "heal", 20);
    }

    @After
    public void tearDown() {
        item = null;
    }

    /**
     * Test of getName method, of class Item.
     */
    @Test
    public void testGetName() {
        assertEquals("Item name should match name of variable in constructor", "Health Potion", item.getName());
    }

    /**
     * Test of getDescription method, of class Item.
     */
    @Test
    public void testGetDescription() {
        assertEquals("Description of item should match description of variable in constructor", "Restores 20 HP", item.getDescription());
    }

    /**
     * Test of getType method, of class Item.
     */
    @Test
    public void testGetType() {
        assertEquals("Item type should match type in variable in constructor", "heal", item.getType());
    }

    /**
     * Test of getValue method, of class Item.
     */
    @Test
    public void testGetValue() {
        assertEquals("Item value should match number present in variable in constructor", 20, item.getValue());
    }

    @Test
    /**
     * Test of getName method, of class Item.
     */
    public void testItemDifferentiation() {
        Item item2 = new Item("Berserk Potion", "Increase attack by 5", "buff", 5);
        assertNotEquals("Items should be differentiated by name, description, type and value",
                item.getName(), item2.getName());
    }
}
