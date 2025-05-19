import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a DVD in the library catalog.
 * A DVD is a type of {@link LibraryItem} and includes an additional field for runtime duration in minutes.
 */
@Entity
@Table(name = "dvds")
public class Dvd extends LibraryItem {

    /**
     * The runtime duration of the DVD in minutes.
     * Must be a positive integer.
     */
    @Column(name = "runtime", nullable = false)
    private int runtimeInMinutes;

    /**
     * Constructs a new DVD with the specified title, director, and runtime.
     *
     * @param title            the title of the DVD (non-null, non-empty)
     * @param creator           the director of the DVD (non-null, non-empty)
     * @param runtimeInMinutes the runtime duration in minutes (must be positive)
     * @throws IllegalArgumentException if runtimeInMinutes is not positive
     */
    public Dvd(String title, String creator, int runtimeInMinutes) {
        super(IDGenerator.generateId("dvd"), title, creator);
        if (runtimeInMinutes <= 0) throw new IllegalArgumentException("Runtime must be greater than zero");
        this.runtimeInMinutes = runtimeInMinutes;
    }

    /**
     * Default constructor for JPA/Hibernate.
     * Initializes with default values.
     */
    public Dvd() {
        super();
        this.runtimeInMinutes = 1;
    }

    /**
     * Returns the runtime duration of the DVD in minutes.
     *
     * @return the runtime in minutes
     */
    public int getRuntime() {
        return runtimeInMinutes;
    }

    /**
     * Returns a string representation of the DVD.
     *
     * @return a formatted string with DVD details
     */
    @Override
    public String toString() {
        return String.format("[DVD] ID: %s - Title: %s - Director: %s - Runtime: %d minutes",
                itemId, title, creator, runtimeInMinutes);
    }

    /**
     * Sets the runtime duration of the DVD.
     *
     * @param value the new runtime duration in minutes (must be positive)
     * @throws IllegalArgumentException if the value is not positive
     */
    public void setDuration(int value) {
        if (value <= 0) throw new IllegalArgumentException("Duration must be greater than zero");
        this.runtimeInMinutes = value;
    }
}
