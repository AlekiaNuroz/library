import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates unique, formatted IDs for different types of library items.
 * Maintains synchronization between in-memory state and database persistence using Hibernate.
 * ID format: [Prefix][5-digit zero-padded number] (e.g., B100001, D200002)
 */
public class IDGenerator {
    /** In-memory cache of last used IDs per prefix */
    private static final Map<String, Integer> usedIds = new HashMap<>();

    // Load existing counters from database on class initialization
    static {
        loadUsedIdsFromDatabase();
    }

    /**
     * Generates a new unique ID for the specified item type.
     * Synchronizes in-memory state with database persistence.
     *
     * @param type The item type (book, dvd, magazine, video_game)
     * @return Formatted unique ID string
     * @throws IllegalArgumentException for unsupported types
     * @throws NullPointerException if type is null
     */
    public static String generateId(String type) {
        String prefix = generatePrefix(type);
        // Atomically increment and get current count for prefix
        int count = usedIds.compute(prefix, (_, v) -> (v == null) ? 1 : v + 1);
        persistPrefixCount(prefix, count);
        return prefix + String.format("%05d", count);  // Zero-pad to 5 digits
    }

    /**
     * Maps item types to their designated ID prefixes.
     * @param type The item type to map
     * @return Corresponding 2-character prefix
     * @throws IllegalArgumentException for unsupported types
     */
    private static String generatePrefix(String type) {
        return switch (type.toLowerCase()) {
            case "book"       -> "B1";  // Book prefix
            case "dvd"        -> "D2";  // DVD prefix
            case "magazine"   -> "M3";  // Magazine prefix
            case "video_game" -> "V4";  // Video Game prefix
            case null         -> throw new NullPointerException("Type cannot be null");
            default           -> throw new IllegalArgumentException("Invalid ID type: " + type);
        };
    }

    /**
     * Persists the current counter value for a prefix to the database.
     * Uses Hibernate for database operations with transaction management.
     * @param prefix The ID prefix to update
     * @param count The new counter value to persist
     */
    private static void persistPrefixCount(String prefix, int count) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Try to find existing counter
            IdCounter counter = session.get(IdCounter.class, prefix);

            if (counter == null) {
                // Create new counter if none exists
                counter = new IdCounter(prefix, count);
                session.persist(counter);
            } else {
                // Update existing counter
                counter.setLastUsedId(count);
                session.merge(counter);
            }
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error persisting prefix count: " + e.getMessage());
        }
    }

    /**
     * Initializes in-memory ID cache from database state.
     * Called automatically during class initialization.
     */
    private static void loadUsedIdsFromDatabase() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            // Load all existing counters from database
            List<IdCounter> counters = session.createQuery("FROM IdCounter", IdCounter.class).list();

            // Populate local cache
            for (IdCounter counter : counters) {
                usedIds.put(counter.getPrefix(), counter.getLastUsedId());
            }
        } catch (Exception e) {
            System.err.println("Error loading usedIds from database: " + e.getMessage());
        }
    }
}