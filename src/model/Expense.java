package model;

/**
 * Represents an expense entry in the expense tracking system.
 */
public class Expense {
    private int id;
    private String title;
    private float amount;
    private char mode; // 'C'=Cash, 'D'=Digital, 'B'=Bank Transfer
    private boolean isRecurring;
    private int categoryId; // Reference to the category table

    // Default constructor
    public Expense() {
    }

    // Parameterized constructor
    public Expense(int id, String title, float amount, char mode, boolean isRecurring, int categoryId) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.mode = mode;
        this.isRecurring = isRecurring;
        this.categoryId = categoryId;
    }

    // Constructor without ID for creating new entries
    public Expense(String title, float amount, char mode, boolean isRecurring, int categoryId) {
        this.title = title;
        this.amount = amount;
        this.mode = mode;
        this.isRecurring = isRecurring;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public char getMode() {
        return mode;
    }

    public void setMode(char mode) {
        this.mode = mode;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    // Helper methods to get mode as string
    public String getModeAsString() {
        switch (mode) {
            case 'C': return "Cash";
            case 'D': return "Digital";
            case 'B': return "Bank Transfer";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", mode=" + getModeAsString() +
                ", isRecurring=" + isRecurring +
                ", categoryId=" + categoryId +
                '}';
    }
} 