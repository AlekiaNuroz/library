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


    private static String generateIdAndReturnDigits(String type) {
        var id = IDGenerator.generateId(type);
        return id.substring(id.length() - 6);
    }
}