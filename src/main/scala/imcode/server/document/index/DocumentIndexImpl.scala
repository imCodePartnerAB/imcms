package imcode.server.document.index

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.service.{DeleteDocFromIndex, AddDocToIndex, DocumentIndexService}
import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.api.DocumentLanguage
import java.util.Collections


/**
 * {@link DocumentIndex} implementation.
 */
class DocumentIndexImpl(override val service: DocumentIndexService, defaultDocumentLanguage: DocumentLanguage) extends DocumentIndex with Log4jLoggerSupport {

  // todo: move language rewrite into wrapper???
  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    if (solrQuery.get(DocumentIndex.FIELD__LANGUAGE_CODE) == null ||
        !solrQuery.getFilterQueries.exists(query => query.contains(s"${DocumentIndex.FIELD__LANGUAGE_CODE}:"))) {
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__LANGUAGE_CODE, defaultDocumentLanguage.getCode))
    }

    service.search(solrQuery, searchingUser).getOrElse(Collections.emptyList())
  }

  @deprecated
  override def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    val queryString = query.getQuery.toString

    if (logger.isDebugEnabled) {
      logger.debug(s"Searching using *legacy* document query $queryString.")
    }

    val solrQuery = new SolrQuery(queryString)

    search(solrQuery, searchingUser)
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