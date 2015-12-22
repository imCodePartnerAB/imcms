package com.imcode.imcms.imagearchive.service.file;

import java.util.HashMap;
import java.util.Map;

public enum LibrarySort {
    /* first two are usually not used */
    SHOW_BUTTONS("showButtons"),
    ACTION_CHECKBOX("actionCheckbox"),
    ARCHIVE("archive"),
    FILENAME("fileName"),
    SIZE("size"),
    DATE("date");

    private static final Map<String, LibrarySort> nameEnumMap =
            new HashMap<String, LibrarySort>(LibrarySort.values().length);

    static {
        for (LibrarySort sort : LibrarySort.values()) {
            nameEnumMap.put(sort.getName(), sort);
        }
    }

    private final String name;
    private DIRECTION direction;

    private LibrarySort(String name) {
        this.name = name;
    }

    public static final LibrarySort findByName(String name) {
        String[] parts = name.split("-");
        LibrarySort sortOrder = values()[Integer.parseInt(parts[0])];
        sortOrder.setDirection(DIRECTION.values()[Integer.parseInt(parts[1])]);

        return sortOrder;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return this.ordinal();
    }

    public enum DIRECTION {
        ASC, DESC;

        public int getOrdinal() {
            return ordinal();
        }
    }
}
