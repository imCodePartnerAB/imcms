package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.List;
import java.util.Collection;

public interface DocumentGetter {
    
    /** Return a list of documents <em>in the same order</em> as the documentIds */ 
    List getDocuments(Collection documentIds);

    DocumentDomainObject getDocument(Integer documentId);
}
