import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IDGenerator {
    private static final Map<String, Integer> usedIds = new HashMap<>();

    static {
        loadUsedIdsFromDatabase();
    }

    public static String generateId(String type) {
        String prefix = generatePrefix(type);
        int count = usedIds.compute(prefix, (_, v) -> (v == null) ? 1 : v + 1);
        persistPrefixCount(prefix, count);
        return prefix + String.format("%05d", count);
    }

    private static String generatePrefix(String type) {
        return switch (type.toLowerCase()) {
            case "book" -> "B1";
            case "dvd" -> "D2";
            case "magazine" -> "M3";
            case "video_game" -> "V4";
            case null -> throw new NullPointerException("Type cannot be null");
            default -> throw new IllegalArgumentException("Invalid ID type: " + type);
        };
    }

    private static void persistPrefixCount(String prefix, int count) {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            IdCounter counter = session.get(IdCounter.class, prefix);
            if (counter == null) {
                counter = new IdCounter(prefix, count);
                session.persist(counter);
            } else {
                counter.setLastUsedId(count);
                session.merge(counter);
            }
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error persisting prefix count: " + e.getMessage());
        }
    }

    private static void loadUsedIdsFromDatabase() {
        try (Session session = DatabaseManager.getSessionFactory().openSession()) {
            List<IdCounter> counters = session.createQuery("FROM IdCounter", IdCounter.class).list();
            for (IdCounter counter : counters) {
                usedIds.put(counter.getPrefix(), counter.getLastUsedId());
            }
        } catch (Exception e) {
            System.err.println("Error loading usedIds from database: " + e.getMessage());
        }
    }
}
