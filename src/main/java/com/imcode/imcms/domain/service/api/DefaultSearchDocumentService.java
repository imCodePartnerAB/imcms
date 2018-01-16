package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
class DefaultSearchDocumentService implements SearchDocumentService {

    private static final int DEFAULT_PAGE_SIZE = 100;

    private final DocumentIndex documentIndex;

    DefaultSearchDocumentService(DocumentIndex documentIndex) {

        this.documentIndex = documentIndex;
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery) {

        StringBuilder indexQuery = new StringBuilder();

        indexQuery.append(
                StringUtils.isNotBlank(searchQuery.getTerm())
                        ? Arrays.stream(new String[]{
                        DocumentIndex.FIELD__META_ID,
                        DocumentIndex.FIELD__META_HEADLINE,
                        DocumentIndex.FIELD__META_TEXT,
                        DocumentIndex.FIELD__KEYWORD,
                        DocumentIndex.FIELD__TEXT,
                        DocumentIndex.FIELD__ALIAS})
                        .map(field -> String.format("%s:*%s*", field, searchQuery.getTerm()))
                        .collect(Collectors.joining(" "))
                        : "*:*"
        );

        if (searchQuery.getCategoriesId() != null) {
            indexQuery = indexQuery.insert(0, "(")
                    .append(") AND (" + DocumentIndex.FIELD__CATEGORY_ID + ":(") // don't be so sad
                    .append(searchQuery.getCategoriesId().stream().map(Object::toString).collect(Collectors.joining(" AND ")))
                    .append("))");
        }

        final SolrQuery solrQuery = new SolrQuery(indexQuery.toString());

        if (searchQuery.getUserId() != null) {
            final String userFilter = DocumentIndex.FIELD__CREATOR_ID + ":" + searchQuery.getUserId();
            solrQuery.addFilterQuery(userFilter);
        }

        if (searchQuery.getPage() != null) {
            Sort sort = searchQuery.getPage().getSort();
            if (sort == null) {
                sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            }

            final Sort.Order order = sort.iterator().next();
            changeSolrQuery(solrQuery, searchQuery.getPage(), order);
        } else {
            PageRequest pageRequest = new PageRequest(0, DEFAULT_PAGE_SIZE,
                    new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID)));
            changeSolrQuery(solrQuery, pageRequest, pageRequest.getSort().iterator().next());
        }

        return documentIndex.search(solrQuery, Imcms.getUser())
                .documentStoredFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .sorted(Comparator.comparingInt(DocumentStoredFieldsDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    private void changeSolrQuery(SolrQuery solrQuery, PageRequest pageRequest, Sort.Order order) {
        final int pageSize = pageRequest.getPageSize();

        solrQuery.setStart(pageRequest.getPageNumber() * pageSize);
        solrQuery.setRows(pageSize);

        solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));
    }
}
