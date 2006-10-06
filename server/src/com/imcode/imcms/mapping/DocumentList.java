package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.*;
import java.io.Serializable;

class DocumentList extends AbstractList<DocumentDomainObject> implements Serializable {

    private List<DocumentDomainObject> list;
    private Map<Integer, DocumentDomainObject> map;

    DocumentList(Map documentMap) {
        map = Collections.synchronizedMap(documentMap);
        list = new ArrayList(documentMap.size());
        for ( Object result : map.values() ) {
            DocumentDomainObject document = (DocumentDomainObject) result;
            list.add(document);
        }
    }

    public synchronized DocumentDomainObject remove(int index) {
        DocumentDomainObject document = list.remove(index);
        document.loadAllLazilyLoaded();
        map.remove(new Integer(document.getId()));
        return document;
    }

    public synchronized DocumentDomainObject set(int index, DocumentDomainObject document) {
        DocumentDomainObject previousDocument = list.set(index, document);
        if ( null != previousDocument ) {
            map.remove(new Integer(previousDocument.getId()));
        }
        map.put(new Integer(document.getId()), document);
        return previousDocument;
    }

    public synchronized DocumentDomainObject get(int index) {
        return list.get(index);
    }

    public synchronized boolean add(DocumentDomainObject document) {
        map.put(new Integer(document.getId()), document);
        return list.add(document);
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized Map getMap() {
        return map;
    }

    public synchronized boolean contains(Object o) {
        Integer documentId;
        if (o instanceof Integer) {
            documentId = (Integer) o;
        } else {
            DocumentDomainObject document = (DocumentDomainObject) o ;
            documentId = document.getId();
        }
        return map.containsKey(documentId) ;
    }
}
