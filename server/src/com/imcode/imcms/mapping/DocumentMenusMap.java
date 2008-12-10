package com.imcode.imcms.mapping;

import imcode.util.LazilyLoadedObject;
import imcode.util.ShouldNotBeThrownException;
import imcode.server.document.textdocument.MenuDomainObject;

import java.util.*;

public class DocumentMenusMap extends AbstractMap implements LazilyLoadedObject.Copyable<DocumentMenusMap> {
    private final HashMap menusMap = new HashMap();

    public DocumentMenusMap copy() {
        DocumentMenusMap menusClone = new DocumentMenusMap() ;
        for ( Iterator iterator = entrySet().iterator(); iterator.hasNext(); ) {
            Entry entry = (Entry)iterator.next();
            Integer menuIndex = (Integer)entry.getKey();
            MenuDomainObject menu = (MenuDomainObject)entry.getValue();
            try {
                menusClone.put(menuIndex, menu.clone()) ;
            } catch ( CloneNotSupportedException e ) {
                throw new ShouldNotBeThrownException(e) ;
            }
        }
        return menusClone ;
    }

    public void clear() {
        menusMap.clear();
    }

    public boolean containsKey(Object key) {
        return menusMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return menusMap.containsValue(value);
    }

    public Set entrySet() {
        return menusMap.entrySet();
    }

    public Object get(Object key) {
        return menusMap.get(key);
    }

    public boolean isEmpty() {
        return menusMap.isEmpty();
    }

    public Set keySet() {
        return menusMap.keySet();
    }

    public Object put(Object key, Object value) {
        return menusMap.put(key, value);
    }

    public void putAll(Map m) {
        menusMap.putAll(m);
    }

    public Object remove(Object key) {
        return menusMap.remove(key);
    }

    public int size() {
        return menusMap.size();
    }

    public Collection values() {
        return menusMap.values();
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
