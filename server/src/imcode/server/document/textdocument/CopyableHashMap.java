package imcode.server.document.textdocument;

import imcode.util.LazilyLoadedObject;

import java.util.HashMap;
import java.util.Map;

public class CopyableHashMap extends HashMap implements LazilyLoadedObject.Copyable {

    public CopyableHashMap() {
    }

    public CopyableHashMap(Map m) {
        super(m);
    }

    public LazilyLoadedObject.Copyable copy() {
        return new CopyableHashMap(this) ;
    }
}
