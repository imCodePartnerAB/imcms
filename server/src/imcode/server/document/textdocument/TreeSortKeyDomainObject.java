package imcode.server.document.textdocument;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TreeSortKeyDomainObject implements Comparable, Serializable {

    private final int[] keys;

    public TreeSortKeyDomainObject( String treeSortKey ) {
        if (null == treeSortKey) {
            throw new NullArgumentException("treeSortKey");
        }
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

    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false;}

        final TreeSortKeyDomainObject that = (TreeSortKeyDomainObject) o;

        return new EqualsBuilder().append(keys, that.keys).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(keys).toHashCode();
    }
}
