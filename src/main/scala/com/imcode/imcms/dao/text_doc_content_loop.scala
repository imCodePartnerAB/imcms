package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.ContentLoop

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
  def getLoop(docId: Int, docVersionNo: Int, no: Int): ContentLoop =
    hibernate.getByNamedQueryAndNamedParams(
      "ContentLoop.getByDocIdAndDocVersionNoAndNo", "docId" -> docId, "docVersionNo" -> docVersionNo, "no" -> no
    )


  /**
   * Returns document content loops.
   *
   * @param docId document id.
   *
   * @return document content loops.
   */
  def getLoops(docId: Int, docVersionNo: Int): JList[ContentLoop] = hibernate.listByNamedQueryAndNamedParams(
    "ContentLoop.getByDocIdAndDocVersionNo", "docId" -> docId, "docVersionNo" -> docVersionNo
  )


  def getNextLoopNo(docId: Int, docVersionNo: Int): Int = hibernate.getByQuery[JInteger](
      "select max(l.no) from ContentLoop l where l.docId = ? and l.docVersionNo = ?", docId, docVersionNo
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


  def deleteLoops(docId: Int, docVersionNo: Int) =
    getLoops(docId, docVersionNo).map(hibernate.delete).size


  def deleteLoop(loopId: Long) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernate.delete(loop)
      true
  }
}