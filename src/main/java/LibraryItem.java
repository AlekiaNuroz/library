import jakarta.persistence.*;

/**
 * Abstract base class representing a library item in the system.
 * Uses JPA annotations for database mapping and employs a JOINED inheritance strategy
 * where subclasses are stored in separate tables with shared common columns.
 */
@Entity
@Table(name = "library_items")  // Maps to main library items table
@Inheritance(strategy = InheritanceType.JOINED)  // Each subclass has its own table
public abstract class LibraryItem {
    /**
     * Unique identifier for the library item, serves as primary key
     * Format: [Prefix][5-digit number] (e.g., B100001)
     */
    @Id
    @Column(name = "item_id", nullable = false)
    protected String itemId;

    /** Title of the item (e.g., book title, movie name) */
    @Column(nullable = false)
    protected String title;

    /** Creator of the item (e.g., author, director, publisher) */
    @Column(nullable = false)
    protected String creator;

    /**
     * Constructs a new LibraryItem with validation checks.
     * @param itemId Unique identifier for the item
     * @param title Title of the item
     * @param creator Creator of the item
     * @throws IllegalArgumentException if any parameter is null or blank
     */
    public LibraryItem(String itemId, String title, String creator) {
        // Validate required fields
        if (itemId == null || itemId.isBlank())
            throw new IllegalArgumentException("ID must not be null or empty");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title must not be null or empty");
        if (creator == null || creator.isBlank())
            throw new IllegalArgumentException("Creator must not be null or empty");

        this.itemId = itemId;
        this.title = title;
        this.creator = creator;
    }

    /** JPA-required no-argument constructor */
    protected LibraryItem() {
        // Required for Hibernate entity operations
    }

    /**
     * @return The item's unique identifier
     */
    public String getItemID() {
        return itemId;
    }

    /**
     * @return The item's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The item's creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the item's title with validation
     * @param title New title value
     * @throws IllegalArgumentException if title is null or blank
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title must not be null or empty");
        this.title = title;
    }

    /**
     * Sets the item's creator with validation
     * @param creator New creator value
     * @throws IllegalArgumentException if creator is null or blank
     */
    public void setCreator(String creator) {
        if (creator == null || creator.isBlank())
            throw new IllegalArgumentException("Creator must not be null or empty");
        this.creator = creator;
    }
}