package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.mapping.DocumentMapper;
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

/**
 * Created by dmizem from Ubrainians for imCode on 19.10.17.
 */
@Service
public class SearchDocumentService {

    private final Function<DocumentStoredFields, DocumentDTO> mapper;
    private final DocumentService documentService;

    public SearchDocumentService(Function<DocumentStoredFields, DocumentDTO> mapper,
                                 DocumentService documentService) {
        this.mapper = mapper;
        this.documentService = documentService;
    }

    public List<DocumentDTO> searchDocuments(SearchQueryDTO searchQuery) {

        if (searchQuery == null) {
            return documentService.getAllDocuments();
        }

        if (searchQuery.getUserId() == null) {
            searchQuery.setUserId(Imcms.getUser().getId());
        }

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

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
                    .append(") AND (" + DocumentIndex.FIELD__CATEGORY_ID + ":(")
                    .append(searchQuery.getCategoriesId().stream().map(Object::toString).collect(Collectors.joining(" AND ")))
                    .append("))");
        }

        SolrQuery solrQuery = new SolrQuery(indexQuery.toString());

        String userFilter = DocumentIndex.FIELD__CREATOR_ID + ":" + searchQuery.getUserId();
        solrQuery.addFilterQuery(userFilter);

        Sort.Order order = searchQuery.getPage().getSort().iterator().next();
        solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));

        result = documentMapper.getDocumentIndex()
                .search(solrQuery, Imcms.getUser())
                .documentStoredFieldsList().stream().map(mapper)
                .collect(Collectors.toList());

        return result;
    }
}
