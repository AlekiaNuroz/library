public abstract class LibraryItem {
    protected String itemId;
    protected String title;
    protected String creator;

    public LibraryItem(String itemId, String title, String creator) {
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("ID must not be null or empty");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title must not be null or empty");
        if (creator == null) creator = "";

        this.itemId = itemId;
        this.title = title;
        this.creator = creator;
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
}
