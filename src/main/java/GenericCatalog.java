import java.util.HashMap;
import java.util.Map;

public class GenericCatalog<T extends LibraryItem> {
    private final Map<String, T> items = new HashMap<>();

    public T getItemById(String itemId) {
        return items.get(itemId);
    }

    public boolean containsItem(String itemId) {
        return items.containsKey(itemId);
    }

    public void addItem(T item) {
        items.put(item.getItemID(), item);
        System.out.println("Item added: " + item);
    }

    public void removeItem(String itemId) {
        if (!items.containsKey(itemId)) {
            throw new IllegalArgumentException("Item with ID " + itemId + " not found.");
        }
        items.remove(itemId);
    }
}
