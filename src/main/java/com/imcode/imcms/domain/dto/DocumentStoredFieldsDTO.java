package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentStoredFields;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    private Date publishedDate;

    private Date modifiedDate;

    public DocumentStoredFieldsDTO(DocumentStoredFields from) {
        id = from.id();
        title = from.headline();
        type = from.documentType();
        documentStatus = from.documentStatus();
        alias = from.alias();
        publishedDate = from.publicationStart();
        modifiedDate = from.modified();
    }

}
