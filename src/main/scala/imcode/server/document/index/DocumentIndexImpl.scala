package imcode.server.document.index

import com.imcode._
import com.imcode.imcms.api.DocumentLanguage

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{DeleteDocFromIndex, AddDocToIndex, DocumentIndexService}
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.common.SolrDocumentList

import java.util.Collections

import scala.util.{Success, Failure}
import com.imcode.imcms.ImcmsServicesSupport


/**
 * {@link DocumentIndex} implementation.
 */
class DocumentIndexImpl(service: DocumentIndexService, defaultDocumentLanguage: DocumentLanguage) extends DocumentIndex with ImcmsServicesSupport with Log4jLoggerSupport {

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

    try {
      queryDocuments(solrQuery, searchingUser)
    } catch {
      case _: Exception => Collections.emptyList()
    }
  }

  @throws(classOf[IndexException])
  override def querySolrDocuments(solrQuery: SolrQuery, searchingUser: UserDomainObject): SolrDocumentList = {
    if (solrQuery.get(DocumentIndex.FIELD__LANGUAGE_CODE) == null &&
      !solrQuery.getFilterQueries.exists(query => query.contains(s"${DocumentIndex.FIELD__LANGUAGE_CODE}:"))) {
      solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__LANGUAGE_CODE, defaultDocumentLanguage.getCode))
    }

    // todo: replace canSearchFor with filter queries
    // UserDomainObject#canSearchFor replacement - not yet complete
    if (!searchingUser.isSuperAdmin) {
      solrQuery.addFilterQuery(s"${DocumentIndex.FIELD__SEARCH_ENABLED}:true")
      solrQuery.addFilterQuery(DocumentIndex.FIELD__ROLE_ID + ":" + searchingUser.getRoleIds.mkString("(", " ", ")"))
    }

    if (solrQuery.getRows == null) solrQuery.setRows(Integer.MAX_VALUE)

    service.query(solrQuery) match {
      case Failure(e) => throw new IndexException(e)
      case Success(queryResponse) => queryResponse.getResults
    }
  }

  @throws(classOf[IndexException])
  override def queryDocuments(solrQuery: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    new java.util.AbstractList[DocumentDomainObject] {
      val solrDocs = querySolrDocuments(solrQuery, searchingUser)
      val docs = scala.collection.mutable.Map.empty[Int, DocumentDomainObject]
      val documentMapper = imcmsServices.getDocumentMapper

      override def get(i: Int): DocumentDomainObject = {
        docs.getOrElseUpdate(i, {
          val solrDoc = solrDocs.get(i)
          val docId = solrDoc.getFieldValue(DocumentIndex.FIELD__META_ID).toString.toInt
          val languageCode = solrDoc.getFieldValue(DocumentIndex.FIELD__LANGUAGE_CODE).toString

          documentMapper.getDefaultDocument[DocumentDomainObject](docId, languageCode) match {
            case null => new TextDocumentDomainObject(docId) |>> { doc =>
              val language = Option(documentMapper.getImcmsServices.getDocumentI18nSupport.getByCode(languageCode)).getOrElse {
                documentMapper.getImcmsServices.getDocumentI18nSupport.getDefaultLanguage
              }

              doc.setLanguage(language)
            }

            case doc => doc
          }
        })
      }

      override val size: Int = solrDocs.size()
    }
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