public class InventoryItem {
    private String id;
    private String name;
    private int quantity;
    private double price;
    private String category;

    // Constructor
    public InventoryItem(String id, String name, int quantity, double price, String category) {
        this.id = id.trim();
        this.name = name.trim();
        this.quantity = quantity;
        this.price = price;
        this.category = category.trim();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category.trim();
    }

    // Convert item to a CSV format line
    public String toCsvLine() {
        // Replace commas in strings to prevent parsing issues
        String safeName = name.replace(",", ";");
        String safeCategory = category.replace(",", ";");
        return String.format("%s,%s,%d,%.2f,%s", id, safeName, quantity, price, safeCategory);
    }

    // Parse item from CSV format line
    public static InventoryItem fromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            return null; // Invalid line format
        }
        
        try {
            String id = parts[0];
            String name = parts[1];
            int quantity = Integer.parseInt(parts[2]);
            double price = Double.parseDouble(parts[3]);
            String category = parts[4];
            
            return new InventoryItem(id, name, quantity, price, category);
        } catch (NumberFormatException e) {
            return null; // Corrupted line format values
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Qty: %d | Price: $%.2f | Category: %s", 
            id, name, quantity, price, category);
    }
}
