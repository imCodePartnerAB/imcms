package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.api.TextHistory

import org.springframework.transaction.annotation.Transactional
import imcode.server.document.textdocument.{DocIdentity, TextDomainObject}

@Transactional(rollbackFor = Array(classOf[Throwable]))
class TextDao extends HibernateSupport {

  /** Inserts or updates text. */
  def saveText(text: TextDomainObject) = hibernate.saveOrUpdate(text)


  def getTextById(id: Long) = hibernate.get[TextDomainObject](id)


  def deleteTexts(docIdentity: DocIdentity, language: I18nLanguage): Int =
    deleteTexts(docIdentity: DocIdentity, language.getId)


  def deleteTexts(docIdentity: DocIdentity, languageId: Int) =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Text.deleteTexts",
      "docIdentity" -> docIdentity, "languageId" -> languageId
    )


  def saveTextHistory(textHistory: TextHistory) = hibernate.save(textHistory)


  /**
   * @param docId
   * @param docVersionNo
   *
   * @return all texts in a doc.
   */
  def getTexts(docIdentity: DocIdentity): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocIdAndDocVersionNo", "docIdentity" -> docIdentity
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docIdentity: DocIdentity, languageId: Int): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocIdAndDocVersionNoAndLanguageId",
      "docIdentity" -> docIdentity, "languageId" -> languageId
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docIdentity: DocIdentity, language: I18nLanguage): JList[TextDomainObject] =
    getTexts(docIdentity: DocIdentity, language.getId)
}