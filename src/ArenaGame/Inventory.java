package ArenaGame;

import java.util.*;

/**
 * Inventory stores and manages items for a gladiator.
 * <p>
 * Provides methods to add, display, and use items.
 */
public class Inventory {

    private List<Item> items = new ArrayList<>();

    /**
     * Add an item to the inventory.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Check if the inventory is empty.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Get the number of items in the inventory.
     *
     * @return inventory size
     */
    public int size() {
        return items.size();
    }

    /**
     * Display all items in the inventory with index numbers.
     */
    public void display() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        System.out.println("===== Inventory =====");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            System.out.println((i + 1) + ". " + item.getName() + " - " + item.getDescription());
        }
    }

    /**
     * Use (and remove) an item from the inventory.
     *
     * @param index index of the item (0-based)
     * @return the item removed, or null if invalid
     */
    public Item useItem(int index) {
        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid item, please choose again.");
            return null;
        }
        return items.remove(index);
    }
}
