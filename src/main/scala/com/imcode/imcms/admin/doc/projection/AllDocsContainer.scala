package com.imcode
package imcms
package admin.doc.projection

import scala.collection.JavaConverters._
import _root_.imcode.server.user.UserDomainObject

/**
 * Read only container which provides access to all docs.
 */
class AllDocsContainer extends FilterableDocsContainer {

  private val docMapper = imcmsServices.getDocumentMapper

  private var filteredDocIds = Seq.empty[DocId]

  protected def innerFilter(solrQuery: Option[String], user: UserDomainObject) {
    filteredDocIds = docMapper.getAllDocumentIds.asScala.toSeq

//    filteredDocIds = solrQuery match {
//      case None => docMapper.getAllDocumentIds.toSeq
//      case Some(query) => docMapper.getDocumentIndex.search(new SimpleDocumentQuery(LuceneParsedQuery.parse(query)), user)
//                                   .map(_.getMeta.getId)
//    }
  }

  def removeItem(itemId: AnyRef) = throw new UnsupportedOperationException

  def addItem(itemId: AnyRef) = throw new UnsupportedOperationException

  def removeAllItems() = throw new UnsupportedOperationException

  def idRange = docMapper.getDocumentIdRange |> { idsRange =>
    Some(idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
  }

  def getItemIds = filteredDocIds.asJava
}
