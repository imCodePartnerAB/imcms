package imcode.server.document.index

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.solr.SolrDocumentIndexService
import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.api.I18nLanguage

/**
 * This implementation of {@link DocumentIndexService} transforms and routes all calls
 * to the wrapped instance of {@link SolrDocumentIndexService}.
 */
class SolrDocumentIndexServiceWrapper(service: SolrDocumentIndexService, defaultLanguage: I18nLanguage) extends DocumentIndexService with Log4jLoggerSupport {

  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    val queryString = query.getQuery.toString

    if (logger.isDebugEnabled) {
      logger.debug("Searching using query %s.".format(queryString))
    }

    val solrQuery = new SolrQuery(queryString)

    // todo: improve
    if (!queryString.toLowerCase.contains(DocumentIndex.FIELD__LANGUAGE)) {
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__LANGUAGE, defaultLanguage.getCode))
    }

    service.search(solrQuery, searchingUser)
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