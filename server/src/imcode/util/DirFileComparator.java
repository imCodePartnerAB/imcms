package imcode.util ;

import java.io.* ;
import java.util.* ;

/**
    This class is a Comparator for files
    that sorts them with directories first.
**/

public class DirFileComparator implements Comparator {

    public int compare(Object o1, Object o2) {
	File file1 = (File)o1 ;
	File file2 = (File)o2 ;
    
	if (file1.equals(file2)) {
	    return 0 ;
	} else if (file1.isDirectory() == file2.isDirectory()) {
	    return file1.getName().compareTo(file2.getName()) ;
	} else if (file1.isDirectory()) {
	    return -1 ;
	} else {
	    return 1 ;
	}
    }
}