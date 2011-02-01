package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.springframework.transaction.annotation.Transactional

import com.imcode.imcms.api.ContentLoop


class ContentLoopDao extends SpringHibernateTemplate {

  @Transactional
  def getLoop(loopId: JLong) = hibernateTemplate.get(classOf[ContentLoop], loopId)

  /**
   * Returns loop or null if loop can not be found.
   *
   * @param docId document id.
   * @param no loop no.
   *
   * @return loop or null if loop can not be found.
   */
  @Transactional
  def getLoop(docId: JInteger, docVersionNo: JInteger, no: JInteger) = withSession {
    _.getNamedQuery("ContentLoop.getByDocIdAndDocVersionNoAndNo")
      .setParameter("docId", docId)
      .setParameter("docVersionNo", docVersionNo)
      .setParameter("no", no)
      .uniqueResult().asInstanceOf[ContentLoop]
  }


  /**
   * Returns document content loops.
   *
   * @param docId document id.
   *
   * @return document content loops.
   */
  @Transactional
  def getLoops(docId: JInteger, docVersionNo: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("ContentLoop.getByDocIdAndDocVersionNo",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[JList[ContentLoop]]


  /**
   * Saves content loop.
   *
   * @param loop content loop.
   * @return saved content loop.
   */
  @Transactional
  def saveLoop(loop: ContentLoop) = letret(loop.clone) { loopClone =>
    withSession { session =>
      session.saveOrUpdate(loopClone)
      session.flush
    }
  }


  @Transactional
  def deleteLoops(docId: JInteger, docVersionNo: JInteger) =
    getLoops(docId, docVersionNo).map(hibernateTemplate.delete).size


  @Transactional
  def deleteLoop(loopId: JLong) = getLoop(loopId) match {
    case null => false
    case loop =>
      hibernateTemplate.delete(loop)
      true
  }
}