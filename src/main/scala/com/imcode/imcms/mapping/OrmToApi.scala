package com.imcode
package imcms.mapping

import com.imcode.imcms.api._
import com.imcode.imcms.mapping.orm.{TextDocLoopItem, TextDocLoopItemRef, TextDocLoop, TextDocText}
import imcode.server.document.textdocument.{TextDomainObject}

import scala.collection.JavaConverters._

object OrmToApi {

  def toApi(orm: orm.DocLanguage): DocumentLanguage = DocumentLanguage.builder
    .code(orm.getCode)
    .name(orm.getName)
    .nativeName(orm.getNativeName)
    .enabled(orm.isEnabled)
    .build()


  def toApi(orm: orm.DocAppearance): DocumentAppearance = DocumentAppearance.builder
    .headline(orm.getHeadline)
    .language(orm.getLanguage |> toApi)
    .menuImageURL(orm.getMenuImageURL)
    .menuText(orm.getMenuText)
    .build()


  def toApi(orm: orm.DocMeta): Meta = new Meta |>> { m =>
    m.setActivate(orm.getActivate)
    m.setActualModifiedDatetime(orm.getActualModifiedDatetime)
    m.setAlias(orm.getAlias)
    m.setArchivedDatetime(orm.getArchivedDatetime)
    m.setCategoryIds(orm.getCategoryIds)
    m.setDefaultVersionNo(orm.getDefaultVersionNo)
    m.setDisabledLanguageShowSetting(orm.getDisabledLanguageShowSetting)
    m.setDocumentType(orm.getDocumentType)
    m.setEnabledLanguages(orm.getEnabledLanguages)
    m.setI18nShowSettings(orm.getI18nShowSetting)
    m.setId(orm.getId)
    m.setKeywords(orm.getKeywords)
    m.setLinkableByOtherUsers(orm.getLinkableByOtherUsers)
    m.setLinkedForUnauthorizedUsers(orm.getLinkedForUnauthorizedUsers)
    m.setModifiedDatetime(orm.getModifiedDatetime)
    m.setPermisionSetEx(orm.getPermisionSetEx)
    m.setPermisionSetExForNew(orm.getPermisionSetExForNew)
    m.setPermissionSetBitsForNewMap(orm.getPermissionSetBitsForNewMap)
    m.setPermissionSets(orm.getPermissionSets)
    m.setPermissionSetsForNew(orm.getPermisionSetExForNew)
    m.setPermissionSetsForNewDocuments(orm.getPermissionSetsForNewDocuments)
    m.setProperties(orm.getProperties)
    m.setPublicationEndDatetime(orm.getPublicationEndDatetime)
    m.setPublicationStartDatetime(orm.getPublicationStartDatetime)
    m.setPublicationStatus(orm.getPublicationStatus)
    m.setPublicationStatusInt(orm.getPublicationStatusInt)
    m.setPublisherId(orm.getPublisherId)
    m.setRestrictedOneMorePrivilegedThanRestrictedTwo(orm.getRestrictedOneMorePrivilegedThanRestrictedTwo)
    //fixme
    //m.setRoleIdsMappedToDocumentPermissionSetTypes(orm.getRoleIdM)
    m.setRoleIdToDocumentPermissionSetTypeMappings(orm.getRoleIdToDocumentPermissionSetTypeMappings)
    m.setRoleIdToPermissionSetIdMap(orm.getRoleIdToPermissionSetIdMap)
    m.setSearchDisabled(orm.getSearchDisabled)
    m.setTarget(orm.getTarget)
  }


  def toApi(orm: TextDocText): TextDomainObject = new TextDomainObject |>> { t=>
    t.setText(orm.getText)
    t.setType(orm.getType.ordinal())
    t.setContentLoopRef(orm.getLoopItemRef |> toApi)
  }


  def toApi(orm: TextDocLoopItemRef): ContentLoopRef = ContentLoopRef.of(orm.getLoopNo, orm.getContentNo)


  def toApi(orm: TextDocLoop): ContentLoop = ContentLoop.of(orm.getNextItemNo, orm.getItems.asScala.map(toApi).asJava)


  def toApi(orm: TextDocLoopItem): Content = Content.of(orm.getNo, orm.isEnabled)
}
