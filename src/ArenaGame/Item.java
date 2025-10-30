package ArenaGame;

/**
 * Item represents a usable object in the game.
 * <p>
 * Each item has a name, description, type, and value.
 */
public class Item {

    private String itemName;
    private String itemDescription;
    private String itemType;
    private int value;

    /**
     * Create a new item.
     *
     * @param itemName        the item's name
     * @param itemDescription description of the item
     * @param itemType        type of item (e.g., healing, buff, etc.)
     * @param value           numeric value (e.g., heal amount, damage bonus)
     */
    public Item(String itemName, String itemDescription, String itemType, int value) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemType = itemType;
        this.value = value;
    }

    /**
     * @return the item's name
     */
    public String getName() {
        return itemName;
    }

    /**
     * @return the item's description
     */
    public String getDescription() {
        return itemDescription;
    }

    /**
     * @return the item's type
     */
    public String getType() {
        return itemType;
    }

    /**
     * @return the item's numeric value
     */
    public int getValue() {
        return value;
    }
}
