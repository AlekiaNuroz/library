import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Main application class for managing a library item catalog through a console interface.
 * Provides options to add, update, remove, and view items, with all changes persisted via a DatabaseManager.
 */
public class Main {
    /**
     * Entry point of the application. Initializes database connection and catalog,
     * then displays an interactive menu for catalog management.
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            DatabaseManager db = new DatabaseManager();
            GenericCatalog<LibraryItem> catalog = new GenericCatalog<>(db);
            String[] mainMenu = {"Add Library Item", "Update Library Item", "Remove Library Item", "List Library Items" };
            boolean running = true;

            // Register hook to ensure catalog state is saved on shutdown
            db.registerShutdownHook(catalog);

            while (running) {
                IOHelper.printMenu("Main Menu", mainMenu, true, "Exit");

                int choice = IOHelper.getIntInput(scanner, "Enter a choice: ", 1, mainMenu.length + 1, false, 3);

                switch (choice) {
                    case 1 -> addLibraryItem(scanner, catalog);
                    case 2 -> updateLibraryItem(scanner, catalog);
                    case 3 -> removeLibraryItem(scanner, catalog);
                    case 4 -> {
                        IOHelper.printCatalog(catalog.getAllItems(), scanner);
                        IOHelper.getStringInput(scanner, "Press ENTER to continue...", true);
                    }
                    default -> running = false; // Exit the loop
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            // Optional: log error or print message
            System.exit(0);
        }
    }


    /**
     * Handles removal of a library item from the catalog.
     * @param scanner Input scanner for user interaction
     * @param catalog Catalog from which to remove items
     */
    private static void removeLibraryItem(Scanner scanner, GenericCatalog<LibraryItem> catalog) {
        if (handleEmptyCatalog(catalog)) return;

        List<LibraryItem> items = catalog.getAllItems();

        IOHelper.printCatalog(items, 2, scanner);

        String itemId = IOHelper.getStringInput(scanner, "Enter an item ID to remove (or type 'None' to cancel): ", false);
        if ("None".equalsIgnoreCase(itemId.trim())) {
            return;
        }
        String confirm = IOHelper.getStringInput(scanner, "Are you sure you want to delete this item? (yes/no): ", false);
        if (confirm.trim().equalsIgnoreCase("no") || confirm.trim().equalsIgnoreCase("n")) {
            System.out.println("Deletion canceled.");
            IOHelper.wait(2);
            return;
        }

        if (catalog.removeItem(itemId)) {
            System.out.println("Item removed successfully.");
        } else {
            System.out.println("No item with the given ID was found.");
        }

        IOHelper.wait(2);
    }

    /**
     * Clears the screen, checks if the catalog is empty, and prints a message if so.
     *
     * @param catalog the catalog to check
     * @return true if the catalog is empty and the method handled the output, false otherwise
     */
    private static boolean handleEmptyCatalog(GenericCatalog<LibraryItem> catalog) {
        IOHelper.clearScreen();
        List<LibraryItem> items = catalog.getAllItems();
        if (items.isEmpty()) {
            System.out.println("There are no items in your catalog.");
            IOHelper.wait(2);
            return true;
        }
        return false;
    }


    /**
     * Handles updating of existing library item details.
     * @param scanner Input scanner for user interaction
     * @param catalog Catalog containing items to update
     */
    private static void updateLibraryItem(Scanner scanner, GenericCatalog<LibraryItem> catalog) {
        IOHelper.clearScreen();

        List<LibraryItem> items = catalog.getAllItems();
        if (items.isEmpty()) {
            System.out.println("There are no items in your catalog.");
            IOHelper.wait(2);
            return;
        }

        IOHelper.printCatalog(items, 2, scanner);

        String itemId = IOHelper.getStringInput(scanner, "Enter item ID to update (or type 'None' to cancel): ", false);
        if ("None".equalsIgnoreCase(itemId.trim())) {
            return;
        }

        LibraryItem item = catalog.getItemById(itemId);
        if (item == null) {
            System.out.println("Item not found.");
            IOHelper.wait(2);
            return;
        }

        IOHelper.printItemDetails(item);

        String[] options = {"Title", "Creator", "Item Specific Field", "Save and Exit"};
        boolean changesMade = false;

        while (true) {
            IOHelper.printMenu("Select Detail to Update", options, true, "Cancel Without Saving");
            int choice = IOHelper.getIntInput(scanner, "Enter choice: ", 1, options.length);

            try {
                switch (choice) {
                    case 1 -> {
                        String newTitle = IOHelper.getStringInput(scanner, "Enter new title: ", false).trim();
                        item.setTitle(newTitle);
                        changesMade = true;
                        System.out.println("Title updated.");
                    }
                    case 2 -> {
                        String newCreator = IOHelper.getStringInput(scanner, "Enter new creator: ", false).trim();
                        item.setCreator(newCreator);
                        changesMade = true;
                        System.out.println("Creator updated.");
                    }
                    case 3 -> {
                        if (updateSpecificField(scanner, item)) {
                            changesMade = true;
                            System.out.println("Specific field updated.");
                        } else {
                            System.out.println("Specific field update canceled or failed.");
                        }
                    }
                    case 4 -> { // Save and exit
                        if (changesMade) {
                            catalog.updateItem(item);
                            System.out.println("Changes saved successfully.");
                        } else {
                            System.out.println("No changes made.");
                        }
                        IOHelper.wait(2);
                        return;
                    }
                    case 5 -> { // Cancel without saving
                        System.out.println("Update canceled. No changes were saved.");
                        IOHelper.wait(2);
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred during update: " + e.getMessage());
            }
        }
    }


    /**
     * Updates type-specific field based on the concrete item type.
     * @param scanner Input scanner for user interaction
     * @param item Item to update, must be one of the supported subclasses
     */
    private static boolean updateSpecificField(Scanner scanner, LibraryItem item) {
        switch (item) {
            case Book book -> {
                int pages = IOHelper.getIntInput(scanner, "Enter new page count: ", 1, 10000);
                book.setPages(pages);
                return true;
            }
            case Dvd dvd -> {
                int duration = IOHelper.getIntInput(scanner, "Enter new duration (minutes): ", 1, 600);
                dvd.setDuration(duration);
                return true;
            }
            case Magazine magazine -> {
                int issueNumber = IOHelper.getIntInput(scanner, "Enter new issue number: ", 1, 500);
                magazine.setIssueNumber(issueNumber);
                return true;
            }
            case VideoGame game -> {
                String platform = IOHelper.getStringInput(scanner, "Enter new platform: ", false).trim();
                game.setPlatform(platform);
                return true;
            }
            case null, default -> {
                System.out.println("Unknown item type. Cannot update specific field.");
                return false;
            }
        }
    }

    /**
     * Handles creation and addition of new library items to the catalog.
     * @param scanner Input scanner for user interaction
     * @param catalog Catalog to which new items will be added
     */
    private static void addLibraryItem(Scanner scanner, GenericCatalog<LibraryItem> catalog) {
        String type = selectItemType(scanner);
        if ("Back".equalsIgnoreCase(type.trim())) {
            return;
        }

        String title = promptForTitle(scanner);
        String creator = promptForCreator(scanner);

        LibraryItem item = createLibraryItem(type, title, creator, scanner);
        if (item == null) {
            System.out.println("Invalid item type selected. Operation cancelled.");
            IOHelper.wait(2);
            return;
        }

        System.out.println("Adding a new " + type + "...");
        catalog.addItem(item);
        System.out.println("Item added successfully.");
        IOHelper.wait(2);
    }

    /**
     * Displays item type selection menu and returns user's choice.
     * @param scanner Input scanner for user interaction
     * @return Selected item type as String, or "Back" for cancellation
     */
    private static String selectItemType(Scanner scanner) {
        String[] selections = {"Book", "DVD", "Magazine", "Video Game"};
        IOHelper.printMenu("Select Item Type:", selections, true, "Back");
        int choice = IOHelper.getIntInput(scanner, "Enter a selection: ", 1, selections.length + 1, false, 1);

        if (choice >= 1 && choice <= selections.length) {
            return selections[choice - 1];
        } else {
            return "Back";  // Handles exit or invalid choices gracefully
        }
    }

    /**
     * Prompts the user to enter a title for the library item.
     *
     * @param scanner the Scanner used for user input
     * @return the trimmed title entered by the user
     */
    private static String promptForTitle(Scanner scanner) {
        return IOHelper.getStringInput(scanner, "Enter title: ", false).trim();
    }

    /**
     * Prompts the user to enter the creator of the library item (e.g., author, director).
     *
     * @param scanner the Scanner used for user input
     * @return the trimmed creator name entered by the user
     */
    private static String promptForCreator(Scanner scanner) {
        return IOHelper.getStringInput(scanner, "Enter creator (Author/Director/Editor/Publisher): ", false).trim();
    }

    /**
     * Creates a specific subclass of LibraryItem based on the provided type.
     * Prompts for additional input specific to the item type.
     *
     * @param type    the type of library item to create (e.g., "Book", "DVD")
     * @param title   the title of the item
     * @param creator the creator (author, director, etc.) of the item
     * @param scanner the Scanner used for user input
     * @return a newly created LibraryItem instance, or {@code null} if the type is invalid
     */
    private static LibraryItem createLibraryItem(String type, String title, String creator, Scanner scanner) {
        return switch (type) {
            case "Book" -> new Book(title, creator, IOHelper.getIntInput(scanner, "Enter number of pages: ", 1, 10000));
            case "DVD" -> new Dvd(title, creator, IOHelper.getIntInput(scanner, "Enter duration (minutes): ", 1, 600));
            case "Magazine" -> new Magazine(title, creator, IOHelper.getIntInput(scanner, "Enter issue number: ", 1, 500));
            case "Video Game" -> new VideoGame(title, creator, IOHelper.getStringInput(scanner, "Enter platform: ", false).trim());
            default -> null;
        };
    }

}