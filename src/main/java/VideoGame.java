import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a Video Game in the library catalog.
 * A Video Game is a type of {@link LibraryItem} that includes a platform on which it runs.
 */
@Entity
@Table(name = "video_games")
public class VideoGame extends LibraryItem {

    /**
     * The platform on which the video game runs (e.g., PC, Xbox, PlayStation).
     * Cannot be null or empty.
     */
    @Column(name = "platform", nullable = false)
    private String platform;

    /**
     * Constructs a new VideoGame with the specified title, creator, and platform.
     *
     * @param title    the title of the video game (non-null, non-empty)
     * @param creator  the creator or publisher of the video game (non-null, non-empty)
     * @param platform the platform of the video game (non-null, non-empty)
     * @throws IllegalArgumentException if platform is null or blank
     */
    public VideoGame(String title, String creator, String platform) {
        super(IDGenerator.generateId("video_game"), title, creator);
        if (platform == null || platform.isBlank())
            throw new IllegalArgumentException("Platform must not be null or empty");
        this.platform = platform;
    }

    /**
     * Default constructor for JPA/Hibernate.
     * Initializes platform to "Unknown".
     */
    public VideoGame() {
        super();
        this.platform = "Unknown";
    }

    /**
     * Returns the platform of the video game.
     *
     * @return the platform string
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Returns a string representation of the video game.
     *
     * @return a formatted string containing video game details
     */
    @Override
    public String toString() {
        return String.format("[Video Game] Id: %s - Title: %s - Publisher: %s - Platform: %s", itemId, title, creator, platform);
    }

    /**
     * Sets the platform of the video game.
     *
     * @param value the new platform value (non-null, non-empty)
     * @throws IllegalArgumentException if value is null or blank
     */
    public void setPlatform(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Platform must not be null or empty");
        this.platform = value;
    }
}
