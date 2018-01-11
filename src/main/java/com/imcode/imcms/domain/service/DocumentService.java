package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;

public interface DocumentService<D extends Document>
        extends EmptyDocumentCreatingService<D>, BasicDocumentService<D>, DocumentSaver<D> {
}
