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

  def indexDocuments(docId: Int) {
    service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(docId))
  }

  def removeDocuments(docId: Int) {
    service.requestIndexUpdate(SolrDocumentIndexService.DeleteDocsFromIndex(docId))
  }

  @Deprecated
  def indexDocument(document: DocumentDomainObject) {
    indexDocuments(document.getId)
  }

  @Deprecated
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