package logic;

import dao.ExpenseDAO;
import model.Expense;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for Expense data that extends the generic DataManager.
 * Provides expense-specific operations and comparator implementations.
 */
public class ExpenseManager extends DataManager<Expense> {
    
    private final ExpenseDAO expenseDAO;
    
    /**
     * Constructor that initializes the ExpenseManager.
     */
    public ExpenseManager() {
        super();
        this.expenseDAO = new ExpenseDAO();
    }
    
    /**
     * Load all expenses from the database into the collection.
     */
    @Override
    public void loadData() {
        clear();
        List<Expense> expenses = expenseDAO.getAllExpenses();
        for (Expense expense : expenses) {
            add(expense);
        }
    }
    
    /**
     * Load only recurring expenses from the database into the collection.
     */
    public void loadRecurringExpenses() {
        clear();
        List<Expense> expenses = expenseDAO.getRecurringExpenses();
        for (Expense expense : expenses) {
            add(expense);
        }
    }
    
    /**
     * Load expenses for a specific category from the database into the collection.
     * 
     * @param categoryId The ID of the category to load expenses for
     */
    public void loadExpensesByCategory(int categoryId) {
        clear();
        List<Expense> expenses = expenseDAO.getExpensesByCategory(categoryId);
        for (Expense expense : expenses) {
            add(expense);
        }
    }
    
    /**
     * Load only non-recurring expenses from the database into the collection.
     */
    public void loadNonRecurringExpenses() {
        clear();
        List<Expense> expenses = expenseDAO.getNonRecurringExpenses();
        for (Expense expense : expenses) {
            add(expense);
        }
    }
    
    /**
     * Load expenses by payment mode from the database into the collection.
     * 
     * @param mode The payment mode ('C'=Cash, 'D'=Digital, 'B'=Bank Transfer)
     */
    public void loadExpensesByPaymentMode(char mode) {
        clear();
        List<Expense> expenses = expenseDAO.getExpensesByPaymentMode(mode);
        for (Expense expense : expenses) {
            add(expense);
        }
    }
    
    /**
     * Calculate total amount spent across all expenses.
     * 
     * @return The total amount
     */
    public float calculateTotalAmount() {
        float total = 0;
        for (Expense expense : dataCollection) {
            total += expense.getAmount();
        }
        return total;
    }
    
    /**
     * Calculate the total spending by payment mode.
     * 
     * @return A map of payment modes to their total amounts
     */
    public Map<Character, Float> calculateTotalByMode() {
        Map<Character, Float> totals = new HashMap<>();
        
        // Initialize the map with all modes
        totals.put('C', 0f); // Cash
        totals.put('D', 0f); // Digital
        totals.put('B', 0f); // Bank Transfer
        
        for (Expense expense : dataCollection) {
            char mode = expense.getMode();
            float currentTotal = totals.getOrDefault(mode, 0f);
            totals.put(mode, currentTotal + expense.getAmount());
        }
        
        return totals;
    }
    
    /**
     * Save a new expense to the database and add it to the collection.
     * 
     * @param expense The expense to save
     * @return true if the expense was saved successfully, false otherwise
     */
    public boolean saveExpense(Expense expense) {
        int id = expenseDAO.insertExpense(expense);
        if (id > 0) {
            expense.setId(id);
            return add(expense);
        }
        return false;
    }
    
    /**
     * Update an existing expense in the database and the collection.
     * 
     * @param expense The expense to update
     * @return true if the expense was updated successfully, false otherwise
     */
    public boolean updateExpense(Expense expense) {
        boolean success = expenseDAO.updateExpense(expense);
        if (success) {
            // Remove and re-add to update the collection
            for (Expense e : dataCollection) {
                if (e.getId() == expense.getId()) {
                    remove(e);
                    break;
                }
            }
            add(expense);
        }
        return success;
    }
    
    /**
     * Delete an expense from the database and the collection.
     * 
     * @param expense The expense to delete
     * @return true if the expense was deleted successfully, false otherwise
     */
    public boolean deleteExpense(Expense expense) {
        boolean success = expenseDAO.deleteExpense(expense.getId());
        if (success) {
            remove(expense);
        }
        return success;
    }
    
    /**
     * Get a comparator for sorting expenses by title.
     * 
     * @return A comparator for sorting by title
     */
    public static Comparator<Expense> getTitleComparator() {
        return Comparator.comparing(Expense::getTitle);
    }
    
    /**
     * Get a comparator for sorting expenses by amount.
     * 
     * @param ascending true for ascending order, false for descending
     * @return A comparator for sorting by amount
     */
    public static Comparator<Expense> getAmountComparator(boolean ascending) {
        return ascending 
            ? Comparator.comparing(Expense::getAmount)
            : Comparator.comparing(Expense::getAmount).reversed();
    }
    
    /**
     * Get a comparator for sorting expenses by payment mode.
     * 
     * @return A comparator for sorting by mode
     */
    public static Comparator<Expense> getModeComparator() {
        return Comparator.comparing(Expense::getMode);
    }
    
    /**
     * Get a comparator for sorting expenses by recurring status.
     * 
     * @return A comparator for sorting by recurring status
     */
    public static Comparator<Expense> getRecurringComparator() {
        return Comparator.comparing(Expense::isRecurring);
    }
} 