import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Utility class for handling console input/output operations.
 * Provides methods for screen management, user input validation, and formatted data display.
 */
public class IOHelper {

    /**
     * Clears the console screen using ANSI escape codes.
     * Note: Effectiveness depends on terminal support for ANSI escape sequences.
     */
    public static void clearScreen() {
        String os = System.getProperty("os.name").toLowerCase();
        // ANSI escape code for clearing screen (works in most modern terminals)
        String clearCode = "\033[H\033[2J";
        System.out.print(clearCode);

        if (!os.contains("win")) {
            // Additional flush for non-Windows systems to ensure clear
            System.out.flush();
        }
    }

    /**
     * Gets validated string input from user.
     * @param scanner Scanner instance to use
     * @param prompt Display prompt for user
     * @param allowEmpty Whether empty input is acceptable
     * @return Validated user input
     */
    public static String getStringInput(final Scanner scanner, final String prompt, final boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (!allowEmpty && input.isEmpty()) {
                    System.out.println("This field cannot be empty!");
                    continue;
                }
                return input;
            } catch (NoSuchElementException | IllegalStateException e) {
                System.out.println("Error: Input is unavailable or scanner is closed.");
                return "None";
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                return "None";
            }
        }
    }

    /**
     * Displays a formatted menu with automatic exit option.
     * @param title Menu title
     * @param options Menu options
     * @param clearScreen Whether to clear screen before display
     * @param exitText Text for exit option
     */
    public static void printMenu(String title, String[] options, boolean clearScreen, String exitText) {
        if (clearScreen) clearScreen();
        System.out.println("\n==== " + title + " ====\n");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        // Add exit option as last item
        System.out.println((options.length + 1) + ". " + exitText + "\n");
    }

    /**
     * Prints a formatted catalog of library items in table format.
     * @param items List of items to display
     */
    public static void printCatalog(List<LibraryItem> items) {
        printCatalog(items, items.size());  // Default: print all columns
    }

    public static void printCatalog(List<LibraryItem> items, int numColumns) {
        // Define fixed column order and getters
        String[] allColumns = {"Item ID", "Title", "Creator"};
        int columnsToPrint = Math.min(numColumns, allColumns.length);

        // Map columns to getters
        var getters = new java.util.HashMap<String, java.util.function.Function<LibraryItem, String>>();
        getters.put("Item ID", LibraryItem::getItemID);
        getters.put("Title", LibraryItem::getTitle);
        getters.put("Creator", LibraryItem::getCreator);

        // Select columns to print
        String[] columns = new String[columnsToPrint];
        System.arraycopy(allColumns, 0, columns, 0, columnsToPrint);

        // Calculate widths
        int[] colWidths = new int[columnsToPrint];
        for (int i = 0; i < columnsToPrint; i++) {
            colWidths[i] = columns[i].length();
        }
        for (LibraryItem item : items) {
            for (int i = 0; i < columnsToPrint; i++) {
                String val = getters.get(columns[i]).apply(item);
                if (val != null) {
                    colWidths[i] = Math.max(colWidths[i], val.length());
                }
            }
        }

        // Build border and row format
        StringBuilder border = new StringBuilder("+");
        StringBuilder rowFormat = new StringBuilder("|");
        for (int width : colWidths) {
            border.append("-".repeat(width + 2)).append("+");
            rowFormat.append(" %-").append(width).append("s |");
        }
        rowFormat.append("%n");

        // Print header
        System.out.println(border);
        System.out.printf(rowFormat.toString(), (Object[]) columns);
        System.out.println(border);

        // Print rows
        for (LibraryItem item : items) {
            Object[] rowValues = new Object[columnsToPrint];
            for (int i = 0; i < columnsToPrint; i++) {
                rowValues[i] = getters.get(columns[i]).apply(item);
            }
            System.out.printf(rowFormat.toString(), rowValues);
        }

        System.out.println(border + "\n");
    }


    /**
     * Displays detailed information about a library item.
     * @param item Item to display details for
     */
    public static void printItemDetails(LibraryItem item) {
        List<String[]> rows = new ArrayList<>();
        // Common fields for all items
        rows.add(new String[]{"Item ID", item.getItemID()});
        rows.add(new String[]{"Title", item.getTitle()});
        rows.add(new String[]{"Creator", item.getCreator()});

        // Type-specific fields
        switch (item) {
            case Book book ->
                    rows.add(new String[]{"Pages", String.valueOf(book.getNumberOfPages())});
            case Dvd dvd ->
                    rows.add(new String[]{"Duration", dvd.getRuntime() + " minutes"});
            case Magazine mag ->
                    rows.add(new String[]{"Issue Number", String.valueOf(mag.getIssueNumber())});
            case VideoGame game ->
                    rows.add(new String[]{"Platform", game.getPlatform()});
            default -> {} // Handle unknown types gracefully
        }

        // Calculate column widths
        int col1 = 0, col2 = 0;
        for (String[] row : rows) {
            col1 = Math.max(col1, row[0].length());
            col2 = Math.max(col2, row[1].length());
        }

        String border = "+" + "-".repeat(col1 + 2) + "+" + "-".repeat(col2 + 2) + "+";
        String fmt = "| %-" + col1 + "s | %-" + col2 + "s |%n";

        System.out.println(border);
        for (String[] row : rows) {
            System.out.printf(fmt, row[0], row[1]);
        }
        System.out.println(border);
    }

    /**
     * Generic numeric input validation method.
     * @param <T> Numeric type (Integer, Double, etc.)
     * @param scanner Scanner instance
     * @param prompt Input prompt
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @param allowEmpty Whether empty input is allowed
     * @param defaultValue Default value if empty input allowed
     * @param parser Function to parse input string
     * @return Validated numeric value
     */
    private static <T extends Number> T getNumericInput(Scanner scanner, String prompt, T min, T max,
                                                        boolean allowEmpty, T defaultValue,
                                                        Function<String, T> parser) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (allowEmpty && input.isEmpty()) {
                return defaultValue;
            }

            try {
                T value = parser.apply(input);
                // Use double comparison to handle different numeric types
                if (value.doubleValue() >= min.doubleValue() &&
                        value.doubleValue() <= max.doubleValue()) {
                    return value;
                }
                System.out.printf("Please enter a value between %s and %s\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Gets validated integer input with default parameters.
     * @param scanner Scanner instance
     * @param prompt Input prompt
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return Validated integer input
     */
    public static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        return getIntInput(scanner, prompt, min, max, false, min);
    }

    /**
     * Gets validated integer input with full parameters.
     * @param scanner Scanner instance
     * @param prompt Input prompt
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @param allowEmpty Whether empty input is allowed
     * @param defaultValue Default value if empty input allowed
     * @return Validated integer input
     */
    public static int getIntInput(Scanner scanner, String prompt, int min, int max,
                                  boolean allowEmpty, int defaultValue) {
        return getNumericInput(scanner, prompt, min, max, allowEmpty, defaultValue, Integer::parseInt);
    }

    /**
     * Pauses execution for specified number of seconds.
     * @param seconds Number of seconds to pause
     */
    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            // Restore interrupted status
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted while waiting.");
        }
    }
}