package model;

/**
 * Represents a spending category in the expense tracking system.
 */
public class Category {
    private int id;
    private String name;
    private float monthlyLimit;
    private char priority; // 'H'=High, 'M'=Medium, 'L'=Low
    private boolean isActive;

    // Default constructor
    public Category() {
    }

    // Parameterized constructor
    public Category(int id, String name, float monthlyLimit, char priority, boolean isActive) {
        this.id = id;
        this.name = name;
        this.monthlyLimit = monthlyLimit;
        this.priority = priority;
        this.isActive = isActive;
    }

    // Constructor without ID for creating new entries
    public Category(String name, float monthlyLimit, char priority, boolean isActive) {
        this.name = name;
        this.monthlyLimit = monthlyLimit;
        this.priority = priority;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(float monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public char getPriority() {
        return priority;
    }

    public void setPriority(char priority) {
        this.priority = priority;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper method to get priority as string
    public String getPriorityAsString() {
        switch (priority) {
            case 'H': return "High";
            case 'M': return "Medium";
            case 'L': return "Low";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyLimit=" + monthlyLimit +
                ", priority=" + getPriorityAsString() +
                ", isActive=" + isActive +
                '}';
    }
} 