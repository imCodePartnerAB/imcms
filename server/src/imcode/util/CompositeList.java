package imcode.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

public class CompositeList extends AbstractList implements Serializable {

    private final List lists = Collections.synchronizedList(new ArrayList());

    public Object get(int index) {
        return operateOnIndex(index, new ListIndexOperation() {
            public Object operate(List list, int index) {
                return list.get(index);
            }
        });
    }

    public Object remove(int index) {
        return operateOnIndex(index, new ListIndexOperation() {
            public Object operate(List list, int index) {
                return list.remove(index);
            }
        });
    }

    public Object set(int index, final Object element) {
        return operateOnIndex(index, new ListIndexOperation() {
            public Object operate(List list, int index) {
                return list.set(index, element);
            }
        });
    }

    private Object operateOnIndex(int index, ListIndexOperation listIndexOperation) {
        if ( index < 0 ) {
            throw new IndexOutOfBoundsException("" + index);
        }
        synchronized ( lists ) {
            int firstIndexInList = 0;
            for ( Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                List list = (List) iterator.next();
                int indexInList = index - firstIndexInList;
                if ( indexInList < list.size() ) {
                    return listIndexOperation.operate(list, indexInList);
                }
                firstIndexInList += list.size();
            }
        }
        throw new IndexOutOfBoundsException("" + index);
    }

    public int size() {
        synchronized ( lists ) {
            int size = 0;
            for ( Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                List list = (List) iterator.next();
                size += list.size();
            }
            return size;
        }
    }

    public boolean contains(Object o) {
        synchronized (lists) {
            for ( Iterator iterator = lists.iterator(); iterator.hasNext(); ) {
                List list = (List) iterator.next();
                if (list.contains(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addList(List list) {
        synchronized (lists) {
            lists.add(list);
        }
    }

    private static interface ListIndexOperation {

        Object operate(List list, int index);
    }

}
