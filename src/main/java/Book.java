import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a Book in the library catalog.
 * A Book is a type of {@link LibraryItem} and includes an additional field for number of pages.
 */
@Entity
@Table(name = "books")
public class Book extends LibraryItem<Integer> {

    /**
     * The number of pages in the book.
     * Must be a positive integer.
     */
    @Column(name = "num_pages", nullable = false)
    private int numberOfPages;

    /**
     * Constructs a new Book with the specified title, creator (author), and number of pages.
     *
     * @param title         the title of the book (non-null, non-empty)
     * @param creator       the author of the book (non-null, non-empty)
     * @param numberOfPages the number of pages in the book (must be positive)
     * @throws IllegalArgumentException if numberOfPages is not positive
     */
    public Book(String title, String creator, int numberOfPages) {
        super(IDGenerator.generateId("book"), title, creator);
        if (numberOfPages <= 0) throw new IllegalArgumentException("Pages must be a positive number");
        this.numberOfPages = numberOfPages;
    }

    /**
     * Default constructor for JPA/Hibernate.
     * Initializes with default values.
     */
    public Book() {
        super();
        this.numberOfPages = 1;
    }

    /**
     * Gets the number of pages in the book.
     *
     * @return the number of pages
     */
    public int getNumberOfPages() {
        return numberOfPages;
    }

    /**
     * Returns a string representation of the Book.
     *
     * @return a formatted string with book details
     */
    @Override
    public String toString() {
        return String.format("[Book] ID: %s - Title: %s - Author: %s - Number of Pages: %d",
                itemId, title, creator, numberOfPages);
    }

    /**
     * Sets the number of pages in the book.
     *
     * @param value the new number of pages (must be positive)
     * @throws IllegalArgumentException if the value is not positive
     */
    public void setPages(int value) {
        if (value <= 0) throw new IllegalArgumentException("Pages must be a positive number");
        this.numberOfPages = value;
    }
}
