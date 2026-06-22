import java.io.*;
import java.util.*;

public class InventoryManager {
    // LinkedHashMap preserves the order in which items are added
    private final Map<String, InventoryItem> inventory = new LinkedHashMap<>();

    // Add a new item to inventory
    public boolean addItem(InventoryItem item) {
        if (item == null || item.getId().isEmpty()) {
            return false;
        }
        
        // Prevent duplicate IDs
        String idUpper = item.getId().toUpperCase();
        if (inventory.containsKey(idUpper)) {
            return false;
        }
        
        inventory.put(idUpper, item);
        return true;
    }

    // Get a specific item by ID
    public InventoryItem getItem(String id) {
        if (id == null) return null;
        return inventory.get(id.toUpperCase());
    }

    // Update quantity of an existing item
    public boolean updateQuantity(String id, int change) {
        InventoryItem item = getItem(id);
        if (item == null) {
            return false;
        }
        
        int newQuantity = item.getQuantity() + change;
        if (newQuantity < 0) {
            return false; // Cannot have negative stock
        }
        
        item.setQuantity(newQuantity);
        return true;
    }

    // Set absolute quantity of an existing item
    public boolean setQuantity(String id, int absoluteQty) {
        InventoryItem item = getItem(id);
        if (item == null || absoluteQty < 0) {
            return false;
        }
        item.setQuantity(absoluteQty);
        return true;
    }

    // Update price of an existing item
    public boolean updatePrice(String id, double newPrice) {
        InventoryItem item = getItem(id);
        if (item == null || newPrice < 0) {
            return false;
        }
        item.setPrice(newPrice);
        return true;
    }

    // Remove an item by ID
    public boolean removeItem(String id) {
        if (id == null) return false;
        return inventory.remove(id.toUpperCase()) != null;
    }

    // Get list of all items
    public Collection<InventoryItem> getAllItems() {
        return inventory.values();
    }

    // Search items by ID, Name, or Category (case insensitive)
    public List<InventoryItem> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(getAllItems());
        }
        
        String lowerQuery = query.toLowerCase().trim();
        List<InventoryItem> results = new ArrayList<>();
        
        for (InventoryItem item : inventory.values()) {
            if (item.getId().toLowerCase().contains(lowerQuery) ||
                item.getName().toLowerCase().contains(lowerQuery) ||
                item.getCategory().toLowerCase().contains(lowerQuery)) {
                results.add(item);
            }
        }
        
        return results;
    }

    // Get items that fall below or equal to a low stock threshold
    public List<InventoryItem> getLowStockItems(int threshold) {
        List<InventoryItem> lowStockList = new ArrayList<>();
        for (InventoryItem item : inventory.values()) {
            if (item.getQuantity() <= threshold) {
                lowStockList.add(item);
            }
        }
        return lowStockList;
    }

    // Save inventory data to CSV file
    public boolean saveToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (InventoryItem item : inventory.values()) {
                writer.println(item.toCsvLine());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving inventory to file: " + e.getMessage());
            return false;
        }
    }

    // Load inventory data from CSV file
    public boolean loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false; // No file to load, start with empty inventory
        }
        
        inventory.clear(); // Clear existing inventory before loading
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                InventoryItem item = InventoryItem.fromCsvLine(line);
                if (item != null) {
                    inventory.put(item.getId().toUpperCase(), item);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error reading inventory file: " + e.getMessage());
            return false;
        }
    }
}
