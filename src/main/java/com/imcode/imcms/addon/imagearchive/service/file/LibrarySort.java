package com.imcode.imcms.addon.imagearchive.service.file;

import java.util.HashMap;
import java.util.Map;

public enum LibrarySort {
    FILENAME("fileName"), 
    DATE("date");
    
    private static final Map<String, LibrarySort> nameEnumMap = 
            new HashMap<String, LibrarySort>(LibrarySort.values().length);
    
    static {
        for (LibrarySort sort : LibrarySort.values()) {
            nameEnumMap.put(sort.getName(), sort);
        }
    }
    
    public static final LibrarySort findByName(String name) {
        return nameEnumMap.get(name);
    }
    
    private final String name;
    
    private LibrarySort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
