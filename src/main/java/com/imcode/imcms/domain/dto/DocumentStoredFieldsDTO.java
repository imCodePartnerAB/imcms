package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentStoredFields;
    import imcode.util.Utility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime published;

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
        created = Utility.convertDateToLocalDateTime(from.created());
        modified = Utility.convertDateToLocalDateTime(from.modified());
        published = Utility.convertDateToLocalDateTime(from.publicationStart());
        createdBy = from.createdBy();
        publishedBy = from.publicationStartBy();
        modifiedBy = from.modifiedBy();
    }

}
