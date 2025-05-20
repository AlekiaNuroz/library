import java.util.*;
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
    public static void printCatalog(List<LibraryItem> items, Scanner scanner) {
        printCatalog(items, items.size(), scanner);  // Default: print all columns
    }

    public static void printCatalog(List<LibraryItem> items, int numColumns, Scanner scanner) {
        IOHelper.clearScreen();

        // Define base columns
        String[] baseColumns = {"Item ID", "Title", "Creator"};
        Map<String, Function<LibraryItem, String>> getters = Map.of(
                "Item ID", LibraryItem::getItemID,
                "Title", LibraryItem::getTitle,
                "Creator", LibraryItem::getCreator
        );

        // Determine total columns (up to 3 base + 1 unique field)
        int columnsToPrint = Math.min(numColumns, baseColumns.length + 1);
        List<String> columns = new ArrayList<>(List.of(baseColumns).subList(0, Math.min(columnsToPrint, baseColumns.length)));

        boolean includeUnique = columnsToPrint > baseColumns.length;
        if (includeUnique) {
            columns.add("Details");
        }

        // Calculate max widths
        int[] colWidths = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            colWidths[i] = columns.get(i).length();
        }

        for (LibraryItem item : items) {
            for (int i = 0; i < columns.size(); i++) {
                String value = (i < baseColumns.length)
                        ? Optional.ofNullable(getters.get(columns.get(i)).apply(item)).orElse("")
                        : getUniqueField(item);
                colWidths[i] = Math.max(colWidths[i], value.length());
            }
        }

        // Construct border and row format
        StringBuilder border = new StringBuilder("+");
        StringBuilder rowFormat = new StringBuilder("|");
        for (int width : colWidths) {
            border.append("-".repeat(width + 2)).append("+");
            rowFormat.append(" %-").append(width).append("s |");
        }
        rowFormat.append("%n");

        String borderLine = border.toString();
        String rowFormatStr = rowFormat.toString();

        // Print header
        printHeader(borderLine, rowFormatStr, columns.toArray(new String[0]));

        // Print rows with pagination
        int count = 0;
        for (LibraryItem item : items) {
            Object[] rowValues = new Object[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                rowValues[i] = (i < baseColumns.length)
                        ? Optional.ofNullable(getters.get(columns.get(i)).apply(item)).orElse("")
                        : getUniqueField(item);
            }
            System.out.printf(rowFormatStr, rowValues);
            count++;

            if (count % 20 == 0 && count < items.size()) {
                System.out.println(borderLine);
                IOHelper.getStringInput(scanner, "Press ENTER to continue...", true);
                IOHelper.clearScreen();
                printHeader(borderLine, rowFormatStr, columns.toArray(new String[0]));
            }
        }

        System.out.println(borderLine + "\n");
    }

    // Helper to print the header row
    private static void printHeader(String border, String rowFormat, String[] columns) {
        System.out.println(border);
        System.out.printf(rowFormat, (Object[]) columns);
        System.out.println(border);
    }

    // Helper to get unique field string based on media type
    private static String getUniqueField(LibraryItem item) {
        return switch (item) {
            case Book book -> "Pages: " + book.getNumberOfPages();
            case Dvd dvd -> "Runtime: " + dvd.getRuntime() + " mins";
            case Magazine mag -> "Issue: " + mag.getIssueNumber();
            case VideoGame game -> "Platform: " + game.getPlatform();
            case null, default -> "";
        };
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