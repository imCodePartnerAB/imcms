package imcode.server.document;

import imcode.util.LazilyLoadedObject;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

public class CopyableHashSet extends HashSet implements LazilyLoadedObject.Copyable {

    public CopyableHashSet() {
    }

    public CopyableHashSet(Collection collection) {
        super(collection) ;
    }

    public LazilyLoadedObject.Copyable copy() {
        return new CopyableHashSet(this);
    }
}
