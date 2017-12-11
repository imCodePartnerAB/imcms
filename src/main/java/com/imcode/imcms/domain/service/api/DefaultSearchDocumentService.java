package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class DefaultSearchDocumentService implements SearchDocumentService {

    private final Function<DocumentStoredFields, DocumentDTO> storedFieldsToDocumentDTO;
    private final DocumentIndex documentIndex;

    DefaultSearchDocumentService(Function<DocumentStoredFields, DocumentDTO> documentStoredFieldToDocumentDto,
                                 DocumentIndex documentIndex) {

        this.storedFieldsToDocumentDTO = documentStoredFieldToDocumentDto;
        this.documentIndex = documentIndex;
    }

    @Override
    public List<DocumentDTO> searchDocuments(SearchQueryDTO searchQuery) {

        if (searchQuery.getUserId() == null) {
            searchQuery.setUserId(Imcms.getUser().getId());
        }

        List<DocumentDTO> result;
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
            indexQuery = indexQuery.insert(0, indexQuery)
                    .append(") AND (" + DocumentIndex.FIELD__CATEGORY_ID + ":(") // don't be so sad
                    .append(searchQuery.getCategoriesId().stream().map(Object::toString).collect(Collectors.joining(" AND ")))
                    .append("))");
        }

        SolrQuery solrQuery = new SolrQuery(indexQuery.toString());

        String userFilter = DocumentIndex.FIELD__CREATOR_ID + ":" + searchQuery.getUserId();
        solrQuery.addFilterQuery(userFilter);

        if (searchQuery.getPage() != null) {
            Sort.Order order = searchQuery.getPage().getSort().iterator().next();
            solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));
        }

        result = documentIndex.search(solrQuery, Imcms.getUser())
                .documentStoredFieldsList().stream().map(storedFieldsToDocumentDTO)
                .collect(Collectors.toList());

        return result;
    }
}
