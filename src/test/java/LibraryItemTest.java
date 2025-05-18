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
            case "Book", "DVD", "Magazine" -> createTypedItem(type, id, title, creator, (int) value);
            case "VideoGame" -> createVideoGame(id, title, creator, (String) value);
            default -> throw new IllegalArgumentException("Unsupported item type: " + type);
        };
    }

    private void validateNotEmpty(String value, String errorMessage) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(errorMessage);
    }

    private LibraryItem createTypedItem(String type, String id, String title, String creator, int numericValue) {
        if (numericValue <= 0) throw new IllegalArgumentException("Numeric value must be positive");
        return switch (type) {
            case "Book" -> new Book(id, title, creator, numericValue);
            case "DVD" -> new Dvd(id, title, creator, numericValue);
            case "Magazine" -> new Magazine(id, title, creator, numericValue);
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    private LibraryItem createVideoGame(String id, String title, String creator, String platform) {
        validateNotEmpty(platform, "Platform must not be null or empty");
        return new VideoGame(id, title, creator, platform);
    }

    private enum MediaType {
        BOOK,
        DVD,
        MAGAZINE,
        VIDEO_GAME
    }
}
