package com.imcode.imcms.domain.component;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.LanguageService;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentSearchQueryConverterTest extends WebAppSpringTestConfig {

    private static final int USER_ID = 1;
    private static final String LANG_CODE = "en";

    private static DocumentSearchQueryConverter documentSearchQueryConverter;

    private SearchQueryDTO searchQueryDTO;

    @Autowired
    private LanguageService languageService;

    @BeforeAll
    public static void setConverter() {
        documentSearchQueryConverter = new DocumentSearchQueryConverter();

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @BeforeEach
    public void setUp() {
        searchQueryDTO = new SearchQueryDTO("");
        Imcms.setLanguage(languageService.getDefaultLanguage());
    }

    @Test
    public void convert_When_TermIsNull_Expect_SearchByAllFields() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        assertThat(solrQuery.get(CommonParams.Q), is("*:*"));
    }

    @Test
    public void convert_When_TermIsNotNull_Expect_SearchBySpecifiedFieldsWithTerm() {
	    final String term = "test";

	    searchQueryDTO.setTerm(term);

	    final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

	    final String expected = Arrays.stream(new String[]{
					    DocumentIndex.FIELD__META_ID,
					    DocumentIndex.FIELD_META_HEADLINE + "_" + LANG_CODE,
					    DocumentIndex.FIELD__META_HEADLINE + "_" + LANG_CODE,
					    DocumentIndex.FIELD__META_TEXT,
					    DocumentIndex.FIELD__KEYWORD,
					    DocumentIndex.FIELD__TEXT,
					    DocumentIndex.FIELD__META_ALIAS + '_' + LANG_CODE,
					    DocumentIndex.FIELD_META_ALIAS + '_' + LANG_CODE,
					    DocumentIndex.FIELD__URL})
			    .map(field -> String.format("%s:(*%s*)", field, term))
			    .collect(Collectors.joining(" "));

	    assertThat(solrQuery.get(CommonParams.Q), is(expected));
    }

    @Test
    public void convert_When_TermIsNotNullAndHaveSpace_Expect_SearchBySpecifiedFieldsWithTerm() {
        final String term = "test test2";

        searchQueryDTO.setTerm(term);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        final String expected = Arrays.stream(new String[]{
				        DocumentIndex.FIELD__META_ID,
				        DocumentIndex.FIELD_META_HEADLINE + "_" + LANG_CODE,
				        DocumentIndex.FIELD__META_HEADLINE + "_" + LANG_CODE,
				        DocumentIndex.FIELD__META_TEXT,
				        DocumentIndex.FIELD__KEYWORD,
				        DocumentIndex.FIELD__TEXT,
				        DocumentIndex.FIELD__META_ALIAS + '_' + LANG_CODE,
				        DocumentIndex.FIELD_META_ALIAS + '_' + LANG_CODE,
				        DocumentIndex.FIELD__URL})
                .map(field -> String.format("%s:(*test* *test2*)", field))
                .collect(Collectors.joining(" "));

        assertThat(solrQuery.get(CommonParams.Q), is(expected));
    }

    @Test
    public void convert_When_TermIsQuote_Expect_SearchBySpecifiedFieldsWithTerm() {
        final String term = "\"test test2\"";

        searchQueryDTO.setTerm(term);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        final String expected = Arrays.stream(new String[]{
				        DocumentIndex.FIELD__META_ID,
				        DocumentIndex.FIELD_META_HEADLINE + "_" + LANG_CODE,
				        DocumentIndex.FIELD__META_HEADLINE + "_" + LANG_CODE,
				        DocumentIndex.FIELD__META_TEXT,
				        DocumentIndex.FIELD__KEYWORD,
				        DocumentIndex.FIELD__TEXT,
				        DocumentIndex.FIELD__META_ALIAS + '_' + LANG_CODE,
				        DocumentIndex.FIELD_META_ALIAS + '_' + LANG_CODE,
				        DocumentIndex.FIELD__URL})
                .map(field -> String.format("%s:(\"test test2\")", field))
                .collect(Collectors.joining(" "));

        assertThat(solrQuery.get(CommonParams.Q), is(expected));
    }

    @Test
    public void convert_WhenDefaultPageRequestIsSet_Expect_StartEq0AndRowsEqMaxValue() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        assertThat(solrQuery.get(CommonParams.START), is("0"));
        assertThat(solrQuery.get(CommonParams.ROWS), is(Integer.MAX_VALUE + ""));
    }

    @Test
    public void convert_WhenSpecifiedPageRequestIsSet_Expect_SpecifiedStartAndRowsValues() {
        final int expectedPage = 1;
        final int expectedSize = 10;

        final PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setSkip(expectedPage * expectedSize);
        pageRequestDTO.setSize(expectedSize);

        searchQueryDTO.setPage(pageRequestDTO);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        assertThat(solrQuery.get(CommonParams.START), is(String.valueOf(expectedPage * expectedSize)));
        assertThat(solrQuery.get(CommonParams.ROWS), is(String.valueOf(expectedSize)));
    }

    @Test
    public void convert_WhenDefaultSortIsSet_Expect_SortByModifiedDatetimeDesc() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        final String expected = String.format("%s %s", DocumentIndex.FIELD__MODIFIED_DATETIME,
                Sort.Direction.DESC.toString().toLowerCase());

        assertThat(expected, is(solrQuery.get(CommonParams.SORT)));
    }

    @Test
    public void convert_When_SpecifiedSortIsSet_Expect_SortParametersIsCorrect() {
        final String expectedProperty = DocumentIndex.FIELD__META_HEADLINE; // by title
        final Sort.Direction expectedDirection = Sort.Direction.ASC;

        final PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setProperty(expectedProperty);
        pageRequestDTO.setDirection(expectedDirection);

        searchQueryDTO.setPage(pageRequestDTO);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        final String expected = String.format("%s %s", expectedProperty, expectedDirection.toString().toLowerCase());

        assertThat(solrQuery.get(CommonParams.SORT), is(expected));
    }

    @Test
    public void convert_When_UserIdIsNull_Expect_CorrespondingFilterNotAdded() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        final boolean noUserFilter = Arrays.stream(solrQuery.getFilterQueries())
                .noneMatch(filterQueryValue -> filterQueryValue.contains(DocumentIndex.FIELD__CREATOR_ID));

        assertTrue(noUserFilter);
    }

    @Test
    public void convert_When_SpecifiedUserIdIsSet_Expect_CorrespondingFilterAdded() {
        searchQueryDTO.setUserId(USER_ID);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        final boolean isUserFilter = Arrays.stream(solrQuery.getFilterQueries())
                .anyMatch(queryString -> queryString.contains(DocumentIndex.FIELD__CREATOR_ID + ":" + USER_ID));

        assertTrue(isUserFilter);
    }

    @Test
    public void convert_When_UserIsNotSuperAdmin_And_LimitSearchIsTrue_Expect_FiltersAreAdded() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);
        final List<String> filters = Arrays.asList(solrQuery.getFilterQueries());

        String expectedFilter = buildFilterQueryWithLinkableByOther(true, Imcms.getUser().getRoleIds(), null);

        assertEquals(1, filters.size());
        assertEquals(expectedFilter, filters.get(0));
    }

    @Test
    public void convert_When_UserIsNotSuperAdmin_And_LimitSearchIsTrue_AndLinkableByOtherUsersIsTrue_Expect_LinkableByOtherUsersAddedToFilters() {
        boolean linkableByOtherUsers = true;
        searchQueryDTO.setLinkableByOtherUsers(linkableByOtherUsers);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);
        final List<String> filters = Arrays.asList(solrQuery.getFilterQueries());

        final String expectedFilter = buildFilterQueryWithLinkableByOther(true, Imcms.getUser().getRoleIds(), linkableByOtherUsers);

        assertEquals(1, filters.size());
        assertEquals(expectedFilter, filters.get(0));
    }

    @Test
    public void convert_When_UserIsNotSuperAdmin_And_LimitSearchIsFalse_Expect_NoFilter() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, false);

        assertNull(solrQuery.getFilterQueries());
    }

    @Test
    public void convert_When_RoleIdIsNotNull_And_LimitSearchIsTrue_Expect_FiltersAreAdded(){
        final int roleId = 100;

        searchQueryDTO.setRoleId(roleId);
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);
        final List<String> filters = Arrays.asList(solrQuery.getFilterQueries());

        final String expectedFilter = buildFilterQueryWithLinkableByOther(true, Collections.singleton(roleId), null);

        assertEquals(1, filters.size());
        assertEquals(expectedFilter, filters.get(0));
    }

    @Test
    public void convert_When_CategoriesIdIsNotSet_Expect_CategoryIdQueryNotExist() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        assertFalse(solrQuery.get(CommonParams.Q).contains(DocumentIndex.FIELD__CATEGORY_ID));
    }

    @Test
    public void convert_When_OneCategoryIdIsSet_Expect_CategoryIdQueryExists() {
        final int categoryId = 1;

        final String expectedQuery = String.format("(*:*) AND (category_id:(%d))", categoryId);

        searchQueryDTO.setCategoriesId(Collections.singletonList(1));

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        assertThat(solrQuery.get(CommonParams.Q), is(expectedQuery));
    }

    @Test
    public void convert_When_TwoCategoriesIdIsSet_Expect_CategoryIdQueryExists() {
        final int firstCategoryId = 1;
        final int secondCategoryId = 2;

        final String expectedQuery = String.format("(*:*) AND (category_id:(%d AND %d))",
                firstCategoryId, secondCategoryId);

        searchQueryDTO.setCategoriesId(Arrays.asList(firstCategoryId, secondCategoryId));

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true);

        assertThat(solrQuery.get(CommonParams.Q), is(expectedQuery));
    }

    @Test
    public void convert_When_QueryIsExist_Expect_SearchBySpecifiedSimpleQuery() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query, true);

        assertThat(solrQuery.get(CommonParams.Q), is(query));
    }

    @Test
    public void convert_When_QueryIsExist_And_LimitSearchIsTrue_Expect_FiltersAreAdded() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query, true);
        final List<String> filters = Arrays.asList(solrQuery.getFilterQueries());

        final String expectedFilter = buildFilterQuery(true, Imcms.getUser().getRoleIds());

        assertEquals(1, filters.size());
        assertEquals(expectedFilter, filters.get(0));
    }

    @Test
    public void convert_When_QueryIsExist_And_LimitSearchIsFalse_Expect_NoFilter() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query, false);

        assertNull(solrQuery.getFilterQueries());
    }

    @Test
    public void convert_When_QueryAndPageRequestAreExist_Expect_SearchBySpecifiedSimpleQueryAndPageRequest() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final String property = DocumentIndex.FIELD__META_HEADLINE;
        final int skip = 45;
        final int size = 100;

        final PageRequestDTO page = new PageRequestDTO(property, Sort.Direction.ASC, skip, size);
        final String expectedSort = String.format("sort=%s+%s", property, Sort.Direction.ASC.toString().toLowerCase());
        final String expectedStart = "start=" + skip;
        final String expectedRows = "rows=" + size;

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query, page, true);
        assertThat(solrQuery.get(CommonParams.Q), is(query));

        final String receivedQuery = solrQuery.toString();
        assertTrue(receivedQuery.contains(expectedSort));
        assertTrue(receivedQuery.contains(expectedStart));
        assertTrue(receivedQuery.contains(expectedRows));
    }

    @Test
    public void convert_When_QueryAndPageRequestAreExist_And_LimitSearchIsTrue_Expect_FiltersAreAdded() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final String property = DocumentIndex.FIELD__META_HEADLINE;
        final PageRequestDTO page = new PageRequestDTO(property, Sort.Direction.ASC, 45, 100);
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query, page, true);
        final List<String> filters = Arrays.asList(solrQuery.getFilterQueries());

        final String expectedFilter = buildFilterQuery(true, Imcms.getUser().getRoleIds());

        assertEquals(1, filters.size());
        assertEquals(expectedFilter, filters.get(0));
    }

    @Test
    public void convert_When_QueryAndPageRequestAreExist_And_LimitSearchIsFalse_Expect_NoFilter() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final String property = DocumentIndex.FIELD__META_HEADLINE;
        final PageRequestDTO page = new PageRequestDTO(property, Sort.Direction.ASC, 45, 100);
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query, page, false);

        assertNull(solrQuery.getFilterQueries());
    }

    private String buildFilterQueryWithLinkableByOther(boolean searchEnabled, Set<Integer> roleIds, Boolean linkableByOther){
        StringBuilder filterQuery = new StringBuilder();
        filterQuery.append("(");
        filterQuery.append(buildFilterQuery(searchEnabled, roleIds));

        if(linkableByOther != null){
            filterQuery.append(" || " + DocumentIndex.FIELD__LINKABLE_OTHER + ":" + linkableByOther);
        }

        filterQuery.append(")");

        return filterQuery.toString();
    }

    private String buildFilterQuery(boolean searchEnabled, Set<Integer> roleIds){
        StringBuilder filterQuery = new StringBuilder();
        filterQuery.append("(");
        filterQuery.append(DocumentIndex.FIELD__SEARCH_ENABLED + ":" + searchEnabled);
        filterQuery.append(" AND ");

        StringJoiner roleJoiner = new StringJoiner(" || ", " || (", ")");
        roleIds.forEach(roleId -> roleJoiner.add("role_id:" + roleId.toString()));

        filterQuery.append("(" + DocumentIndex.FIELD__VISIBLE + ":true" + roleJoiner + ")");
        filterQuery.append(")");

        return filterQuery.toString();
    }

}