import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

public class LibraryItemTestHelper {

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