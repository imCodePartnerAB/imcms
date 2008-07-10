package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;

import org.apache.commons.collections.Transformer;

public class SectionFromIdTransformer implements Transformer {

    private ImcmsServices services;

    public SectionFromIdTransformer(ImcmsServices services) {
        this.services = services ;
    }

    public Object transform(Object input) {
        return services.getDocumentMapper().getSectionById(((Integer)input).intValue()) ;
    }
}
