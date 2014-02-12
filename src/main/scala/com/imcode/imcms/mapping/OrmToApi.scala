package com.imcode
package imcms.mapping

import com.imcode.imcms.api._
import com.imcode.imcms.mapping.orm._
import imcode.server.document.textdocument.{TextDomainObject}

import scala.collection.JavaConverters._

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


  def toApi(orm: DocAppearance): DocumentAppearance = DocumentAppearance.builder
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
    m.setI18nShowSettings(orm.getI18nShowSetting)
    m.setId(orm.getId)
    m.setKeywords(orm.getKeywords)
    m.setLinkableByOtherUsers(orm.getLinkableByOtherUsers)
    m.setLinkedForUnauthorizedUsers(orm.getLinkedForUnauthorizedUsers)
    m.setModifiedDatetime(orm.getModifiedDatetime)
    m.setPermissionSets(orm.getPermissionSets)
    m.setPermissionSetsForNew(orm.getPermisionSetExForNew)
    m.setPermissionSetsForNewDocuments(orm.getPermissionSetsForNewDocuments)
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
    t.setContentLoopRef(orm.getLoopItemRef |> toApi)
  }


  def toApi(orm: TextDocLoopItemRef): ContentLoopItemRef = ContentLoopItemRef.of(orm.getLoopNo, orm.getContentNo)


  def toApi(orm: TextDocLoop): ContentLoop = ContentLoop.of(orm.getItems.asScala.map(toApi).asJava)


  def toApi(orm: TextDocLoopItem): Content = Content.of(orm.getNo, orm.isEnabled)
}
