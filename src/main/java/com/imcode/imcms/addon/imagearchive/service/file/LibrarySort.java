package com.imcode.imcms.addon.imagearchive.service.file;

import java.util.HashMap;
import java.util.Map;

public enum LibrarySort {
    ARCHIVE("archive"),
    FILENAME("fileName"),
    SIZE("size"),
    DATE("date");

    public enum DIRECTION {
        ASC, DESC;

        public int getOrdinal() {
            return ordinal();
        }
    }

    private DIRECTION direction;


    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    private static final Map<String, LibrarySort> nameEnumMap = 
            new HashMap<String, LibrarySort>(LibrarySort.values().length);
    
    static {
        for (LibrarySort sort : LibrarySort.values()) {
            nameEnumMap.put(sort.getName(), sort);
        }
    }
    
    public static final LibrarySort findByName(String name) {
        String[] parts = name.split("-");
        LibrarySort sortOrder = values()[Integer.parseInt(parts[0])];
        sortOrder.setDirection(DIRECTION.values()[Integer.parseInt(parts[1])]);

        return sortOrder;
    }
    
    private final String name;
    
    private LibrarySort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return this.ordinal();
    }
}
