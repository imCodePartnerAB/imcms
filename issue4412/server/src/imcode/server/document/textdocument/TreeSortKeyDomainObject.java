package imcode.server.document.textdocument;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TreeSortKeyDomainObject implements Comparable, Serializable {

    private int[] keys;

    public TreeSortKeyDomainObject( String treeSortKey ) {
        String[] keyStrings = treeSortKey.trim().split( "\\D+",0 ) ;
        List keyList = new ArrayList() ;
        for (int i = 0; i < keyStrings.length; ++i) {
            String keyString = keyStrings[i];
            try {
                Integer key = Integer.valueOf(keyString) ;
                keyList.add( key );
            } catch( NumberFormatException ignored ) {}
        }
        keys = ArrayUtils.toPrimitive( (Integer[])keyList.toArray( new Integer[keyList.size()] )) ;
    }

    public int getLevelCount() {
        return keys.length ;
    }

    public int getLevelKey( int level ) {
        try {
            return keys[level] ;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException() ;
        }
    }

    public String toString() {
        return StringUtils.join( ArrayUtils.toObject( keys ), '.' );
    }

    public int compareTo( Object o ) {
        int[] keys1 = keys ;
        int[] keys2 = ((TreeSortKeyDomainObject)o).keys ;

        return compareIntArrays( keys1, keys2 );
    }

    private int compareIntArrays( int[] keys1, int[] keys2 ) {
        for ( int i = 0; i < keys1.length && i < keys2.length; i++ ) {
            int difference = keys1[i] - keys2[i] ;
            if (0 != difference) {
                return difference ;
            }
        }
        return keys1.length - keys2.length ;
    }
}
