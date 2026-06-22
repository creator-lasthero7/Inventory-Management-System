import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String DATA_FILE = "inventory.csv";
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final InventoryManager manager = new InventoryManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Initializing Inventory Management System...   ");
        System.out.println("=================================================");

        // Load data from file if present
        if (manager.loadFromFile(DATA_FILE)) {
            System.out.println("Database loaded successfully from '" + DATA_FILE + "'.");
        } else {
            System.out.println("No existing database file found. Starting with a fresh inventory.");
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-8): ");
            String choice = scanner.nextLine().trim();

            System.out.println();
            switch (choice) {
                case "1":
                    viewAllProducts();
                    break;
                case "2":
                    addNewProduct();
                    break;
                case "3":
                    updateStockLevel();
                    break;
                case "4":
                    updateProductPrice();
                    break;
                case "5":
                    removeProduct();
                    break;
                case "6":
                    searchProducts();
                    break;
                case "7":
                    checkLowStockAlerts();
                    break;
                case "8":
                    exitAndSave();
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please enter a number between 1 and 8.");
            }
            if (running) {
                System.out.println("\nPress Enter to return to the Main Menu...");
                scanner.nextLine();
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=================================================");
        System.out.println("          INVENTORY MANAGEMENT SYSTEM            ");
        System.out.println("=================================================");
        System.out.println(" 1. View All Products");
        System.out.println(" 2. Add New Product");
        System.out.println(" 3. Update Stock Level (Restock / Sell)");
        System.out.println(" 4. Update Product Price");
        System.out.println(" 5. Remove Product");
        System.out.println(" 6. Search Products");
        System.out.println(" 7. Check Low Stock Alerts");
        System.out.println(" 8. Save & Exit");
        System.out.println("=================================================");
    }

    // 1. View all products in a structured table
    private static void viewAllProducts() {
        Collection<InventoryItem> items = manager.getAllItems();
        if (items.isEmpty()) {
            System.out.println("⚠️ The inventory is currently empty.");
            return;
        }
        printTable(items);
    }

    // Helper method to draw ASCII tables
    private static void printTable(Collection<InventoryItem> items) {
        System.out.println("+----------+----------------------+---------------+------------+----------+");
        System.out.printf("| %-8s | %-20s | %-13s | %-10s | %-8s |\n", "ID", "Name", "Category", "Price", "Stock");
        System.out.println("+----------+----------------------+---------------+------------+----------+");
        
        double totalValue = 0;
        int totalItems = 0;
        
        for (InventoryItem item : items) {
            System.out.printf("| %-8s | %-20s | %-13s | $%-9.2f | %-8d |\n",
                item.getId(),
                truncate(item.getName(), 20),
                truncate(item.getCategory(), 13),
                item.getPrice(),
                item.getQuantity()
            );
            totalValue += (item.getPrice() * item.getQuantity());
            totalItems += item.getQuantity();
        }
        System.out.println("+----------+----------------------+---------------+------------+----------+");
        System.out.printf("Total Unique Items: %-5d | Total Stock Units: %-5d | Inventory Value: $%,.2f\n", 
            items.size(), totalItems, totalValue);
    }

    private static String truncate(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }

    // 2. Add new product
    private static void addNewProduct() {
        System.out.println("--- Add New Product ---");
        
        System.out.print("Enter Product ID (e.g. P101): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("❌ Product ID cannot be empty.");
            return;
        }
        
        if (manager.getItem(id) != null) {
            System.out.println("❌ Error: A product with ID '" + id + "' already exists.");
            return;
        }

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Product Name cannot be empty.");
            return;
        }

        System.out.print("Enter Category: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            category = "General";
        }

        int quantity = readIntInput("Enter Initial Stock Quantity: ", 0, Integer.MAX_VALUE);
        double price = readDoubleInput("Enter Price ($): ", 0.0, Double.MAX_VALUE);

        InventoryItem newItem = new InventoryItem(id, name, quantity, price, category);
        if (manager.addItem(newItem)) {
            System.out.println("✅ Product '" + name + "' added successfully!");
        } else {
            System.out.println("❌ Failed to add product.");
        }
    }

    // 3. Update stock (Restock / Sell)
    private static void updateStockLevel() {
        System.out.println("--- Update Product Stock ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();
        
        InventoryItem item = manager.getItem(id);
        if (item == null) {
            System.out.println("❌ Product not found.");
            return;
        }
        
        System.out.println("Selected Product: " + item.getName() + " (Current Stock: " + item.getQuantity() + ")");
        System.out.println("1. Add Stock (Restock)");
        System.out.println("2. Remove Stock (Sell/Write-off)");
        System.out.print("Choose option (1-2): ");
        String option = scanner.nextLine().trim();
        
        if (option.equals("1")) {
            int addAmount = readIntInput("Enter quantity to ADD: ", 1, Integer.MAX_VALUE);
            if (manager.updateQuantity(id, addAmount)) {
                System.out.println("✅ Stock updated. New Quantity: " + item.getQuantity());
            } else {
                System.out.println("❌ Failed to update stock.");
            }
        } else if (option.equals("2")) {
            int removeAmount = readIntInput("Enter quantity to REMOVE: ", 1, item.getQuantity());
            if (manager.updateQuantity(id, -removeAmount)) {
                System.out.println("✅ Stock updated. New Quantity: " + item.getQuantity());
                if (item.getQuantity() <= LOW_STOCK_THRESHOLD) {
                    System.out.println("⚠️ Low stock warning! Stock is below threshold of " + LOW_STOCK_THRESHOLD);
                }
            } else {
                System.out.println("❌ Failed to update stock. Ensure you aren't removing more than is available.");
            }
        } else {
            System.out.println("❌ Invalid option. Cancelled.");
        }
    }

    // 4. Update Product Price
    private static void updateProductPrice() {
        System.out.println("--- Update Product Price ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();
        
        InventoryItem item = manager.getItem(id);
        if (item == null) {
            System.out.println("❌ Product not found.");
            return;
        }
        
        System.out.println("Selected Product: " + item.getName() + " (Current Price: $" + String.format("%.2f", item.getPrice()) + ")");
        double newPrice = readDoubleInput("Enter New Price ($): ", 0.0, Double.MAX_VALUE);
        
        if (manager.updatePrice(id, newPrice)) {
            System.out.println("✅ Price updated successfully to $" + String.format("%.2f", newPrice));
        } else {
            System.out.println("❌ Failed to update price.");
        }
    }

    // 5. Remove product
    private static void removeProduct() {
        System.out.println("--- Remove Product ---");
        System.out.print("Enter Product ID to delete: ");
        String id = scanner.nextLine().trim();
        
        InventoryItem item = manager.getItem(id);
        if (item == null) {
            System.out.println("❌ Product not found.");
            return;
        }
        
        System.out.print("Are you sure you want to permanently delete '" + item.getName() + "'? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            if (manager.removeItem(id)) {
                System.out.println("✅ Product removed successfully.");
            } else {
                System.out.println("❌ Failed to remove product.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // 6. Search products
    private static void searchProducts() {
        System.out.println("--- Search Inventory ---");
        System.out.print("Enter search keyword (ID, Name, or Category): ");
        String query = scanner.nextLine().trim();
        
        List<InventoryItem> results = manager.searchItems(query);
        if (results.isEmpty()) {
            System.out.println("ℹ️ No products matched your query '" + query + "'.");
        } else {
            System.out.println("\nSearch Results (" + results.size() + " matches found):");
            printTable(results);
        }
    }

    // 7. Check low stock alerts
    private static void checkLowStockAlerts() {
        System.out.println("--- Low Stock Alerts ---");
        List<InventoryItem> alerts = manager.getLowStockItems(LOW_STOCK_THRESHOLD);
        
        if (alerts.isEmpty()) {
            System.out.println("✅ All stock levels are healthy (Threshold: > " + LOW_STOCK_THRESHOLD + " units).");
        } else {
            System.out.println("⚠️ Warning: The following items are running low on stock (<= " + LOW_STOCK_THRESHOLD + " units):");
            printTable(alerts);
        }
    }

    // 8. Exit and Save Data
    private static void exitAndSave() {
        System.out.println("💾 Saving inventory database to '" + DATA_FILE + "'...");
        if (manager.saveToFile(DATA_FILE)) {
            System.out.println("✅ Saved successfully!");
        } else {
            System.out.println("❌ Error: Failed to save database. Progress may be lost.");
        }
        System.out.println("\nThank you for using Inventory Management System. Goodbye!");
    }

    // Helper: Read validated integer input
    private static int readIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("❌ Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid format. Please enter a valid whole number.");
            }
        }
    }

    // Helper: Read validated double input
    private static double readDoubleInput(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("❌ Please enter a decimal number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid format. Please enter a valid decimal number.");
            }
        }
    }
}
