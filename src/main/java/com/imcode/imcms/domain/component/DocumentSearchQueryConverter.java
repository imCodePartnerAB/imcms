package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.domain.Sort.Order;

@Component
public class DocumentSearchQueryConverter {

    public SolrQuery convertToSolrQuery(SearchQueryDTO searchQuery, boolean limitSearch) {
        final UserDomainObject searchingUser = Imcms.getUser();
        final String currentLanguage = Imcms.getLanguage().getCode();

        final StringBuilder indexQuery = new StringBuilder();

        indexQuery.append(termToDefaultQuery(searchQuery.getTerm(), searchQuery.getSearchRange(), currentLanguage));

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
        solrQuery.setIncludeScore(true);

        if (searchQuery.getUserId() != null) {
            final String userFilter = DocumentIndex.FIELD__CREATOR_ID + ":" + searchQuery.getUserId();
            solrQuery.addFilterQuery(userFilter);
        }

        prepareSolrQueryPaging(searchQuery, solrQuery);

        if(limitSearch){
            Integer roleId = searchQuery.getRoleId();
            Set<Integer> roleIds = roleId != null ? Collections.singleton(roleId) : searchingUser.getRoleIds();

            StringJoiner orFilterJoiner = new StringJoiner(" || ", "(", ")");
            orFilterJoiner.add(generateFilters(roleIds));
            if(searchQuery.getLinkableByOtherUsers() != null){
                orFilterJoiner.add(DocumentIndex.FIELD__LINKABLE_OTHER + ":" + searchQuery.getLinkableByOtherUsers());
            }

            solrQuery.addFilterQuery(orFilterJoiner.toString());
        }

        return solrQuery;
    }

    public SolrQuery convertToSolrQuery(String searchQuery, boolean limitSearch) {
        return convertToSolrQuery(searchQuery, null, limitSearch);
    }

    public SolrQuery convertToSolrQuery(String searchQuery, DocumentPageRequestDTO page, boolean limitSearch) {
        final UserDomainObject user = Imcms.getUser();
        final SolrQuery solrQuery = new SolrQuery(searchQuery);

        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO(null);
        searchQueryDTO.setPage(page);
        prepareSolrQueryPaging(searchQueryDTO, solrQuery);

        if(limitSearch) solrQuery.addFilterQuery(generateFilters(user.getRoleIds()));

        return solrQuery;
    }

    private String termToDefaultQuery(String term, SearchQueryDTO.SearchRange searchRange, String language){
        if(StringUtils.isBlank(term)) return "*:*";

        List<SearchFieldDTO> searchFields = getSearchFieldsByRange(searchRange, language);
        if(term.startsWith("\"") && term.endsWith("\"")){   //If enclosed in quotes, search for an exact match
            return searchFields.stream()
                    .map(searchField ->
                            String.format("%s:(%s)^%d", searchField.getName(), term, searchField.getPriorityContains()))
                    .collect(Collectors.joining(" "));
        } else {
            final String[] splits = term.split("\\s+");

            final String startsWithTerm = Arrays.stream(splits)
                    .map(split -> String.format("%s*", split))
                    .collect(Collectors.joining(" "));
            final String containsTerm = Arrays.stream(splits)
                    .map(split -> String.format("*%s*", split))
                    .collect(Collectors.joining(" "));
            return searchFields.stream()
                    .flatMap(searchField -> Stream.of(
                            String.format("%s:(%s)^%d", searchField.getName(), startsWithTerm, searchField.getPriorityStartsWith()),
                            String.format("%s:(%s)^%d", searchField.getName(), containsTerm, searchField.getPriorityContains())
                    ))
                    .collect(Collectors.joining(" "));
        }
    }

    private List<SearchFieldDTO> getSearchFieldsByRange(SearchQueryDTO.SearchRange searchRange, String language) {
        List<SearchFieldDTO> searchFields = new ArrayList<>();

        switch (searchRange) {
            case ALL:
            default:
                searchFields.addAll(List.of(
                        new SearchFieldDTO(DocumentIndex.FIELD__URL, 6, 3),
                        new SearchFieldDTO(DocumentIndex.FIELD__KEYWORD, 4, 2),
                        new SearchFieldDTO(DocumentIndex.FIELD__META_TEXT, 1, 1),
                        new SearchFieldDTO(DocumentIndex.FIELD__TEXT, 1, 1)
                ));
            case BASIC:
                searchFields.addAll(List.of(
                        new SearchFieldDTO(DocumentIndex.FIELD__META_ID, 9, 5),
                        new SearchFieldDTO(DocumentIndex.FIELD_META_HEADLINE + "_" + language, 8, 5),
                        new SearchFieldDTO(DocumentIndex.FIELD__META_HEADLINE + "_" + language, 8, 5),
                        new SearchFieldDTO(DocumentIndex.FIELD__META_ALIAS + "_" + language, 7, 5),
                        new SearchFieldDTO(DocumentIndex.FIELD_META_ALIAS + "_" + language, 7, 5)
                ));
        }

        return searchFields;
    }

    private void prepareSolrQueryPaging(SearchQueryDTO searchQuery, SolrQuery solrQuery) {
	    PageRequestDTO page = searchQuery.getPage();

	    if (page == null) {
		    page = new DocumentPageRequestDTO();
	    }

	    solrQuery.setStart(page.getSkip());
	    solrQuery.setRows(page.getSize());

	    Sort sort = page.getSort();
	    if (sort != Sort.unsorted()) {
            final Order order = sort.iterator().next();
            solrQuery.addSort(order.getProperty(), SolrQuery.ORDER.valueOf(order.getDirection().name().toLowerCase()));
        }
    }

    private String generateFilters(Set<Integer> roleIds) {
        StringJoiner roleJoiner = new StringJoiner(" || ", "(", ")");
        roleIds.forEach(roleId -> roleJoiner.add(DocumentIndex.FIELD__ROLE_ID + ":" + roleId));

        StringJoiner accessFilterJoiner = new StringJoiner(" || ", "(", ")");
        accessFilterJoiner.add(DocumentIndex.FIELD__VISIBLE + ":" + true);
        accessFilterJoiner.add(roleJoiner.toString());

        StringJoiner searchAndAccessFilter = new StringJoiner(" AND ", "(", ")");
        searchAndAccessFilter.add(DocumentIndex.FIELD__SEARCH_ENABLED + ":" + true);
        searchAndAccessFilter.add(accessFilterJoiner.toString());

        return searchAndAccessFilter.toString();
    }

    @Data
    @AllArgsConstructor
    private class SearchFieldDTO{
        String name;
        int priorityStartsWith;
        int priorityContains;
    }

}
