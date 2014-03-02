package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Image;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.TemplateNames;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts JPA entities to/from API and domain-objects.
 */
@Service
public class EntityConverter {

    @Inject
    private LanguageRepository languageRepository;


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

    public Loop fromEntity(com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop entity) {
        if (entity == null) return null;

        Map<Integer, Boolean> entries = new HashMap<>();

        for (com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry ormEntry : entity.getEntries()) {
            entries.put(ormEntry.getNo(), ormEntry.isEnabled());
        }

        return Loop.of(entries, entity.getNextEntryNo());
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


    //todo: set null vs
    public Image toEntity(ImageDomainObject image) {
        Image e = new Image();
        e.setAlign(image.getAlign());
        e.setAlternateText(image.getAlternateText());
        e.setBorder(image.getBorder());
        //e.setCropRegion()
        e.setFormat(image.getFormat() == null ? 0 : image.getFormat().getOrdinal());
        e.setGeneratedFilename(image.getGeneratedFilename());
        e.setHeight(image.getHeight());
        e.setHorizontalSpace(image.getHorizontalSpace());
        e.setUrl(image.getSource().toStorageString());
        e.setLinkUrl(image.getLinkUrl());

        e.setLowResolutionUrl(image.getLowResolutionUrl());
        e.setName(image.getName());
        e.setResize(image.getResize() == null ? 0 : image.getResize().getOrdinal());
        e.setRotateAngle(image.getRotateDirection() == null ? 0 : image.getRotateDirection().getAngle());
        e.setTarget(image.getTarget());
        e.setType(image.getSource().getTypeId());
        e.setVerticalSpace(image.getVerticalSpace());
        e.setWidth(image.getWidth());
        e.setHeight(image.getHeight());

        //e.setNo(image.get)
        //e.setDocVersion()
        //e.setId()
        //e.setLanguage()

        return e;
    }

//  public RotateDirection getRotateDirection() {
//    return RotateDirection.getByAngleDefaultIfNull(rotateAngle);
//  }
//
//  public void setRotateDirection(RotateDirection dir) {
//    this.rotateAngle = (short) (dir != null ? dir.getAngle() : 0);
//  }

}
