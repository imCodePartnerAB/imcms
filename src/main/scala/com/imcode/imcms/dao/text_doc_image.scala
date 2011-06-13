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
  def initImagesSources(images: JList[ImageDomainObject]) = letret(images) { _ foreach initImageSource }

  /** Inits Text doc's image source. */
  def initImageSource(image: ImageDomainObject) = letret(image) { _ =>
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

class ImageDao extends SpringHibernateTemplate {

  @scala.reflect.BeanProperty
  var languageDao: LanguageDao = _

  /**
   * Please note that createImageIfNotExists merely creates an instance of ImageDomainObject not a database entry.
   */
  @Transactional
  def getImagesByIndex(docId: JInteger, docVersionNo: JInteger, no: Int, loopNo: JInteger, contentNo: JInteger,
                       createImageIfNotExists: Boolean): JList[ImageDomainObject] =
    for {
      language <- languageDao.getAllLanguages
      image <- PartialFunction.condOpt(getImage(language.getId, docId, docVersionNo, no, loopNo, contentNo)) {
        case image if image != null => image
        case _ if createImageIfNotExists => new ImageDomainObject {
          setDocId(docId)
          setName(no.toString)

          setLanguage(language)
          setContentLoopNo(loopNo)
          setContentNo(contentNo)
        }
      }
    } yield image


  @Transactional
  def getImage(languageId: JInteger, docId: JInteger, docVersionNo: JInteger, no: Int, loopNo: JInteger,
               contentNo: JInteger) = {

    val queryStr = if (loopNo == null)
      "select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no and i.language.id = :languageId AND i.contentLoopNo IS NULL AND i.contentNo IS NULL"
    else
      "select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no and i.language.id = :languageId AND i.contentLoopNo = :contentLoopNo AND i.contentNo = :contentNo";

    withSession { session =>
      let(session.createQuery(queryStr)) { query =>
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

  @Transactional
  def saveImage(image: ImageDomainObject) = letret(image) {
    hibernateTemplate.saveOrUpdate(_)
  }

  @Transactional
  def saveImageHistory(imageHistory: ImageHistory) = hibernateTemplate.save(imageHistory)

  @Transactional
  def getImages(docId: JInteger, docVersionNo: JInteger) = {
    val images = hibernateTemplate.findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNo",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[JList[ImageDomainObject]]

    ImageUtil.initImagesSources(images)
  }

  @Transactional
  def getImages(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) = {
    val images = hibernateTemplate.findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNoAndLanguageId",
      Array("docId", "docVersionNo", "languageId"),
      Array[AnyRef](docId, docVersionNo, languageId)).asInstanceOf[JList[ImageDomainObject]]

    ImageUtil.initImagesSources(images)
  }


  @Transactional
  def deleteImages(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) = withSession {
    _.getNamedQuery("Image.deleteImages")
     .setParameter("docId", docId)
     .setParameter("docVersionNo", docVersionNo)
     .setParameter("languageId", languageId)
     .executeUpdate()
  }
}