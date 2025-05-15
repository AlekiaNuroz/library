import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryItemTest {
    @ParameterizedTest
    @CsvSource({
            "Book, B001, Clean Code, Robert Martin, 464",
            "DVD, D001, Interstellar, Nolan, 169",
            "Magazine, M001, Tech Weekly, Editor, 52",
            "VideoGame, G001, Halo Infinite, Bungie, Xbox"
    })
    void testValidLibraryItems(String type, String id, String title, String creator, String value) {
        LibraryItem item = createItem(type, normalize(id), normalize(title), normalize(creator), value);
        assertNotNull(item);
        System.out.println("✅ PASSED: " + type + " - Valid input");
    }

    @ParameterizedTest
    @CsvSource({
            "Book, null, Clean Code, Robert Martin, 464",          // null id
            "Book, B002, null, Robert Martin, 464",                // null title
            "DVD, D003, , , 0",                                    // empty strings and zero
            "Magazine, M004, Time, Editor, -1",                    // negative pages
            "VideoGame, G005, GameName, Dev, ",                    // null platform
            "Comic, C001, Spider-Man, Stan Lee, 25"                // unsupported type
    })
    void testInvalidLibraryItemsShouldFail(String type, String id, String title, String creator, String value) {
        assertThrows(IllegalArgumentException.class, () -> createItem(type, normalize(id), normalize(title), normalize(creator), value));
        System.out.println("❌ FAILED AS EXPECTED: " + type + " - Invalid input");
    }

    private LibraryItem createItem(String type, String id, String title, String creator, String value) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID must not be null or empty");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title must not be null or empty");

        switch (type) {
            case "Book":
            case "DVD":
            case "Magazine":
                int numericValue = Integer.parseInt(value);
                if (numericValue <= 0) throw new IllegalArgumentException("Numeric value must be positive");
                switch (type) {
                    case "Book":
                        return new Book(id, title, creator, numericValue);
                    case "DVD":
                        return new Dvd(id, title, creator, numericValue);
                    case "Magazine":
                        return new Magazine(id, title, creator, numericValue);
                }
            case "VideoGame":
                if (value == null || value.isBlank()) throw new IllegalArgumentException("Platform must not be null or empty");
                return new VideoGame(id, title, creator, value);
            default:
                throw new IllegalArgumentException("Unsupported item type: " + type);
        }
    }

    private String normalize(String input) {
        return (input == null || "null".equalsIgnoreCase(input)) ? null : input;
    }
}
