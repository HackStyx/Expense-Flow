package ui;

import model.Category;
import model.Expense;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for generating expense reports in various formats.
 */
public class ReportGenerator {
    
    /**
     * Generate a text report of expenses grouped by category.
     * 
     * @param expenses The list of expenses
     * @param categories The list of categories
     * @param filePath The path where the report will be saved
     * @return true if the report was generated successfully, false otherwise
     */
    public static boolean generateExpenseReport(List<Expense> expenses, List<Category> categories, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Create a map of category IDs to category objects for easier lookup
            Map<Integer, Category> categoryMap = new HashMap<>();
            for (Category category : categories) {
                categoryMap.put(category.getId(), category);
            }
            
            // Write header
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());
            
            writer.write("====================================================");
            writer.newLine();
            writer.write("          MINI EXPENSE INTELLIGENCE REPORT          ");
            writer.newLine();
            writer.write("====================================================");
            writer.newLine();
            writer.write("Generated: " + currentTime);
            writer.newLine();
            writer.write("Total Expenses: " + expenses.size());
            writer.newLine();
            writer.newLine();
            
            // Organize expenses by category
            Map<Integer, Float> categoryTotals = new HashMap<>();
            
            for (Expense expense : expenses) {
                int categoryId = expense.getCategoryId();
                float amount = expense.getAmount();
                
                categoryTotals.put(categoryId, categoryTotals.getOrDefault(categoryId, 0f) + amount);
            }
            
            // Calculate total amount
            float totalAmount = 0;
            for (float amount : categoryTotals.values()) {
                totalAmount += amount;
            }
            
            // Write category summaries
            writer.write("SPENDING BY CATEGORY");
            writer.newLine();
            writer.write("-------------------");
            writer.newLine();
            
            for (Map.Entry<Integer, Float> entry : categoryTotals.entrySet()) {
                int categoryId = entry.getKey();
                float amount = entry.getValue();
                
                Category category = categoryMap.get(categoryId);
                String categoryName = (category != null) ? category.getName() : "Unknown Category";
                float limit = (category != null) ? category.getMonthlyLimit() : 0;
                
                writer.write(String.format("%-20s: ₹%.2f (%.1f%% of total)", 
                        categoryName, amount, (amount / totalAmount) * 100));
                writer.newLine();
                
                if (category != null && limit > 0) {
                    float remainingBudget = limit - amount;
                    writer.write(String.format("  Monthly Limit: ₹%.2f, Remaining: ₹%.2f (%.1f%%)", 
                            limit, remainingBudget, (remainingBudget / limit) * 100));
                    writer.newLine();
                    
                    if (amount > limit) {
                        writer.write("  *** OVER BUDGET ***");
                        writer.newLine();
                    }
                }
            }
            
            writer.newLine();
            
            // Write expense details
            writer.write("EXPENSE DETAILS");
            writer.newLine();
            writer.write("--------------");
            writer.newLine();
            
            for (int categoryId : categoryTotals.keySet()) {
                Category category = categoryMap.get(categoryId);
                String categoryName = (category != null) ? category.getName() : "Unknown Category";
                
                writer.write(categoryName + ":");
                writer.newLine();
                
                for (Expense expense : expenses) {
                    if (expense.getCategoryId() == categoryId) {
                        writer.write(String.format("  %-30s ₹%.2f (%s, %s)", 
                                expense.getTitle(), 
                                expense.getAmount(), 
                                expense.getModeAsString(),
                                expense.isRecurring() ? "Recurring" : "One-time"));
                        writer.newLine();
                    }
                }
                
                writer.newLine();
            }
            
            // Write summary
            writer.write("====================================================");
            writer.newLine();
            writer.write(String.format("TOTAL SPENDING: ₹%.2f", totalAmount));
            writer.newLine();
            writer.write("====================================================");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating expense report: " + e.getMessage());
            return false;
        }
    }
} 