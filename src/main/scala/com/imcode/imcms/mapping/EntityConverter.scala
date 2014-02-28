package com.imcode
package imcms.mapping

import com.imcode.imcms.api
import com.imcode.imcms.mapping.jpa.doc
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent
import com.imcode.imcms.mapping.jpa.doc.content.textdoc._
import imcode.server.document.textdocument.{MenuDomainObject, MenuItemDomainObject, TextDocumentDomainObject, TextDomainObject}

import scala.collection.JavaConverters._
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}

/**
 * Converts JPA entities to/from API and domain-objects.
 */
object EntityConverter {

  def fromEntity(entity: doc.DocVersion): api.DocumentVersion = api.DocumentVersion.builder
    .no(entity.getNo)
    .createdBy(entity.getCreatedBy.getId)
    .modifiedBy(entity.getModifiedBy.getId)
    .createdDt(entity.getCreatedDt)
    .modifiedDt(entity.getModifiedDt)
    .build()

  def fromEntity(entity: doc.Language): api.DocumentLanguage = api.DocumentLanguage.builder
    .code(entity.getCode)
    .name(entity.getName)
    .nativeName(entity.getNativeName)
    .build()


  def fromEntity(entity: CommonContent): DocumentCommonContent = DocumentCommonContent.builder
    .headline(entity.getHeadline)
    .menuImageURL(entity.getMenuImageURL)
    .menuText(entity.getMenuText)
    .build()

  def fromEntity(entity: doc.Meta): DocumentMeta = new DocumentMeta |>> { m =>
    m.setArchivedDatetime(entity.getArchivedDatetime)
    m.setCategoryIds(entity.getCategoryIds)
    m.setCreatedDatetime(entity.getCreatedDatetime)
    m.setCreatorId(entity.getCreatorId)
    m.setDefaultVersionNo(entity.getDefaultVersionNo)
    m.setDisabledLanguageShowSetting(DocumentMeta.DisabledLanguageShowSetting.values()(entity.getDisabledLanguageShowSetting.ordinal()))
    m.setDocumentType(entity.getDocumentType)
    m.setEnabledLanguages(entity.getEnabledLanguages.asScala.map(fromEntity).asJava)
    m.setId(entity.getId)
    m.setKeywords(entity.getKeywords)
    m.setLinkableByOtherUsers(entity.getLinkableByOtherUsers)
    m.setLinkedForUnauthorizedUsers(entity.getLinkedForUnauthorizedUsers)
    m.setModifiedDatetime(entity.getModifiedDatetime)
    //m.setPermissionSets(entity.getPermissionSets)
    //m.setPermissionSetsForNew(entity.getPermissionSetExForNew)
    //m.setPermissionSetsForNewDocuments(entity.getPermissionSetsForNewDocuments)
    m.setProperties(entity.getProperties)
    m.setPublicationEndDatetime(entity.getPublicationEndDatetime)
    m.setPublicationStartDatetime(entity.getPublicationStartDatetime)
    //m.setPublicationStatus(entity.getPublicationStatusInt)
    m.setPublisherId(entity.getPublisherId)
    m.setRestrictedOneMorePrivilegedThanRestrictedTwo(entity.getRestrictedOneMorePrivilegedThanRestrictedTwo)
    m.setSearchDisabled(entity.getSearchDisabled)
    m.setTarget(entity.getTarget)
  }


  def fromEntity(entity: Text): TextDomainObject = new TextDomainObject |>> { t=>
    t.setText(entity.getText)
    t.setType(entity.getType.ordinal())
  }


  def fromEntity(entity: Loop): api.Loop = {
    val entries = entity.getEntries.asScala.map(e => Int.box(e.getNo) -> Boolean.box(e.isEnabled)).toMap.asJava
    api.Loop.of(entries, entity.getNextEntryNo)
  }

  def fromEntity(entity: doc.CategoryType): CategoryTypeDomainObject = new CategoryTypeDomainObject(
    entity.getId,
    entity.getName,
    entity.getMaxChoices,
    entity.isInherited,
    entity.isImageArchive
  )

  def fromEntity(entity: doc.Category): CategoryDomainObject = new CategoryDomainObject(
    entity.getId,
    entity.getName,
    entity.getDescription,
    entity.getImageUrl,
    fromEntity(entity.getType)
  )

  def toEntity(vo: CategoryTypeDomainObject): doc.CategoryType = new doc.CategoryType(
    vo.getId, vo.getName, vo.getMaxChoices, vo.isInherited, vo.isImageArchive
  )

  def toEntity(vo: CategoryDomainObject): doc.Category = new doc.Category(
    vo.getId, vo.getName, vo.getDescription, vo.getImageUrl, toEntity(vo.getType)
  )

  def fromEntity(entity: TemplateNames): TextDocumentDomainObject.TemplateNames = new TextDocumentDomainObject.TemplateNames |>> { vo =>
    vo.setDefaultTemplateName(entity.getDefaultTemplateName)
    vo.setDefaultTemplateNameForRestricted1(entity.getDefaultTemplateNameForRestricted1)
    vo.setDefaultTemplateNameForRestricted2(entity.getDefaultTemplateNameForRestricted2)
    vo.setTemplateGroupId(entity.getTemplateGroupId)
    vo.setTemplateName(entity.getTemplateName)
  }

  def toEntity(vo: TextDocumentDomainObject.TemplateNames): TemplateNames = new TemplateNames |>> { entity =>
    entity.setDefaultTemplateName(vo.getDefaultTemplateName)
    entity.setDefaultTemplateNameForRestricted1(vo.getDefaultTemplateNameForRestricted1)
    entity.setDefaultTemplateNameForRestricted2(vo.getDefaultTemplateNameForRestricted2)
    entity.setTemplateGroupId(vo.getTemplateGroupId)
    entity.setTemplateName(vo.getTemplateName)
  }


  def fromEntity(entity: Menu): MenuDomainObject = new MenuDomainObject |>> { vo =>
    vo.setSortOrder(entity.getSortOrder)
    entity.getItems.asScala.foreach {
      case (toDocId, textDocMenuItem) => vo.addMenuItemUnchecked(fromEntity(textDocMenuItem))
    }
  }

  def fromEntity(entity: MenuItem): MenuItemDomainObject = new MenuItemDomainObject |>> { vo =>
    vo.setSortKey(entity.getSortKey)
    vo.setTreeSortIndex(entity.getTreeSortIndex)
  }

}
