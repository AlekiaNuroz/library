public class Magazine extends LibraryItem {
    private final int issueNumber;

    public Magazine(String itemId, String title, String author, int issueNumber) {
        super(itemId, title, author);
        if (issueNumber <= 0) throw new IllegalArgumentException("Issue number must be positive");
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    public String toString() {
        return String.format("[Magazine] ID: %s - Title: %s - Editor: %s - Issue Number: %d", itemId, title, creator, issueNumber);
    }
}
