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

    private Integer currentVersion;

    private Boolean isShownTitle;

    private String created;

    private String modified;

    private String published;

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
        isShownTitle = from.isShownTitle();
        created = from.created();
        modified = from.modified();
        published = from.publicationStart();
        createdBy = from.createdBy();
        publishedBy = from.publicationStartBy();
        modifiedBy = from.modifiedBy();
    }

}
