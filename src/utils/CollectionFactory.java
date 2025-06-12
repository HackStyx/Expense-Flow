package utils;

import java.util.*;

/**
 * Factory class for creating collection instances based on the collection type.
 */
public class CollectionFactory {
    
    /**
     * Enumeration of supported collection types.
     */
    public enum CollectionType {
        ARRAY_LIST("ArrayList"),
        LINKED_LIST("LinkedList"),
        HASH_SET("HashSet"),
        TREE_SET("TreeSet"),
        PRIORITY_QUEUE("PriorityQueue");
        
        private final String name;
        
        CollectionType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        /**
         * Get a CollectionType enum from its name.
         * 
         * @param name The name of the collection type
         * @return The corresponding CollectionType or null if not found
         */
        public static CollectionType fromString(String name) {
            for (CollectionType type : values()) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }
    
    /**
     * Create a new collection instance based on the specified type.
     * 
     * @param <T> The type of elements in the collection
     * @param type The collection type to create
     * @return A new collection instance of the specified type
     */
    public static <T> Collection<T> createCollection(CollectionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Collection type cannot be null");
        }
        
        switch (type) {
            case ARRAY_LIST:
                return new ArrayList<>();
            case LINKED_LIST:
                return new LinkedList<>();
            case HASH_SET:
                return new HashSet<>();
            case TREE_SET:
                return new TreeSet<>();
            case PRIORITY_QUEUE:
                return new PriorityQueue<>();
            default:
                throw new IllegalArgumentException("Unsupported collection type: " + type);
        }
    }
    
    /**
     * Create a new collection instance based on the type name.
     * 
     * @param <T> The type of elements in the collection
     * @param typeName The name of the collection type to create
     * @return A new collection instance of the specified type
     */
    public static <T> Collection<T> createCollection(String typeName) {
        CollectionType type = CollectionType.fromString(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown collection type: " + typeName);
        }
        return createCollection(type);
    }
    
    /**
     * Check if the provided collection type name is valid.
     * 
     * @param typeName The collection type name to check
     * @return true if the type name is valid, false otherwise
     */
    public static boolean isValidCollectionType(String typeName) {
        return CollectionType.fromString(typeName) != null;
    }
    
    /**
     * Get the names of all supported collection types.
     * 
     * @return An array of collection type names
     */
    public static String[] getCollectionTypeNames() {
        CollectionType[] types = CollectionType.values();
        String[] names = new String[types.length];
        
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getName();
        }
        
        return names;
    }
} 