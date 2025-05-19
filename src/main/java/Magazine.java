import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a Magazine in the library catalog.
 * A Magazine is a type of {@link LibraryItem} and includes an issue number.
 */
@Entity
@Table(name = "magazines")
public class Magazine extends LibraryItem {

    /**
     * The issue number of the magazine.
     * Must be a positive integer.
     */
    @Column(name = "issue", nullable = false)
    private int issueNumber;

    /**
     * Constructs a new Magazine with the specified title, editor, and issue number.
     *
     * @param title       the title of the magazine (non-null, non-empty)
     * @param creator     the editor of the magazine (non-null, non-empty)
     * @param issueNumber the issue number (must be positive)
     * @throws IllegalArgumentException if issueNumber is not positive
     */
    public Magazine(String title, String creator, int issueNumber) {
        super(IDGenerator.generateId("magazine"), title, creator);
        if (issueNumber <= 0) throw new IllegalArgumentException("Issue number must be positive");
        this.issueNumber = issueNumber;
    }

    /**
     * Default constructor for JPA/Hibernate.
     * Initializes with default values.
     */
    public Magazine() {
        super();
        this.issueNumber = 1;
    }

    /**
     * Returns the issue number of the magazine.
     *
     * @return the issue number
     */
    public int getIssueNumber() {
        return issueNumber;
    }

    /**
     * Returns a string representation of the magazine.
     *
     * @return a formatted string with magazine details
     */
    @Override
    public String toString() {
        return String.format("[Magazine] ID: %s - Title: %s - Editor: %s - Issue Number: %d",
                itemId, title, creator, issueNumber);
    }

    /**
     * Sets the issue number of the magazine.
     *
     * @param value the new issue number (must be positive)
     * @throws IllegalArgumentException if the value is not positive
     */
    public void setIssueNumber(int value) {
        if (value <= 0) throw new IllegalArgumentException("Issue number must be greater than zero");
        this.issueNumber = value;
    }
}
