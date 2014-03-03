package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.Language;
import org.springframework.stereotype.Service;

/**
 * Converts JPA entities to/from API and domain-objects.
 */
@Service
public class EntityConverter {

    public DocumentVersion fromEntity(DocVersion entity) {
        return DocumentVersion.builder()
                .no(entity.getNo())
                .createdBy(entity.getCreatedBy().getId())
                .modifiedBy(entity.getModifiedBy().getId())
                .createdDt(entity.getCreatedDt())
                .modifiedDt(entity.getModifiedDt())
                .build();
    }

    public DocumentLanguage fromEntity(Language entity) {
        return entity == null
                ? null
                : DocumentLanguage.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .nativeName(entity.getNativeName())
                .build();
    }
}
