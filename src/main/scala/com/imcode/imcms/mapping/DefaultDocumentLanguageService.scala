package com.imcode
package imcms.mapping

import javax.inject.Inject
import com.imcode.imcms.dao.{SystemDao, DocLanguageDao}
import java.util
import com.imcode.imcms.api.{DocumentLanguage, DocumentLanguageService}
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

// todo: security, transactions
class DefaultDocumentLanguageService extends DocumentLanguageService with Log4jLoggerSupport {

  @Inject
  @BeanProperty
  var languageDao : DocLanguageDao = null

  @Inject
  @BeanProperty
  var systemDao : SystemDao = null

  override def getByCode(code: String): DocumentLanguage = languageDao.getByCode(code) |> OrmToApi.toApi

  override def getLanguages: util.List[DocumentLanguage] = {
    languageDao.getAllLanguages().asScala.map(OrmToApi.toApi).asJava
  }

  override def isDefault(language: DocumentLanguage): Boolean = getDefault == language

  override def getDefault: DocumentLanguage = systemDao.getProperty("DefaultLanguageId") match {
    case null =>
      logger.info("Default document language property (DefaultLanguageId) is not set.")
      null

    case property => property.getValue match {
      case NonNegInt(id) => languageDao.getById(property.getValue.toInt)
        case Some(language) => language |> OrmToApi.toApi
        case None =>
          logger.error(s"I18n configuration error. Language with id $id does not exists.")
          null
      }

      case other =>
        val msg = s"""|I18n configuration error. Illegal DefaultLanguageId system property value.
                      | Must be non-negativei integer but was $other.
                   """.stripMargin()

        logger.error(msg)
        null
  }
}