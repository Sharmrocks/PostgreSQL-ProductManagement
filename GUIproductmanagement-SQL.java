	//GUI 
	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.*;
	//SQL 
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	//To store dynamic list of product
	import java.util.ArrayList;
	
	
	public class GUIproductmanagement extends JFrame implements ActionListener {
	
	    // GUI components
	    private JRadioButton addProduct;
	    private JRadioButton viewInventory;
	    private JRadioButton deleteProduct;
	    private JRadioButton sellProduct;
	    private JRadioButton totalSales;
	    private JButton executeButton;
	    private JButton exitButton;
	    private JTextArea outputArea;
	    private ArrayList<Product> products;
	
	    public GUIproductmanagement() {
	        // Set up the GUI window
	        setTitle("Product Management");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(500, 500);
	        setLayout(new FlowLayout());
	
	        // radio buttons and other components
	        addProduct = new JRadioButton("Add Product");
	        viewInventory = new JRadioButton("View Inventory");
	        deleteProduct = new JRadioButton("Delete Product");
	        sellProduct = new JRadioButton("Sell Product");
	        totalSales = new JRadioButton("Total Sales");
	        
	        // select one at a time
	        ButtonGroup bg = new ButtonGroup();
	        bg.add(addProduct);
	        bg.add(viewInventory);
	        bg.add(deleteProduct);
	        bg.add(sellProduct);
	        bg.add(totalSales);
	
	        // EXECUTE/ENTER
	        executeButton = new JButton("Execute");
	        executeButton.addActionListener(this);
	
	        // EXIT
	        exitButton = new JButton("Exit");
	        exitButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                System.exit(0);
	            }
	        });
	        
	        // AREA FOR RESULTS	
	        outputArea = new JTextArea(20, 40);
	        outputArea.setEditable(false);
	        JScrollPane scrollPane = new JScrollPane(outputArea);
	
	        //COMPONENTS FOR FRAME
	        add(addProduct);
	        add(viewInventory);
	        add(deleteProduct);
	        add(sellProduct);
	        add(totalSales);
	        add(executeButton);
	        add(exitButton);
	        add(scrollPane);
	
	        products = new ArrayList<Product>();
	
	        // CENTER THE WINDOW ON THE SCREEN
	        setVisible(true);
	        setLocationRelativeTo(null);
	    }
	
	    public static void main(String[] args) {
	       
	    	// TO START THE APPLICATION
	    	new GUIproductmanagement();
	    }
	
	    public void actionPerformed(ActionEvent e) {
	    	// EXECUTION OF METHODS BASED ON THE SELECTED BUTTON
	        if (e.getSource() == executeButton) {
	            if (addProduct.isSelected()) {
	                addProduct();
	            } else if (viewInventory.isSelected()) {
	                viewInventory();
	            } else if (deleteProduct.isSelected()) {
	                deleteProduct();
	            } else if (sellProduct.isSelected()) {
	                sellProduct();
	            } else if (totalSales.isSelected()) {
	                totalSales();
	            }
	        }
	    }
	
	    // Method to connect to the PostgreSQL database
	    private Connection connect() throws SQLException {
	        String url = "jdbc:postgresql://localhost:5432/inventorydb"; 
	        String user = "postgres"; 
	        String password = "123123"; 
	        return DriverManager.getConnection(url, user, password);
	    }
	
	    // TO ADD THE PRODUCT
	    private void addProduct() {
	        JTextField nameField = new JTextField(20);
	        JTextField quantityField = new JTextField(10);
	        JTextField priceField = new JTextField(10);
	        JCheckBox addBox = new JCheckBox();
	
	        // TO COLLECT THE DETAILS 
	        JPanel myPanel = new JPanel();
	        myPanel.setLayout(new GridLayout(0, 1));
	        myPanel.add(new JLabel("Add to inventory:"));
	        myPanel.add(addBox);
	        myPanel.add(new JLabel("Product name:"));
	        myPanel.add(nameField);
	        myPanel.add(new JLabel("Quantity:"));
	        myPanel.add(quantityField);
	        myPanel.add(new JLabel("Price:"));
	        myPanel.add(priceField);
	
	        // TO GET THE USER INPUT
	        int result = JOptionPane.showConfirmDialog(null, myPanel,
	                "Please enter product details", JOptionPane.OK_CANCEL_OPTION);
	        if (result == JOptionPane.OK_OPTION) {
	        	// PROCESS THE INPUT
	            String name = nameField.getText().trim().toLowerCase();
	            String quantityString = quantityField.getText();
	            String priceString = priceField.getText();
	            if (name.isEmpty() || quantityString.isEmpty() || priceString.isEmpty()) {
	                JOptionPane.showMessageDialog(null, "Please fill out all fields.",
	                        "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }
	            int quantity = Integer.parseInt(quantityString);
	            double price = Double.parseDouble(priceString);
	
	            // UPDATE THE PRODUCT IN THE DATABASE
	            try (Connection conn = connect()) {
	                String query = "INSERT INTO products (product_name, quantity, price) VALUES (?, ?, ?) "
	                             + "ON CONFLICT (product_name) DO UPDATE SET quantity = products.quantity + EXCLUDED.quantity, "
	                             + "price = EXCLUDED.price";
	                try (PreparedStatement stmt = conn.prepareStatement(query)) {
	                    stmt.setString(1, name);
	                    stmt.setInt(2, quantity);
	                    stmt.setDouble(3, price);
	                    int rowsAffected = stmt.executeUpdate();
	                    if (rowsAffected > 0) {
	                        outputArea.append("Product added/updated: " + name + "\n");
	                    } else {
	                        outputArea.append("Failed to add/update product: " + name + "\n");
	                    }
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                JOptionPane.showMessageDialog(null, "Error interacting with the database.",
	                        "Database Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    }
	
	    // TO VIEW INVENTORY
	    private void viewInventory() {
	        outputArea.setText("");
	        String[] columnNames = {"Product Name", "Quantity", "Price", "Total Sales"};
	        try (Connection conn = connect()) {
	            String query = "SELECT product_name, quantity, price, total_sales FROM products";
	            try (PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {
	                ArrayList<Object[]> dataList = new ArrayList<>();
	                while (rs.next()) {
	                	// Add each product's details to the list
	                    dataList.add(new Object[] {
	                        rs.getString("product_name"),
	                        rs.getInt("quantity"),
	                        "₱" + String.format("%.2f", rs.getDouble("price")),
	                        "₱" + String.format("%.2f", rs.getDouble("total_sales"))
	                    });
	                }
	                // TO DISPLAY THE DATA IN TABLE FORMAT
	                Object[][] data = dataList.toArray(new Object[0][]);
	                JTable table = new JTable(data, columnNames);
	                table.getColumnModel().getColumn(0).setPreferredWidth(150);
	                JScrollPane scrollPane = new JScrollPane(table);
	                JOptionPane.showMessageDialog(null, scrollPane, "PRODUCT INVENTORY", JOptionPane.PLAIN_MESSAGE);
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error interacting with the database.",
	                    "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	
	    // TO DELETE PRODUCT
	    private void deleteProduct() {
	        JComboBox<String> productComboBox = new JComboBox<>(); // DROPDOWN
	        try (Connection conn = connect()) {
	            String query = "SELECT product_name FROM products"; // TO GET PRODUCT NAMES
	            try (PreparedStatement stmt = conn.prepareStatement(query); 
	                 ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    productComboBox.addItem(rs.getString("product_name")); // ADD PRODUCTS TO THE DROPDOWN
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error interacting with the database.",
	                    "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	
	        JPanel myPanel = new JPanel();
	        myPanel.setLayout(new GridLayout(0, 1));
	        myPanel.add(new JLabel("Select product to delete:"));
	        myPanel.add(productComboBox);
	
	        int result = JOptionPane.showConfirmDialog(null, myPanel,
	                "Select product to delete", JOptionPane.OK_CANCEL_OPTION);
	        if (result == JOptionPane.OK_OPTION) {
	            String selectedProduct = (String) productComboBox.getSelectedItem();
	            int confirmDelete = JOptionPane.showConfirmDialog(null,
	                "Are you sure you want to delete " + selectedProduct + "?",
	                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
	            if (confirmDelete == JOptionPane.YES_OPTION) {
	                try (Connection conn = connect()) {
	                    String query = "DELETE FROM products WHERE product_nam"
	                    		+ "e = ?"; // DELETE QUERY
	                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	                        stmt.setString(1, selectedProduct);
	                        int rowsAffected = stmt.executeUpdate();
	                        if (rowsAffected > 0) {
	                            outputArea.append("Product deleted: " + selectedProduct + "\n");
	                        } else {
	                            outputArea.append("Failed to delete product: " + selectedProduct + "\n");
	                        }
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Error interacting with the database.",
	                            "Database Error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        }
	    }
	    
	    // TO SELL PRODUCT
	    private void sellProduct() {
	        JComboBox<String> productComboBox = new JComboBox<>(); // DROPDOWN
	        try (Connection conn = connect()) {
	            String query = "SELECT product_name FROM products"; // Query to get product names
	            try (PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    productComboBox.addItem(rs.getString("product_name")); // Add products to the dropdown
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error interacting with the database.",
	                    "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	
	        // SELECT PRODUCT AND QUANTITY TO SELL
	        JTextField quantityField = new JTextField(10);
	        JCheckBox sellBox = new JCheckBox();
	
	        JPanel myPanel = new JPanel();
	        myPanel.setLayout(new GridLayout(0, 1));
	        myPanel.add(new JLabel("Sell from inventory:"));
	        myPanel.add(sellBox);
	        myPanel.add(new JLabel("Product name:"));
	        myPanel.add(productComboBox);
	        myPanel.add(new JLabel("Quantity:"));
	        myPanel.add(quantityField);
	
	        int result = JOptionPane.showConfirmDialog(null, myPanel,
	                "Please enter product details", JOptionPane.OK_CANCEL_OPTION);
	        if (result == JOptionPane.OK_OPTION) {
	            String productName = (String) productComboBox.getSelectedItem();
	            int quantity = 0;
	            try {
	                quantity = Integer.parseInt(quantityField.getText());
	            } catch (NumberFormatException ex) {
	                JOptionPane.showMessageDialog(null, "Invalid quantity entered.",
	                        "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }
	            
	            // PROCESS THE SALE IN THE DATA BASE
	            try (Connection conn = connect()) {
	                String query = "SELECT quantity, price FROM products WHERE product_name = ?";
	                try (PreparedStatement stmt = conn.prepareStatement(query)) {
	                    stmt.setString(1, productName);
	                    try (ResultSet rs = stmt.executeQuery()) {
	                        if (rs.next()) {
	                            int availableQuantity = rs.getInt("quantity");
	                            double price = rs.getDouble("price");
	                            if (availableQuantity >= quantity) {
	                                if (sellBox.isSelected()) {
	                                    query = "UPDATE products SET quantity = quantity - ?, total_sales = total_sales + ? WHERE product_name = ?";
	                                    try (PreparedStatement updateStmt = conn.prepareStatement(query)) {
	                                        updateStmt.setInt(1, quantity);
	                                        updateStmt.setDouble(2, quantity * price);
	                                        updateStmt.setString(3, productName);
	                                        int rowsAffected = updateStmt.executeUpdate();
	                                        if (rowsAffected > 0) {
	                                            outputArea.append("Product sold: " + productName + " | Quantity: " + quantity + "\n");
	                                        } else {
	                                            outputArea.append("Failed to sell product: " + productName + "\n");
	                                        }
	                                    }
	                                } else {
	                                    JOptionPane.showMessageDialog(null, "Please check the 'Sell' box to confirm the sale.",
	                                            "Error", JOptionPane.ERROR_MESSAGE);
	                                }
	                            } else {
	                                JOptionPane.showMessageDialog(null, "Not enough stock available.",
	                                        "Error", JOptionPane.ERROR_MESSAGE);
	                            }
	                        } else {
	                            JOptionPane.showMessageDialog(null, "Product not found.", 
	                                    "Error", JOptionPane.ERROR_MESSAGE);
	                        }
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Error interacting with the database.", // IF IT HAS AN ERROR CONNECTING
	                            "Database Error", JOptionPane.ERROR_MESSAGE); 
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                JOptionPane.showMessageDialog(null, "Error interacting with the database.", // IF IT HAS AN ERROR CONNECTING
	                        "Database Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    }
	    
	    
	
	    // TO SEE TOTAL SALES
	    private void totalSales() {
	        try (Connection conn = connect()) {
	            String query = "SELECT SUM(total_sales) FROM products"; // TOTAL SALES COLUMN
	            try (PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    double totalSales = rs.getDouble(1);
	                    JOptionPane.showMessageDialog(null, "Total sales amount: ₱" + String.format("%.2f", totalSales),
	                            "Total Sales", JOptionPane.INFORMATION_MESSAGE);
	                } else {
	                    JOptionPane.showMessageDialog(null, "No sales data found.",
	                            "Total Sales", JOptionPane.INFORMATION_MESSAGE);
	                }
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error interacting with the database.",
	                    "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	
	    // REPRESENTS A PRODUCT IN THE INVENTORY
	    private class Product {
	    	
	    	// TO STORE 
	        private String name;
	        private int quantity;
	        private double price;
	        private double sales;
	
	        // CONSTRUCTOR TO INITIALIZE A NEW PRODUCT
	        public Product(String name, int quantity, double price) {
	            this.name = name;
	            this.quantity = quantity;
	            this.price = price;
	            this.sales = 0; // SET TO 0 
	        }
	
	        // GETTER METHOD FOR THE PRODUCT NAME 
	        public String getName() {
	            return name;
	        }
	        
	        // GETTER METHOD FOR THE QUANTITY
	        public int getQuantity() {
	            return quantity;
	        }
	        // GETTER METHOD FOR THE PRICE
	        public double getPrice() {
	            return price;
	        }
	        // GETTER METHOD TO UPDATE QUANTITY
	        public void setQuantity(int quantity) {
	            this.quantity = quantity;
	        }
	        // GETTER METHOD TO SELL THE PRODUCT
	        public void sell(int quantity) {
	            this.quantity -= quantity; // REDUCE THE STOCK BY THE AMOUNT SOLD
	            this.sales += quantity * price; // INCREASE THE TOTAL SALES
	        }
	        // GETTER METHOD TO SEE SALES
	        public double getSales() {
	            return sales;
	        }
	    }
	}