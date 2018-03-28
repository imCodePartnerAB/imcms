package imcode.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeList<T> extends AbstractList<T> implements Serializable {

    private final List<List<T>> lists = Collections.synchronizedList(new ArrayList<>());

    public T get(int index) {
        return operateOnIndex(index, List::get);
    }

    public T remove(int index) {
        return operateOnIndex(index, (ts, o) -> ts.remove(o));
    }

    @Override
    public T set(int index, final T element) {
        return operateOnIndex(index, (list, index1) -> list.set(index1, element));
    }

    private T operateOnIndex(int index, ListIndexOperation<T> listIndexOperation) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("" + index);
        }
        synchronized (lists) {
            int firstIndexInList = 0;
            for (List<T> list : lists) {
                int indexInList = index - firstIndexInList;
                if (indexInList < list.size()) {
                    return listIndexOperation.operate(list, indexInList);
                }
                firstIndexInList += list.size();
            }
        }
        throw new IndexOutOfBoundsException("" + index);
    }

    public int size() {
        synchronized (lists) {
            int size = 0;
            for (List list : lists) {
                size += list.size();
            }
            return size;
        }
    }

    public boolean contains(Object o) {
        synchronized (lists) {
            for (List<T> list : lists) {
                if (list.contains(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addList(List<T> list) {
        synchronized (lists) {
            lists.add(list);
        }
    }

    private interface ListIndexOperation<T> {
        T operate(List<T> list, int index);
    }
}
