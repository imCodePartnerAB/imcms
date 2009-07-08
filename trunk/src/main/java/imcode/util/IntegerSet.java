package imcode.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntegerSet extends AbstractSet {

    private final int[] ints;

    public IntegerSet(int integer) {
        this(new int[] { integer }) ;
    }
    
    public IntegerSet(int[] ints) {
        this.ints = ints;
    }

    public int size() {
        return ints.length ;
    }

    public Iterator iterator() {
        return new Iterator() {
            int arrayIndex = 0 ;
            public void remove() {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext() {
                return arrayIndex < size() ;
            }

            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException() ;
                }
                return new Integer(ints[arrayIndex++]) ;
            }
        } ;
    }
}
