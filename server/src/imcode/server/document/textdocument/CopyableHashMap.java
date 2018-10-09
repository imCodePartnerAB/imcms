package imcode.server.document.textdocument;

import imcode.util.LazilyLoadedObject;

import java.util.HashMap;
import java.util.Map;

public class CopyableHashMap<K, V> extends HashMap<K, V> implements LazilyLoadedObject.Copyable<CopyableHashMap<K, V>> {

    public CopyableHashMap() {
    }

    public CopyableHashMap(Map<K, V> m) {
        super(m);
    }

    public CopyableHashMap<K, V> copy() {
        return new CopyableHashMap<>(this);
    }
}
