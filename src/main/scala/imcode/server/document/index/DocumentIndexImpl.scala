package imcode.server.document.index

import java.util.Collections

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{AddDocToIndex, DeleteDocFromIndex, DocumentIndexService}
import _root_.imcode.server.user.UserDomainObject
import com.imcode._
import com.imcode.imcms.ImcmsServicesSupport
import org.apache.solr.client.solrj.SolrQuery

import scala.collection.JavaConverters._


/**
 * {@link DocumentIndex} implementation.
 */
class DocumentIndexImpl(service: DocumentIndexService) extends DocumentIndex with ImcmsServicesSupport with Log4jLogger {

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

    val documentMapper = imcmsServices.getDocumentMapper

    try {
      val docs = new java.util.LinkedList[DocumentDomainObject]
      for {
        storedDocumentMeta <- search(solrQuery, searchingUser).documentStoredFieldsList().asScala
        doc <- (documentMapper.getDefaultDocument(storedDocumentMeta.id(), storedDocumentMeta.languageCode()): DocumentDomainObject).asOption
      } {
        docs.add(doc)
      }

      docs
    } catch {
      case _: Exception => Collections.emptyList()
    }
  }


  override def search(query: DocumentQuery, searchingUser: UserDomainObject, startPosition: Int, maxResults: Int): com.imcode.imcms.api.SearchResult[DocumentDomainObject] = {
    ???
  }

  @throws(classOf[IndexException])
  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): IndexSearchResult = {
    if (solrQuery.get(DocumentIndex.FIELD__LANGUAGE_CODE) == null
      && (solrQuery.getFilterQueries == null ||
      !solrQuery.getFilterQueries.exists(query => query.contains(s"${DocumentIndex.FIELD__LANGUAGE_CODE}:")))) {
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__LANGUAGE_CODE, imcmsServices.getDocumentLanguages.getDefault.getCode))
    }

    if (solrQuery.get(DocumentIndex.FIELD__META_ID) == null
      && (solrQuery.getFilterQueries == null ||
      !solrQuery.getFilterQueries.exists(query => query.contains(s"${DocumentIndex.FIELD__META_ID}:"))))
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__META_ID, "[* TO *]"))

    // todo: replace canSearchFor with filter queries
    // UserDomainObject#canSearchFor replacement - not yet complete
    if (!searchingUser.isSuperAdmin) {
      solrQuery.addFilterQuery(s"${DocumentIndex.FIELD__SEARCH_ENABLED}:true")
      solrQuery.addFilterQuery(DocumentIndex.FIELD__ROLE_ID + ":" + searchingUser.getRoleIds.mkString("(", " ", ")"))
    }

    if (solrQuery.getRows == null) solrQuery.setRows(Integer.MAX_VALUE)

    val queryResponse = service.query(solrQuery)
    new IndexSearchResult(solrQuery, queryResponse)
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