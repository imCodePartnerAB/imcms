package com.imcode
package imcms.dao

import com.imcode.imcms.api.{DocRef}
import com.imcode.imcms.mapping.orm._
import imcode.server.document.DocumentDomainObject
import org.apache.commons.lang.StringUtils
import org.springframework.transaction.annotation.Transactional

import imcode.server.user.UserDomainObject
import java.util.Date
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class MetaDao extends HibernateSupport {

  private val META_HEADLINE_MAX_LENGTH = 255
  private val META_TEXT_MAX_LENGTH = 1000

  def getMeta(docId: Int): DocMeta = hibernate.get[DocMeta](docId)

  /**  Updates doc's access and modified date-time. */
  def touch(docRef: DocRef, user: UserDomainObject): Unit = touch(docRef, user, new Date)
  def touch(docRef: DocRef, user: UserDomainObject, date: Date): Unit =
    touch(docRef.getDocId, docRef.getVersionNo, user.getId, date)

  private def touch(docId: Int, docVersionNo: Int, userId: Int, dt: Date) {
    hibernate.bulkUpdateByNamedParams(
      "UPDATE Meta m SET m.modifiedDatetime = :modifiedDt WHERE m.id = :docId",

      "modifiedDt" -> dt,
      "docId" -> docId
    )

    hibernate.bulkUpdateByNamedParams(
      """|UPDATE DocVersion v SET v.modifiedDt = :modifiedDt, v.modifiedBy = :modifiedBy
         |WHERE v.docId = :docId AND v.no = :docVersionNo""".stripMargin,

      "modifiedDt" -> dt,
      "modifiedBy" -> userId,
      "docId" -> docId,
      "docVersionNo" -> docVersionNo
    )
  }

  def getDocumentIdByAlias(alias: String): JInteger = hibernate.getByNamedQueryAndNamedParams(
    "DocumentProperty.getDocumentIdByAlias",

    "name" -> DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS,
    "value" -> alias.toLowerCase
  )

  def getI18nMeta(docId: Int, language: DocLanguage): DocAppearance =
    hibernate.getByNamedQueryAndNamedParams[DocAppearance](
      "DocI18nMeta.getByDocIdAndLanguageId", "docId" -> docId, "languageId" -> language.getId
    ).asOption.getOrElse(
      DocAppearance.builder() |> {
        _.docId(docId)
         .language(language)
         .headline("")
         .menuText("")
         .menuImageURL("")
         .build()
      }
    )


  def getI18nMetas(docId: Int): JList[DocAppearance] = hibernate.listByNamedQueryAndNamedParams(
    "DocI18nMeta.getByDocId", "docId" -> docId
  )

  def deleteI18nMeta(docId: Int, languageId: Int) = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "DocI18nMeta.deleteByDocIdAndLanguageId", "docId" -> docId, "languageId" -> languageId
  )

  def saveI18nMeta(i18nMeta: DocAppearance): DocAppearance = {
    val headline = i18nMeta.getHeadline
    val text = i18nMeta.getMenuText

    val headlineThatFitsInDB = headline.take(java.lang.Math.min(headline.length, META_HEADLINE_MAX_LENGTH - 1))
    val textThatFitsInDB = text.take(java.lang.Math.min(text.length, META_TEXT_MAX_LENGTH - 1))

    DocAppearance.builder(i18nMeta) |> {
      _.headline(headlineThatFitsInDB)
       .menuText(textThatFitsInDB)
       .build()
    } |> hibernate.mergeAndSaveOrUpdate
  }


  def insertPropertyIfNotExists(docId: Int, name: String, value: String): Boolean = {
    hibernate.getByNamedQueryAndNamedParams[DocProperty](
      "DocumentProperty.getProperty", "docId" -> docId, "name" -> name
    ).asOption.getOrElse(
      new DocProperty |>> { property =>
        property.setDocId(docId)
        property.setName(name)
      }
    ) match {
      case property if StringUtils.isBlank(property.getValue) =>
        property.setValue(value)
        hibernate.saveOrUpdate(property)
        true

      case _ => false
    }
  }



  def saveMeta(meta: DocMeta) = hibernate.saveOrUpdate(meta)


  def deleteIncludes(docId: Int) = hibernate.bulkUpdate("delete Include i where i.metaId = ?1", 1 -> docId)


  def saveInclude(include: Include) = hibernate.saveOrUpdate(include)


  def deleteHtmlReference(docRef: DocRef) = hibernate.bulkUpdateByNamedParams(
    "delete from HtmlReference r where r.docRef = :docRef", "docRef" -> docRef
  )


  def deleteUrlReference(docRef: DocRef) = hibernate.bulkUpdateByNamedParams(
    "delete from UrlReference r where r.docRef = :docRef", "docRef" -> docRef
  )


  def saveTemplateNames(templateNames: TemplateNames) = hibernate.merge(templateNames)


  def getIncludes(docId: Int): JList[Include] =
    hibernate.listByQuery("select i from Include i where i.metaId = ?1", 1 -> docId)


  def getTemplateNames(docId: Int) = hibernate.get[TemplateNames](docId)


  def deleteTemplateNames(docId: Int) = hibernate.bulkUpdateByNamedParams(
    "DELETE FROM TemplateNames n WHERE n.docId = :docId", "docId" -> docId
  )


  def getFileReferences(docRef: DocRef): JList[FileDocItem] =
    hibernate.listByNamedQueryAndNamedParams(
      "FileDoc.getReferences", "docRef" -> docRef
    )


  def saveFileReference(fileRef: FileDocItem) = hibernate.saveOrUpdate(fileRef)


  def deleteFileReferences(docRef: DocRef): Int = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "FileDoc.deleteAllReferences", "docRef" -> docRef
  )


  def getHtmlReference(docRef: DocRef): HtmlDocContent = hibernate.getByNamedQueryAndNamedParams(
    "HtmlDoc.getReference", "docRef" -> docRef
  )


  def saveHtmlReference(reference: HtmlDocContent) = hibernate.saveOrUpdate(reference)


  def getUrlReference(docRef: DocRef): UrlDocContent = hibernate.getByNamedQueryAndNamedParams(
    "UrlDoc.getReference", "docRef" -> docRef
  )


  def saveUrlReference(reference: UrlDocContent) = hibernate.merge(reference)


  def getAllAliases(): JList[String] = hibernate.listByNamedQueryAndNamedParams(
    "DocumentProperty.getAllAliases",

    "name" -> DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS
  )



  def getAliasProperty(alias: String): DocProperty  = hibernate.getByNamedQueryAndNamedParams(
    "DocumentProperty.getAliasProperty",

    "name" -> DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS,
    "value" -> alias
  )


  def getDocIdByAliasOpt(alias: String) = getAliasProperty(alias).asOption.map(_.getDocId.toInt)


  def deleteDocument(docId: Int): Unit = hibernate.withCurrentSession { session =>
    List(
      "DELETE FROM document_categories WHERE meta_id = ?",
      "DELETE FROM imcms_text_doc_menu_items WHERE to_doc_id = ?",
      "DELETE FROM imcms_text_doc_menu_items WHERE menu_id IN (SELECT doc_id FROM imcms_text_doc_menus WHERE doc_id = ?)",
      "DELETE FROM imcms_text_doc_menus WHERE doc_id = ?",
      "DELETE FROM text_docs WHERE meta_id = ?",
      "DELETE FROM imcms_text_doc_texts WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_images WHERE doc_id = ?",
      "DELETE FROM roles_rights WHERE meta_id = ?",
      "DELETE FROM user_rights WHERE meta_id = ?",
      "DELETE FROM imcms_url_docs WHERE doc_id = ?",
      "DELETE FROM fileupload_docs WHERE meta_id = ?",
      "DELETE FROM imcms_html_docs WHERE doc_id = ?",
      "DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?",
      "DELETE FROM new_doc_permission_sets WHERE meta_id = ?",
      "DELETE FROM doc_permission_sets_ex WHERE meta_id = ?",
      "DELETE FROM doc_permission_sets WHERE meta_id = ?",
      "DELETE FROM includes WHERE meta_id = ?",
      "DELETE FROM includes WHERE included_meta_id = ?",
      "DELETE FROM imcms_text_doc_texts_history WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_images_history WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_menu_items_history WHERE to_doc_id = ?",
      "DELETE FROM imcms_text_doc_menu_items_history WHERE menu_id IN (SELECT menu_id FROM imcms_text_doc_menus_history WHERE doc_id = ?)",
      "DELETE FROM imcms_text_doc_menus_history WHERE doc_id = ?",
      "DELETE FROM document_properties WHERE meta_id = ?",
      "DELETE FROM imcms_doc_i18n_meta WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_contents WHERE doc_id = ?",
      "DELETE FROM imcms_text_doc_content_loops WHERE doc_id = ?",
      "DELETE FROM imcms_doc_languages WHERE doc_id = ?",
      "DELETE FROM imcms_doc_keywords WHERE doc_id = ?",
      "DELETE FROM imcms_doc_versions WHERE doc_id = ?",
      "DELETE FROM meta WHERE meta_id = ?"
    ).foreach { sql =>
      session.createSQLQuery(sql).setParameter(0, docId).executeUpdate()
    }
  }


  def getAllDocumentIds(): JList[JInteger] = hibernate.listByNamedQuery("Meta.getAllDocumentIds")


  def getDocumentIdsInRange(min: Int, max: Int): JList[JInteger] = hibernate.listByNamedQueryAndNamedParams(
    "Meta.getDocumentIdsInRange", "min" -> min, "max" -> max
  )


  def getMaxDocumentId(): Int = hibernate.getByNamedQuery("Meta.getMaxDocumentId")


  def getMinDocumentId(): Int = hibernate.getByNamedQuery("Meta.getMinDocumentId")


  def getMinMaxDocumentIds(): Array[JInteger] =
    hibernate.getByNamedQuery[Array[Object]]("Meta.getMinMaxDocumentIds") |> { pair =>
      Array(pair(0).asInstanceOf[JInteger], pair(1).asInstanceOf[JInteger])
    }


  def getEnabledLanguages(docId: Int) = sys.error("Not implemented")
}