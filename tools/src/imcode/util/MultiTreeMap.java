package imcode.util;

import org.apache.commons.collections.MultiMap;

import java.util.*;

/**
 * @author kreiger
 */
public class MultiTreeMap extends TreeMap implements MultiMap {

    public MultiTreeMap() {
        super() ;
    }

    public MultiTreeMap(Comparator comparator) {
        super(comparator) ;
    }

    public Object put( Object key, Object value ) {
        if ( value instanceof ArrayList ) {
            super.put( key, value );
        }

        Collection previousValue = (Collection)super.get( key );
        Collection newValue;
        if ( null == previousValue ) {
            newValue = new ArrayList();
            super.put( key, newValue );
        } else {
            newValue = new ArrayList( previousValue );
        }
        newValue.add( value );
        return previousValue;
    }

    public boolean containsValue( Object value ) {
        Set pairs = super.entrySet();

        if ( pairs == null ) {
            return false;
        }

        Iterator pairsIterator = pairs.iterator();
        while ( pairsIterator.hasNext() ) {
            Map.Entry keyValuePair = (Map.Entry)( pairsIterator.next() );
            ArrayList list = (ArrayList)( keyValuePair.getValue() );
            if ( list.contains( value ) ) {
                return true;
            }
        }
        return false;
    }

    public Object remove( Object key, Object item ) {
        ArrayList valuesForKey = (ArrayList)super.get( key );

        if ( valuesForKey == null ) {
            return null;
        }

        valuesForKey.remove( item );
        return item;
    }

    public void clear() {
        Set pairs = super.entrySet();
        Iterator pairsIterator = pairs.iterator();
        while ( pairsIterator.hasNext() ) {
            Map.Entry keyValuePair = (Map.Entry)( pairsIterator.next() );
            ArrayList list = (ArrayList)( keyValuePair.getValue() );
            list.clear();
        }
        super.clear();
    }

    public void putAll( Map mapToPut ) {
        super.putAll( mapToPut );
    }

    public Collection values() {
        ArrayList returnList = new ArrayList( super.size() );

        Set pairs = super.entrySet();
        Iterator pairsIterator = pairs.iterator();
        while ( pairsIterator.hasNext() ) {
            Map.Entry keyValuePair = (Map.Entry)( pairsIterator.next() );
            ArrayList list = (ArrayList)( keyValuePair.getValue() );

            Object[] values = list.toArray();
            for ( int ii = 0; ii < values.length; ii++ ) {
                returnList.add( values[ii] );
            }
        }
        return returnList;
    }

}
