package com.imcode
package imcms.mapping

import com.imcode.imcms.api._
import com.imcode.imcms.mapping.orm._
import imcode.server.document.textdocument.{MenuDomainObject, MenuItemDomainObject, TextDocumentDomainObject, TextDomainObject}

import scala.collection.JavaConverters._
import imcode.server.document.{CategoryTypeDomainObject, CategoryDomainObject}

object OrmToApi {

  def toApi(orm: DocVersion): DocumentVersion = DocumentVersion.builder
    .no(orm.getNo)
    .createdBy(orm.getCreatedBy.getId)
    .modifiedBy(orm.getModifiedBy.getId)
    .createdDt(orm.getCreatedDt)
    .modifiedDt(orm.getModifiedDt)
    .build()

  def toApi(orm: DocLanguage): DocumentLanguage = DocumentLanguage.builder
    .code(orm.getCode)
    .name(orm.getName)
    .nativeName(orm.getNativeName)
    .enabled(orm.isEnabled)
    .build()


  def toApi(orm: DocCommonContent): DocumentCommonContent = DocumentCommonContent.builder
    .headline(orm.getHeadline)
    .menuImageURL(orm.getMenuImageURL)
    .menuText(orm.getMenuText)
    .build()

  def toApi(orm: DocMeta): Meta = new Meta |>> { m =>
    m.setArchivedDatetime(orm.getArchivedDatetime)
    m.setCategoryIds(orm.getCategoryIds)
    m.setDefaultVersionNo(orm.getDefaultVersionNo)
    m.setDisabledLanguageShowSetting(Meta.DisabledLanguageShowSetting.values()(orm.getDisabledLanguageShowSetting.ordinal()))
    m.setDocumentType(orm.getDocumentType)
    //fixme
    //m.setEnabledLanguages(orm.getEnabledLanguages)
    m.setId(orm.getId)
    m.setKeywords(orm.getKeywords)
    m.setLinkableByOtherUsers(orm.getLinkableByOtherUsers)
    m.setLinkedForUnauthorizedUsers(orm.getLinkedForUnauthorizedUsers)
    m.setModifiedDatetime(orm.getModifiedDatetime)
    //m.setPermissionSets(orm.getPermissionSets)
    //m.setPermissionSetsForNew(orm.getPermisionSetExForNew)
    //m.setPermissionSetsForNewDocuments(orm.getPermissionSetsForNewDocuments)
    m.setProperties(orm.getProperties)
    m.setPublicationEndDatetime(orm.getPublicationEndDatetime)
    m.setPublicationStartDatetime(orm.getPublicationStartDatetime)
    m.setPublisherId(orm.getPublisherId)
    m.setRestrictedOneMorePrivilegedThanRestrictedTwo(orm.getRestrictedOneMorePrivilegedThanRestrictedTwo)
    m.setSearchDisabled(orm.getSearchDisabled)
    m.setTarget(orm.getTarget)
  }


  def toApi(orm: TextDocText): TextDomainObject = new TextDomainObject |>> { t=>
    t.setText(orm.getText)
    t.setType(orm.getType.ordinal())
  }


  def toApi(orm: TextDocLoop): Loop = {
    val entries = orm.getEntries.asScala.map(e => Int.box(e.getNo) -> Boolean.box(e.isEnabled)).toMap.asJava
    Loop.of(entries, orm.getNextEntryNo)
  }

  def toApi(orm: DocCategoryType): CategoryTypeDomainObject = new CategoryTypeDomainObject(
    orm.getId,
    orm.getName,
    orm.getMaxChoices,
    orm.isInherited,
    orm.isImageArchive
  )

  def toApi(orm: DocCategory): CategoryDomainObject = new CategoryDomainObject(
    orm.getId,
    orm.getName,
    orm.getDescription,
    orm.getImageUrl,
    toApi(orm.getType)
  )

  def toOrm(api: CategoryTypeDomainObject): DocCategoryType = new DocCategoryType(
    api.getId, api.getName, api.getMaxChoices, api.isInherited, api.isImageArchive
  )

  def toOrm(api: CategoryDomainObject): DocCategory = new DocCategory(
    api.getId, api.getName, api.getDescription, api.getImageUrl, toOrm(api.getType)
  )

  def toApi(orm: TextDocTemplateNames): TextDocumentDomainObject.TemplateNames = new TextDocumentDomainObject.TemplateNames |>> { api =>
    api.setDefaultTemplateName(orm.getDefaultTemplateName)
    api.setDefaultTemplateNameForRestricted1(orm.getDefaultTemplateNameForRestricted1)
    api.setDefaultTemplateNameForRestricted2(orm.getDefaultTemplateNameForRestricted2)
    api.setTemplateGroupId(orm.getTemplateGroupId)
    api.setTemplateName(orm.getTemplateName)
  }

  def toOrm(api: TextDocumentDomainObject.TemplateNames): TextDocTemplateNames = new TextDocTemplateNames |>> { orm =>
    orm.setDefaultTemplateName(orm.getDefaultTemplateName)
    orm.setDefaultTemplateNameForRestricted1(orm.getDefaultTemplateNameForRestricted1)
    orm.setDefaultTemplateNameForRestricted2(orm.getDefaultTemplateNameForRestricted2)
    orm.setTemplateGroupId(orm.getTemplateGroupId)
    orm.setTemplateName(orm.getTemplateName)
  }


  def toApi(orm: TextDocMenu): MenuDomainObject = new MenuDomainObject |>> { api =>
    api.setSortOrder(orm.getSortOrder)
    orm.getItems.asScala.foreach {
      case (toDocId, textDocMenuItem) => api.addMenuItemUnchecked(toApi(textDocMenuItem))
    }
  }

  def toApi(orm: TextDocMenuItem): MenuItemDomainObject = new MenuItemDomainObject |>> { api =>
    api.setSortKey(orm.getSortKey)
    api.setTreeSortIndex(orm.getTreeSortIndex)
  }

}
