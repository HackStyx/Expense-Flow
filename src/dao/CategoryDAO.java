package dao;

import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category entities.
 * Handles all database operations related to categories.
 */
public class CategoryDAO {
    
    /**
     * Insert a new category into the database.
     * 
     * @param category The category to insert
     * @return The generated ID of the new category or -1 if the operation failed
     */
    public int insertCategory(Category category) {
        String sql = "INSERT INTO categories (name, monthly_limit, priority, is_active) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getName());
            stmt.setFloat(2, category.getMonthlyLimit());
            stmt.setString(3, String.valueOf(category.getPriority()));
            stmt.setBoolean(4, category.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                    return category.getId();
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting category: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Update an existing category in the database.
     * 
     * @param category The category to update
     * @return true if successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ?, monthly_limit = ?, priority = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getName());
            stmt.setFloat(2, category.getMonthlyLimit());
            stmt.setString(3, String.valueOf(category.getPriority()));
            stmt.setBoolean(4, category.isActive());
            stmt.setInt(5, category.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a category from the database.
     * 
     * @param id The ID of the category to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get a category by its ID.
     * 
     * @param id The ID of the category to retrieve
     * @return The category object or null if not found
     */
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving category: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all categories from the database.
     * 
     * @return A list of all categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get all active categories.
     * 
     * @return A list of active categories
     */
    public List<Category> getActiveCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving active categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get categories by priority.
     * 
     * @param priority The priority level ('H', 'M', or 'L')
     * @return A list of categories with the specified priority
     */
    public List<Category> getCategoriesByPriority(char priority) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE priority = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, String.valueOf(priority));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving categories by priority: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get categories that are over budget based on expenses.
     * 
     * @return A list of categories where total expenses exceed monthly limit
     */
    public List<Category> getOverBudgetCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = 
            "SELECT c.*, SUM(e.amount) as total_spent " +
            "FROM categories c " +
            "JOIN expenses e ON c.id = e.category_id " +
            "GROUP BY c.id " +
            "HAVING total_spent > c.monthly_limit";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving over budget categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Helper method to map a ResultSet row to a Category object.
     * 
     * @param rs The ResultSet containing category data
     * @return A Category object
     * @throws SQLException if a database access error occurs
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setMonthlyLimit(rs.getFloat("monthly_limit"));
        category.setPriority(rs.getString("priority").charAt(0));
        category.setActive(rs.getBoolean("is_active"));
        return category;
    }
} 