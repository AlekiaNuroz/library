import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericCatalog<T extends LibraryItem> {
    private final Map<String, T> items = new HashMap<>();
    private final DatabaseManager db;

    @SuppressWarnings("unchecked")
    public GenericCatalog(DatabaseManager db) {
        this.db = db;
        var results = db.getAllLibraryItems();

        for (LibraryItem item : results) {
            this.items.put(item.getItemID(), (T) item);
        }
    }

    public T getItemById(String itemId) {
        return items.get(itemId);
    }

    public boolean containsItem(String itemId) {
        return items.containsKey(itemId);
    }

    public void addItem(T item) {
        items.put(item.getItemID(), item);
        db.saveOrUpdate(item);
        System.out.println("Item added: " + item);
    }

    public void updateItem(T item) {
        items.put(item.getItemID(), item);
        db.saveOrUpdate(item);
        System.out.println("Item updated: " + item);
    }

    public void removeItem(String itemId) {
        LibraryItem item = items.remove(itemId);

        if (item == null) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found.");
        }

        db.delete(item);
        System.out.println("Item deleted: " + item);
    }


    public List<LibraryItem> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
