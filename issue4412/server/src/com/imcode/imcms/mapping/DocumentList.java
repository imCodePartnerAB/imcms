package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.*;
import java.io.Serializable;

class DocumentList extends AbstractList implements Serializable {

    private ArrayList list;
    private Map map;

    DocumentList(Map documentMap) {
        map = Collections.synchronizedMap(documentMap);
        list = new ArrayList(documentMap.size());
        for ( Iterator iterator = map.values().iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            list.add(document) ;
        }
    }

    public synchronized Object remove(int index) {
        Object o = list.remove(index);
        DocumentDomainObject document = (DocumentDomainObject) o;
        map.remove(new Integer(document.getId()));
        return o;
    }

    public synchronized Object set(int index, Object o) {
        DocumentDomainObject document = (DocumentDomainObject) o;
        DocumentDomainObject previousDocument = (DocumentDomainObject) list.set(index, o);
        if ( null != previousDocument ) {
            map.remove(new Integer(previousDocument.getId()));
        }
        map.put(new Integer(document.getId()), document);
        return previousDocument;
    }

    public synchronized Object get(int index) {
        return list.get(index);
    }

    public synchronized Iterator iterator() {
        return list.iterator();
    }

    public synchronized boolean add(Object o) {
        DocumentDomainObject document = (DocumentDomainObject) o;
        map.put(new Integer(document.getId()), document);
        return list.add(o);
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized Map getMap() {
        return map;
    }

    public boolean contains(Object o) {
        if (o instanceof Integer) {
            return map.containsKey(o) ;
        }
        DocumentDomainObject document = (DocumentDomainObject) o ;
        return map.containsKey(new Integer(document.getId())) ;
    }
}
