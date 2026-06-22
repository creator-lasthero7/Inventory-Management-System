# Terminal-Based Inventory Management System

A simple, robust, terminal-based Inventory Management System built in Java. This project does not use any GUI, external databases, or HTML frameworks, running entirely in the command line interface (CLI).

It uses standard file I/O to read and write database records dynamically to a local `inventory.csv` file, serving as a lightweight persistence engine.

## Features

- **View Inventory**: Displays all items in a beautifully aligned ASCII console table, along with totals for unique items, total units, and overall valuation.
- **Add Products**: Prompts the user to register new items with custom IDs, names, categories, initial stock levels, and prices (prevents duplicates).
- **Update Stock Level**: Adjust stock levels up (restocking) or down (selling/writing-off) with active verification to prevent negative stock counts.
- **Update Pricing**: Modify the unit cost of existing items dynamically.
- **Remove Products**: Permanently delete products with safety confirmations.
- **Search Filters**: Match keyword substrings against IDs, names, or categories (case-insensitive).
- **Low Stock Alerts**: Displays alerts for items with quantity below a warning threshold of 5 units.
- **Auto-Persistence**: Loads database state on start and saves all data to a CSV file automatically upon exiting.

## File Structure

```text
inventory-system/
│
├── src/
│   ├── InventoryItem.java    # Product model class and CSV serialization
│   ├── InventoryManager.java # Data repository and logical managers
│   └── Main.java             # Console menu wrapper and Scanner inputs
│
└── README.md                 # Project details and guide
```

## How to Build & Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher installed.

### Compilation
Compile the source code from the root of the project directory:
```bash
javac src/*.java -d bin
```

### Execution
Run the compiled application:
```bash
java -cp bin Main
```
