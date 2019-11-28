package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentStoredFields;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document's stored fields in solr indexes
 */
@Data
@NoArgsConstructor
public class DocumentStoredFieldsDTO {

    private Integer id;

    private String title;

    private Meta.DocumentType type;

    private DocumentStatus documentStatus;

    private String alias;

    private boolean hasNewerVersion;

    public DocumentStoredFieldsDTO(DocumentStoredFields from) {
        id = from.id();
        title = from.headline();
        type = from.documentType();
        documentStatus = from.documentStatus();
        alias = from.alias();
        hasNewerVersion = from.versionNo() == 0; //working version == 0
    }

}
