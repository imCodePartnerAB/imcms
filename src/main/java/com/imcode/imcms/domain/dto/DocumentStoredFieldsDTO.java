package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentStoredFields;
import lombok.Data;

/**
 * Document's stored fields in solr indexes
 */
@Data
public class DocumentStoredFieldsDTO {

    protected Integer id;

    protected String title;

    protected Meta.DocumentType type;

    protected String alias;

    public DocumentStoredFieldsDTO(DocumentStoredFields from) {
        id = from.id();
        title = from.headline();
        type = Meta.DocumentType.values()[from.documentType()];
        alias = from.alias();
    }

}
