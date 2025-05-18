import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "magazines")
public class Magazine extends LibraryItem {
    @Column(name = "issue", nullable = false)
    private final int issueNumber;

    public Magazine(String title, String author, int issueNumber) {
        super(IDGenerator.generateId("magazine"), title, author);
        if (issueNumber <= 0) throw new IllegalArgumentException("Issue number must be positive");
        this.issueNumber = issueNumber;
    }

    public Magazine() {
        super();
        this.issueNumber = 1;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    public String toString() {
        return String.format("[Magazine] ID: %s - Title: %s - Editor: %s - Issue Number: %d", itemId, title, creator, issueNumber);
    }
}
