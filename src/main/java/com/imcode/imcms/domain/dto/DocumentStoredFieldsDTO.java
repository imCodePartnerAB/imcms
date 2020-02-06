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

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date created;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date modified;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date published;

    private String createdBy;

    private String modifiedBy;

    private String publishedBy;

    public DocumentStoredFieldsDTO(DocumentStoredFields from) {
        id = from.id();
        title = from.headline();
        type = from.documentType();
        documentStatus = from.documentStatus();
        alias = from.alias();
        currentVersion = from.versionNo();
        created = from.created();
        modified = from.modified();
        published = from.publicationStart();
        createdBy = from.createdBy();
        publishedBy = from.publicationStartBy();
        modifiedBy = from.modifiedBy();
    }

}
