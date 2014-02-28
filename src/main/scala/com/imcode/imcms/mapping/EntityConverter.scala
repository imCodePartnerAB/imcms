package com.imcode
package imcms.mapping

import com.imcode.imcms.api
import com.imcode.imcms.mapping.container.TextDocImageContainer
import com.imcode.imcms.mapping.jpa.doc
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent
import com.imcode.imcms.mapping.jpa.doc.content.textdoc._
import imcode.server.document.textdocument._

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

  def fromEntity(entity: doc.Language): api.DocumentLanguage = entity match {
    case null => null
    case _ =>
      api.DocumentLanguage.builder
      .code(entity.getCode)
      .name(entity.getName)
      .nativeName(entity.getNativeName)
      .build()
  }


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
    m.setDisabledLanguageShowMode(DocumentMeta.DisabledLanguageShowMode.valueOf(entity.getDisabledLanguageShowMode.name()))
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
    //m.setRoleIdToDocumentPermissionSetTypeMappings()
    m.setSearchDisabled(entity.getSearchDisabled)
    m.setTarget(entity.getTarget)
  }

  def toEntity(m: DocumentMeta): doc.Meta = new doc.Meta |>> { e =>
    e.setArchivedDatetime(m.getArchivedDatetime)
    e.setCategoryIds(m.getCategoryIds)
    e.setCreatedDatetime(m.getCreatedDatetime)
    e.setCreatorId(m.getCreatorId)
    e.setDefaultVersionNo(m.getDefaultVersionNo)
    e.setDisabledLanguageShowMode(doc.Meta.DisabledLanguageShowMode.valueOf(m.getDisabledLanguageShowMode.name()))
    e.setDocumentType(m.getDocumentType)
    //e.setEnabledLanguages()
    e.setId(m.getId)
    e.setKeywords(m.getKeywords)
    e.setLinkableByOtherUsers(m.getLinkableByOtherUsers)
    e.setLinkedForUnauthorizedUsers(m.getLinkedForUnauthorizedUsers)
    e.setModifiedDatetime(m.getModifiedDatetime)
    //e.setPermissionSets(m.getPermissionSets)
    //e.setPermissionSetsForNew(m.getPermissionSetExForNew)
    //e.setPermissionSetsForNewDocuments(m.getPermissionSetsForNewDocuments)
    e.setProperties(m.getProperties)
    e.setPublicationEndDatetime(m.getPublicationEndDatetime)
    e.setPublicationStartDatetime(m.getPublicationStartDatetime)
    e.setPublicationStatus(m.getPublicationStatus.asInt())
    e.setPublisherId(m.getPublisherId)
    e.setRestrictedOneMorePrivilegedThanRestrictedTwo(m.getRestrictedOneMorePrivilegedThanRestrictedTwo)
    //e.setRoleIdToPermissionSetIdMap()
    e.setSearchDisabled(m.getSearchDisabled)
    e.setTarget(m.getTarget)
  }


  def fromEntity(entity: Text): TextDomainObject = entity match {
    case null => null
    case _ => new TextDomainObject |>> { t=>
      t.setText(entity.getText)
      t.setType(entity.getType.ordinal())
    }
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


  def toEntity(imageContainer: TextDocImageContainer): doc.content.textdoc.Image = {

  }

  //todo: set null vs
  def toEntity(image: ImageDomainObject): doc.content.textdoc.Image = new doc.content.textdoc.Image |>> { e =>
    e.setAlign(image.getAlign)
    e.setAlternateText(image.getAlternateText)
    e.setBorder(image.getBorder)
    //e.setCropRegion()
    e.setFormat(if (image.getFormat == null) 0 else image.getFormat.getOrdinal)
    e.setGeneratedFilename(image.getGeneratedFilename)
    e.setHeight(image.getHeight)
    e.setHorizontalSpace(image.getHorizontalSpace)
    e.setImageUrl(image.getSource.toStorageString)
    e.setLinkUrl(image.getLinkUrl)

    e.setLowResolutionUrl(image.getLowResolutionUrl)
    e.setName(image.getName)
    e.setResize(if (image.getResize == null) 0 else image.getResize.getOrdinal)
    e.setRotateAngle(if (image.getRotateDirection == null) 0 else image.getRotateDirection.getAngle)
    e.setTarget(image.getTarget)
    e.setType(image.getSource.getTypeId)
    e.setVerticalSpace(image.getVerticalSpace)
    e.setWidth(image.getWidth)
    e.setHeight(image.getHeight)

    //e.setNo(image.get)
    //e.setDocVersion()
    //e.setId()
    //e.setLanguage()
  }

//  public RotateDirection getRotateDirection() {
//    return RotateDirection.getByAngleDefaultIfNull(rotateAngle);
//  }
//
//  public void setRotateDirection(RotateDirection dir) {
//    this.rotateAngle = (short) (dir != null ? dir.getAngle() : 0);
//  }

}
