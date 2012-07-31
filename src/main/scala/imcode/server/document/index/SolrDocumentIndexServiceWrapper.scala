package imcode.server.document.index

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.solr.SolrDocumentIndexService
import org.apache.solr.client.solrj.SolrQuery

/**
 * This implementation of {@link DocumentIndexService} transforms and routes all calls
 * to the wrapped instance of {@link SolrDocumentIndexService}.
 */
class SolrDocumentIndexServiceWrapper(service: SolrDocumentIndexService) extends DocumentIndexService with Log4jLoggerSupport {
  // todo: extract search from query
  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    val queryString = query.getQuery.toString

    if (logger.isDebugEnabled) {
      logger.debug("Searching using query %s.".format(queryString))
    }

    service.search(new SolrQuery(queryString), searchingUser)
  }

  def indexDocuments(docId: Int) {
    service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(docId))
  }

  def removeDocuments(docId: Int) {
    service.requestIndexUpdate(SolrDocumentIndexService.DeleteDocsFromIndex(docId))
  }

  def indexDocument(document: DocumentDomainObject) {
    indexDocuments(document.getId)
  }

  def removeDocument(document: DocumentDomainObject) {
    removeDocuments(document.getId)
  }

  def rebuild() {
    service.requestIndexRebuild()
  }

  def shutdown() {
    service.shutdown()
  }
}