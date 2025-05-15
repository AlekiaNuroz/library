public class Book extends LibraryItem {
    private final int numberOfPages;

    public Book(String itemId, String title, String creator, int numberOfPages) {
        super(itemId, title, creator);
        if (numberOfPages <= 0) throw new IllegalArgumentException("Pages must be a positive number");
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public String toString() {
        return String.format("[Book] ID: %s - Title: %s - Author: %s - Number of Pages: %d", itemId, title, creator, numberOfPages);
    }
}
