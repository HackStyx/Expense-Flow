package dao;

import model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Expense entities.
 * Handles all database operations related to expenses.
 */
public class ExpenseDAO {
    
    /**
     * Insert a new expense into the database.
     * 
     * @param expense The expense to insert
     * @return The generated ID of the new expense or -1 if the operation failed
     */
    public int insertExpense(Expense expense) {
        String sql = "INSERT INTO expenses (title, amount, mode, is_recurring, category_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, expense.getTitle());
            stmt.setFloat(2, expense.getAmount());
            stmt.setString(3, String.valueOf(expense.getMode()));
            stmt.setBoolean(4, expense.isRecurring());
            stmt.setInt(5, expense.getCategoryId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating expense failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getInt(1));
                    return expense.getId();
                } else {
                    throw new SQLException("Creating expense failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting expense: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Update an existing expense in the database.
     * 
     * @param expense The expense to update
     * @return true if successful, false otherwise
     */
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET title = ?, amount = ?, mode = ?, is_recurring = ?, category_id = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, expense.getTitle());
            stmt.setFloat(2, expense.getAmount());
            stmt.setString(3, String.valueOf(expense.getMode()));
            stmt.setBoolean(4, expense.isRecurring());
            stmt.setInt(5, expense.getCategoryId());
            stmt.setInt(6, expense.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an expense from the database.
     * 
     * @param id The ID of the expense to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get an expense by its ID.
     * 
     * @param id The ID of the expense to retrieve
     * @return The expense object or null if not found
     */
    public Expense getExpenseById(int id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpense(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expense: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all expenses from the database.
     * 
     * @return A list of all expenses
     */
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get all recurring expenses.
     * 
     * @return A list of recurring expenses
     */
    public List<Expense> getRecurringExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE is_recurring = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving recurring expenses: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get expenses by category ID.
     * 
     * @param categoryId The category ID
     * @return A list of expenses in the specified category
     */
    public List<Expense> getExpensesByCategory(int categoryId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE category_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving expenses by category: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get total spending by category.
     * 
     * @return A list of category IDs and their total spending
     */
    public List<Object[]> getTotalSpendingByCategory() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT category_id, SUM(amount) as total FROM expenses GROUP BY category_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {rs.getInt("category_id"), rs.getFloat("total")};
                result.add(row);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total spending by category: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get all non-recurring expenses from the database.
     * 
     * @return A list of non-recurring expense objects
     */
    public List<Expense> getNonRecurringExpenses() {
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM expenses WHERE is_recurring = FALSE")) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getFloat("amount"),
                    rs.getString("mode").charAt(0),
                    rs.getBoolean("is_recurring"),
                    rs.getInt("category_id")
                );
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting non-recurring expenses: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Get expenses with a specific payment mode from the database.
     * 
     * @param mode The payment mode to filter by ('C'=Cash, 'D'=Digital, 'B'=Bank Transfer)
     * @return A list of expense objects with the specified payment mode
     */
    public List<Expense> getExpensesByPaymentMode(char mode) {
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM expenses WHERE mode = ?")) {
            
            stmt.setString(1, String.valueOf(mode));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getFloat("amount"),
                    rs.getString("mode").charAt(0),
                    rs.getBoolean("is_recurring"),
                    rs.getInt("category_id")
                );
                expenses.add(expense);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting expenses by payment mode: " + e.getMessage());
        }
        
        return expenses;
    }
    
    /**
     * Helper method to map a ResultSet row to an Expense object.
     * 
     * @param rs The ResultSet containing expense data
     * @return An Expense object
     * @throws SQLException if a database access error occurs
     */
    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setTitle(rs.getString("title"));
        expense.setAmount(rs.getFloat("amount"));
        expense.setMode(rs.getString("mode").charAt(0));
        expense.setRecurring(rs.getBoolean("is_recurring"));
        expense.setCategoryId(rs.getInt("category_id"));
        return expense;
    }
} 