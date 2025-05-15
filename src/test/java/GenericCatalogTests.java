import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class GenericCatalogTests {
    private GenericCatalog<LibraryItem> catalog;

    @BeforeEach
    public void setUp() {
        catalog = new GenericCatalog<>();
    }

    @ParameterizedTest
    @CsvSource({
            "B23456, The Drive - University of the People, John Doe, 200, BOOK",
            "D56789, Inception, Christopher Nolan, 120, DVD",
            "M98765, Science Monthly, Jane Smith, 35, MAGAZINE",
            "V34567, The Legend of Gaming, Epic Studios, PC, VIDEO_GAME",
            "INVALID, , , -1, INVALID_TYPE"
    })
    public void testAddBookToCatalog(String itemId, String title, String creator, String spec, String type) {
        LibraryItem itemToAdd = createLibraryItem(itemId, title, creator, spec, type);

        if (itemToAdd != null) {
            catalog.addItem(itemToAdd);
            assertTrue(catalog.containsItem(itemId), "Item should exist");
            LibraryItem itemToValidate = catalog.getItemById(itemId);
            LibraryItemTestHelper.assertLibraryItemMatches(itemToAdd, itemToValidate);
        } else {
            assertFalse(catalog.containsItem(itemId), "Item should not exist");
        }
    }

    @ParameterizedTest
    @CsvSource({
            "B23456, The Drive - University of the People, John Doe, 200, BOOK, true",
            "D56789, Inception, Christopher Nolan, 120, DVD, true",
            "M98765, Science Monthly, Jane Smith, 35, MAGAZINE, true",
            "V34567, The Legend of Gaming, Epic Studios, PC, VIDEO_GAME, true",
            "INVALID, , , -1, INVALID_TYPE, false"
    })
    public void testRemoveBookFromCatalog(String itemId, String title, String creator, String spec, String type, boolean shouldExist) {
        LibraryItem item = createLibraryItem(itemId, title, creator, spec, type);

        if (item != null) {
            catalog.addItem(item);
        }

        if (shouldExist) {
            assertTrue(catalog.containsItem(itemId), "Item should exist before removal");
            catalog.removeItem(itemId);
            assertFalse(catalog.containsItem(itemId), "Item should not exist after removal");
        } else {
            assertThrows(IllegalArgumentException.class, () -> catalog.removeItem(itemId), "Removing an invalid item should throw an exception");
        }
    }

    private LibraryItem createLibraryItem(String itemId, String title, String creator, String spec, String type) {
        return switch (type) {
            case "BOOK" -> new Book(itemId, title, creator, Integer.parseInt(spec));
            case "DVD" -> new Dvd(itemId, title, creator, Integer.parseInt(spec));
            case "MAGAZINE" -> new Magazine(itemId, title, creator, Integer.parseInt(spec));
            case "VIDEO_GAME" -> new VideoGame(itemId, title, creator, spec);
            default -> null;
        };
    }
}
