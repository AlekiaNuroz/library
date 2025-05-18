import jakarta.persistence.*;

@Entity
@Table(name = "library_items")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LibraryItem {
    @Id
    @Column(name = "item_id", nullable = false)
    protected String itemId;
    @Column(nullable = false)
    protected String title;
    @Column(nullable = false)
    protected String creator;

    public LibraryItem(String itemId, String title, String creator) {
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("ID must not be null or empty");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title must not be null or empty");
        if (creator == null || creator.isBlank()) throw new IllegalArgumentException("Creator must not be null or empty");

        this.itemId = itemId;
        this.title = title;
        this.creator = creator;
    }

    public LibraryItem() {

    }

    public String getItemID() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title must not be null or empty");
        this.title = title;
    }

    public void setCreator(String creator) {
        if (creator == null || creator.isBlank()) throw new IllegalArgumentException("Creator must not be null or empty");
        this.creator = creator;
    }
}
