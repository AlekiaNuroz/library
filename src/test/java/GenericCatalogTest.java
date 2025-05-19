import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class GenericCatalogTest {
    private GenericCatalog<LibraryItem> catalog;
    private final DatabaseManager databaseManager = new DatabaseManager();

    @BeforeEach
    public void setUp() {
        catalog = new GenericCatalog<>(databaseManager);
    }

    /**
     * Parameterized test that verifies adding different types of LibraryItem objects to the catalog.
     * Includes valid items (Book, DVD, Magazine, VideoGame) and an invalid type case that should throw an exception.
     *
     * @param title   the title of the item
     * @param creator the creator (author, director, etc.)
     * @param spec    type-specific field (e.g., pages, duration)
     * @param type    the type of LibraryItem
     */
    @ParameterizedTest
    @CsvSource({
            "The Drive - University of the People, John Doe, 200, BOOK",
            "Inception, Christopher Nolan, 120, DVD",
            "Science Monthly, Jane Smith, 35, MAGAZINE",
            "The Legend of Gaming, Epic Studios, PC, VIDEO_GAME",
            "null, null, -1, INVALID_TYPE"
    })
    public void testAddLibraryItemToCatalog(String title, String creator, String spec, String type) {
        if ("INVALID_TYPE".equals(type)) {
            assertThrows(IllegalArgumentException.class, () -> createLibraryItem(
                    "null".equals(title) ? null : title,
                    "null".equals(creator) ? null : creator,
                    "null".equals(spec) ? null : spec,
                    type
            ), "Expected an exception for invalid type");
        } else {
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
    }

    /**
     * Parameterized test that verifies removing items from the catalog.
     * First creates and adds a LibraryItem (if valid), then removes it and asserts correct behavior.
     *
     * @param title        the title of the item
     * @param creator      the creator of the item
     * @param spec         type-specific detail (pages, duration, etc.)
     * @param type         the type of the LibraryItem
     * @param shouldExist  whether the item should be valid and successfully removed
     */
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

    /**
     * Creates a concrete {@link LibraryItem} instance based on the provided type and field values.
     *
     * @param title  the title of the item (can be null in invalid test cases)
     * @param creator the creator (author, director, etc.)
     * @param spec   the type-specific field (e.g., page count, duration, issue number, or platform)
     * @param type   the type of item to create (BOOK, DVD, MAGAZINE, VIDEO_GAME)
     * @return a new instance of the appropriate {@link LibraryItem} subclass
     * @throws IllegalArgumentException if the type is unrecognized or spec is invalid for the given type
     */
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
