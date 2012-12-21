package imcode.server.document.index

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.service.{DeleteDocFromIndex, AddDocToIndex, DocumentIndexService}
import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.api.I18nLanguage

/**
 * {@link DocumentIndex} implementation.
 */
class DocumentIndexImpl(val service: DocumentIndexService, defaultLanguage: I18nLanguage) extends DocumentIndex with Log4jLoggerSupport {

  override def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
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

  override def rebuild() {
    service.requestIndexRebuild()
  }

  override def indexDocument(document: DocumentDomainObject) {
    indexDocument(document.getId)
  }

  override def removeDocument(document: DocumentDomainObject) {
    removeDocument(document.getId)
  }

  override def indexDocument(docId: Int) {
    service.requestIndexUpdate(AddDocToIndex(docId))
  }

  override def removeDocument(docId: Int) {
    service.requestIndexUpdate(DeleteDocFromIndex(docId))
  }
}