package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.api.TextHistory
import imcode.server.document.textdocument.TextDomainObject

import org.springframework.transaction.annotation.Transactional

@Transactional(rollbackFor = Array(classOf[Throwable]))
class TextDao extends HibernateSupport {

  /** Inserts or updates text. */
  def saveText(text: TextDomainObject) = hibernate.saveOrUpdate(text)


  def getTextById(id: Long) = hibernate.get[TextDomainObject](id)


  def deleteTexts(docId: Int, docVersionNo: Int, language: I18nLanguage): Int =
    deleteTexts(docId, docVersionNo, language.getId)


  def deleteTexts(docId: Int, docVersionNo: Int, languageId: Int) =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Text.deleteTexts",
      "docId" -> docId, "docVersionNo" -> docVersionNo, "languageId" -> languageId
    )


  def saveTextHistory(textHistory: TextHistory) = hibernate.save(textHistory)


  /**
   * @param docId
   * @param docVersionNo
   *
   * @return all texts in a doc.
   */
  def getTexts(docId: Int, docVersionNo: Int): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocIdAndDocVersionNo", "docId" -> docId, "docVersionNo" -> docVersionNo
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docId: Int, docVersionNo: Int, languageId: Int): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocIdAndDocVersionNoAndLanguageId",
      "docId" -> docId, "docVersionNo" -> docVersionNo, "languageId" -> languageId
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docId: Int, docVersionNo: Int, language: I18nLanguage): JList[TextDomainObject] =
    getTexts(docId, docVersionNo, language.getId)
}