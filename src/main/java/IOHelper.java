import java.util.Scanner;
import java.util.function.Function;

/**
 * Utility class for handling input and output operations in the console.
 * Provides methods for clearing the screen, getting string and numeric input
 * from the user with validation, displaying menus, and pausing execution.
 */
public class IOHelper {
    /**
     * Clears the console screen. Supports Windows, macOS, and Linux by using
     * ANSI escape codes.
     */
    public static void clearScreen() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.out.print("\033[H\033[2J"); // ANSI escape code for Windows terminals
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    /**
     * Gets a string input from the user.
     *
     * @param scanner     The {@code Scanner} object used to read user input.
     * @param prompt      The message displayed to the user prompting for input.
     * @param allowEmpty  A boolean indicating whether an empty input string is allowed.
     * @return The string entered by the user, trimmed of leading and trailing whitespace.
     * If {@code allowEmpty} is false and the input is empty, the user will be prompted again.
     */
    public static String getStringInput(Scanner scanner, String prompt, boolean allowEmpty) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (!allowEmpty && input.isEmpty()) {
            System.out.println("This field cannot be empty!");
            return getStringInput(scanner, prompt, false);
        }
        return input;
    }

    /**
     * Displays a menu in the console with a given title and a list of options.
     *
     * @param title        The title of the menu to be displayed.
     * @param options      An array of strings representing the menu options. Each option
     * will be displayed with a preceding number.
     * @param clearScreen  A boolean indicating whether to clear the console screen before
     * printing the menu.
     * @param exitText     The text to display for the exit option, which will be numbered
     * as the last option in the menu.
     */
    public static void printMenu(String title, String[] options, boolean clearScreen, String exitText) {
        if (clearScreen) clearScreen();
        System.out.println("\n==== " + title + " ====\n");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println((options.length + 1) + ". " + exitText + "\n");
    }

    /**
     * A private generic method to get numeric input from the user within a specified range.
     * It uses a provided {@code Function} to parse the input string to the desired numeric type.
     *
     * @param <T>          The numeric type of the input (e.g., Integer, Double).
     * @param scanner      The {@code Scanner} object for user input.
     * @param prompt       The message displayed to the user.
     * @param min          The minimum valid numeric value (inclusive).
     * @param max          The maximum valid numeric value (inclusive).
     * @param allowEmpty   Whether empty input is allowed.
     * @param defaultValue The default value to return if empty input is allowed.
     * @param parser       A {@code Function} that takes a string and returns an object of type T
     * (e.g., {@code Integer::parseInt}, {@code Double::parseDouble}).
     * @return The numeric value entered by the user, or the default value if allowed and the input is empty.
     * The user is prompted again if the input is invalid or out of range.
     */
    private static <T extends Number> T getNumericInput(Scanner scanner, String prompt, T min, T max, boolean allowEmpty, T defaultValue, Function<String, T> parser) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (allowEmpty && input.isEmpty()) return defaultValue;

            try {
                T value = parser.apply(input);
                if (value.doubleValue() >= min.doubleValue() && value.doubleValue() <= max.doubleValue()) {
                    return value;
                }
                System.out.printf("Please enter a value between %s and %s\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Gets an integer input from the user within a specified range.
     * This is a convenience method that calls {@link #getIntInput(Scanner, String, int, int, boolean, int)}
     * with {@code allowEmpty} set to {@code false} and {@code defaultValue} set to {@code min}.
     *
     * @param scanner The {@code Scanner} object for user input.
     * @param prompt  The message displayed to the user.
     * @param min     The minimum valid integer value (inclusive).
     * @param max     The maximum valid integer value (inclusive).
     * @return The integer entered by the user. The user is prompted again if the input
     * is not a valid integer or is outside the specified range.
     */
    public static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        return getIntInput(scanner, prompt, min, max, false, min);
    }

    /**
     * Gets an integer input from the user within a specified range, with an optional default value
     * if empty input is allowed.
     *
     * @param scanner      The {@code Scanner} object for user input.
     * @param prompt       The message displayed to the user.
     * @param min          The minimum valid integer value (inclusive).
     * @param max          The maximum valid integer value (inclusive).
     * @param allowEmpty   Whether an empty input string is allowed.
     * @param defaultValue The default integer value to return if {@code allowEmpty} is true
     * and the user enters an empty string.
     * @return The integer entered by the user, or the default value if allowed and the input is empty.
     * The user is prompted again if the input is not a valid integer or is outside the specified range.
     */
    public static int getIntInput(Scanner scanner, String prompt, int min, int max, boolean allowEmpty, int defaultValue) {
        return getNumericInput(scanner, prompt, min, max, allowEmpty, defaultValue, Integer::parseInt);
    }

    /**
     * Pauses the execution of the current thread for a specified number of seconds.
     *
     * @param seconds The number of seconds to wait.
     */
    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted while waiting.");
        }
    }
}