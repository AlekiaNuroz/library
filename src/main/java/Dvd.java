import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dvds")
public class Dvd extends LibraryItem{
    @Column(name = "runtime", nullable = false)
    private int runtimeInMinutes;

    public Dvd(String title, String author, int runtimeInMinutes) {
        super(IDGenerator.generateId("dvd"), title, author);
        if (runtimeInMinutes <= 0) throw new IllegalArgumentException("Runtime must be greater than zero");
        this.runtimeInMinutes = runtimeInMinutes;
    }

    public Dvd() {
        super();
        this.runtimeInMinutes = 1;
    }


    public int getRuntime() {
        return runtimeInMinutes;
    }

    @Override
    public String toString() {
        return String.format("[DVD] ID: %s - Title: %s - Director: %s - Runtime: %d minutes", itemId, title, creator, runtimeInMinutes);
    }

    public void setDuration(int value) {
        if (value <= 0) throw new IllegalArgumentException("Duration must be greater than zero");
        this.runtimeInMinutes = value;
    }
}
