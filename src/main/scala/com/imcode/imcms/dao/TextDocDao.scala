package com.imcode
package imcms.dao

import _root_.javax.inject.Inject
import com.imcode.imcms.api._
import com.imcode.imcms.mapping.orm._
import scala.collection.JavaConverters._
import scala.collection.breakOut
import org.hibernate.{ScrollMode, CacheMode}

import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class TextDocDao extends HibernateSupport {

  @Inject
  @scala.reflect.BeanProperty
  var docLanguageDao: DocLanguageDao = _

  @Inject
  @scala.reflect.BeanProperty
  var docVersionDao: DocVersionDao = _

  /**
   * Please note that createIfNotExists merely creates non-managed instance of TextDocText.
   */
  def getTextsInAllLanguages(docVersionRef: DocVersionRef, no: Int, loopItemRefOpt: Option[TextDocLoopItemRef],
               createIfNotExists: Boolean): JList[TextDocText] = {
    for {
      language <- languageDao.getAllLanguages.asScala
      docRef = DocRef.of(docVersionRef, language)
      text <- PartialFunction.condOpt(getText(docRef, no, loopItemRefOpt)) {
        case text if text != null => text
        case _ if createIfNotExists => new TextDocText |>> { txt =>
          txt.setNo(no)
          //txt.setLoopItemRef()
          //txt.setContentLoopRef(loopItemRefOpt.orNull)
        }
      }
    } yield text
  } |> { _.asJava }

  /** Inserts or updates text. */
  def saveText(text: TextDocText): TextDocText = hibernate.saveOrUpdate(text)


  def getTextById(id: Long): TextDocText = hibernate.get[TextDocText](id)


  def deleteTexts(ref: DocRef): Int = {
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "TextDocText.deleteTextsBy_DocId_and_DocVersionNo_and_DocLanguageCode",
      "docId" -> ref.getId(),
      "docVersionNo" -> ref.getVersionNo(),
      "docLanguageCode" -> ref.getLanguage.getCode
    )
  }


  def saveTextHistory(textHistory: TextDocTextHistory) = hibernate.save(textHistory)


  /**
   * @return all texts in a doc.
   */
  def getTextsInAllLanguages(ref: DocVersionRef): JList[TextDocText] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByDocRef", "docRef" -> docRef
    )


  /**
   * Returns text fields for the same doc, version and language.
   */
  def getTexts(ref: DocRef): JList[TextDocText] =
    hibernate.listByNamedQueryAndNamedParams(
      "Text.getByI18nDocRef",
      "ref" -> ref
    )


  def getText(ref: DocRef, no: Int, contentRefOpt: Option[TextDocLoopItemRef]) = {
    val queryStr =
      if (contentRefOpt.isDefined)
        """select t from Text t where t.ref = :ref and t.no = :no
           AND t.contentRef = :contentRef"""
      else
        """select t from Text t where t.ref = :ref and t.no = :no
           AND t.contentRef IS NULL"""

    hibernate.withCurrentSession { session =>
      session.createQuery(queryStr) |> { query =>
        query.setParameter("ref", ref)
             .setParameter("no", no)

        if (contentRefOpt.isDefined) {
          query.setParameter("contentRef", contentRefOpt.get)
        }

        query.uniqueResult.asInstanceOf[TextDocText]
      }
    }
  }

  def getMenu(ref: DocVersionRef, no: Int): TextDocMenu = hibernate.getByNamedQueryAndNamedParams(
    "Menu.getMenuByDocRefAndNo", "ref" -> ref, "no" -> no
  )


  def getMenus(ref: DocVersionRef): JList[TextDocMenu] =
    hibernate.listByNamedQueryAndNamedParams(
      "Menu.getMenusByDocRef", "ref" -> ref
    )


  def saveMenu(menu: TextDocMenu): TextDocMenu = hibernate.saveOrUpdate(menu)


  def saveMenuHistory(menuHistory: TextDocMenuHistory) = hibernate.save(menuHistory)


  def deleteMenus(ref: DocVersionRef) = hibernate.withCurrentSession { session =>
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


  def deleteMenu(menu: TextDocMenu): Unit = hibernate.delete(menu)

  /**
   * Please note that createIfNotExists creates an instance of TextDocImage not a database entry.
   */
  def getImagesInAllLanguages(ref: DocVersionRef, no: Int, contentRefOpt: Option[TextDocLoopItemRef] = None,
                createIfNotExists: Boolean = false): JList[TextDocImage] = {
    for {
      language <- languageDao.getAllLanguages.asScala
      image <- PartialFunction.condOpt(getImage(docRef, no, language, contentRefOpt)) {
        case image if image != null => image
        case _ if createIfNotExists => new TextDocImage |>> { img =>
          img.setDocRef(docRef)
          img.setNo(no)
          img.setLanguage(language)
          img.setContentLoopRef(contentRefOpt.orNull)
        }
      }
    } yield image
  } |> { _.asJava }


//  def getImages(docRef: DocRef, no: Int, contentRefOpt: Option[ContentRef] = None): Map[DocumentLanguage, Option[TextDocImage]] = {
//    (
//      for (language <- languageDao.getAllLanguages.asScala)
//      yield language -> getImage(docRef, no, language, contentRefOpt).asOption
//    )(breakOut)
//  }

  def getImage(docRef: DocRef, no: Int, language: DocLanguage, contentRefOpt: Option[TextDocLoopItemRef]) = {
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

        ImageUtil.initImageSource(query.uniqueResult.asInstanceOf[TextDocImage])
      }
    }
  }


  def saveImage(image: TextDocImage) = hibernate.saveOrUpdate(image)


  def saveImageHistory(imageHistory: TextDocImageHistory) = hibernate.save(imageHistory)


  def getImagesInAllLanguages(docRef: DocRef): JList[TextDocImage] =
    hibernate.listByNamedQueryAndNamedParams[TextDocImage](
      "Image.getByDocRef", "docRef" -> docRef
    ) |> ImageUtil.initImagesSources


  def getImages(ref: DocRef): JList[TextDocImage] =
    hibernate.listByNamedQueryAndNamedParams[TextDocImage](
      "Image.getByDocRefAndLanguage",
      "docRef" -> docRef, "language" -> language
    ) |> ImageUtil.initImagesSources



  def deleteImages(docRef: DocRef, language: DocLanguage): Int =
    hibernate.bulkUpdateByNamedQueryAndNamedParams(
      "Image.deleteImagesByDocRefAndLanguage", "docRef" -> docRef, "language" -> language
    )

  def getLoop(loopId: Long) = hibernate.get[TextDocLoop](loopId)

  /**
   * Returns loop or null if loop can not be found.

   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  def getLoop(docRef: DocRef, no: Int): TextDocLoop =
    hibernate.getByNamedQueryAndNamedParams(
      "ContentLoop.getByDocRefAndNo", "docRef" -> docRef, "no" -> no
    )


  /**
   * Returns document content loops.

   * @return document content loops.
   */
  def getLoops(docRef: DocRef): JList[TextDocLoop] = hibernate.listByNamedQueryAndNamedParams(
    "ContentLoop.getByDocRef", "docRef" -> docRef
  )


  def getNextLoopNo(docRef: DocRef): Int = hibernate.getByQuery[JInteger](
    "select max(l.no) from ContentLoop l where l.docRef = ?1",
    1 -> docRef
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
  def saveLoop(loop: TextDocLoop) = loop.clone() |>> { loopClone =>
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