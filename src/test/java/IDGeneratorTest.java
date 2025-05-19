import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class IDGeneratorTest {

    static Stream<Arguments> idCases() {
        return Stream.of(
            Arguments.of("BOOK", "B", null),  // valid case
            Arguments.of("DVD", "D", null),   // valid case
            Arguments.of("MAGAZINE", "M", null),  // valid case
            Arguments.of("VIDEO_GAME", "V", null),  // valid case
            Arguments.of(null, null, NullPointerException.class),  // invalid case
            Arguments.of("", null, IllegalArgumentException.class),  // invalid case
            Arguments.of("ABC", null, IllegalArgumentException.class)  // invalid case
        );
    }

    /**
     * Tests the {@link IDGenerator#generateId(String)} method with various input types.
     * <p>
     * Verifies that:
     * <ul>
     *     <li>Valid types return an ID starting with the expected character and followed by digits</li>
     *     <li>Invalid types throw the expected exceptions</li>
     * </ul>
     *
     * @param type the type of item for which an ID should be generated
     * @param expectedFirstCharacter the expected first character of the ID for valid types
     * @param expectedException the exception expected for invalid types, or {@code null} for valid cases
     */
    @ParameterizedTest
    @MethodSource("idCases")
    void testGeneratedId(String type, Character expectedFirstCharacter, Class<? extends Throwable> expectedException) {
        if (expectedException != null) {
            assertThrows(expectedException, () -> IDGenerator.generateId(type));
        } else {
            var id = IDGenerator.generateId(type);
            assertEquals(expectedFirstCharacter, id.charAt(0));
            assertTrue(generateIdAndReturnDigits(type).matches("\\d+"));
        }
    }

    // Simple helper to extract numeric suffix from generated ID
    private static String generateIdAndReturnDigits(String type) {
        var id = IDGenerator.generateId(type);
        return id.substring(id.length() - 6);
    }
}