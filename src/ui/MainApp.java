package ui;

import logic.CategoryManager;
import logic.ExpenseManager;
import model.Category;
import model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import javax.imageio.ImageIO;

/**
 * Main application class that implements the Swing UI for the Expense Tracker application.
 */
public class MainApp extends JFrame {

    // UI Components
    private JComboBox<String> tableComboBox;
    private JComboBox<String> sortByComboBox;
    private JButton sortButton;
    private JButton editButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton reportButton;
    
    // Expense filter checkboxes
    private JCheckBox recurringCheckBox;
    private JCheckBox nonRecurringCheckBox;
    private JCheckBox cashPaymentCheckBox;
    private JCheckBox digitalPaymentCheckBox;
    private JCheckBox bankTransferCheckBox;
    
    // Category filter checkboxes
    private JCheckBox highPriorityCheckBox;
    private JCheckBox mediumPriorityCheckBox;
    private JCheckBox lowPriorityCheckBox;
    private JCheckBox activeCheckBox;
    private JCheckBox inactiveCheckBox;
    
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JTextArea summaryTextArea;
    
    // Managers
    private ExpenseManager expenseManager;
    private CategoryManager categoryManager;
    
    // Current state
    private String selectedTable;
    private String selectedSortColumn;
    
    // New summary panel
    private JPanel mainSummaryPanel;
    
    /**
     * Constructor that initializes the application.
     */
    public MainApp() {
        // Initialize managers
        expenseManager = new ExpenseManager();
        categoryManager = new CategoryManager();
        
        // Set up the JFrame
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Increased window size
        setLocationRelativeTo(null);
        
        // Set custom application icon
        try {
            // Look for the wallet icon in the resources folder
            File iconFile = new File("resources/icon.png");
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
                setIconImage(icon.getImage());
                System.out.println("Using wallet icon from: " + iconFile.getAbsolutePath());
            } else {
                // Try alternate locations
                File altIconFile = new File("icon.png");
                if (altIconFile.exists()) {
                    ImageIcon icon = new ImageIcon(altIconFile.getAbsolutePath());
                    setIconImage(icon.getImage());
                    System.out.println("Using wallet icon from: " + altIconFile.getAbsolutePath());
                } else {
                    System.err.println("Wallet icon not found in resources folder or root directory");
                    // Fallback to generated icon
                    File fallbackIconFile = new File("resources/expense_icon.png");
                    if (!fallbackIconFile.exists()) {
                        createApplicationIcon("resources/expense_icon.png", 64);
                    }
                    ImageIcon fallbackIcon = new ImageIcon(fallbackIconFile.getAbsolutePath());
                    setIconImage(fallbackIcon.getImage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting application icon: " + e.getMessage());
        }
        
        // Set custom look and feel
        setupCustomLookAndFeel();
        
        // Initialize UI components
        initComponents();
        
        // Layout the UI
        layoutUI();
        
        // Add event listeners
        setupEventListeners();
        
        // Fetch data automatically when the application starts
        fetchData();
    }
    
    /**
     * Set up custom look and feel settings for the application.
     */
    private void setupCustomLookAndFeel() {
        try {
            // Use system look and feel for base styling
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Increase font size in the entire application
            setUIFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // Enhance JButton appearance
            UIManager.put("Button.background", new Color(240, 240, 240));
            UIManager.put("Button.foreground", new Color(50, 50, 50));
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.put("Button.margin", new Insets(8, 12, 8, 12));
            
            // Enhance JTable appearance
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Table.rowHeight", 25);
            UIManager.put("Table.showGrid", true);
            UIManager.put("Table.gridColor", new Color(220, 220, 220));
            UIManager.put("Table.selectionBackground", new Color(184, 207, 229));
            
            // Enhance JComboBox appearance
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
            
            // Enhance JCheckBox appearance
            UIManager.put("CheckBox.font", new Font("Segoe UI", Font.PLAIN, 14));
            
            // Enhance JTextArea appearance
            UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 14));
            
        } catch (Exception e) {
            System.err.println("Error setting up custom look and feel: " + e.getMessage());
        }
    }
    
    /**
     * Set the UI font for all Swing components.
     * 
     * @param font The font to use for all components
     */
    private void setUIFont(Font font) {
        // Create a dummy component to get the UI defaults
        JLabel label = new JLabel();
        
        // Get all the keys from the UIManager
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            
            // If the value is a font, update it
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
    
    /**
     * Initialize UI components.
     */
    private void initComponents() {
        // ComboBoxes
        String[] tables = {"Expenses", "Categories"};
        tableComboBox = new JComboBox<>(tables);
        
        // Initialize sortByComboBox
        sortByComboBox = new JComboBox<>();
        
        // Buttons
        sortButton = new JButton("Apply Sort");
        editButton = new JButton("Edit");
        addButton = new JButton("Add New");
        deleteButton = new JButton("Delete");
        reportButton = new JButton("Generate Report");
        
        // Category filters
        highPriorityCheckBox = new JCheckBox("High Priority");
        mediumPriorityCheckBox = new JCheckBox("Medium Priority");
        lowPriorityCheckBox = new JCheckBox("Low Priority");
        activeCheckBox = new JCheckBox("Active");
        inactiveCheckBox = new JCheckBox("Inactive");
        
        // Check boxes for filters
        recurringCheckBox = new JCheckBox("Recurring");
        nonRecurringCheckBox = new JCheckBox("Non-Recurring");
        cashPaymentCheckBox = new JCheckBox("Cash Payment");
        digitalPaymentCheckBox = new JCheckBox("Digital Payment");
        bankTransferCheckBox = new JCheckBox("Bank Transfer");
        
        // Data table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make cells non-editable directly
                return false;
            }
        };
        
        dataTable = new JTable(tableModel);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Allow only single selection
        dataTable.setRowSelectionAllowed(true);
        dataTable.setColumnSelectionAllowed(false);
        
        // Summary text area
        summaryTextArea = new JTextArea(5, 50);
        summaryTextArea.setEditable(false);
        summaryTextArea.setLineWrap(true);
        
        // Set initial state
        selectedTable = (String) tableComboBox.getSelectedItem();
        
        // Now that all components are initialized, populate the sort combo box
        updateSortByComboBox();
        
        // Get the initial sort column
        selectedSortColumn = (String) sortByComboBox.getSelectedItem();
    }
    
    /**
     * Layout the UI components.
     */
    private void layoutUI() {
        // Set background color for the frame
        this.getContentPane().setBackground(new Color(245, 245, 250));
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // Control panel for the top with 2 rows instead of 3
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 10));
        controlPanel.setBackground(new Color(245, 245, 250));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                new EmptyBorder(0, 0, 10, 0)));
        
        // First row: combine table selector, sort controls, and action buttons in one row
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlsPanel.setBackground(new Color(245, 245, 250));
        
        // Style the label and dropdown
        JLabel tableLabel = new JLabel("Table:");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controlsPanel.add(tableLabel);
        
        tableComboBox.setPreferredSize(new Dimension(120, 32));
        controlsPanel.add(tableComboBox);
        
        JLabel sortByLabel = new JLabel("Sort By:");
        sortByLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controlsPanel.add(sortByLabel);
        
        sortByComboBox.setPreferredSize(new Dimension(150, 32));
        controlsPanel.add(sortByComboBox);
        
        // Style the buttons
        styleButton(sortButton, new Color(100, 150, 220));
        styleButton(editButton, new Color(100, 150, 220));
        styleButton(addButton, new Color(76, 175, 80));
        styleButton(deleteButton, new Color(244, 67, 54));
        styleButton(reportButton, new Color(255, 152, 0));
        
        controlsPanel.add(sortButton);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(editButton);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(addButton);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(deleteButton);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(reportButton);
        
        // Second row: filters
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        optionsPanel.setBackground(new Color(245, 245, 250));
        
        JLabel filtersLabel = new JLabel("Filters:");
        filtersLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        optionsPanel.add(filtersLabel);
        
        // Style checkboxes
        styleCheckBox(recurringCheckBox);
        styleCheckBox(nonRecurringCheckBox);
        styleCheckBox(cashPaymentCheckBox);
        styleCheckBox(digitalPaymentCheckBox);
        styleCheckBox(bankTransferCheckBox);
        styleCheckBox(highPriorityCheckBox);
        styleCheckBox(mediumPriorityCheckBox);
        styleCheckBox(lowPriorityCheckBox);
        styleCheckBox(activeCheckBox);
        styleCheckBox(inactiveCheckBox);
        
        optionsPanel.add(recurringCheckBox);
        optionsPanel.add(nonRecurringCheckBox);
        optionsPanel.add(cashPaymentCheckBox);
        optionsPanel.add(digitalPaymentCheckBox);
        optionsPanel.add(bankTransferCheckBox);
        optionsPanel.add(highPriorityCheckBox);
        optionsPanel.add(mediumPriorityCheckBox);
        optionsPanel.add(lowPriorityCheckBox);
        optionsPanel.add(activeCheckBox);
        optionsPanel.add(inactiveCheckBox);
        
        // Add the two rows to the control panel
        controlPanel.add(controlsPanel);
        controlPanel.add(optionsPanel);
        
        // Data panel for the center with a border
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        
        // Customize the table
        dataTable.setIntercellSpacing(new Dimension(10, 5));
        dataTable.setRowHeight(30);
        dataTable.setShowGrid(true);
        dataTable.setGridColor(new Color(230, 230, 230));
        dataTable.getTableHeader().setBackground(new Color(240, 240, 245));
        dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        dataTable.setSelectionBackground(new Color(184, 207, 229));
        dataTable.setSelectionForeground(Color.BLACK);
        
        // Summary panel - Redesigned for better visualization without scrolling
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBackground(new Color(245, 245, 250));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(100, 150, 220), 1),
                        "Summary",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Create a panel with FlowLayout for the cards
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        cardsPanel.setBackground(new Color(245, 245, 250));
        
        // Create a panel for the summary text (optional - for additional details)
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(245, 245, 250));
        
        // Style the summary text area
        summaryTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        summaryTextArea.setBackground(new Color(250, 250, 255));
        summaryTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        summaryTextArea.setVisible(false); // Hide by default, show with a button if needed
        
        // Create a main summary display panel
        mainSummaryPanel = new JPanel();
        mainSummaryPanel.setLayout(new BoxLayout(mainSummaryPanel, BoxLayout.Y_AXIS));
        mainSummaryPanel.setBackground(new Color(245, 245, 250));
        
        // Add both panels to the summary panel
        summaryPanel.add(mainSummaryPanel, BorderLayout.CENTER);
        
        // Add all panels to the main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Set the main panel as the content pane
        setContentPane(mainPanel);
    }
    
    /**
     * Apply styling to a button.
     *
     * @param button The button to style
     * @param color The main color for the button
     */
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMargin(new Insets(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(lightenColor(color, 0.2f));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    /**
     * Apply styling to a checkbox.
     *
     * @param checkBox The checkbox to style
     */
    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        checkBox.setBackground(new Color(245, 245, 250));
        checkBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        checkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Lighten a color by a factor.
     *
     * @param color The color to lighten
     * @param factor The factor to lighten by (0.0 to 1.0)
     * @return The lightened color
     */
    private Color lightenColor(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Set up event listeners for UI components.
     */
    private void setupEventListeners() {
        // Table selection changes the available columns
        tableComboBox.addActionListener(e -> {
            selectedTable = (String) tableComboBox.getSelectedItem();
            System.out.println("Selected table: " + selectedTable);
            
            // Reset the filters when changing tables
            resetAllFilters();
            
            // Update sort options based on the new table
            updateSortByComboBox();
            
            // Fetch data for the selected table
            fetchData();
        });
        
        // Sort by selection changes the sort column but doesn't trigger sorting automatically
        sortByComboBox.addActionListener(e -> {
            selectedSortColumn = (String) sortByComboBox.getSelectedItem();
            System.out.println("Selected sort column: " + selectedSortColumn); // Debug message
        });
        
        // Sort button triggers the sorting operation
        sortButton.addActionListener(e -> {
            // Get the selected sort column directly from the combo box
            String column = (String) sortByComboBox.getSelectedItem();
            if (column == null) {
                System.out.println("No sort column selected");
                return;
            }
            
            System.out.println("Sort button clicked. Sorting by: " + column);
            selectedSortColumn = column; // Update the stored value
            
            // Perform the sorting
            sortData();
        });
        
        // Edit button opens a dialog to edit the selected item
        editButton.addActionListener(e -> {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if ("Expenses".equals(selectedTable)) {
                showEditExpenseDialog(selectedRow);
            } else {
                showEditCategoryDialog(selectedRow);
            }
        });
        
        // Add button opens a dialog to add a new expense or category
        addButton.addActionListener(e -> {
            if ("Expenses".equals(selectedTable)) {
                showAddExpenseDialog();
            } else {
                showAddCategoryDialog();
            }
        });
        
        // Delete button opens a dialog to delete an expense or category
        deleteButton.addActionListener(e -> {
            if ("Expenses".equals(selectedTable)) {
                showDeleteExpenseDialog();
            } else {
                showDeleteCategoryDialog();
            }
        });
        
        // Report button generates and saves a report
        reportButton.addActionListener(e -> generateReport());
        
        // Filter checkboxes for Expenses
        recurringCheckBox.addActionListener(e -> handleFilterChange(recurringCheckBox, "recurring"));
        nonRecurringCheckBox.addActionListener(e -> handleFilterChange(nonRecurringCheckBox, "non-recurring"));
        cashPaymentCheckBox.addActionListener(e -> handleFilterChange(cashPaymentCheckBox, "cash"));
        digitalPaymentCheckBox.addActionListener(e -> handleFilterChange(digitalPaymentCheckBox, "digital"));
        bankTransferCheckBox.addActionListener(e -> handleFilterChange(bankTransferCheckBox, "bank"));
        
        // Category filters
        highPriorityCheckBox.addActionListener(e -> handleCategoryFilterChange(highPriorityCheckBox, 'H'));
        mediumPriorityCheckBox.addActionListener(e -> handleCategoryFilterChange(mediumPriorityCheckBox, 'M'));
        lowPriorityCheckBox.addActionListener(e -> handleCategoryFilterChange(lowPriorityCheckBox, 'L'));
        activeCheckBox.addActionListener(e -> handleCategoryFilterChange(activeCheckBox, 'A'));
        inactiveCheckBox.addActionListener(e -> handleCategoryFilterChange(inactiveCheckBox, 'I'));
    }
    
    /**
     * Handle expense filter checkbox changes.
     * Ensures only one filter is active at a time and resets to all expenses when a filter is unchecked.
     */
    private void handleFilterChange(JCheckBox changedCheckBox, String filterType) {
        if (!"Expenses".equals(selectedTable)) return;
        
        // If the filter is being checked, uncheck all other filters
        if (changedCheckBox.isSelected()) {
            uncheckAllExpenseFiltersExcept(changedCheckBox);
            
            // Apply the selected filter
            switch (filterType) {
                case "recurring":
                    expenseManager.loadRecurringExpenses();
                    break;
                case "non-recurring":
                    expenseManager.loadNonRecurringExpenses();
                    break;
                case "cash":
                    expenseManager.loadExpensesByPaymentMode('C');
                    break;
                case "digital":
                    expenseManager.loadExpensesByPaymentMode('D');
                    break;
                case "bank":
                    expenseManager.loadExpensesByPaymentMode('B');
                    break;
            }
        } else {
            // If the filter is being unchecked, load all expenses
            expenseManager.loadData();
        }
        
        // Update display and sorting
        displayData();
        sortData();
    }
    
    /**
     * Uncheck all expense filter checkboxes except the specified one.
     */
    private void uncheckAllExpenseFiltersExcept(JCheckBox exceptCheckBox) {
        if (recurringCheckBox != exceptCheckBox) recurringCheckBox.setSelected(false);
        if (nonRecurringCheckBox != exceptCheckBox) nonRecurringCheckBox.setSelected(false);
        if (cashPaymentCheckBox != exceptCheckBox) cashPaymentCheckBox.setSelected(false);
        if (digitalPaymentCheckBox != exceptCheckBox) digitalPaymentCheckBox.setSelected(false);
        if (bankTransferCheckBox != exceptCheckBox) bankTransferCheckBox.setSelected(false);
    }
    
    /**
     * Reset all filter checkboxes to unchecked.
     */
    private void resetAllFilters() {
        // Reset expense filters
        recurringCheckBox.setSelected(false);
        nonRecurringCheckBox.setSelected(false);
        cashPaymentCheckBox.setSelected(false);
        digitalPaymentCheckBox.setSelected(false);
        bankTransferCheckBox.setSelected(false);
        
        // Reset category filters
        highPriorityCheckBox.setSelected(false);
        mediumPriorityCheckBox.setSelected(false);
        lowPriorityCheckBox.setSelected(false);
        activeCheckBox.setSelected(false);
        inactiveCheckBox.setSelected(false);
    }
    
    /**
     * Update the sort-by combo box based on the selected table.
     */
    private void updateSortByComboBox() {
        sortByComboBox.removeAllItems();
        
        // Add table-specific sort options
        if ("Expenses".equals(selectedTable)) {
            sortByComboBox.addItem("ID");
            sortByComboBox.addItem("Title");
            sortByComboBox.addItem("Amount");
            sortByComboBox.addItem("Payment Mode");
            sortByComboBox.addItem("Recurring");
        } else { // Categories
            sortByComboBox.addItem("ID");
            sortByComboBox.addItem("Name");
            sortByComboBox.addItem("Monthly Limit");
            sortByComboBox.addItem("Priority");
            sortByComboBox.addItem("Active");
        }
        
        // Set ID as the default sort column
        if (sortByComboBox.getItemCount() > 0) {
            sortByComboBox.setSelectedIndex(0); // Select ID
            selectedSortColumn = (String) sortByComboBox.getSelectedItem();
        }
        
        // Update the UI based on the selected filter options
        updateFilterVisibility();
    }
    
    /**
     * Update filter visibility based on the selected table.
     */
    private void updateFilterVisibility() {
        // Safety check to ensure all components are initialized
        if (recurringCheckBox == null || nonRecurringCheckBox == null || cashPaymentCheckBox == null 
            || digitalPaymentCheckBox == null || bankTransferCheckBox == null
            || highPriorityCheckBox == null || mediumPriorityCheckBox == null 
            || lowPriorityCheckBox == null || activeCheckBox == null || inactiveCheckBox == null) {
            return;
        }
        
        if ("Expenses".equals(selectedTable)) {
            // Show expense filters
            recurringCheckBox.setVisible(true);
            nonRecurringCheckBox.setVisible(true);
            cashPaymentCheckBox.setVisible(true);
            digitalPaymentCheckBox.setVisible(true);
            bankTransferCheckBox.setVisible(true);
            
            // Hide category filters
            highPriorityCheckBox.setVisible(false);
            mediumPriorityCheckBox.setVisible(false);
            lowPriorityCheckBox.setVisible(false);
            activeCheckBox.setVisible(false);
            inactiveCheckBox.setVisible(false);
        } else { // Categories
            // Hide expense filters
            recurringCheckBox.setVisible(false);
            nonRecurringCheckBox.setVisible(false);
            cashPaymentCheckBox.setVisible(false);
            digitalPaymentCheckBox.setVisible(false);
            bankTransferCheckBox.setVisible(false);
            
            // Show category filters
            highPriorityCheckBox.setVisible(true);
            mediumPriorityCheckBox.setVisible(true);
            lowPriorityCheckBox.setVisible(true);
            activeCheckBox.setVisible(true);
            inactiveCheckBox.setVisible(true);
        }
    }
    
    /**
     * Fetch data from the database based on the selected table and apply any filters.
     */
    private void fetchData() {
        try {
            // Always load categories first regardless of which table is selected
            categoryManager.loadData();
            
            if ("Expenses".equals(selectedTable)) {
                // Apply expense filters if any are active
                if (recurringCheckBox.isSelected()) {
                    expenseManager.loadRecurringExpenses();
                } else if (nonRecurringCheckBox.isSelected()) {
                    expenseManager.loadNonRecurringExpenses();
                } else if (cashPaymentCheckBox.isSelected()) {
                    expenseManager.loadExpensesByPaymentMode('C');
                } else if (digitalPaymentCheckBox.isSelected()) {
                    expenseManager.loadExpensesByPaymentMode('D');
                } else if (bankTransferCheckBox.isSelected()) {
                    expenseManager.loadExpensesByPaymentMode('B');
                } else {
                    expenseManager.loadData(); // No filter active, load all expenses
                }
            } else { // Categories
                // Apply category filters if any are active
                if (highPriorityCheckBox.isSelected()) {
                    loadCategoriesByPriority('H');
                } else if (mediumPriorityCheckBox.isSelected()) {
                    loadCategoriesByPriority('M');
                } else if (lowPriorityCheckBox.isSelected()) {
                    loadCategoriesByPriority('L');
                } else if (activeCheckBox.isSelected()) {
                    loadCategoriesByActiveStatus(true);
                } else if (inactiveCheckBox.isSelected()) {
                    loadCategoriesByActiveStatus(false);
                }
                // No else needed - categories are already loaded above
            }
            
            // Sort the data before displaying
            sortData();
            
            // Display data and update summary
            displayData();
            updateSummary();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error fetching data: " + ex.getMessage(), 
                    "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Display data in the table.
     */
    private void displayData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Set up columns based on the selected table
        if ("Expenses".equals(selectedTable)) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Amount", "Payment Mode", "Recurring", "Category"});
            
            // Display expenses using for-each loop
            for (Expense expense : expenseManager.getAll()) {
                addExpenseToTable(expense);
            }
            
        } else { // Categories
            tableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Monthly Limit", "Priority", "Active"});
            
            // Display categories using for-each loop
            for (Category category : categoryManager.getAll()) {
                addCategoryToTable(category);
            }
        }
    }
    
    /**
     * Add an expense to the table model.
     * 
     * @param expense The expense to add
     */
    private void addExpenseToTable(Expense expense) {
        // Get category name for the expense
        String categoryName = "Unknown";
        for (Category category : categoryManager.getAll()) {
            if (category.getId() == expense.getCategoryId()) {
                categoryName = category.getName();
                break;
            }
        }
        
        Object[] row = {
            expense.getId(),
            expense.getTitle(),
            String.format("₹%.2f", expense.getAmount()),
            expense.getModeAsString(),
            expense.isRecurring() ? "Yes" : "No",
            categoryName
        };
        
        tableModel.addRow(row);
    }
    
    /**
     * Add a category to the table model.
     * 
     * @param category The category to add
     */
    private void addCategoryToTable(Category category) {
        Object[] row = {
            category.getId(),
            category.getName(),
            String.format("₹%.2f", category.getMonthlyLimit()),
            category.getPriorityAsString(),
            category.isActive() ? "Yes" : "No"
        };
        
        tableModel.addRow(row);
    }
    
    /**
     * Sort data based on the selected column.
     */
    private void sortData() {
        System.out.println("Executing sortData() with column: " + selectedSortColumn);
        
        if ("Expenses".equals(selectedTable)) {
            Comparator<Expense> comparator = null;
            
            switch (selectedSortColumn) {
                case "ID":
                    comparator = Comparator.comparing(Expense::getId);
                    break;
                case "Title":
                    comparator = ExpenseManager.getTitleComparator();
                    break;
                case "Amount":
                    comparator = ExpenseManager.getAmountComparator(true);
                    break;
                case "Payment Mode":
                    comparator = ExpenseManager.getModeComparator();
                    break;
                case "Recurring":
                    comparator = ExpenseManager.getRecurringComparator();
                    break;
                default:
                    System.out.println("Warning: Unknown sort column for Expenses: " + selectedSortColumn);
                    comparator = Comparator.comparing(Expense::getId); // Default to ID
                    break;
            }
            
            try {
                List<Expense> sortedExpenses = expenseManager.sort(comparator);
                
                // Update the expense manager with the sorted list
                expenseManager.clear();
                for (Expense expense : sortedExpenses) {
                    expenseManager.add(expense);
                }
                
                // Refresh the display
                displayData();
                System.out.println("Expenses sorted successfully by: " + selectedSortColumn);
            } catch (Exception ex) {
                System.err.println("Error sorting expenses: " + ex.getMessage());
                ex.printStackTrace();
            }
            
        } else { // Categories
            Comparator<Category> comparator = null;
            
            switch (selectedSortColumn) {
                case "ID":
                    comparator = Comparator.comparing(Category::getId);
                    break;
                case "Name":
                    comparator = CategoryManager.getNameComparator();
                    break;
                case "Monthly Limit":
                    comparator = CategoryManager.getMonthlyLimitComparator(true);
                    break;
                case "Priority":
                    comparator = CategoryManager.getPriorityComparator();
                    break;
                case "Active":
                    comparator = CategoryManager.getActiveStatusComparator(true);
                    break;
                default:
                    System.out.println("Warning: Unknown sort column for Categories: " + selectedSortColumn);
                    comparator = Comparator.comparing(Category::getId); // Default to ID
                    break;
            }
            
            try {
                List<Category> sortedCategories = categoryManager.sort(comparator);
                
                // Update the category manager with the sorted list
                categoryManager.clear();
                for (Category category : sortedCategories) {
                    categoryManager.add(category);
                }
                
                // Refresh the display
                displayData();
                System.out.println("Categories sorted successfully by: " + selectedSortColumn);
            } catch (Exception ex) {
                System.err.println("Error sorting categories: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Update the summary panel with information about the data.
     */
    private void updateSummary() {
        // Clear the existing summary panel
        mainSummaryPanel.removeAll();
        
        // Create a container for cards with FlowLayout
        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        cardsContainer.setBackground(new Color(245, 245, 250));
        
        if ("Expenses".equals(selectedTable)) {
            float totalAmount = expenseManager.calculateTotalAmount();
            
            // Card 1: Total expenses count
            JPanel countCard = createSummaryCard("Total Expenses", 
                    String.format("%d", expenseManager.size()), 
                    new Color(100, 150, 220));
            cardsContainer.add(countCard);
            
            // Card 2: Total amount
            JPanel amountCard = createSummaryCard("Total Amount", 
                    String.format("₹%.2f", totalAmount), 
                    new Color(76, 175, 80));
            cardsContainer.add(amountCard);
            
            // Payment mode breakdown
            Map<Character, Float> modeAmounts = expenseManager.calculateTotalByMode();
            
            // Cash
            float cashAmount = modeAmounts.getOrDefault('C', 0f);
            if (cashAmount > 0) {
                JPanel cashCard = createSummaryCard("Cash", 
                        String.format("₹%.2f (%.1f%%)", cashAmount, (cashAmount / totalAmount) * 100),
                        new Color(255, 152, 0));
                cardsContainer.add(cashCard);
            }
            
            // Digital
            float digitalAmount = modeAmounts.getOrDefault('D', 0f);
            if (digitalAmount > 0) {
                JPanel digitalCard = createSummaryCard("Digital", 
                        String.format("₹%.2f (%.1f%%)", digitalAmount, (digitalAmount / totalAmount) * 100),
                        new Color(123, 104, 238));
                cardsContainer.add(digitalCard);
            }
            
            // Bank Transfer
            float bankAmount = modeAmounts.getOrDefault('B', 0f);
            if (bankAmount > 0) {
                JPanel bankCard = createSummaryCard("Bank Transfer", 
                        String.format("₹%.2f (%.1f%%)", bankAmount, (bankAmount / totalAmount) * 100),
                        new Color(0, 150, 136));
                cardsContainer.add(bankCard);
            }
            
        } else { // Categories
            // Card 1: Total categories count
            JPanel countCard = createSummaryCard("Total Categories", 
                    String.format("%d", categoryManager.size()), 
                    new Color(100, 150, 220));
            cardsContainer.add(countCard);
            
            // Count categories by priority
            int highPriority = 0;
            int mediumPriority = 0;
            int lowPriority = 0;
            
            int activeCount = 0;
            int inactiveCount = 0;
            
            for (Category category : categoryManager.getAll()) {
                // Count by priority
                switch (category.getPriority()) {
                    case 'H': highPriority++; break;
                    case 'M': mediumPriority++; break;
                    case 'L': lowPriority++; break;
                }
                
                // Count by active status
                if (category.isActive()) {
                    activeCount++;
                } else {
                    inactiveCount++;
                }
            }
            
            // Card 2: High Priority
            if (highPriority > 0) {
                JPanel highCard = createSummaryCard("High Priority", 
                        String.format("%d", highPriority), 
                        new Color(244, 67, 54));
                cardsContainer.add(highCard);
            }
            
            // Card 3: Medium Priority
            if (mediumPriority > 0) {
                JPanel mediumCard = createSummaryCard("Medium Priority", 
                        String.format("%d", mediumPriority), 
                        new Color(255, 152, 0));
                cardsContainer.add(mediumCard);
            }
            
            // Card 4: Low Priority
            if (lowPriority > 0) {
                JPanel lowCard = createSummaryCard("Low Priority", 
                        String.format("%d", lowPriority), 
                        new Color(76, 175, 80));
                cardsContainer.add(lowCard);
            }
            
            // Card 5: Active Status
            if (activeCount > 0) {
                JPanel activeCard = createSummaryCard("Active", 
                        String.format("%d", activeCount), 
                        new Color(33, 150, 243));
                cardsContainer.add(activeCard);
            }
            
            // Card 6: Inactive Status
            if (inactiveCount > 0) {
                JPanel inactiveCard = createSummaryCard("Inactive", 
                        String.format("%d", inactiveCount), 
                        new Color(158, 158, 158));
                cardsContainer.add(inactiveCard);
            }
        }
        
        // Add the cards container to the main summary panel
        mainSummaryPanel.add(cardsContainer);
        
        // Force a repaint of the UI
        mainSummaryPanel.revalidate();
        mainSummaryPanel.repaint();
    }
    
    /**
     * Create a styled card for the summary panel.
     *
     * @param title The card title
     * @param value The value to display
     * @param color The accent color for the card
     * @return A styled JPanel containing the card
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        // Create a panel with a border layout
        JPanel card = new JPanel(new BorderLayout(5, 5));
        
        // Adjust card width based on content type (wider for payment mode cards)
        int cardWidth = title.contains("Cash") || title.contains("Digital") || title.contains("Bank") ? 
                        220 : 180;
        
        card.setPreferredSize(new Dimension(cardWidth, 100));
        card.setBackground(Color.WHITE);
        
        // Add a border with the accent color
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Create a title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(60, 60, 60));
        
        // Create a value label with larger font
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        // Create a colored bar at the top
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(card.getWidth(), 5));
        
        // Add components to the card
        card.add(colorBar, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
        
        return card;
    }
    
    /**
     * Style a dialog to match the application's look and feel.
     *
     * @param dialog The dialog to style
     * @param title The dialog title
     */
    private void styleDialog(JDialog dialog, String title) {
        dialog.setTitle(title);
        dialog.getContentPane().setBackground(new Color(245, 245, 250));
        
        // Set dialog title with a custom font
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBackground(new Color(100, 150, 220));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        
        // Replace the title bar with our custom title
        dialog.setUndecorated(true);
        
        // Create a panel for the title and content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 220), 1));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Set the content panel as the content pane
        Container originalContentPane = dialog.getContentPane();
        dialog.setContentPane(contentPanel);
        
        // Add the original content back in the center
        contentPanel.add(originalContentPane, BorderLayout.CENTER);
        
        // Make the dialog draggable
        addDragCapability(dialog, titleLabel);
    }
    
    /**
     * Add drag capability to a dialog via its title label.
     *
     * @param dialog The dialog to make draggable
     * @param titleComponent The component (usually a label) to use as the drag handle
     */
    private void addDragCapability(JDialog dialog, JComponent titleComponent) {
        final Point[] dragPoint = {new Point(0, 0)};
        
        titleComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                dragPoint[0] = null;
            }
        });
        
        titleComponent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    Point currentLocation = dialog.getLocation();
                    dialog.setLocation(
                            currentLocation.x + e.getX() - dragPoint[0].x,
                            currentLocation.y + e.getY() - dragPoint[0].y
                    );
                }
            }
        });
    }
    
    /**
     * Style a form label.
     *
     * @param label The label to style
     */
    private void styleFormLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    /**
     * Style a text field.
     *
     * @param textField The text field to style
     */
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
    
    /**
     * Show a dialog to add a new expense.
     */
    private void showAddExpenseDialog() {
        // Create a dialog for adding a new expense
        JDialog dialog = new JDialog(this, "Add New Expense", true);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);
        
        // Create the main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel with a better layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create form fields
        JTextField titleField = new JTextField(20);
        styleTextField(titleField);
        
        JTextField amountField = new JTextField(10);
        styleTextField(amountField);
        
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"Cash", "Digital", "Bank Transfer"});
        modeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeComboBox.setPreferredSize(new Dimension(150, 32));
        
        JCheckBox recurringCheckBox = new JCheckBox();
        styleCheckBox(recurringCheckBox);
        
        // Category dropdown
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.setPreferredSize(new Dimension(150, 32));
        
        for (Category category : categoryManager.getAll()) {
            categoryComboBox.addItem(category.getName());
        }
        
        // Add components to the form panel
        JLabel titleLabel = new JLabel("Title:");
        styleFormLabel(titleLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(titleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(titleField, gbc);
        
        JLabel amountLabel = new JLabel("Amount:");
        styleFormLabel(amountLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(amountField, gbc);
        
        JLabel modeLabel = new JLabel("Payment Mode:");
        styleFormLabel(modeLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(modeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(modeComboBox, gbc);
        
        JLabel recurringLabel = new JLabel("Recurring:");
        styleFormLabel(recurringLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(recurringLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(recurringCheckBox, gbc);
        
        JLabel categoryLabel = new JLabel("Category:");
        styleFormLabel(categoryLabel);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(categoryComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 250));
        
        // Add Save button
        JButton saveButton = new JButton("Save");
        styleButton(saveButton, new Color(76, 175, 80));
        buttonPanel.add(saveButton);
        
        // Add Cancel button
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(150, 150, 150));
        buttonPanel.add(cancelButton);
        
        // Add panels to the main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set the main panel as the content pane
        dialog.setContentPane(mainPanel);
        
        // Apply custom styling to the dialog
        styleDialog(dialog, "Add New Expense");
        
        // Set up button actions
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    throw new IllegalArgumentException("Title cannot be empty");
                }
                
                float amount;
                try {
                    amount = Float.parseFloat(amountField.getText().trim());
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid amount format");
                }
                
                // Get payment mode
                char mode;
                String modeStr = (String) modeComboBox.getSelectedItem();
                switch (modeStr) {
                    case "Cash": mode = 'C'; break;
                    case "Digital": mode = 'D'; break;
                    case "Bank Transfer": mode = 'B'; break;
                    default: mode = 'C'; break;
                }
                
                // Get category ID
                int categoryId = -1;
                String categoryName = (String) categoryComboBox.getSelectedItem();
                for (Category category : categoryManager.getAll()) {
                    if (category.getName().equals(categoryName)) {
                        categoryId = category.getId();
                        break;
                    }
                }
                
                if (categoryId == -1) {
                    throw new IllegalArgumentException("Invalid category");
                }
                
                // Create and save the expense
                Expense expense = new Expense(title, amount, mode, recurringCheckBox.isSelected(), categoryId);
                boolean success = expenseManager.saveExpense(expense);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Expense saved successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    displayData();
                    updateSummary();
                } else {
                    throw new IllegalArgumentException("Failed to save expense");
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                        ex.getMessage(), 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /**
     * Show a dialog to add a new category.
     */
    private void showAddCategoryDialog() {
        // Create a dialog for adding a new category
        JDialog dialog = new JDialog(this, "Add New Category", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        
        // Create form fields
        JTextField nameField = new JTextField(20);
        JTextField limitField = new JTextField(10);
        
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        JCheckBox activeCheckBox = new JCheckBox();
        activeCheckBox.setSelected(true);
        
        // Add components to the dialog
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Monthly Limit:"));
        dialog.add(limitField);
        dialog.add(new JLabel("Priority:"));
        dialog.add(priorityComboBox);
        dialog.add(new JLabel("Active:"));
        dialog.add(activeCheckBox);
        
        // Add Save button
        JButton saveButton = new JButton("Save");
        dialog.add(saveButton);
        
        // Add Cancel button
        JButton cancelButton = new JButton("Cancel");
        dialog.add(cancelButton);
        
        // Set up button actions
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be empty");
                }
                
                float limit;
                try {
                    limit = Float.parseFloat(limitField.getText().trim());
                    if (limit <= 0) {
                        throw new IllegalArgumentException("Monthly limit must be positive");
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid limit format");
                }
                
                // Get priority
                char priority;
                String priorityStr = (String) priorityComboBox.getSelectedItem();
                switch (priorityStr) {
                    case "High": priority = 'H'; break;
                    case "Medium": priority = 'M'; break;
                    case "Low": priority = 'L'; break;
                    default: priority = 'M'; break;
                }
                
                // Create and save the category
                Category category = new Category(name, limit, priority, activeCheckBox.isSelected());
                boolean success = categoryManager.saveCategory(category);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Category saved successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    displayData();
                    updateSummary();
                } else {
                    throw new IllegalArgumentException("Failed to save category");
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                        ex.getMessage(), 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /**
     * Show a dialog to delete an expense.
     */
    private void showDeleteExpenseDialog() {
        // Check if we have any expenses to delete
        if (expenseManager.size() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "No expenses available to delete", 
                    "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the currently selected row
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an expense to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get expense ID from the selected row
        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        String expenseTitle = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Find the expense object
        Expense expenseToDelete = null;
        for (Expense expense : expenseManager.getAll()) {
            if (expense.getId() == expenseId) {
                expenseToDelete = expense;
                break;
            }
        }
        
        if (expenseToDelete == null) {
            JOptionPane.showMessageDialog(this, 
                    "Could not find the selected expense", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete expense '" + expenseTitle + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = expenseManager.deleteExpense(expenseToDelete);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Expense deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the display
                displayData();
                updateSummary();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to delete expense", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Show a dialog to delete a category.
     */
    private void showDeleteCategoryDialog() {
        // Check if we have any categories to delete
        if (categoryManager.size() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "No categories available to delete", 
                    "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the currently selected row
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a category to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get category ID from the selected row
        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        String categoryName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Find the category object
        Category categoryToDelete = null;
        for (Category category : categoryManager.getAll()) {
            if (category.getId() == categoryId) {
                categoryToDelete = category;
                break;
            }
        }
        
        if (categoryToDelete == null) {
            JOptionPane.showMessageDialog(this, 
                    "Could not find the selected category", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if the category has any expenses associated with it
        expenseManager.loadData(); // Make sure we have all expenses
        boolean hasExpenses = false;
        for (Expense expense : expenseManager.getAll()) {
            if (expense.getCategoryId() == categoryId) {
                hasExpenses = true;
                break;
            }
        }
        
        if (hasExpenses) {
            JOptionPane.showMessageDialog(this, 
                    "Cannot delete category '" + categoryName + "' because it has expenses associated with it.\n" +
                    "Delete all associated expenses first.", 
                    "Category In Use", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete category '" + categoryName + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryManager.deleteCategory(categoryToDelete);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Category deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the display
                displayData();
                updateSummary();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to delete category", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Generate and save an expense report.
     */
    private void generateReport() {
        // First, make sure we have data to report
        if ("Expenses".equals(selectedTable) && expenseManager.size() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "No expense data to generate a report", 
                    "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Load all data needed for the report
        expenseManager.loadData();
        categoryManager.loadData();
        
        // Let the user choose where to save the report
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Expense Report");
        fileChooser.setSelectedFile(new File("expense_report.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Generate the report
            boolean success = ReportGenerator.generateExpenseReport(
                    expenseManager.getAll(), 
                    categoryManager.getAll(), 
                    file.getAbsolutePath());
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Report generated successfully: " + file.getAbsolutePath(), 
                        "Report Generated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to generate report", 
                        "Report Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handle category filter checkbox changes.
     * Ensures only one filter is active at a time and resets to all categories when a filter is unchecked.
     */
    private void handleCategoryFilterChange(JCheckBox changedCheckBox, char filterType) {
        if (!"Categories".equals(selectedTable)) return;
        
        // If the filter is being checked, uncheck all other filters
        if (changedCheckBox.isSelected()) {
            uncheckAllCategoryFiltersExcept(changedCheckBox);
            
            // Apply the selected filter
            switch (filterType) {
                case 'H': // High priority
                    loadCategoriesByPriority('H');
                    break;
                case 'M': // Medium priority
                    loadCategoriesByPriority('M');
                    break;
                case 'L': // Low priority
                    loadCategoriesByPriority('L');
                    break;
                case 'A': // Active
                    loadCategoriesByActiveStatus(true);
                    break;
                case 'I': // Inactive
                    loadCategoriesByActiveStatus(false);
                    break;
            }
        } else {
            // If the filter is being unchecked, load all categories
            categoryManager.loadData();
        }
        
        // Update display and sorting
        displayData();
        sortData();
    }
    
    /**
     * Uncheck all category filter checkboxes except the specified one.
     */
    private void uncheckAllCategoryFiltersExcept(JCheckBox exceptCheckBox) {
        if (highPriorityCheckBox != exceptCheckBox) highPriorityCheckBox.setSelected(false);
        if (mediumPriorityCheckBox != exceptCheckBox) mediumPriorityCheckBox.setSelected(false);
        if (lowPriorityCheckBox != exceptCheckBox) lowPriorityCheckBox.setSelected(false);
        if (activeCheckBox != exceptCheckBox) activeCheckBox.setSelected(false);
        if (inactiveCheckBox != exceptCheckBox) inactiveCheckBox.setSelected(false);
    }
    
    /**
     * Load categories filtered by priority.
     */
    private void loadCategoriesByPriority(char priority) {
        categoryManager.loadData(); // Load all categories first
        
        // Filter by priority
        List<Category> filtered = categoryManager.getAll().stream()
                .filter(category -> category.getPriority() == priority)
                .toList();
        
        // Update the category manager with the filtered list
        categoryManager.clear();
        for (Category category : filtered) {
            categoryManager.add(category);
        }
        
        System.out.println("Loaded " + filtered.size() + " categories with priority: " + priority);
    }
    
    /**
     * Load categories filtered by active status.
     */
    private void loadCategoriesByActiveStatus(boolean active) {
        categoryManager.loadData(); // Load all categories first
        
        // Filter by active status
        List<Category> filtered = categoryManager.getAll().stream()
                .filter(category -> category.isActive() == active)
                .toList();
        
        // Update the category manager with the filtered list
        categoryManager.clear();
        for (Category category : filtered) {
            categoryManager.add(category);
        }
        
        System.out.println("Loaded " + filtered.size() + " categories with active status: " + active);
    }
    
    /**
     * Show a dialog to edit an expense.
     * 
     * @param selectedRow The selected row in the table
     */
    private void showEditExpenseDialog(int selectedRow) {
        // Get expense ID from the selected row
        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the expense object
        final Expense expenseToEdit = findExpenseById(expenseId);
        
        if (expenseToEdit == null) {
            JOptionPane.showMessageDialog(this, 
                    "Could not find the selected expense", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create a dialog for editing an expense
        JDialog dialog = new JDialog(this, "Edit Expense", true);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);
        
        // Create the main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel with a better layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create form fields with existing data
        JTextField titleField = new JTextField(expenseToEdit.getTitle(), 20);
        styleTextField(titleField);
        
        JTextField amountField = new JTextField(String.valueOf(expenseToEdit.getAmount()), 10);
        styleTextField(amountField);
        
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"Cash", "Digital", "Bank Transfer"});
        modeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeComboBox.setPreferredSize(new Dimension(150, 32));
        
        // Set the current payment mode
        String currentMode;
        switch (expenseToEdit.getMode()) {
            case 'C': currentMode = "Cash"; break;
            case 'D': currentMode = "Digital"; break;
            case 'B': currentMode = "Bank Transfer"; break;
            default: currentMode = "Cash"; break;
        }
        modeComboBox.setSelectedItem(currentMode);
        
        JCheckBox recurringCheckBox = new JCheckBox();
        styleCheckBox(recurringCheckBox);
        recurringCheckBox.setSelected(expenseToEdit.isRecurring());
        
        // Category dropdown
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.setPreferredSize(new Dimension(150, 32));
        
        String currentCategoryName = "Unknown";
        for (Category category : categoryManager.getAll()) {
            categoryComboBox.addItem(category.getName());
            if (category.getId() == expenseToEdit.getCategoryId()) {
                currentCategoryName = category.getName();
            }
        }
        categoryComboBox.setSelectedItem(currentCategoryName);
        
        // Add components to the form panel
        JLabel titleLabel = new JLabel("Title:");
        styleFormLabel(titleLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(titleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(titleField, gbc);
        
        JLabel amountLabel = new JLabel("Amount:");
        styleFormLabel(amountLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(amountField, gbc);
        
        JLabel modeLabel = new JLabel("Payment Mode:");
        styleFormLabel(modeLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(modeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(modeComboBox, gbc);
        
        JLabel recurringLabel = new JLabel("Recurring:");
        styleFormLabel(recurringLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(recurringLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(recurringCheckBox, gbc);
        
        JLabel categoryLabel = new JLabel("Category:");
        styleFormLabel(categoryLabel);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(categoryComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 250));
        
        // Add Save button
        JButton saveButton = new JButton("Save");
        styleButton(saveButton, new Color(76, 175, 80));
        buttonPanel.add(saveButton);
        
        // Add Cancel button
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(150, 150, 150));
        buttonPanel.add(cancelButton);
        
        // Add panels to the main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set the main panel as the content pane
        dialog.setContentPane(mainPanel);
        
        // Apply custom styling to the dialog
        styleDialog(dialog, "Edit Expense");
        
        // Set up button actions
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    throw new IllegalArgumentException("Title cannot be empty");
                }
                
                float amount;
                try {
                    amount = Float.parseFloat(amountField.getText().trim());
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid amount format");
                }
                
                // Get payment mode
                char mode;
                String modeStr = (String) modeComboBox.getSelectedItem();
                switch (modeStr) {
                    case "Cash": mode = 'C'; break;
                    case "Digital": mode = 'D'; break;
                    case "Bank Transfer": mode = 'B'; break;
                    default: mode = 'C'; break;
                }
                
                // Get category ID
                int categoryId = -1;
                String categoryName = (String) categoryComboBox.getSelectedItem();
                for (Category category : categoryManager.getAll()) {
                    if (category.getName().equals(categoryName)) {
                        categoryId = category.getId();
                        break;
                    }
                }
                
                if (categoryId == -1) {
                    throw new IllegalArgumentException("Invalid category");
                }
                
                // Update the expense
                expenseToEdit.setTitle(title);
                expenseToEdit.setAmount(amount);
                expenseToEdit.setMode(mode);
                expenseToEdit.setRecurring(recurringCheckBox.isSelected());
                expenseToEdit.setCategoryId(categoryId);
                
                boolean success = expenseManager.updateExpense(expenseToEdit);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Expense updated successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    displayData();
                    updateSummary();
                } else {
                    throw new IllegalArgumentException("Failed to update expense");
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                        ex.getMessage(), 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /**
     * Helper method to find an expense by ID.
     * 
     * @param id The ID to look for
     * @return The Expense object or null if not found
     */
    private Expense findExpenseById(int id) {
        for (Expense expense : expenseManager.getAll()) {
            if (expense.getId() == id) {
                return expense;
            }
        }
        return null;
    }
    
    /**
     * Helper method to find a category by ID.
     * 
     * @param id The ID to look for
     * @return The Category object or null if not found
     */
    private Category findCategoryById(int id) {
        for (Category category : categoryManager.getAll()) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }
    
    /**
     * Show a dialog to edit a category.
     * 
     * @param selectedRow The selected row in the table
     */
    private void showEditCategoryDialog(int selectedRow) {
        // Get category ID from the selected row
        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Find the category object
        final Category categoryToEdit = findCategoryById(categoryId);
        
        if (categoryToEdit == null) {
            JOptionPane.showMessageDialog(this, 
                    "Could not find the selected category", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create a dialog for editing a category
        JDialog dialog = new JDialog(this, "Edit Category", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        // Create the main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel with a better layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create form fields with existing data
        JTextField nameField = new JTextField(categoryToEdit.getName(), 20);
        styleTextField(nameField);
        
        JTextField limitField = new JTextField(String.valueOf(categoryToEdit.getMonthlyLimit()), 10);
        styleTextField(limitField);
        
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priorityComboBox.setPreferredSize(new Dimension(150, 32));
        
        // Set the current priority
        String currentPriority;
        switch (categoryToEdit.getPriority()) {
            case 'H': currentPriority = "High"; break;
            case 'M': currentPriority = "Medium"; break;
            case 'L': currentPriority = "Low"; break;
            default: currentPriority = "Medium"; break;
        }
        priorityComboBox.setSelectedItem(currentPriority);
        
        JCheckBox activeCheckBox = new JCheckBox();
        styleCheckBox(activeCheckBox);
        activeCheckBox.setSelected(categoryToEdit.isActive());
        
        // Add components to the form panel
        JLabel nameLabel = new JLabel("Name:");
        styleFormLabel(nameLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);
        
        JLabel limitLabel = new JLabel("Monthly Limit:");
        styleFormLabel(limitLabel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(limitLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(limitField, gbc);
        
        JLabel priorityLabel = new JLabel("Priority:");
        styleFormLabel(priorityLabel);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(priorityLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(priorityComboBox, gbc);
        
        JLabel activeLabel = new JLabel("Active:");
        styleFormLabel(activeLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(activeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(activeCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 250));
        
        // Add Save button
        JButton saveButton = new JButton("Save");
        styleButton(saveButton, new Color(76, 175, 80));
        buttonPanel.add(saveButton);
        
        // Add Cancel button
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(150, 150, 150));
        buttonPanel.add(cancelButton);
        
        // Add panels to the main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set the main panel as the content pane
        dialog.setContentPane(mainPanel);
        
        // Apply custom styling to the dialog
        styleDialog(dialog, "Edit Category");
        
        // Set up button actions
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be empty");
                }
                
                float limit;
                try {
                    limit = Float.parseFloat(limitField.getText().trim());
                    if (limit <= 0) {
                        throw new IllegalArgumentException("Monthly limit must be positive");
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid limit format");
                }
                
                // Get priority
                char priority;
                String priorityStr = (String) priorityComboBox.getSelectedItem();
                switch (priorityStr) {
                    case "High": priority = 'H'; break;
                    case "Medium": priority = 'M'; break;
                    case "Low": priority = 'L'; break;
                    default: priority = 'M'; break;
                }
                
                // Update the category
                categoryToEdit.setName(name);
                categoryToEdit.setMonthlyLimit(limit);
                categoryToEdit.setPriority(priority);
                categoryToEdit.setActive(activeCheckBox.isSelected());
                
                boolean success = categoryManager.updateCategory(categoryToEdit);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Category updated successfully", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    displayData();
                    updateSummary();
                } else {
                    throw new IllegalArgumentException("Failed to update category");
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                        ex.getMessage(), 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /**
     * Main method to launch the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set the look and feel to match the system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the application
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }

    /**
     * Creates a custom application icon and saves it as a PNG file.
     * 
     * @param path The path where to save the icon
     * @param size The size of the icon
     * @return true if successful, false otherwise
     */
    private boolean createApplicationIcon(String path, int size) {
        try {
            // Create a buffered image with transparency
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // Set anti-aliasing for better quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Fill background with a gradient
            Color startColor = new Color(100, 150, 220);
            Color endColor = new Color(76, 175, 80);
            g2d.setPaint(new java.awt.GradientPaint(0, 0, startColor, size, size, endColor));
            g2d.fillRoundRect(0, 0, size - 1, size - 1, size / 4, size / 4);
            
            // Draw the border
            g2d.setColor(new Color(60, 60, 60));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(0, 0, size - 1, size - 1, size / 4, size / 4);
            
            // Draw a dollar sign or "ET" for Expense Tracker
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
            g2d.drawString("ET", size / 5, size * 2 / 3);
            
            // Draw a coin or circle for money representation
            g2d.setColor(new Color(255, 215, 0, 220)); // Gold with transparency
            g2d.fillOval(size / 2, size / 5, size / 3, size / 3);
            
            // Outline for the coin
            g2d.setColor(new Color(210, 180, 20));
            g2d.drawOval(size / 2, size / 5, size / 3, size / 3);
            
            // Clean up
            g2d.dispose();
            
            // Save the icon as a PNG file
            File iconFile = new File(path);
            // Create directories if they don't exist
            File parentDir = iconFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("Creating directory: " + parentDir.getAbsolutePath() + " - Success: " + dirCreated);
            }
            
            boolean saved = ImageIO.write(image, "png", iconFile);
            System.out.println("Icon successfully created at: " + iconFile.getAbsolutePath() + " - Save success: " + saved);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating icon: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 