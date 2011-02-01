package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import com.imcode.imcms.api.ImageHistory
import imcode.server.document.textdocument.ImageDomainObject
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource

import org.springframework.transaction.annotation.Transactional

import imcode.server.document.textdocument.ImageArchiveImageSource
import imcode.server.document.textdocument.ImageSource

object ImageUtil {

  def initImagesSources(images: JList[ImageDomainObject]) = letret(images) { _ foreach initImageSource }

  def initImageSource(image: ImageDomainObject) = letret(image) {
    case null =>
    case image =>
      ?(image.getImageUrl) map (_.trim) match {
        case Some(url) => image.setSource(
          if (image.getType() == ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE) new ImageArchiveImageSource(url)
          else new ImagesPathRelativePathImageSource(url))
        case _ =>
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
      imageOpt = ?(getImage(language.getId, docId, docVersionNo, no, loopNo, contentNo))
      if imageOpt.isDefined || createImageIfNotExists
    } yield {
      imageOpt match {
        case (Some(image)) => image
        case _ => letret(new ImageDomainObject) { image =>
          image.setDocId(docId)
          image.setName(no.toString)

          image.setLanguage(language)
          image.setContentLoopNo(loopNo)
          image.setContentNo(contentNo)
        }
      }
    }


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