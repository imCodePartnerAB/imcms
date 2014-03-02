package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.jpa.doc.*;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.textdocument.*;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

  public DocumentCommonContent fromEntity(CommonContent entity) {
      return entity == null
      ? null
              :DocumentCommonContent.builder()
              .headline(entity.getHeadline())
              .menuImageURL(entity.getMenuImageURL())
              .menuText(entity.getMenuText())
              .build();
  }

    public DocumentMeta fromEntity(Meta entity) {
        if (entity == null) return null;

        DocumentMeta m = new DocumentMeta();

        m.setArchivedDatetime(entity.getArchivedDatetime());
        m.setCategoryIds(entity.getCategoryIds());
        m.setCreatedDatetime(entity.getCreatedDatetime());
        m.setCreatorId(entity.getCreatorId());
        m.setDefaultVersionNo(entity.getDefaultVersionNo());
        m.setDisabledLanguageShowMode(DocumentMeta.DisabledLanguageShowMode.valueOf(entity.getDisabledLanguageShowMode().name()));
        m.setDocumentType(entity.getDocumentType());
        //fixme
        //m.setEnabledLanguages(entity.getEnabledLanguages().asScala.map(fromEntity).asJava)
        m.setId(entity.getId());
        m.setKeywords(entity.getKeywords());
        m.setLinkableByOtherUsers(entity.getLinkableByOtherUsers());
        m.setLinkedForUnauthorizedUsers(entity.getLinkedForUnauthorizedUsers());
        m.setModifiedDatetime(entity.getModifiedDatetime());
        //m.setPermissionSets(entity.getPermissionSets)
        //m.setPermissionSetsForNew(entity.getPermissionSetExForNew)
        //m.setPermissionSetsForNewDocuments(entity.getPermissionSetsForNewDocuments)
        m.setProperties(entity.getProperties());
        m.setPublicationEndDatetime(entity.getPublicationEndDatetime());
        m.setPublicationStartDatetime(entity.getPublicationStartDatetime());
        //m.setPublicationStatus(entity.getPublicationStatusInt)
        m.setPublisherId(entity.getPublisherId());
        m.setRestrictedOneMorePrivilegedThanRestrictedTwo(entity.getRestrictedOneMorePrivilegedThanRestrictedTwo());
        //m.setRoleIdToDocumentPermissionSetTypeMappings()
        m.setSearchDisabled(entity.getSearchDisabled());
        m.setTarget(entity.getTarget());


        return m;
  }

  public Meta toEntity(DocumentMeta m) {
      Meta e = new Meta();

    e.setArchivedDatetime(m.getArchivedDatetime());
    e.setCategoryIds(m.getCategoryIds());
    e.setCreatedDatetime(m.getCreatedDatetime());
    e.setCreatorId(m.getCreatorId());
    e.setDefaultVersionNo(m.getDefaultVersionNo());
    e.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.valueOf(m.getDisabledLanguageShowMode().name()));
    e.setDocumentType(m.getDocumentType());
    //e.setEnabledLanguages()
    e.setId(m.getId());
    e.setKeywords(m.getKeywords());
    e.setLinkableByOtherUsers(m.getLinkableByOtherUsers());
    e.setLinkedForUnauthorizedUsers(m.getLinkedForUnauthorizedUsers());
    e.setModifiedDatetime(m.getModifiedDatetime());
    //e.setPermissionSets(m.getPermissionSets)
    //e.setPermissionSetsForNew(m.getPermissionSetExForNew)
    //e.setPermissionSetsForNewDocuments(m.getPermissionSetsForNewDocuments)
    e.setProperties(m.getProperties());
    e.setPublicationEndDatetime(m.getPublicationEndDatetime());
    e.setPublicationStartDatetime(m.getPublicationStartDatetime()) ;
    e.setPublicationStatusInt(m.getPublicationStatus().asInt());
    e.setPublisherId(m.getPublisherId());
    e.setRestrictedOneMorePrivilegedThanRestrictedTwo(m.getRestrictedOneMorePrivilegedThanRestrictedTwo());
    //e.setRoleIdToPermissionSetIdMap()
    e.setSearchDisabled(m.getSearchDisabled());
    e.setTarget(m.getTarget());

            return e;
  }


  public TextDomainObject fromEntity(Text entity){
    return entity == null
      ? null
      : new TextDomainObject(entity.getText(),entity.getType().ordinal() );
  }

  public Loop fromEntity(com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop entity) {
      if (entity == null) return null;

      Map<Integer, Boolean> entries = new HashMap<>();

      for (com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry ormEntry : entity.getEntries()) {
          entries.put(ormEntry.getNo(), ormEntry.isEnabled());
      }

    return Loop.of(entries, entity.getNextEntryNo());
  }

  public CategoryTypeDomainObject fromEntity(CategoryType entity) {
      return entity == null
              ? null
              : new CategoryTypeDomainObject(
    entity.getId(),
    entity.getName(),
    entity.getMaxChoices(),
    entity.isInherited(),
    entity.isImageArchive()
  );
  }

  public CategoryDomainObject fromEntity(Category entity) {
      return entity == null
              ? null
              : new CategoryDomainObject(
    entity.getId(),
    entity.getName(),
    entity.getDescription(),
    entity.getImageUrl(),
    fromEntity(entity.getType())
  );
  }


  public CategoryType toEntity(CategoryTypeDomainObject c) {
      return new CategoryType(
              c.getId(), c.getName(), c.getMaxChoices(), c.isInherited(), c.isImageArchive()
      );
  }

  public Category toEntity(CategoryDomainObject c) {
      return new Category(
              c.getId(), c.getName(), c.getDescription(), c.getImageUrl(), toEntity(c.getType())
      );
  }

  public TextDocumentDomainObject.TemplateNames fromEntity(TemplateNames entity) {
      TextDocumentDomainObject.TemplateNames templateNames = new TextDocumentDomainObject.TemplateNames();

      templateNames.setDefaultTemplateName(entity.getDefaultTemplateName())                              ;
      templateNames.setDefaultTemplateNameForRestricted1(entity.getDefaultTemplateNameForRestricted1()) ;
      templateNames.setDefaultTemplateNameForRestricted2(entity.getDefaultTemplateNameForRestricted2());
      templateNames.setTemplateGroupId(entity.getTemplateGroupId());
      templateNames.setTemplateName(entity.getTemplateName());

      return templateNames;
  }

  public TemplateNames toEntity(TextDocumentDomainObject.TemplateNames vo) {
      TemplateNames entity = new TemplateNames();

      entity.setDefaultTemplateName(vo.getDefaultTemplateName())                              ;
      entity.setDefaultTemplateNameForRestricted1(vo.getDefaultTemplateNameForRestricted1()) ;
      entity.setDefaultTemplateNameForRestricted2(vo.getDefaultTemplateNameForRestricted2());
      entity.setTemplateGroupId(vo.getTemplateGroupId());
      entity.setTemplateName(vo.getTemplateName());

      return entity;
  }

  // todo: fixme!!! - init items
  public MenuDomainObject fromEntity(Menu entity) {
      MenuDomainObject menu = new MenuDomainObject();

      menu.setSortOrder(entity.getSortOrder());

      for (Map.Entry<Integer, MenuItem> e : entity.getItems().entrySet()) {
       // menu.addMenuItemUnchecked(fromEntity());
      }

      return menu;

  }

  public MenuItemDomainObject fromEntity( MenuItem entity) {
      MenuItemDomainObject item = new MenuItemDomainObject();

      item.setSortKey(entity.getSortKey());
      item.setTreeSortIndex(entity.getTreeSortIndex());

      return item;
  }

  //fixme; implement
  public ImageDomainObject fromEntity(Image entity) {
      throw new NotImplementedException();
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
