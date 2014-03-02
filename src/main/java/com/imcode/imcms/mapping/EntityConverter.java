package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Image;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TemplateNames;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
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



    public TextDocumentDomainObject.TemplateNames fromEntity(TemplateNames entity) {
        TextDocumentDomainObject.TemplateNames templateNames = new TextDocumentDomainObject.TemplateNames();

        templateNames.setDefaultTemplateName(entity.getDefaultTemplateName());
        templateNames.setDefaultTemplateNameForRestricted1(entity.getDefaultTemplateNameForRestricted1());
        templateNames.setDefaultTemplateNameForRestricted2(entity.getDefaultTemplateNameForRestricted2());
        templateNames.setTemplateGroupId(entity.getTemplateGroupId());
        templateNames.setTemplateName(entity.getTemplateName());

        return templateNames;
    }

    public TemplateNames toEntity(TextDocumentDomainObject.TemplateNames vo) {
        TemplateNames entity = new TemplateNames();

        entity.setDefaultTemplateName(vo.getDefaultTemplateName());
        entity.setDefaultTemplateNameForRestricted1(vo.getDefaultTemplateNameForRestricted1());
        entity.setDefaultTemplateNameForRestricted2(vo.getDefaultTemplateNameForRestricted2());
        entity.setTemplateGroupId(vo.getTemplateGroupId());
        entity.setTemplateName(vo.getTemplateName());

        return entity;
    }
}
