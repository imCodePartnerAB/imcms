package com.imcode
package imcms.dao

import com.imcode.imcms.api.DocVersionRef
import com.imcode.imcms.mapping.{DocVersionRef, DocRef}
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
  def touch(docIdentity: DocRef, user: UserDomainObject): Unit = touch(docIdentity, user, new Date)
  def touch(docIdentity: DocRef, user: UserDomainObject, date: Date): Unit =
    touch(docIdentity.getDocId, docIdentity.getDocVersionNo, user.getId, date)

  def touch(docIdentity: DocVersionRef, user: UserDomainObject): Unit = touch(docIdentity, user, new Date)
  def touch(docIdentity: DocVersionRef, user: UserDomainObject, date: Date): Unit =
    touch(docIdentity.getDocId, docIdentity.getDocVersionNo, user.getId, date)

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

  def getDocAppearance(docRef: DocRef): DocCommonContent =
    hibernate.getByNamedQueryAndNamedParams[DocCommonContent](
      "DocAppearance.getByDocIdAndLanguageId", "docId" -> docRef.getDocId, "languageCode" -> docRef.getDocLanguageCode
    )


  def getAppearance(docId: Int): JList[DocCommonContent] = hibernate.listByNamedQueryAndNamedParams(
    "DocAppearance.getByDocId", "docId" -> docId
  )

  def deleteAppearance(docRef: DocRef): Int = getDocAppearance(docRef) match {
    case null => 0
    case appearance => hibernate.delete(appearance); 1
  }

  def saveAppearance(docAppearance: DocCommonContent): DocCommonContent = {
    val headline = docAppearance.getHeadline
    val text = docAppearance.getMenuText

    val headlineThatFitsInDB = headline.take(java.lang.Math.min(headline.length, META_HEADLINE_MAX_LENGTH - 1))
    val textThatFitsInDB = text.take(java.lang.Math.min(text.length, META_TEXT_MAX_LENGTH - 1))

    docAppearance.setHeadline(headlineThatFitsInDB)
    docAppearance.setMenuText(textThatFitsInDB)

    hibernate.mergeAndSaveOrUpdate(docAppearance)
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


  def deleteHtmlReference(docIdentity: DocRef) = hibernate.bulkUpdateByNamedParams(
    "delete from HtmlReference r where r.docIdentity = :docIdentity", "docIdentity" -> docIdentity
  )


  def deleteUrlReference(docIdentity: DocRef) = hibernate.bulkUpdateByNamedParams(
    "delete from UrlReference r where r.docIdentity = :docIdentity", "docIdentity" -> docIdentity
  )


  def saveTemplateNames(templateNames: TemplateNames) = hibernate.merge(templateNames)


  def getIncludes(docId: Int): JList[Include] =
    hibernate.listByQuery("select i from Include i where i.metaId = ?1", 1 -> docId)


  def getTemplateNames(docId: Int) = hibernate.get[TemplateNames](docId)


  def deleteTemplateNames(docId: Int) = hibernate.bulkUpdateByNamedParams(
    "DELETE FROM TemplateNames n WHERE n.docId = :docId", "docId" -> docId
  )


  def getFileDocItems(docIdentity: DocRef): JList[FileDocItem] =
    hibernate.listByNamedQueryAndNamedParams(
      "FileDoc.getReferences", "docIdentity" -> docIdentity
    )


  def saveFileReference(fileRef: FileDocItem) = hibernate.saveOrUpdate(fileRef)


  def deleteFileReferences(docIdentity: DocRef): Int = hibernate.bulkUpdateByNamedQueryAndNamedParams(
    "FileDoc.deleteAllReferences", "docIdentity" -> docIdentity
  )


  def getHtmlDocContent(docIdentity: DocRef): HtmlDocContent = hibernate.getByNamedQueryAndNamedParams(
    "HtmlDoc.getReference", "docIdentity" -> docIdentity
  )


  def saveHtmlReference(reference: HtmlDocContent) = hibernate.saveOrUpdate(reference)


  def getUrlDocContent(docIdentity: DocRef): UrlDocContent = hibernate.getByNamedQueryAndNamedParams(
    "UrlDoc.getReference", "docIdentity" -> docIdentity
  )


  def saveUrlReference(reference: UrlDocContent) = hibernate.merge(reference)


  def getAllAliases: JList[String] = hibernate.listByNamedQueryAndNamedParams(
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


  def getAllDocumentIds: JList[JInteger] = hibernate.listByNamedQuery("Meta.getAllDocumentIds")


  def getDocumentIdsInRange(min: Int, max: Int): JList[JInteger] = hibernate.listByNamedQueryAndNamedParams(
    "Meta.getDocumentIdsInRange", "min" -> min, "max" -> max
  )


  def getMaxDocumentId: Int = hibernate.getByNamedQuery("Meta.getMaxDocumentId")


  def getMinDocumentId: Int = hibernate.getByNamedQuery("Meta.getMinDocumentId")


  def getMinMaxDocumentIds: Array[JInteger] =
    hibernate.getByNamedQuery[Array[Object]]("Meta.getMinMaxDocumentIds") |> { pair =>
      Array(pair(0).asInstanceOf[JInteger], pair(1).asInstanceOf[JInteger])
    }


  def getEnabledLanguages(docId: Int) = ???
}