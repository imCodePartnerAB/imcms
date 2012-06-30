package imcode.server.document.index.solr

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.{DocumentIndexService, DocumentQuery}
import org.apache.solr.client.solrj.SolrQuery

/**
 * This implementation of {@link DocumentIndexService} routes all calls to the wrapped instance of {@link SolrDocumentIndexService}.
 */
class SolrDocumentIndexServiceWrapper(service: SolrDocumentIndexService) extends DocumentIndexService with Log4jLoggerSupport {
  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject]
    = service.search(new SolrQuery(query.getQuery.toString), searchingUser)

  def indexDocument(docId: Int) {
    service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(docId))
  }

  def indexDocument(document: DocumentDomainObject) {
    service.requestIndexUpdate(SolrDocumentIndexService.AddDocToIndex(document))
  }

  def removeDocument(docId: Int) {
    service.requestIndexUpdate(SolrDocumentIndexService.DeleteDocsFromIndex(docId))
  }

  def removeDocument(document: DocumentDomainObject) {
    service.requestIndexUpdate(SolrDocumentIndexService.DeleteDocFromIndex(document))
  }

  def rebuild() {
    service.requestIndexRebuild()
  }

  def shutdown() {
    service.shutdown()
  }
}