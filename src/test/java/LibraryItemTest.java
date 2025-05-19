import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class LibraryItemTest {

    static Stream<Arguments> validLibraryItems() {
        return Stream.of(
                Arguments.of("Book", "B001", "Clean Code", "Robert Martin", 464),
                Arguments.of("DVD", "D001", "Interstellar", "Nolan", 169),
                Arguments.of("Magazine", "M001", "Tech Weekly", "Editor", 52),
                Arguments.of("VideoGame", "G001", "Halo Infinite", "Bungie", "Xbox")
        );
    }

    /**
     * Tests that valid combinations of input parameters produce non-null {@link LibraryItem} instances.
     *
     * @param type    the type of the library item (Book, DVD, etc.)
     * @param id      the identifier for the item
     * @param title   the title of the item
     * @param creator the creator (author/director/etc.)
     * @param value   the type-specific field (pages, duration, issue, or platform)
     */
    @ParameterizedTest
    @MethodSource("validLibraryItems")
    void testValidLibraryItems(String type, String id, String title, String creator, Object value) {
        LibraryItem item = createItem(type, id, title, creator, value);
        assertNotNull(item);
    }

    static Stream<Arguments> invalidLibraryItems() {
        return Stream.of(
                Arguments.of(MediaType.BOOK.name(), null, "Clean Code", "Robert Martin", 464),      // null id
                Arguments.of(MediaType.BOOK.name(), "generate", null, "Robert Martin", 464),        // null title
                Arguments.of(MediaType.DVD.name(), "generate", "", "", 0),                          // empty strings & zero
                Arguments.of(MediaType.MAGAZINE.name(), "generate", "Time", "Editor", -1),          // negative pages
                Arguments.of(MediaType.VIDEO_GAME.name(), "generate", "GameName", "Dev", null),     // null platform
                Arguments.of("Comic", "generate", "Spider-Man", "Stan Lee", 25)                     // unsupported type
        );
    }

    /**
     * Tests that invalid input parameters throw the expected {@link IllegalArgumentException} or {@link NullPointerException}.
     * <p>
     * This includes cases such as unsupported types, null/blank fields, negative numbers, and null platform values.
     *
     * @param typeStr  the type string of the library item
     * @param id       the ID, or "generate" to trigger automatic ID generation
     * @param title    the title of the item
     * @param creator  the creator of the item
     * @param value    the type-specific value (can be null or invalid)
     */
    @ParameterizedTest
    @MethodSource("invalidLibraryItems")
    void testInvalidLibraryItemsShouldFail(String typeStr, String id, String title, String creator, Object value) {
        if (!Set.of("BOOK", "DVD", "MAGAZINE", "VIDEO_GAME").contains(typeStr)) {
            assertThrows(IllegalArgumentException.class, () -> IDGenerator.generateId(typeStr));
        } else {
            String generatedId = Objects.equals(id, "generate") ? IDGenerator.generateId(typeStr) : id;
            assertThrows(IllegalArgumentException.class, () -> createItem(typeStr, generatedId, title, creator, value));
        }
    }

    private LibraryItem createItem(String type, String id, String title, String creator, Object value) {
        validateNotEmpty(id, "ID must not be null or empty");
        validateNotEmpty(title, "Title must not be null or empty");

        return switch (type) {
            case "Book", "DVD", "Magazine" -> createTypedItem(type, title, creator, (int) value);
            case "VideoGame" -> createVideoGame(title, creator, (String) value);
            default -> throw new IllegalArgumentException("Unsupported item type: " + type);
        };
    }

    private void validateNotEmpty(String value, String errorMessage) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(errorMessage);
    }

    private LibraryItem createTypedItem(String type, String title, String creator, int numericValue) {
        if (numericValue <= 0) throw new IllegalArgumentException("Numeric value must be positive");
        return switch (type) {
            case "Book" -> new Book(title, creator, numericValue);
            case "DVD" -> new Dvd(title, creator, numericValue);
            case "Magazine" -> new Magazine(title, creator, numericValue);
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    private LibraryItem createVideoGame(String title, String creator, String platform) {
        validateNotEmpty(platform, "Platform must not be null or empty");
        return new VideoGame(title, creator, platform);
    }

    private enum MediaType {
        BOOK,
        DVD,
        MAGAZINE,
        VIDEO_GAME
    }
}
