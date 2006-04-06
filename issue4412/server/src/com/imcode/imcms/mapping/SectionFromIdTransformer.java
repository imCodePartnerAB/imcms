package com.imcode.imcms.mapping;

import org.apache.commons.collections.Transformer;
import imcode.server.ImcmsServices;

public class SectionFromIdTransformer implements Transformer {

    private ImcmsServices services;

    public SectionFromIdTransformer(ImcmsServices services) {
        this.services = services ;
    }

    public Object transform(Object input) {
        return services.getDocumentMapper().getSectionById(((Integer)input).intValue()) ;
    }
}
