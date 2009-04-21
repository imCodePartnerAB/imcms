package com.imcode.imcms.addon.imagearchive.service.file;

import com.imcode.imcms.addon.imagearchive.dto.LibraryEntryDto;
import java.util.Comparator;

public class LibraryEntryComparator implements Comparator<LibraryEntryDto> {
    private final LibrarySort sortBy;
    
    public LibraryEntryComparator(LibrarySort sortBy) {
        this.sortBy = sortBy;
    }
    
    public int compare(LibraryEntryDto e1, LibraryEntryDto e2) {
        if (sortBy == LibrarySort.FILENAME) {
            return e1.getFileName().compareTo(e2.getFileName());
        } else {
            Long lastModified1 = e1.getLastModified();
            Long lastModified2 = e2.getLastModified();
            
            return -1 * lastModified1.compareTo(lastModified2);
        }
    }
}
