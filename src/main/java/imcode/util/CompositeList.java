package imcode.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeList extends AbstractList implements Serializable {

	private final List<List> lists = Collections.synchronizedList(new ArrayList());

	public Object get(int index) {
		return operateOnIndex(index, List::get);
	}

	public Object remove(int index) {
		return operateOnIndex(index, List::remove);
	}

	public Object set(int index, final Object element) {
		return operateOnIndex(index, (list, index1) -> list.set(index1, element));
	}

	private Object operateOnIndex(int index, ListIndexOperation listIndexOperation) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("" + index);
		}
		synchronized (lists) {
			int firstIndexInList = 0;
			for (List list : lists) {
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
			for (List list : lists) {
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

	private interface ListIndexOperation {
		Object operate(List list, int index);
	}
}
