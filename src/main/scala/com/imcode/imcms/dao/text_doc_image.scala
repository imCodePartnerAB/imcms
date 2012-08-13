package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import com.imcode.imcms.api.ImageHistory

import org.springframework.transaction.annotation.Transactional
import imcode.server.document.textdocument._


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
  def getImagesByNo(docId: Int, docVersionNo: Int, no: Int, contentLoopRef: Option[ContentLoopRef],
                    createImageIfNotExists: Boolean): JList[ImageDomainObject] = {
    for {
      language <- languageDao.getAllLanguages().asScala
      image <- PartialFunction.condOpt(getImage(language.getId, docId, docVersionNo, no, contentLoopRef)) {
        case image if image != null => image
        case _ if createImageIfNotExists => new ImageDomainObject |>> { img =>
          img.setDocId(docId)
          img.setName(no.toString)

          img.setLanguage(language)
          img.setContentLoopRef(contentLoopRef.orNull)
        }
      }
    } yield image
  } |> { _.asJava }



  def getImage(languageId: Int, docId: Int, docVersionNo: Int, no: Int, contentLoopRef: Option[ContentLoopRef]) = {

    val queryStr = if (contentLoopRef.isDefined)
      """select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no
         and i.language.id = :languageId AND i.contentLoopRef = :contentLoopRef"""
    else
      """select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no
         and i.language.id = :languageId AND i.contentLoopRef IS NULL"""

    hibernate.withCurrentSession { session =>
      session.createQuery(queryStr) |> { query =>
        query.setParameter("docId", docId)
          .setParameter("docVersionNo", docVersionNo)
          .setParameter("no", "" + no)
          .setParameter("languageId", languageId)

        if (contentLoopRef.isDefined) {
          query.setParameter("contentLoopRef", contentLoopRef.get)
        }

        ImageUtil.initImageSource(query.uniqueResult.asInstanceOf[ImageDomainObject])
      }
    }
  }


  def saveImage(image: ImageDomainObject) = hibernate.saveOrUpdate(image)


  def saveImageHistory(imageHistory: ImageHistory) = hibernate.save(imageHistory)


  def getImages(docId: Int, docVersionNo: Int): JList[ImageDomainObject] =
    hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
      "Image.getByDocIdAndDocVersionNo", "docId" -> docId, "docVersionNo" -> docVersionNo
    ) |> ImageUtil.initImagesSources


  def getImages(docId: Int, docVersionNo: Int, languageId: Int): JList[ImageDomainObject] =
     hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
       "Image.getByDocIdAndDocVersionNoAndLanguageId",
       "docId" -> docId, "docVersionNo" -> docVersionNo, "languageId" -> languageId
     ) |>  ImageUtil.initImagesSources



  def deleteImages(docId: Int, docVersionNo: Int, languageId: Int) =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Image.deleteImages", "docId" -> docId, "docVersionNo" -> docVersionNo, "languageId" -> languageId
    )
}