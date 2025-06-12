package logic;

import java.util.*;
import java.util.function.Predicate;

/**
 * Abstract generic class to manage collections of data objects.
 * Provides methods for common operations like sorting, filtering, and iteration.
 *
 * @param <T> The type of objects this manager will handle.
 */
public abstract class DataManager<T> {
    
    // The collection of data elements
    protected List<T> dataCollection;
    
    /**
     * Constructor that initializes the data manager with an ArrayList.
     */
    public DataManager() {
        this.dataCollection = new ArrayList<>();
    }
    
    /**
     * Loads data from the database into the collection.
     */
    public abstract void loadData();
    
    /**
     * Add an item to the collection.
     *
     * @param item The item to add
     * @return true if the item was added, false otherwise
     */
    public boolean add(T item) {
        return dataCollection.add(item);
    }
    
    /**
     * Remove an item from the collection.
     *
     * @param item The item to remove
     * @return true if the item was removed, false otherwise
     */
    public boolean remove(T item) {
        return dataCollection.remove(item);
    }
    
    /**
     * Get all items in the collection.
     *
     * @return A new list containing all items
     */
    public List<T> getAll() {
        return new ArrayList<>(dataCollection);
    }
    
    /**
     * Sort the items according to the provided comparator.
     *
     * @param comparator The comparator to use for sorting
     * @return A sorted list of items
     */
    public List<T> sort(Comparator<T> comparator) {
        List<T> sortedList = new ArrayList<>(dataCollection);
        sortedList.sort(comparator);
        return sortedList;
    }
    
    /**
     * Filter the items according to the provided predicate.
     *
     * @param predicate The predicate to use for filtering
     * @return A filtered list of items
     */
    public List<T> filter(Predicate<T> predicate) {
        List<T> filteredList = new ArrayList<>();
        for (T item : dataCollection) {
            if (predicate.test(item)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }
    
    /**
     * Get an iterator for the collection.
     *
     * @return An iterator over the elements in the collection
     */
    public Iterator<T> iterator() {
        return dataCollection.iterator();
    }
    
    /**
     * Clear all items from the collection.
     */
    public void clear() {
        dataCollection.clear();
    }
    
    /**
     * Get the number of items in the collection.
     *
     * @return The number of items
     */
    public int size() {
        return dataCollection.size();
    }
} 