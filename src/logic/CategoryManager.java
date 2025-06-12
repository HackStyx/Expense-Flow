package logic;

import dao.CategoryDAO;
import model.Category;

import java.util.Comparator;
import java.util.List;

/**
 * Manager class for Category data that extends the generic DataManager.
 * Provides category-specific operations and comparator implementations.
 */
public class CategoryManager extends DataManager<Category> {
    
    private final CategoryDAO categoryDAO;
    
    /**
     * Constructor that initializes the CategoryManager.
     */
    public CategoryManager() {
        super();
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Load all categories from the database into the collection.
     */
    @Override
    public void loadData() {
        clear();
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            add(category);
        }
    }
    
    /**
     * Load only active categories from the database into the collection.
     */
    public void loadActiveCategories() {
        clear();
        List<Category> categories = categoryDAO.getActiveCategories();
        for (Category category : categories) {
            add(category);
        }
    }
    
    /**
     * Load categories by priority from the database into the collection.
     * 
     * @param priority The priority level ('H', 'M', or 'L')
     */
    public void loadCategoriesByPriority(char priority) {
        clear();
        List<Category> categories = categoryDAO.getCategoriesByPriority(priority);
        for (Category category : categories) {
            add(category);
        }
    }
    
    /**
     * Load over budget categories from the database into the collection.
     */
    public void loadOverBudgetCategories() {
        clear();
        List<Category> categories = categoryDAO.getOverBudgetCategories();
        for (Category category : categories) {
            add(category);
        }
    }
    
    /**
     * Save a new category to the database and add it to the collection.
     * 
     * @param category The category to save
     * @return true if the category was saved successfully, false otherwise
     */
    public boolean saveCategory(Category category) {
        int id = categoryDAO.insertCategory(category);
        if (id > 0) {
            category.setId(id);
            return add(category);
        }
        return false;
    }
    
    /**
     * Update an existing category in the database and the collection.
     * 
     * @param category The category to update
     * @return true if the category was updated successfully, false otherwise
     */
    public boolean updateCategory(Category category) {
        boolean success = categoryDAO.updateCategory(category);
        if (success) {
            // Remove and re-add to update the collection
            for (Category c : dataCollection) {
                if (c.getId() == category.getId()) {
                    remove(c);
                    break;
                }
            }
            add(category);
        }
        return success;
    }
    
    /**
     * Delete a category from the database and the collection.
     * 
     * @param category The category to delete
     * @return true if the category was deleted successfully, false otherwise
     */
    public boolean deleteCategory(Category category) {
        boolean success = categoryDAO.deleteCategory(category.getId());
        if (success) {
            remove(category);
        }
        return success;
    }
    
    /**
     * Get a comparator for sorting categories by name.
     * 
     * @return A comparator for sorting by name
     */
    public static Comparator<Category> getNameComparator() {
        return Comparator.comparing(Category::getName);
    }
    
    /**
     * Get a comparator for sorting categories by monthly limit.
     * 
     * @param ascending true for ascending order, false for descending
     * @return A comparator for sorting by monthly limit
     */
    public static Comparator<Category> getMonthlyLimitComparator(boolean ascending) {
        return ascending
            ? Comparator.comparing(Category::getMonthlyLimit)
            : Comparator.comparing(Category::getMonthlyLimit).reversed();
    }
    
    /**
     * Get a comparator for sorting categories by priority.
     * This creates a custom order: High (H) > Medium (M) > Low (L)
     * 
     * @return A comparator for sorting by priority
     */
    public static Comparator<Category> getPriorityComparator() {
        return (c1, c2) -> {
            // Custom priority ordering: H > M > L
            char p1 = c1.getPriority();
            char p2 = c2.getPriority();
            
            // Convert to numerical values for comparison
            int v1 = (p1 == 'H') ? 3 : (p1 == 'M') ? 2 : 1;
            int v2 = (p2 == 'H') ? 3 : (p2 == 'M') ? 2 : 1;
            
            return Integer.compare(v2, v1); // Reverse to get H first
        };
    }
    
    /**
     * Get a comparator for sorting categories by active status.
     * 
     * @param activeFirst true to put active categories first, false for inactive first
     * @return A comparator for sorting by active status
     */
    public static Comparator<Category> getActiveStatusComparator(boolean activeFirst) {
        return activeFirst
            ? Comparator.comparing(Category::isActive).reversed()
            : Comparator.comparing(Category::isActive);
    }
} 