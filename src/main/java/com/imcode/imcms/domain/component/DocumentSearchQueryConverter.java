package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Order;

@Component
public class DocumentSearchQueryConverter {

    private static final Integer DEFAULT_MAX_SIZE = Integer.MAX_VALUE;

    public SolrQuery convertToSolrQuery(SearchQueryDTO searchQuery) {
        final UserDomainObject searchingUser = Imcms.getUser();
        final StringBuilder indexQuery = new StringBuilder();

        indexQuery.append(
                StringUtils.isNotBlank(searchQuery.getTerm())
                        ? Arrays.stream(new String[]{
                        DocumentIndex.FIELD__META_ID,
                        DocumentIndex.FIELD_META_HEADLINE + "_" + searchingUser.getLanguage(),
                        DocumentIndex.FIELD__META_HEADLINE + "_" + searchingUser.getLanguage(),
                        DocumentIndex.FIELD__META_TEXT,
                        DocumentIndex.FIELD__KEYWORD,
                        DocumentIndex.FIELD__TEXT,
                        DocumentIndex.FIELD__VERSION_NO,
                        DocumentIndex.FIELD__ALIAS,
                        DocumentIndex.FIELD__URL})
                        .map(field -> String.format("%s:*%s*", field,
                                searchQuery.getTerm().replaceAll("\\s+", "?")))
                        .collect(Collectors.joining(" "))
                        : "*:*"
        );

        if (searchQuery.getCategoriesId() != null) {
            final String categoriesIdStringValues = searchQuery.getCategoriesId()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" AND "));

            indexQuery.insert(0, "(")
                    .append(") AND (" + DocumentIndex.FIELD__CATEGORY_ID + ":(") // don't be so sad
                    .append(categoriesIdStringValues)
                    .append("))");
        }

        final SolrQuery solrQuery = new SolrQuery(indexQuery.toString());

        if (searchQuery.getUserId() != null) {
            final String userFilter = DocumentIndex.FIELD__CREATOR_ID + ":" + searchQuery.getUserId();
            solrQuery.addFilterQuery(userFilter);
        }

        prepareSolrQueryPaging(searchQuery, solrQuery);

        prepareSolrIsSuperAdminQuery(searchingUser, solrQuery);

        return solrQuery;
    }

    public SolrQuery convertToSolrQuery(String searchQuery) {
        final UserDomainObject user = Imcms.getUser();
        final SolrQuery solrQuery = new SolrQuery(searchQuery);
        prepareSolrQueryPaging(new SearchQueryDTO(null), solrQuery);
        prepareSolrIsSuperAdminQuery(user, solrQuery);

        return solrQuery;
    }

    private void prepareSolrQueryPaging(SearchQueryDTO searchQuery, SolrQuery solrQuery) {
        PageRequestDTO page;
        if (StringUtils.isNotBlank(searchQuery.getTerm())) {
            page = searchQuery.getPage();
        } else {
            page = new PageRequestDTO(DEFAULT_MAX_SIZE);
        }

        if (page == null) {
            page = new PageRequestDTO();
        }

        solrQuery.setStart(page.getSkip());
        solrQuery.setRows(page.getSize());

        final Order order = Optional.ofNullable(page.getSort())
                .orElse(new Sort(new Order(Direction.DESC, DocumentIndex.FIELD_META_HEADLINE + "_" + Imcms.getLanguage().getCode())))
                .iterator()
                .next();

        solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));
    }

    private void prepareSolrIsSuperAdminQuery(UserDomainObject searchingUser, SolrQuery solrQuery) {

        if (!searchingUser.isSuperAdmin()) {
            solrQuery.addFilterQuery(DocumentIndex.FIELD__SEARCH_ENABLED + ":true");

            final String userRoleIdsFormatted = searchingUser.getRoleIds()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" ", "(", ")"));

            solrQuery.addFilterQuery(DocumentIndex.FIELD__ROLE_ID + ":" + userRoleIdsFormatted);
        }
    }
}
