public class VideoGame extends LibraryItem {
    private final String platform;

    public VideoGame(String itemId, String title, String author, String platform) {
        super(itemId, title, author);
        if (platform == null || platform.isBlank()) throw new IllegalArgumentException("Platform must not be null or empty");
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return String.format("[Video Game] Id: %s - Title: %s - Publisher: %s - Platform: %s", itemId, title, creator, platform);
    }
}
