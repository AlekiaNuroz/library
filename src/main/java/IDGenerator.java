import java.util.HashMap;
import java.util.Map;

public class IDGenerator {
    private static final Map<String, Integer> usedIds = new HashMap<>();

    /**
     * Generates a unique ID based on the supplied ID type.
     *
     * @param type The type of ID to generate. Supports BOOK, DVD, MAGAZINE, and VIDEO_GAME. All other media types
     *             throw an {@code IllegalArgumentException}.
     * @return A unique ID as a {@code String} beginning with the prefix of the media type.
     */
    public static String generateId(String type) {
        String prefix = generatePrefix(type);

        int count = usedIds.compute(prefix, (_, v) -> (v == null) ? 1 : v + 1);

        return prefix + String.format("%05d", count);
    }

    private static String generatePrefix(String type) {
        return switch (type.toLowerCase()) {
            case "book" -> "B1";
            case "dvd" -> "D2";
            case "magazine" -> "M3";
            case "video_game" -> "V4";
            case null -> throw new NullPointerException("Type cannot be null");
            default -> throw new IllegalArgumentException("Invalid ID type: " + type);
        };
    }
}
