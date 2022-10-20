package com.imcode.imcms.storage;

public enum StorageLocation {
    DISK,
    CLOUD,
    SYNC;

    public static StorageLocation getByName(String name){
        return switch (name.toLowerCase()) {
            case "disk" -> DISK;
            case "cloud" -> CLOUD;
            case "sync" -> SYNC;
            default -> null;
        };
    }

}
