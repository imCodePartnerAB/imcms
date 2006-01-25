package imcode.util;

import org.apache.commons.collections.map.AbstractMapDecorator;

import java.util.List;
import java.util.Map;

public class LfuMap extends AbstractMapDecorator {

    private final FrequencyOrderedBag requestsInMap = new FrequencyOrderedBag();
    private final FrequencyOrderedBag requestsOutsideMap = new FrequencyOrderedBag();
    private final int maxSize  ;

    public LfuMap(Map map, int maxSize) {
        super(map);
        this.maxSize = maxSize ;
    }

    public Object get(Object key) {
        Object result = map.get(key);
        if (null != result) {
            requestsInMap.add(key) ;
        } else {
            requestsOutsideMap.add(key) ;
        }
        return result ;
    }

    public Object put(Object key, Object value) {
        if ( !map.containsKey(key) && map.size() == maxSize && requestsInMap.size() >= maxSize ) {
            List list = requestsInMap.asList();
            Object lfuKey = list.get(maxSize-1);
            int keyFrequency = requestsOutsideMap.getFrequency(key);
            int lfuKeyFrequency = requestsInMap.getFrequency(lfuKey);
            if (keyFrequency > lfuKeyFrequency ) {
                remove(lfuKey) ;
            }
        }
        if ( map.size() < maxSize ) {
            Object result = map.put(key, value);
            if ( null == result ) {
                int frequency = requestsOutsideMap.remove(key);
                requestsInMap.add(key, frequency);
            }
            return result;
        }
        return null ;
    }

    public Object remove(Object key) {
        int frequency = requestsInMap.remove(key);
        requestsOutsideMap.add(key, frequency);
        return map.remove(key) ;
    }

}
