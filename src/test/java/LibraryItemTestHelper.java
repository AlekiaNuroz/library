import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

/**
 * Helper class for asserting deep equality between {@link LibraryItem} instances in unit tests.
 */
public class LibraryItemTestHelper {

    /**
     * Asserts that two {@link LibraryItem} objects are equal by comparing all of their fields via reflection.
     * <p>
     * This method checks:
     * <ul>
     *     <li>That neither object is null</li>
     *     <li>That both objects are of the same class</li>
     *     <li>That all declared fields (including inherited fields) have equal values</li>
     * </ul>
     *
     * @param expected the expected item to compare
     * @param actual   the actual item to compare
     * @param <T>      the type of the library item
     * @throws AssertionError if any field values are not equal or reflection access fails
     */
    public static <T extends LibraryItem> void assertLibraryItemMatches(T expected, T actual) {
        assertNotNull(expected, "Expected item is null");
        assertNotNull(actual, "Actual item is null");
        assertEquals(expected.getClass(), actual.getClass(), "Classes should match");

        Class<?> clazz = expected.getClass();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object expectedValue = field.get(expected);
                    Object actualValue = field.get(actual);
                    assertEquals(expectedValue, actualValue,
                            "Field mismatch: " + field.getName());
                } catch (IllegalAccessException e) {
                    fail("Unable to access field: " + field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}