package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.api.TextHistory
import imcode.server.document.textdocument.TextDomainObject

import org.springframework.transaction.annotation.Transactional

class TextDao extends SpringHibernateTemplate {

  /** Inserts or updates text. */
  @Transactional
  def saveText(text: TextDomainObject) = letret(text) { hibernateTemplate.saveOrUpdate }


  @Transactional
  def getTextById(id: JLong) = hibernateTemplate.get(classOf[TextDomainObject], id)


  @Transactional
  def deleteTexts(docId: JInteger, docVersionNo: JInteger, language: I18nLanguage): Int =
    deleteTexts(docId, docVersionNo, language.getId)


  @Transactional
  def deleteTexts(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) = withSession {
    _.getNamedQuery("Text.deleteTexts")
      .setParameter("docId", docId)
      .setParameter("docVersionNo", docVersionNo)
      .setParameter("languageId", languageId)
      .executeUpdate()
  }


  @Transactional
  def saveTextHistory(textHistory: TextHistory) = hibernateTemplate.save(textHistory)


  /**
   * @param docId
   * @param docVersionNo
   *
   * @return all texts in a doc.
   */
  @Transactional
  def getTexts(docId: JInteger, docVersionNo: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[JList[TextDomainObject]]


  /**
   * Returns text fields for the same doc, version and language.
   */
  @Transactional
  def getTexts(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNoAndLanguageId",
      Array("docId", "docVersionNo", "languageId"),
      Array[AnyRef](docId, docVersionNo, languageId)).asInstanceOf[JList[TextDomainObject]]


  /**
   * Returns text fields for the same doc, version and language.
   */
  @Transactional
  def getTexts(docId: JInteger, docVersionNo: JInteger, language: I18nLanguage): JList[TextDomainObject] =
    getTexts(docId, docVersionNo, language.getId)
}