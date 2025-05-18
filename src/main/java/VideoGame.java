import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_games")
public class VideoGame extends LibraryItem {
    @Column(name = "platform", nullable = false)
    private String platform;

    public VideoGame(String title, String author, String platform) {
        super(IDGenerator.generateId("video_game"), title, author);
        if (platform == null || platform.isBlank()) throw new IllegalArgumentException("Platform must not be null or empty");
        this.platform = platform;
    }

    public VideoGame() {
        super();
        this.platform = "Unknown";
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return String.format("[Video Game] Id: %s - Title: %s - Publisher: %s - Platform: %s", itemId, title, creator, platform);
    }

    public void setPlatform(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Platform must not be null or empty");
        this.platform = value;
    }
}
