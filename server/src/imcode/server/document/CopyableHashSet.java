package imcode.server.document;

import imcode.util.LazilyLoadedObject;

import java.util.Collection;
import java.util.HashSet;

public class CopyableHashSet<K> extends HashSet<K> implements LazilyLoadedObject.Copyable<CopyableHashSet<K>> {

    public CopyableHashSet() {
    }

    public CopyableHashSet(Collection<K> collection) {
        super(collection);
    }

    public CopyableHashSet<K> copy() {
        return new CopyableHashSet<>(this);
    }
}
