package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.ContentLoop
import imcode.server.document.textdocument.DocIdentity

@Transactional(rollbackFor = Array(classOf[Throwable]))
class ContentLoopDao extends HibernateSupport {

  def getLoop(loopId: Long) = hibernate.get[ContentLoop](loopId)

  /**
   * Returns loop or null if loop can not be found.
   *
   * @param docId document id.
   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  def getLoop(docIdentity: DocIdentity, no: Int): ContentLoop =
    hibernate.getByNamedQueryAndNamedParams(
      "ContentLoop.getByDocIdAndDocVersionNoAndNo", "docIdentity" -> docIdentity, "no" -> no
    )


  /**
   * Returns document content loops.
   *
   * @param docId document id.
   *
   * @return document content loops.
   */
  def getLoops(docIdentity: DocIdentity): JList[ContentLoop] = hibernate.listByNamedQueryAndNamedParams(
    "ContentLoop.getByDocIdAndDocVersionNo", "docIdentity" -> docIdentity
  )


  def getNextLoopNo(docIdentity: DocIdentity): Int = hibernate.getByQuery[JInteger](
      "select max(l.no) from ContentLoop l where l.docIdentity = ?", docIdentity
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


  def deleteLoops(docIdentity: DocIdentity) =
    getLoops(docIdentity).asScala.map(hibernate.delete).size


  def deleteLoop(loopId: Long) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernate.delete(loop)
      true
  }
}