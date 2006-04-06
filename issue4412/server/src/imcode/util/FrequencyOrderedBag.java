package imcode.util;

import org.apache.commons.collections.map.AbstractHashedMap;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class FrequencyOrderedBag {

    private final FrequencyMap map;

    private List entries = new AbstractList() {
        private List list = new ArrayList();

        public Object get(int index) {
            return list.get(index);
        }

        public Object set(int index, Object element) {
            FrequencyMap.Entry link = (FrequencyMap.Entry) element;
            link.index = index;
            return list.set(index, link);
        }

        public void add(int index, Object element) {
            FrequencyMap.Entry link = (FrequencyMap.Entry) element;
            link.index = index;
            list.add(index, link);
        }

        public Object remove(int index) {
            Object result = list.remove(index);
            for ( int i = index; i < size(); ++i ) {
                FrequencyMap.Entry entry = (FrequencyMap.Entry) list.get(i);
                entry.index = i;
            }
            return result;
        }

        public int size() {
            return list.size();
        }
    };

    private AbstractList keys = new AbstractList() {
        public Object get(int index) {
            FrequencyMap.Entry entry = (FrequencyMap.Entry) entries.get(index);
            return entry.getKey();
        }

        public int size() {
            return entries.size();
        }
    };

    public FrequencyOrderedBag() {
        map = new FrequencyMap(10);
    }

    public int getFrequency(Object key) {
        return ( (Integer) map.get(key) ).intValue();
    }

    public int add(Object key) {
        return map.add(key);
    }

    public List asList() {
        return keys;
    }

    public int size() {
        return map.size();
    }

    public void add(Object key, int count) {
        map.add(key, count);
    }

    public String toString() {
        return entries.toString();
    }

    public int remove(Object key) {
        return ( (Integer) map.remove(key) ).intValue();
    }

    private class FrequencyMap extends AbstractHashedMap {

        FrequencyMap(int i) {
            super(i);
        }

        protected HashEntry createEntry(HashEntry next, int hashCode, Object key,
                                        Object value) {
            return new Entry(next, hashCode, key, value);
        }

        public Object get(Object key) {
            Integer frequency = (Integer) super.get(key);
            if ( null == frequency ) {
                frequency = new Integer(0);
            }
            return frequency;
        }

        public Object put(Object key, Object value) {
            if ( !( value instanceof Integer ) ) {
                throw new IllegalArgumentException("" + value);
            }
            return super.put(key, value);
        }

        protected void updateEntry(HashEntry entry, Object newValue) {
            Entry frequencyEntry = (Entry) entry;
            super.updateEntry(frequencyEntry, newValue);
            moveInPlace(frequencyEntry);
        }

        private void moveInPlace(Entry frequencyEntry) {
            while ( frequencyEntry.index > 0 && frequencyEntry.getFrequency() >= getFrequencyAt( frequencyEntry.index - 1) ) {
                int entryIndex = frequencyEntry.index;
                int previousEntryIndex = entryIndex - 1;
                entries.set(entryIndex, entries.set(previousEntryIndex, frequencyEntry));
            }
        }

        private int getFrequencyAt(int index) {
            FrequencyMap.Entry entry = (FrequencyMap.Entry) entries.get(index);
            return entry.getFrequency();
        }

        protected void addEntry(HashEntry entry, int hashIndex) {
            super.addEntry(entry, hashIndex);
            entries.add(entry);
            moveInPlace((Entry) entry);
        }

        public int add(Object key) {
            return add(key, 1);
        }

        private int add(Object key, int nCopies) {
            Entry entry = (Entry) getEntry(key);
            Integer frequency;
            if ( null == entry ) {
                frequency = new Integer(nCopies);
                put(key, frequency);
            } else {
                frequency = (Integer) entry.getValue();
                updateEntry(entry, new Integer(frequency.intValue() + nCopies));
            }
            return frequency.intValue() ;
        }

        public Object remove(Object key) {
            Entry entry = (Entry) getEntry(key);
            if ( null != entry ) {
                entries.remove(entry.index);
                return (Integer) super.remove(key);
            } else {
                return new Integer(0);
            }
        }

        private class Entry extends AbstractHashedMap.HashEntry {

            private int index = -1;

            private Entry(AbstractHashedMap.HashEntry next, int hashCode, Object key, Object value) {
                super(next, hashCode, key, value);
            }

            private int getFrequency() {
                return ( (Integer) value ).intValue();
            }
        }

    }
}

