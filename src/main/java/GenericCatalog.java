import java.util.*;

/**
 * A generic catalog for managing items of type T, which extends LibraryItem.
 * The catalog maintains a collection of items in-memory and synchronizes with a database
 * through the provided DatabaseManager. All CRUD operations are persisted to the database.
 *
 * @param <T> the type of items in the catalog, must be a subclass of LibraryItem
 */
public class GenericCatalog<T extends LibraryItem> {
    /** In-memory storage of items mapped by their unique ID */
    private final Map<String, T> items = new HashMap<>();
    /** Database manager for persisting catalog changes */
    private final DatabaseManager db;

    /**
     * Constructs a GenericCatalog and initializes it with all items from the database.
     * Performs an unchecked cast from LibraryItem to T, assuming that all items retrieved
     * from the database are compatible with type T.
     *
     * @param db the DatabaseManager used to interact with the database
     */
    @SuppressWarnings("unchecked")
    public GenericCatalog(DatabaseManager db) {
        this.db = db;
        var results = db.getAllLibraryItems();

        // Cast each LibraryItem to T, assuming the database contains compatible types
        for (LibraryItem item : results) {
            this.items.put(item.getItemID(), (T) item);
        }
    }

    /**
     * Retrieves an item from the catalog by its ID.
     *
     * @param itemId the ID of the item to retrieve
     * @return the item with the specified ID, or null if not found
     */
    public T getItemById(String itemId) {
        return items.get(itemId);
    }

    /**
     * Checks if an item with the specified ID exists in the catalog.
     *
     * @param itemId the ID to check for existence
     * @return true if the item exists, false otherwise
     */
    public boolean containsItem(String itemId) {
        return items.containsKey(itemId);
    }

    /**
     * Adds a new item to the catalog and persists it to the database.
     *
     * @param item the item to add
     */
    public void addItem(T item) {
        items.put(item.getItemID(), item);
        db.saveOrUpdate(item);
        System.out.println("Item added: " + item);
    }

    /**
     * Updates an existing item in the catalog and persists changes to the database.
     *
     * @param item the item to update
     */
    public void updateItem(T item) {
        items.put(item.getItemID(), item);
        db.saveOrUpdate(item);
        System.out.println("Item updated: " + item);
    }

    /**
     * Removes an item from the catalog and deletes it from the database.
     *
     * @param itemId the ID of the item to remove
     * @throws IllegalArgumentException if the item with the specified ID is not found
     */
    public boolean removeItem(String itemId) {
        LibraryItem item = items.remove(itemId);

        if (item == null) {
            return false;
        }

        db.delete(item);
        System.out.println("Item deleted: " + item);
        return true;
    }

    /**
     * Returns a list of all items in the catalog.
     * The returned list is a copy of the catalog's items to prevent modification of the internal state.
     *
     * @return a list of all items as LibraryItem instances
     */
    public List<LibraryItem> getAllItems() {
        // Safe to cast to List<LibraryItem> since T extends LibraryItem
        List <LibraryItem> catalogItems = new ArrayList<>(items.values());
        catalogItems.sort(Comparator.comparing(item -> item.itemId));
        return catalogItems;
    }
}