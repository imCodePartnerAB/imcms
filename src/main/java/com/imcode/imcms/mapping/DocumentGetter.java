package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** Return a list of documents <em>in the same order</em> as the documentIds */ 
    List getDocuments(Collection documentIds);

    DocumentDomainObject getDocument(Integer documentId);
}
