package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private Integer currentVersion;

    @JsonFormat(pattern = "yyyy-mm-dd hh:mm")
    private Date modified;

    @JsonFormat(pattern = "yyyy-mm-dd hh:mm")
    private Date published;

    public DocumentStoredFieldsDTO(DocumentStoredFields from) {
        id = from.id();
        title = from.headline();
        type = from.documentType();
        documentStatus = from.documentStatus();
        alias = from.alias();
        currentVersion = from.versionNo();
        modified = from.modified();
        published = from.publicationStart();
    }

}
