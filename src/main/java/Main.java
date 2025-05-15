import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GenericCatalog<LibraryItem> catalog = new GenericCatalog<>();
        Scanner scanner = new Scanner(System.in);
        String[] mainMenu = {"Add Library Item", "Remove Library Item"};
        boolean runing = true;

        while (runing) {
            IOHelper.printMenu("Main Menu", mainMenu, true, "Exit");

            int choice = IOHelper.getIntInput(scanner, "Enter a choice: ", 1, mainMenu.length + 1, false, 3);

            switch (choice) {
                case 1:
                    System.out.println("Add Library Item");
                    break;
                case 2:
                    System.out.println("Remove Library Item");
                    break;
                default:
                    runing = false;
            }
        }
    }
}
