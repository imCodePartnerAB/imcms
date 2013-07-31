package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import com.imcode.imcms.api._
import org.hibernate.{ScrollMode, CacheMode}
import imcode.server.document.textdocument.{ImageDomainObject, MenuDomainObject, ContentRef, TextDomainObject}

import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class TextDocDao extends HibernateSupport {

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


  def deleteTexts(docRef: DocRef, language: DocumentLanguage): Int =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Text.deleteTextsByDocRefAndLanguage",
      "docRef" -> docRef, "language" -> language
    )


  def saveTextHistory(textHistory: TextHistory) = hibernate.save(textHistory)


  /**
   * @return all texts in a doc.
   */
  def getTexts(docRef: DocRef): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocRef", "docRef" -> docRef
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(docRef: DocRef, language: DocumentLanguage): JList[TextDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocRefAndLanguage",
      "docRef" -> docRef, "language" -> language
    )


  def getText(docRef: DocRef, no: Int, language: DocumentLanguage, contentRefOpt: Option[ContentRef]) = {
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

  def getMenu(docRef: DocRef, no: Int): MenuDomainObject = hibernate.getByNamedQueryAndNamedParams(
    "Menu.getMenuByDocRefAndNo", "docRef" -> docRef, "no" -> no
  )


  def getMenus(docRef: DocRef): JList[MenuDomainObject] =
    hibernate.listByNamedQueryAndNamedParams(
      "Menu.getMenusByDocRef", "docRef" -> docRef
    )


  def saveMenu(menu: MenuDomainObject): MenuDomainObject = hibernate.saveOrUpdate(menu)


  def saveMenuHistory(menuHistory: MenuHistory) = hibernate.save(menuHistory)


  def deleteMenus(docRef: DocRef) = hibernate.withCurrentSession { session =>
    val scroll = session.getNamedQuery("Menu.getMenusByDocRef")
      .setParameter("docRef", docRef)
      .setCacheMode(CacheMode.IGNORE)
      .scroll(ScrollMode.FORWARD_ONLY)

    var count = 0
    while (scroll.next) {
      session.delete(scroll.get(0))
      count += 1
      if (count % 25 == 0) {
        session.flush()
        session.clear()
      }
    }

    session.flush()

    count
  }


  def deleteMenu(menu: MenuDomainObject): Unit = hibernate.delete(menu)

  /**
   * Please note that createIfNotExists creates an instance of ImageDomainObject not a database entry.
   */
  def getImages(docRef: DocRef, no: Int, contentRefOpt: Option[ContentRef],
                createIfNotExists: Boolean): JList[ImageDomainObject] = {
    for {
      language <- languageDao.getAllLanguages.asScala
      image <- PartialFunction.condOpt(getImage(docRef, no, language, contentRefOpt)) {
        case image if image != null => image
        case _ if createIfNotExists => new ImageDomainObject |>> { img =>
          img.setDocRef(docRef)
          img.setNo(no)
          img.setLanguage(language)
          img.setContentRef(contentRefOpt.orNull)
        }
      }
    } yield image
  } |> { _.asJava }



  def getImage(docRef: DocRef, no: Int, language: DocumentLanguage, contentRefOpt: Option[ContentRef]) = {
    val queryStr =
      if (contentRefOpt.isDefined)
        """select i from Image i where i.docRef = :docRef and i.no = :no
           and i.language = :language AND i.contentRef = :contentRef"""
      else
        """select i from Image i where i.docRef = :docRef and i.no = :no
           and i.language = :language AND i.contentRef IS NULL"""

    hibernate.withCurrentSession { session =>
      session.createQuery(queryStr) |> { query =>
        query.setParameter("docRef", docRef)
          .setParameter("no", no)
          .setParameter("language", language)

        if (contentRefOpt.isDefined) {
          query.setParameter("contentRef", contentRefOpt.get)
        }

        ImageUtil.initImageSource(query.uniqueResult.asInstanceOf[ImageDomainObject])
      }
    }
  }


  def saveImage(image: ImageDomainObject) = hibernate.saveOrUpdate(image)


  def saveImageHistory(imageHistory: ImageHistory) = hibernate.save(imageHistory)


  def getImages(docRef: DocRef): JList[ImageDomainObject] =
    hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
      "Image.getByDocRef", "docRef" -> docRef
    ) |> ImageUtil.initImagesSources


  def getImages(docRef: DocRef, language: DocumentLanguage): JList[ImageDomainObject] =
    hibernate.listByNamedQueryAndNamedParams[ImageDomainObject](
      "Image.getByDocRefAndLanguage",
      "docRef" -> docRef, "language" -> language
    ) |> ImageUtil.initImagesSources



  def deleteImages(docRef: DocRef, language: DocumentLanguage): Int =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Image.deleteImagesByDocRefAndLanguage", "docRef" -> docRef, "language" -> language
    )

  def getLoop(loopId: Long) = hibernate.get[ContentLoop](loopId)

  /**
   * Returns loop or null if loop can not be found.

   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  def getLoop(docRef: DocRef, no: Int): ContentLoop =
    hibernate.getByNamedQueryAndNamedParams(
      "ContentLoop.getByDocIdAndDocVersionNoAndNo", "docId" -> docRef.docId, "docVersionNo" -> docRef.docVersionNo, "no" -> no
    )


  /**
   * Returns document content loops.

   * @return document content loops.
   */
  def getLoops(docRef: DocRef): JList[ContentLoop] = hibernate.listByNamedQueryAndNamedParams(
    "ContentLoop.getByDocIdAndDocVersionNo", "docId" -> docRef.docId, "docVersionNo" -> docRef.docVersionNo
  )


  def getNextLoopNo(docRef: DocRef): Int = hibernate.getByQuery[JInteger](
    "select max(l.no) from ContentLoop l where l.docId = ?1 and l.docVersionNo = ?2",
    1 -> docRef.docId,
    2 -> docRef.docVersionNo
  ) match {
    case null => 0
    case n => n.intValue + 1
  }


  /**
   * Saves content loop.
   *
   * @param loop content loop.
   * @return saved content loop.
   */
  def saveLoop(loop: ContentLoop) = loop.clone() |>> { loopClone =>
    hibernate.saveOrUpdate(loopClone)
    hibernate.flush()
  }


  def deleteLoops(docRef: DocRef) =
    getLoops(docRef).asScala.map(hibernate.delete).size


  def deleteLoop(loopId: Long) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernate.delete(loop)
      true
  }
}