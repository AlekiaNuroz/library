import java.util.Scanner;

public class Main {
    public static void main() {
        DatabaseManager db = new DatabaseManager();
        GenericCatalog<LibraryItem> catalog = new GenericCatalog<>(db);
        Scanner scanner = new Scanner(System.in);
        String[] mainMenu = {"Add Library Item", "Update Library Item", "Remove Library Item"};
        boolean runing = true;

        db.registerShutdownHook(catalog);

        while (runing) {
            IOHelper.printMenu("Main Menu", mainMenu, true, "Exit");

            int choice = IOHelper.getIntInput(scanner, "Enter a choice: ", 1, mainMenu.length + 1, false, 3);

            switch (choice) {
                case 1 -> addLibraryItem(scanner, catalog);
                case 2 -> updateLibraryItem(scanner, catalog);
                case 3 -> removeLibraryItem(scanner, catalog);
                default -> runing = false;
            }
        }
    }

    private static void removeLibraryItem(Scanner scanner, GenericCatalog<LibraryItem> catalog) {
        IOHelper.clearScreen();
        IOHelper.printCatalog(catalog.getAllItems());

        String itemId = IOHelper.getStringInput(scanner, "Enter an item ID to remove: ", false);
        catalog.removeItem(itemId);
        IOHelper.wait(2);
    }

    private static void updateLibraryItem(Scanner scanner, GenericCatalog<LibraryItem> catalog) {
        IOHelper.clearScreen();
        IOHelper.printCatalog(catalog.getAllItems());

        String itemId = IOHelper.getStringInput(scanner, "Enter item ID to update: ", false);
        LibraryItem item = catalog.getItemById(itemId);

        if (item == null) {
            System.out.println("Item not found.");
            IOHelper.wait(2);
            return;
        }

        IOHelper.printItemDetails(item);

        IOHelper.printMenu("Select Detail to Update", new String[]{"Title", "Creator", "Item Specific Field"}, true, "Cancel");
        int choice = IOHelper.getIntInput(scanner, "Enter choice: ", 1, 4);

        switch (choice) {
            case 1 -> item.setTitle(IOHelper.getStringInput(scanner, "Enter new title: ", false));
            case 2 -> item.setCreator(IOHelper.getStringInput(scanner, "Enter new creator: ", false));
            case 3 -> updateSpecificField(scanner, item);
            case 4 -> {
                System.out.println("Update canceled.");
                return;
            }
        }

        // Save updates
        catalog.updateItem(item);
        IOHelper.wait(2);
    }

    private static void updateSpecificField(Scanner scanner, LibraryItem item) {
        if (item instanceof Book book) {
            book.setPages(IOHelper.getIntInput(scanner, "Enter new page count: ", 1, 10000));
        } else if (item instanceof Dvd dvd) {
            dvd.setDuration(IOHelper.getIntInput(scanner, "Enter new duration (minutes): ", 1, 600));
        } else if (item instanceof Magazine magazine) {
            magazine.setIssueNumber(IOHelper.getIntInput(scanner, "Enter new issue number: ", 1, 500));
        } else if (item instanceof VideoGame game) {
            game.setPlatform(IOHelper.getStringInput(scanner, "Enter new platform: ", false));
        }
    }


    private static void addLibraryItem(Scanner scanner, GenericCatalog<LibraryItem> catalog) {
        String type = selectItemType(scanner);
        if (type.equals("Back")) return;

        String title = IOHelper.getStringInput(scanner, "Enter title: ", false);
        String creator = IOHelper.getStringInput(scanner, "Enter creator (Author/Director/Editor/Publisher): ", false);

        LibraryItem item = switch (type) {
            case "Book" -> new Book(title, creator, IOHelper.getIntInput(scanner, "Enter number of pages: ", 1, 10000));
            case "DVD" -> new Dvd(title, creator, IOHelper.getIntInput(scanner, "Enter duration (minutes): ", 1, 600));
            case "Magazine" -> new Magazine(title, creator, IOHelper.getIntInput(scanner, "Enter issue number: ", 1, 500));
            case "Video Game" -> new VideoGame(title, creator, IOHelper.getStringInput(scanner, "Enter platform: ", false));
            default -> null;
        };

        System.out.println("Adding a new " + type + "...");

        if (item != null) {
            catalog.addItem(item);
            IOHelper.wait(2);
        }
    }

    private static String selectItemType(Scanner scanner) {
        String[] selections = {"Book", "DVD", "Magazine", "Video Game"};
        IOHelper.printMenu("Select Item Type: ", selections, true, "Back");
        int choice = IOHelper.getIntInput(scanner, "Enter a selection", 1, selections.length + 1, false, 1);
        return switch (choice) {
            case 1 -> "Book";
            case 2 -> "DVD";
            case 3 -> "Magazine";
            case 4 -> "Video Game";
            default -> "Back";
        };
    }
}
