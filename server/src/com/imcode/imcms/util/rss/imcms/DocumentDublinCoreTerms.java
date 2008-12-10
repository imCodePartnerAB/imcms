package com.imcode.imcms.util.rss.imcms;

import imcode.util.Utility;

import java.util.Date;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.util.rss.dc.DublinCoreTerms;
import com.imcode.imcms.util.rss.dc.DublinCoreEntity;

public class DocumentDublinCoreTerms implements DublinCoreTerms {

    private final String urlRoot;
    private Document document;

    public DocumentDublinCoreTerms(String urlRoot, Document document) {
        this.urlRoot = urlRoot;
        this.document = document;
    }

    public Date getIssued() {
        return document.getPublicationStartDatetime();
    }

    public Date getModified() {
        return document.getModifiedDatetime();
    }

    public Date getCreated() {
        return document.getCreatedDatetime();
    }

    public DublinCoreEntity getCreator() {
        return new DublinCoreEntity() {
            public String getEmailAddress() {
                return document.getCreator().getEmailAddress();
            }

            public String getName() {
                return document.getCreator().getFirstName()+" "+document.getCreator().getLastName();
            }
        };
    }

    public String getDescription() {
        return document.getMenuText();
    }

    public String getTitle() {
        return document.getHeadline();
    }

    public String getIdentifer() {
        return urlRoot + Utility
                .getContextRelativePathToDocumentWithName(document.getName());
    }

}
