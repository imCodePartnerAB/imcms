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
import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order;

@Component
public class DocumentSearchQueryConverter {

    public SolrQuery convertToSolrQuery(SearchQueryDTO searchQuery, boolean limitSearch) {
        final UserDomainObject searchingUser = Imcms.getUser();
        final StringBuilder indexQuery = new StringBuilder();

        indexQuery.append(termToDefaultQuery(searchQuery.getTerm(), searchingUser.getLanguage()));

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

        if(searchQuery.getLinkableByOtherUsers() != null){
            solrQuery.addFilterQuery(DocumentIndex.FIELD__LINKABLE_OTHER + ":" + searchQuery.getLinkableByOtherUsers());
        }

        prepareSolrQueryPaging(searchQuery, solrQuery);

        if(limitSearch){
            Integer roleId = searchQuery.getRoleId();

            Set<Integer> roleIds = roleId != null ? Collections.singleton(roleId) : searchingUser.getRoleIds();
            addFilters(roleIds, solrQuery);
        }

        return solrQuery;
    }

    public SolrQuery convertToSolrQuery(String searchQuery, boolean limitSearch) {
        return convertToSolrQuery(searchQuery, null, limitSearch);
    }

    public SolrQuery convertToSolrQuery(String searchQuery, PageRequestDTO page, boolean limitSearch) {
        final UserDomainObject user = Imcms.getUser();
        final SolrQuery solrQuery = new SolrQuery(searchQuery);

        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO(null);
        searchQueryDTO.setPage(page);
        prepareSolrQueryPaging(searchQueryDTO, solrQuery);

        if(limitSearch) addFilters(user.getRoleIds(), solrQuery);

        return solrQuery;
    }

    private String termToDefaultQuery(String term, String language){
        if(StringUtils.isBlank(term)) return "*:*";

        if(!term.startsWith("\"") && !term.endsWith("\"")){
            String[] splits = term.split("\\s+");

            StringBuilder termBuilder = new StringBuilder();
            for(String split: splits){
                termBuilder.append(String.format("*%s* ", split));
            }

            term = termBuilder.toString().trim();
        }

        final String finalTerm = term;
        return Arrays.stream(new String[]{
				        DocumentIndex.FIELD__META_ID,
				        DocumentIndex.FIELD_META_HEADLINE + "_" + language,
				        DocumentIndex.FIELD__META_HEADLINE + "_" + language,
				        DocumentIndex.FIELD__META_TEXT,
				        DocumentIndex.FIELD__KEYWORD,
				        DocumentIndex.FIELD__TEXT,
				        DocumentIndex.FIELD__META_ALIAS + "_" + language,
				        DocumentIndex.FIELD_META_ALIAS + "_" + language,
				        DocumentIndex.FIELD__URL})
                        .map(field -> String.format("%s:(%s)", field, finalTerm))
                        .collect(Collectors.joining(" "));
    }

    private void prepareSolrQueryPaging(SearchQueryDTO searchQuery, SolrQuery solrQuery) {
	    PageRequestDTO page = searchQuery.getPage();

	    if (page == null) {
		    page = new PageRequestDTO();
	    }

	    solrQuery.setStart(page.getSkip());
	    solrQuery.setRows(page.getSize());

	    Sort sort = page.getSort();
	    if (sort == Sort.unsorted()) {
		    sort = Sort.by(Order.desc(DocumentIndex.FIELD__MODIFIED_DATETIME));
	    }
	    final Order order = sort.iterator().next();

	    solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));
    }

    private void addFilters(Set<Integer> roleIds, SolrQuery solrQuery) {
            solrQuery.addFilterQuery(DocumentIndex.FIELD__SEARCH_ENABLED + ":" + true);

            StringJoiner filterJoiner = new StringJoiner(" || ", "(", ")");
            filterJoiner.add(DocumentIndex.FIELD__VISIBLE + ":" + true);

            StringJoiner roleJoiner = new StringJoiner(" || ", "(", ")");
            for(Integer roleId: roleIds){
                roleJoiner.add(Integer.toString(roleId));
            }
            filterJoiner.add(DocumentIndex.FIELD__ROLE_ID + ":" + roleJoiner);

            solrQuery.addFilterQuery(filterJoiner.toString());
    }
}
