package com.imcode
package imcms.dao

import scala.collection.JavaConverters._

import org.springframework.transaction.annotation.Transactional
import imcode.server.document.textdocument._
import com.imcode.imcms.api.{I18nLanguage, ImageHistory}


object ImageUtil {

  /** Inits Text docs images sources. */
  def initImagesSources(images: JList[ImageDomainObject]) = images |>> { _.asScala.foreach(initImageSource) }

  /** Inits Text doc's image source. */
  def initImageSource(image: ImageDomainObject) = image |>> { _ =>
    for (url <- Option(image).map(_.getImageUrl).map(_.trim)) {
      image.setSource(
        image.getType.intValue match {
          case ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT =>
            // This type is used in file docs ONLY
            sys.error("Illegal image source type - IMAGE_TYPE_ID__FILE_DOCUMENT. Id: %s, no: %s. url: %s.".format(image.getId, image.getNo, url))

          case ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH => new ImagesPathRelativePathImageSource(url)
          case ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE => new ImageArchiveImageSource(url)
          case _ => new NullImageSource
        }
      )
    }
  }
}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class ImageDao extends HibernateSupport {

  @scala.reflect.BeanProperty
  var languageDao: LanguageDao = _

  /**
   * Please note that createImageIfNotExists merely creates an instance of ImageDomainObject not a database entry.
   */
  def getImages(docIdentity: DocIdentity, no: Int, contentLoopIdentity: Option[ContentLoopIdentity],
                    createImageIfNotExists: Boolean): JList[ImageDomainObject] = {
    for {
      language <- languageDao.getAllLanguages().asScala
      image <- PartialFunction.condOpt(getImage(docIdentity, no, language, contentLoopIdentity)) {
        case image if image != null => image
        case _ if createImageIfNotExists => new ImageDomainObject |>> { img =>
          img.setDocIdentity(docIdentity)
          img.setName(no.toString)

          img.setLanguage(language)
          img.setContentLoopIdentity(contentLoopIdentity.orNull)
        }
      }
    } yield image
  } |> { _.asJava }



  def getImage(docIdentity: DocIdentity, no: Int, language: I18nLanguage, contentLoopRef: Option[ContentLoopIdentity]) = {

    val queryStr = if (contentLoopRef.isDefined)
      """select i from Image i where i.docIdentity = :docIdentity and i.no = :no
         and i.language = :language AND i.contentLoopIdentity = :contentLoopIdentity"""
    else
      """select i from Image i where i.docIdentity = :docIdentity and i.no = :no
         and i.language = :language AND i.contentLoopIdentity IS NULL"""

    hibernate.withCurrentSession { session =>
      session.createQuery(queryStr) |> { query =>
        query.setParameter("docIdentity", docIdentity)
          .setParameter("no", "" + no)
          .setParameter("language", language)

        if (contentLoopRef.isDefined) {
          query.setParameter("contentLoopIdentity", contentLoopRef.get)
        }

        ImageUtil.initImageSource(query.uniqueResult.asInstanceOf[ImageDomainObject])
      }
    }
  }


  def saveImage(image: ImageDomainObject) = hibernate.saveOrUpdate(image)


  def saveImageHistory(imageHistory: ImageHistory) = hibernate.save(imageHistory)


  def getImages(docIdentity: DocIdentity): JList[ImageDomainObject] =
    hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
      "Image.getByDocIdAndDocVersionNo", "docIdentity" -> docIdentity
    ) |> ImageUtil.initImagesSources


  def getImages(docIdentity: DocIdentity, languageId: Int): JList[ImageDomainObject] =
     hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
       "Image.getByDocIdAndDocVersionNoAndLanguageId",
       "docIdentity" -> docIdentity, "languageId" -> languageId
     ) |> ImageUtil.initImagesSources



  def deleteImages(docIdentity: DocIdentity, languageId: Int): Int =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Image.deleteImages", "docIdentity" -> docIdentity, "languageId" -> languageId
    )
}