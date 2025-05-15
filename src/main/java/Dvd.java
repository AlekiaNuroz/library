public class Dvd extends LibraryItem{
    private final int runtimeInMinutes;

    public Dvd(String itemId, String title, String author, int runtimeInMinutes) {
        super(itemId, title, author);
        if (runtimeInMinutes <= 0) throw new IllegalArgumentException("Runtime must be greater than zero");
        this.runtimeInMinutes = runtimeInMinutes;
    }


    public int getRuntime() {
        return runtimeInMinutes;
    }

    @Override
    public String toString() {
        return String.format("[DVD] ID: %s - Title: %s - Director: %s - Runtime: %d minutes", itemId, title, creator, runtimeInMinutes);
    }
}
