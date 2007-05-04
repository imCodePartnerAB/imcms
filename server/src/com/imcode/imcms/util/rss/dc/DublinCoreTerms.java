package com.imcode.imcms.util.rss.dc;

import com.imcode.imcms.util.rss.NameSpace;
import com.imcode.imcms.util.rss.SimpleNameSpace;

import java.util.Date;

public interface DublinCoreTerms {

    String DUBLIN_CORE_METADATA_ELEMENTS_NAMESPACE_URI = "http://purl.org/dc/elements/1.1/";
    String DUBLIN_CORE_METADATA_TERMS_NAMESPACE_URI = "http://purl.org/dc/terms/";
    NameSpace DUBLIN_CORE_ELEMENTS_NAME_SPACE = new SimpleNameSpace("dc", DUBLIN_CORE_METADATA_ELEMENTS_NAMESPACE_URI);
    NameSpace DUBLIN_CORE_TERMS_NAME_SPACE = new SimpleNameSpace("dcterms",DUBLIN_CORE_METADATA_TERMS_NAMESPACE_URI);

    Date getIssued();

    Date getModified();

    Date getCreated();

    DublinCoreEntity getCreator();

    String getDescription();

    String getTitle();

    String getIdentifer();

}