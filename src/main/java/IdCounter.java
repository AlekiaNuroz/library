import jakarta.persistence.*;

@Entity
@Table(name = "id_counters")
public class IdCounter {
    @Id
    @Column(name = "prefix")
    private String prefix;

    @Column(name = "last_used_id")
    private int lastUsedId;

    public IdCounter() {}

    public IdCounter(String prefix, int lastUsedId) {
        this.prefix = prefix;
        this.lastUsedId = lastUsedId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(int lastUsedId) {
        this.lastUsedId = lastUsedId;
    }
}
