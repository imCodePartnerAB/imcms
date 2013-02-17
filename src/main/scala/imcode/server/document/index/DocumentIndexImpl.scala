package imcode.server.document.index

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.service.{DeleteDocFromIndex, AddDocToIndex, DocumentIndexService}
import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.api.DocumentLanguage
import scala.collection.JavaConverters._
import org.apache.solr.client.solrj.response.QueryResponse
import com.google.common.collect.Lists

/**
 * {@link DocumentIndex} implementation.
 */
class DocumentIndexImpl(val service: DocumentIndexService, defaultDocumentLanguage: DocumentLanguage) extends DocumentIndex with Log4jLoggerSupport {

  override def query(solrQuery: SolrQuery): QueryResponse = service.query(solrQuery)

  // todo: ???Use request language???
  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): JIterator[DocumentDomainObject] = {
    if (solrQuery.get(DocumentIndex.FIELD__LANGUAGE_CODE) == null) {
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__LANGUAGE_CODE, defaultDocumentLanguage.getCode))
    }

    service.search(solrQuery, searchingUser).asJava
  }

  @deprecated
  override def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    val queryString = query.getQuery.toString

    if (logger.isDebugEnabled) {
      logger.debug(s"Searching using *legacy* document query $queryString.")
    }

    val solrQuery = new SolrQuery(queryString)

    Lists.newArrayList(search(solrQuery, searchingUser))
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