package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.api.TextHistory

import org.springframework.transaction.annotation.Transactional
import imcode.server.document.textdocument.{DocRef, TextDomainObject}

@Transactional(rollbackFor = Array(classOf[Throwable]))
class TextDao extends HibernateSupport {

  /** Inserts or updates text. */
  def saveText(text: TextDomainObject) = hibernate.saveOrUpdate(text)


  def getTextById(id: Long) = hibernate.get[TextDomainObject](id)


  def deleteTexts(docRef: DocRef, language: I18nLanguage): Int =
    deleteTexts(docRef: DocRef, language.getId)


  def deleteTexts(docRef: DocRef, languageId: Int) =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Text.deleteTexts",
      "docRef" -> docRef, "languageId" -> languageId
    )


  def saveTextHistory(textHistory: TextHistory) = hibernate.save(textHistory)


  /**
   * @param docId
   * @param docVersionNo
   *
   * @return all texts in a doc.
   */
  def getTexts(docRef: DocRef): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocIdAndDocVersionNo", "docRef" -> docRef
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docRef: DocRef, languageId: Int): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocIdAndDocVersionNoAndLanguageId",
      "docRef" -> docRef, "languageId" -> languageId
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docRef: DocRef, language: I18nLanguage): JList[TextDomainObject] =
    getTexts(docRef: DocRef, language.getId)
}