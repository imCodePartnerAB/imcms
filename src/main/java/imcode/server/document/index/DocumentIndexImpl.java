package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.service.AddDocToIndex;
import imcode.server.document.index.service.DeleteDocFromIndex;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import lombok.val;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// translated from scala...
public class DocumentIndexImpl implements DocumentIndex {

    private final static Logger logger = Logger.getLogger(DocumentIndexImpl.class);
    private final DocumentIndexService service;

    DocumentIndexImpl(DocumentIndexService service) {
        this.service = service;
    }

    /**
     * @deprecated use {@link DocumentIndexImpl#search(org.apache.solr.client.solrj.SolrQuery, imcode.server.user.UserDomainObject)}
     */
    @Override
    @Deprecated
    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        final String queryString = query.getQuery().toString();

        if (logger.isDebugEnabled()) {
            logger.debug("Searching using *legacy* document query " + queryString);
        }

        final SolrQuery solrQuery = new SolrQuery(queryString);

        final Sort sort = query.getSort();
        final SortField[] sortFields = sort.getSort();

        for (SortField sortField : sortFields) {
            final String field = sortField.getField();
            solrQuery.addSort(field, (sortField.getReverse()) ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc);
//            sortField.getReverse(); not sure
        }

        try {
            final DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

            return search(solrQuery, searchingUser)
                    .documentStoredFieldsList()
                    .stream()
                    .map(storedDocumentMeta -> (DocumentDomainObject) documentMapper.getDefaultDocument(
                            storedDocumentMeta.id(), storedDocumentMeta.languageCode()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * @deprecated use {@link DocumentIndexImpl#search(org.apache.solr.client.solrj.SolrQuery, imcode.server.user.UserDomainObject)}
     */
    @Override
    @Deprecated
    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        throw new NotImplementedException("Method " + getClass().getName() + "#search SearchResult<DocumentDomainObject> is not implemented");
    }

    @Override
    public void rebuild() throws IndexException {
        service.rebuild();
    }

    @Override
    public void indexDocument(DocumentDomainObject document) throws IndexException {
        indexDocument(document.getId());
    }

    @Override
    public void removeDocument(DocumentDomainObject document) throws IndexException {
        removeDocument(document.getId());
    }

    @Override
    public IndexSearchResult search(SolrQuery solrQuery, UserDomainObject searchingUser) throws IndexException {
        final String langCode = solrQuery.get(DocumentIndex.FIELD__LANGUAGE_CODE);
        final String[] filterQueriesArr = solrQuery.getFilterQueries();
        final String metaId = solrQuery.get(DocumentIndex.FIELD__META_ID);

        final List<String> filterQueries = (filterQueriesArr == null)
                ? new ArrayList<>() : Arrays.asList(filterQueriesArr);

        final boolean hasNoLangCode = filterQueries.stream().noneMatch(s -> s.contains(DocumentIndex.FIELD__LANGUAGE_CODE + ":"));
        final boolean hasNoMetaId = filterQueries.stream().noneMatch(s -> s.contains(DocumentIndex.FIELD__META_ID + ":"));

        if ((langCode == null) && hasNoLangCode) {
            final String defaultLangCode = Imcms.getServices().getDocumentLanguages().getDefault().getCode();
            solrQuery.addFilterQuery(DocumentIndex.FIELD__LANGUAGE_CODE + ":" + defaultLangCode);
        }

        if ((metaId == null) && hasNoMetaId) {
            solrQuery.addFilterQuery(DocumentIndex.FIELD__META_ID + ":[* TO *]");
        }

        if (!searchingUser.isSuperAdmin()) {
            solrQuery.addFilterQuery(DocumentIndex.FIELD__SEARCH_ENABLED + ":true");

            final String userRoleIdsFormatted = Stream.of(searchingUser.getRoleIds())
                    .map(RoleId::toString)
                    .collect(Collectors.joining(" ", "(", ")"));

            solrQuery.addFilterQuery(DocumentIndex.FIELD__ROLE_ID + ":" + userRoleIdsFormatted);
        }

        if (solrQuery.getRows() == null) {
            solrQuery.setRows(Integer.MAX_VALUE);
        }

        val queryResponse = service.query(solrQuery);
        return new IndexSearchResult(solrQuery, queryResponse);
    }

    @Override
    public void indexDocument(int docId) throws IndexException {
        service.update(new AddDocToIndex(docId));
    }

    @Override
    public void removeDocument(int docId) throws IndexException {
        service.update(new DeleteDocFromIndex(docId));
    }

    @Override
    public DocumentIndexService getService() {
        return service;
    }

    @Override
    public boolean isUpdateDone() {
        return service.isUpdateDone();
    }
}
