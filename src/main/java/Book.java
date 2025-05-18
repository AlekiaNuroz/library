import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book extends LibraryItem {
    @Column(name = "num_pages", nullable = false)
    private final int numberOfPages;

    public Book(String title, String creator, int numberOfPages) {
        super(IDGenerator.generateId("book"), title, creator);
        if (numberOfPages <= 0) throw new IllegalArgumentException("Pages must be a positive number");
        this.numberOfPages = numberOfPages;
    }

    public Book() {
        super();
        this.numberOfPages = 1;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public String toString() {
        return String.format("[Book] ID: %s - Title: %s - Author: %s - Number of Pages: %d", itemId, title, creator, numberOfPages);
    }
}
