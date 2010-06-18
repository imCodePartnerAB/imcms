package com.imcode.imcms.addon.imagearchive.service.file;

import java.util.HashMap;
import java.util.Map;

public enum ThumbSize {
    SMALL("small", 150, 113), 
    MEDIUM("medium", 300, 225);

    private static final Map<String, ThumbSize> nameEnumMap = new HashMap<String, ThumbSize>(ThumbSize.values().length);
    static {
        for (ThumbSize thumbSize : ThumbSize.values()) {
            nameEnumMap.put(thumbSize.name, thumbSize);
        }
    }

    private final String name;
    private final int width;
    private final int height;

    private ThumbSize(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public static ThumbSize findByName(String name) {
        return nameEnumMap.get(name);
    }
}