package imcode.server.document.textdocument;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

public class TreeSortKeyDomainObject implements Comparable {

    private int[] keys;

    public TreeSortKeyDomainObject( String treeSortKey ) {
        StringTokenizer tokenizer = new StringTokenizer( treeSortKey, "." );
        keys = new int[tokenizer.countTokens()] ;
        for (int i = 0; i < keys.length; ++i) {
            String token = tokenizer.nextToken();
            keys[i] = Integer.parseInt(token) ;
        }
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
