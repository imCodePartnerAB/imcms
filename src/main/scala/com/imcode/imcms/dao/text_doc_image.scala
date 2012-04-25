package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import com.imcode.imcms.api.ImageHistory
import imcode.server.document.textdocument.ImageDomainObject
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource

import org.springframework.transaction.annotation.Transactional

import imcode.server.document.textdocument.ImageArchiveImageSource
import imcode.server.document.textdocument.NullImageSource
import imcode.server.document.textdocument.ImageSource

object ImageUtil {

  /** Inits Text docs images sources. */
  def initImagesSources(images: JList[ImageDomainObject]) = images |< { _ foreach initImageSource }

  /** Inits Text doc's image source. */
  def initImageSource(image: ImageDomainObject) = image |< { _ =>
    for (url <- ?(image) map (_.getImageUrl) map (_.trim)) {
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
  def getImagesByIndex(docId: JInteger, docVersionNo: JInteger, no: Int, loopNo: JInteger, contentNo: JInteger,
                       createImageIfNotExists: Boolean): JList[ImageDomainObject] =
    for {
      language <- languageDao.getAllLanguages()
      image <- PartialFunction.condOpt(getImage(language.getId, docId, docVersionNo, no, loopNo, contentNo)) {
        case image if image != null => image
        case _ if createImageIfNotExists => new ImageDomainObject |< { img =>
          img.setDocId(docId)
          img.setName(no.toString)

          img.setLanguage(language)
          img.setContentLoopNo(loopNo)
          img.setContentNo(contentNo)
        }
      }
    } yield image



  def getImage(languageId: JInteger, docId: JInteger, docVersionNo: JInteger, no: Int, loopNo: JInteger,
               contentNo: JInteger) = {

    val queryStr = if (loopNo == null)
      """select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no
         and i.language.id = :languageId AND i.contentLoopNo IS NULL AND i.contentNo IS NULL"""
    else
      """select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no
         and i.language.id = :languageId AND i.contentLoopNo = :contentLoopNo AND i.contentNo = :contentNo"""

    hibernate.withSession { session =>
      session.createQuery(queryStr) |> { query =>
        query.setParameter("docId", docId)
          .setParameter("docVersionNo", docVersionNo)
          .setParameter("no", "" + no)
          .setParameter("languageId", languageId)

        if (loopNo != null) {
          query.setParameter("contentLoopNo", loopNo).setParameter("contentNo", contentNo)
        }

        ImageUtil.initImageSource(query.uniqueResult.asInstanceOf[ImageDomainObject])
      }
    }
  }


  def saveImage(image: ImageDomainObject) = hibernate.saveOrUpdate(image)


  def saveImageHistory(imageHistory: ImageHistory) = hibernate.save(imageHistory)


  def getImages(docId: JInteger, docVersionNo: JInteger): JList[ImageDomainObject] =
    hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
      "Image.getByDocIdAndDocVersionNo", "docId" -> docId, "docVersionNo" -> docVersionNo
    ) |> ImageUtil.initImagesSources


  def getImages(docId: JInteger, docVersionNo: JInteger, languageId: JInteger): JList[ImageDomainObject] =
     hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
       "Image.getByDocIdAndDocVersionNoAndLanguageId",
       "docId" -> docId, "docVersionNo" -> docVersionNo, "languageId" -> languageId
     ) |>  ImageUtil.initImagesSources



  def deleteImages(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Image.deleteImages", "docId" -> docId, "docVersionNo" -> docVersionNo, "languageId" -> languageId
    )
}