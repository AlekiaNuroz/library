import jakarta.persistence.*;

/**
 * Entity class representing ID counter records for different item type prefixes.
 * Used in conjunction with {@link IDGenerator} to maintain sequential ID generation
 * across application restarts by persisting counter state to the database.
 */
@Entity
@Table(name = "id_counters")  // Maps to database table storing prefix counters
public class IdCounter {
    /**
     * ID prefix (e.g., "B1", "D2") serving as primary key
     */
    @Id
    @Column(name = "prefix")
    private String prefix;

    /**
     * Last used numeric value for this prefix
     */
    @Column(name = "last_used_id")
    private int lastUsedId;

    /**
     * JPA-required no-argument constructor
     */
    public IdCounter() {
        // Required for Hibernate entity operations
    }

    /**
     * Constructs a new counter record for a prefix
     * @param prefix The ID prefix (2-character identifier)
     * @param lastUsedId Starting counter value
     */
    public IdCounter(String prefix, int lastUsedId) {
        this.prefix = prefix;
        this.lastUsedId = lastUsedId;
    }

    /**
     * @return The ID prefix this counter tracks
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Updates the prefix value (should only be called during initialization)
     * @param prefix New prefix value
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return Last used ID number for this prefix
     */
    public int getLastUsedId() {
        return lastUsedId;
    }

    /**
     * Updates the last used ID counter
     * @param lastUsedId New counter value
     */
    public void setLastUsedId(int lastUsedId) {
        this.lastUsedId = lastUsedId;
    }
}