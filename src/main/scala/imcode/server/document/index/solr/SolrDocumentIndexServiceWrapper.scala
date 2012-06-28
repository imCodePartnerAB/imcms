package imcode.server.document.index.solr

import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.{DocumentIndexService, DocumentQuery}

class SolrDocumentIndexServiceWrapper(service: SolrDocumentIndexService) extends DocumentIndexService with Log4jLoggerSupport {
  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject]
    = service.search(new SolrQuery(query.getQuery.toString), searchingUser)

  def indexDocument(docId: Int) {
    service.requestAlter(service.AddDocsToIndex(docId))
  }

  def indexDocument(document: DocumentDomainObject) {
    service.requestAlter(service.AddDocToIndex(document))
  }

  def removeDocument(docId: Int) {
    service.requestAlter(service.DeleteDocsFromIndex(docId))
  }

  def removeDocument(document: DocumentDomainObject) {
    service.requestAlter(service.DeleteDocFromIndex(document))
  }

  def rebuild() {
    service.requestRebuild()
  }

  def shutdown() {
    service.shutdown()
  }
}