import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class GenericCatalogTests {
    private GenericCatalog<LibraryItem> catalog;
    private final DatabaseManager databaseManager = new DatabaseManager();

    @BeforeEach
    public void setUp() {
        catalog = new GenericCatalog<>(databaseManager);
    }

    @ParameterizedTest
    @CsvSource({
            "The Drive - University of the People, John Doe, 200, BOOK",
            "Inception, Christopher Nolan, 120, DVD",
            "Science Monthly, Jane Smith, 35, MAGAZINE",
            "The Legend of Gaming, Epic Studios, PC, VIDEO_GAME",
            "null, null, -1, INVALID_TYPE"
    })
    public void testAddLibraryItemToCatalog(String title, String creator, String spec, String type) {
        LibraryItem itemToAdd = createLibraryItem(
                "null".equals(title) ? null : title,
                "null".equals(creator) ? null : creator,
                "null".equals(spec) ? null : spec,
                type
        );

        catalog.addItem(itemToAdd);
        assertTrue(catalog.containsItem(itemToAdd.itemId), "Item should exist");
        LibraryItem itemToValidate = catalog.getItemById(itemToAdd.itemId);
        LibraryItemTestHelper.assertLibraryItemMatches(itemToAdd, itemToValidate);
    }

    @ParameterizedTest
    @CsvSource({
            "The Drive - University of the People, John Doe, 200, BOOK, true",
            "Inception, Christopher Nolan, 120, DVD, true",
            "Science Monthly, Jane Smith, 35, MAGAZINE, true",
            "The Legend of Gaming, Epic Studios, PC, VIDEO_GAME, true",
            "null, null, -1, INVALID_TYPE, false"
    })
    public void testRemoveLibraryItemFromCatalog(String title, String creator, String spec, String type, boolean shouldExist) {
        LibraryItem item = null;
        Exception creationException = null;

        try {
            item = createLibraryItem(
                    "null".equals(title) ? null : title,
                    "null".equals(creator) ? null : creator,
                    "null".equals(spec) ? null : spec,
                    type
            );
        } catch (Exception e) {
            creationException = e;
        }

        if (!shouldExist) {
            assertNotNull(creationException, "Expected an exception during item creation");
        } else {
            assertNull(creationException, "Did not expect exception during item creation");
            catalog.addItem(item);
            assertTrue(catalog.containsItem(item.itemId), "Item should exist before removal");
            catalog.removeItem(item.itemId);
            assertFalse(catalog.containsItem(item.itemId), "Item should not exist after removal");
        }
    }

    private LibraryItem createLibraryItem(String title, String creator, String spec, String type) {
        return switch (type) {
            case "BOOK" -> new Book(title, creator, Integer.parseInt(spec));
            case "DVD" -> new Dvd(title, creator, Integer.parseInt(spec));
            case "MAGAZINE" -> new Magazine(title, creator, Integer.parseInt(spec));
            case "VIDEO_GAME" -> new VideoGame(title, creator, spec);
            default -> throw new IllegalArgumentException("Invalid LibraryItem type: " + type);
        };
    }
}
