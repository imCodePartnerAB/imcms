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
class DocumentIndexImpl(service: DocumentIndexService, defaultDocumentLanguage: DocumentLanguage) extends DocumentIndex with Log4jLoggerSupport {

  // todo: move language rewrite into wrapper???
  // todo: replace canSearchFor with filter queries
  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    if (solrQuery.get(DocumentIndex.FIELD__LANGUAGE_CODE) == null ||
        !solrQuery.getFilterQueries.exists(query => query.contains(s"${DocumentIndex.FIELD__LANGUAGE_CODE}:"))) {
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__LANGUAGE_CODE, defaultDocumentLanguage.getCode))
    }

    // part of UserDomainObject#canSearchFor using
    if (!searchingUser.isSuperAdmin) {
      solrQuery.addFilterQuery(s"${DocumentIndex.FIELD__SEARCH_ENABLED}:true")
    }

    if (solrQuery.getRows == null) solrQuery.setRows(Integer.MAX_VALUE)

    service.search(solrQuery, searchingUser).getOrElse(Collections.emptyList())
  }

  @deprecated
  override def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    val queryString = query.getQuery.toString

    if (logger.isDebugEnabled) {
      logger.debug(s"Searching using *legacy* document query $queryString.")
    }

    val solrQuery = new SolrQuery(queryString)
    for {
      sort <- query.getSort.asOption
      sortField <- sort.getSort
      field <- sortField.getField.asOption
    } {
      solrQuery.addSort(field, if (sortField.getReverse) SolrQuery.ORDER.desc else SolrQuery.ORDER.asc)
      sortField.getReverse
    }

    search(solrQuery, searchingUser)
  }

  override def rebuild() {
    service.rebuild()
  }

  override def indexDocument(document: DocumentDomainObject) {
    indexDocument(document.getId)
  }

  override def removeDocument(document: DocumentDomainObject) {
    removeDocument(document.getId)
  }

  override def indexDocument(docId: Int) {
    service.update(AddDocToIndex(docId))
  }

  override def removeDocument(docId: Int) {
    service.update(DeleteDocFromIndex(docId))
  }

  override def getService(): DocumentIndexService = service
}