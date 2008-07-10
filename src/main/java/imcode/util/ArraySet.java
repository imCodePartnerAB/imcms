package imcode.util;

import java.util.AbstractSet;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ObjectArrayIterator;

public class ArraySet<T> extends AbstractSet<T> {
	
    private final Object[] array;

    public ArraySet(Object[] array) {
        this.array = array;
    }

    public int size() {
        return array.length ;
    }

    public Iterator<T> iterator() {
        return new ObjectArrayIterator(array);
    }
}
