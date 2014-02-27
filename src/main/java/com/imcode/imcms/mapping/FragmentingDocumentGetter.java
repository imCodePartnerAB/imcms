package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.util.CompositeList;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Deprecated
public class FragmentingDocumentGetter extends DocumentGetterWrapper {

    private static final int DOCUMENTS_PER_FRAGMENT = 50;

    private final static Logger log = Logger.getLogger(FragmentingDocumentGetter.class);

    public FragmentingDocumentGetter(DocumentGetter documentGetter) {
        super(documentGetter);
    }

    public List<DocumentDomainObject> getDocuments(final Collection<Integer> documentIds) {
        if (documentIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> documentIdList = new ArrayList<Integer>(documentIds);
        CompositeList compositeDocumentList = new CompositeList();
        for (int i = 0; i < documentIds.size(); i += DOCUMENTS_PER_FRAGMENT) {
            int toIndex = Math.min(documentIds.size(), i + DOCUMENTS_PER_FRAGMENT);
            List<Integer> documentIdSubList = documentIdList.subList(i, toIndex);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            List<DocumentDomainObject> documentList = super.getDocuments(documentIdSubList);
            stopWatch.stop();
            if (log.isTraceEnabled()) {
                log.trace("Got " + documentList.size() + " documents in " + stopWatch.getTime() + "ms.");
            }
            compositeDocumentList.addList(documentList);
        }
        return compositeDocumentList;
    }
}
