package com.imcode.imcms.mapping;

import imcode.server.document.textdocument.MenuDomainObject;
import imcode.util.LazilyLoadedObject;

import java.util.HashMap;
import java.util.Iterator;

public class DocumentMenusMap extends HashMap<Integer, MenuDomainObject> implements LazilyLoadedObject.Copyable<DocumentMenusMap> {

    public DocumentMenusMap copy() {
        DocumentMenusMap menusClone = new DocumentMenusMap();
        final Iterator<Entry<Integer, MenuDomainObject>> iterator;

        for (iterator = entrySet().iterator(); iterator.hasNext(); ) {
            final Entry<Integer, MenuDomainObject> entry = iterator.next();
            Integer menuIndex = entry.getKey();
            MenuDomainObject menu = entry.getValue();
            menusClone.put(menuIndex, menu.clone());
        }
        return menusClone;
    }
}
