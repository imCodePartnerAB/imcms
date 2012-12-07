package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.api.TextHistory

import org.springframework.transaction.annotation.Transactional
import imcode.server.document.textdocument.{ContentRef, DocRef, TextDomainObject}

@Transactional(rollbackFor = Array(classOf[Throwable]))
class TextDao extends HibernateSupport {

  @scala.reflect.BeanProperty
  var languageDao: LanguageDao = _

  /**
   * Please note that createIfNotExists merely creates an instance of TextDomainObject not a database entry.
   */
  def getTexts(docRef: DocRef, no: Int, contentRefOpt: Option[ContentRef],
               createIfNotExists: Boolean): JList[TextDomainObject] = {
    for {
      language <- languageDao.getAllLanguages.asScala
      text <- PartialFunction.condOpt(getText(docRef, no, language, contentRefOpt)) {
        case text if text != null => text
        case _ if createIfNotExists => new TextDomainObject |>> { txt =>
          txt.setDocRef(docRef)
          txt.setNo(no)

          txt.setLanguage(language)
          txt.setContentRef(contentRefOpt.orNull)
        }
      }
    } yield text
  } |> { _.asJava }

  /** Inserts or updates text. */
  def saveText(text: TextDomainObject): TextDomainObject = hibernate.saveOrUpdate(text)


  def getTextById(id: Long): TextDomainObject = hibernate.get[TextDomainObject](id)


  def deleteTexts(docRef: DocRef, language: I18nLanguage): Int =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Text.deleteTextsByDocRefAndLanguage",
      "docRef" -> docRef, "language" -> language
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
      "Text.getByDocRef", "docRef" -> docRef
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docRef: DocRef, language: I18nLanguage): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocRefAndLanguage",
      "docRef" -> docRef, "language" -> language
    )


  def getText(docRef: DocRef, no: Int, language: I18nLanguage, contentRefOpt: Option[ContentRef]) = {
    val queryStr =
      if (contentRefOpt.isDefined)
        """select t from Text t where t.docRef = :docRef and t.no = :no
           and t.language = :language AND t.contentRef = :contentRef"""
      else
        """select t from Text t where t.docRef = :docRef and t.no = :no
           and t.language = :language AND t.contentRef IS NULL"""

    hibernate.withCurrentSession { session =>
      session.createQuery(queryStr) |> { query =>
        query.setParameter("docRef", docRef)
          .setParameter("no", no)
          .setParameter("language", language)

        if (contentRefOpt.isDefined) {
          query.setParameter("contentRef", contentRefOpt.get)
        }

        query.uniqueResult.asInstanceOf[TextDomainObject]
      }
    }
  }
}