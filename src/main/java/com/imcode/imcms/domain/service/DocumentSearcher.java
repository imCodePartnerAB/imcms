package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;

import java.util.List;

public interface DocumentSearcher<D extends Document> {

    List<D> getDocumentsByTemplateName(String templateName);

    int countDocumentsByTemplateName(String templateName);
}
