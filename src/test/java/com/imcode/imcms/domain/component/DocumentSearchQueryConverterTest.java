package com.imcode.imcms.domain.component;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentSearchQueryConverterTest extends WebAppSpringTestConfig {

    private static final int USER_ID = 1;
    private static final String LANG_CODE = "en";

    private static DocumentSearchQueryConverter documentSearchQueryConverter;

    private SearchQueryDTO searchQueryDTO;

    @BeforeAll
    public static void setConverter() {
        documentSearchQueryConverter = new DocumentSearchQueryConverter();

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @BeforeEach
    public void setUp() {
        searchQueryDTO = new SearchQueryDTO();
    }

    @Test
    public void convert_When_TermIsNull_Expect_SearchByAllFields() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        assertThat(solrQuery.get(CommonParams.Q), is("*:*"));
    }

    @Test
    public void convert_When_TermIsNotNull_Expect_SearchBySpecifiedFieldsWithTerm() {
        final String term = "test";

        searchQueryDTO.setTerm(term);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        final String expected = Arrays.stream(new String[]{
                DocumentIndex.FIELD__META_ID,
                DocumentIndex.FIELD__META_HEADLINE + "_" + LANG_CODE,
                DocumentIndex.FIELD__META_TEXT,
                DocumentIndex.FIELD__KEYWORD,
                DocumentIndex.FIELD__TEXT,
                DocumentIndex.FIELD__VERSION_NO,
                DocumentIndex.FIELD__ALIAS,
                DocumentIndex.FIELD__URL})
                .map(field -> String.format("%s:*%s*", field, term))
                .collect(Collectors.joining(" "));

        assertThat(solrQuery.get(CommonParams.Q), is(expected));
    }

    @Test
    public void convert_When_QueryIsExist_Expect_SearchBySpecifiedSimpleQuery() {
        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED;
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(query);

        assertThat(solrQuery.get(CommonParams.Q), is(query));
    }

    @Test
    public void convert_WhenDefaultPageRequestIsSet_Expect_StartEq0AndRowsEq100() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        assertThat(solrQuery.get(CommonParams.START), is("0"));
        assertThat(solrQuery.get(CommonParams.ROWS), is("100"));
    }

    @Test
    public void convert_WhenSpecifiedPageRequestIsSet_Expect_SpecifiedStartAndRowsValues() {
        final int expectedPage = 1;
        final int expectedSize = 10;

        final PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setSkip(expectedPage * expectedSize);
        pageRequestDTO.setSize(expectedSize);

        searchQueryDTO.setPage(pageRequestDTO);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        assertThat(solrQuery.get(CommonParams.START), is(String.valueOf(expectedPage * expectedSize)));
        assertThat(solrQuery.get(CommonParams.ROWS), is(String.valueOf(expectedSize)));
    }

    @Test
    public void convert_WhenDefaultSortIsSet_Expect_SortByMetaIdDesc() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        final String expected = String.format("%s %s", DocumentIndex.FIELD__META_ID,
                Sort.Direction.DESC.toString().toLowerCase());

        assertThat(solrQuery.get(CommonParams.SORT), is(expected));
    }

    @Test
    public void convert_When_SpecifiedSortIsSet_Expect_SortParametersIsCorrect() {
        final String expectedProperty = DocumentIndex.FIELD__META_HEADLINE; // by title
        final Sort.Direction expectedDirection = Sort.Direction.ASC;

        final PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setProperty(expectedProperty);
        pageRequestDTO.setDirection(expectedDirection);

        searchQueryDTO.setPage(pageRequestDTO);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        final String expected = String.format("%s %s", expectedProperty, expectedDirection.toString().toLowerCase());

        assertThat(solrQuery.get(CommonParams.SORT), is(expected));
    }

    @Test
    public void convert_When_UserIdIsNull_Expect_CorrespondingFilterNotAdded() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        final boolean noUserFilter = Arrays.stream(solrQuery.getFilterQueries())
                .noneMatch(filterQueryValue -> filterQueryValue.startsWith(DocumentIndex.FIELD__CREATOR_ID));

        assertTrue(noUserFilter);
    }

    @Test
    public void convert_When_SpecifiedUserIdIsSet_Expect_CorrespondingFilterAdded() {
        searchQueryDTO.setUserId(USER_ID);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        final boolean isUserFilter = Arrays.asList(solrQuery.getFilterQueries()).contains(DocumentIndex.FIELD__CREATOR_ID + ":" + USER_ID);

        assertTrue(isUserFilter);
    }

    @Test
    public void convert_When_UserIsSuperAdmin_Expect_FiltersForSearchEnabledAndRolesIdNotAdded() {
        final Integer superAdminRoleId = Roles.SUPER_ADMIN.getId();

        Imcms.getUser().addRoleId(superAdminRoleId);

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        Imcms.getUser().removeRoleId(superAdminRoleId);

        final Predicate<String> predicate = filterQueryValue
                -> filterQueryValue.startsWith(DocumentIndex.FIELD__SEARCH_ENABLED)
                || filterQueryValue.startsWith(DocumentIndex.FIELD__ROLE_ID);

        boolean noSpecifiedFilters = true;
        if (solrQuery.getFilterQueries() != null) {
            noSpecifiedFilters = Arrays.stream(solrQuery.getFilterQueries())
                    .noneMatch(predicate);
        }

        assertTrue(noSpecifiedFilters);
    }

    @Test
    public void convert_When_UserIsNotSuperAdmin_Expect_FiltersForSearchEnabledAndRolesIdAdded() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        final List<String> filters = Arrays.asList(solrQuery.getFilterQueries());

        assertTrue(filters.contains(DocumentIndex.FIELD__SEARCH_ENABLED + ":true"));
        assertTrue(filters.contains(DocumentIndex.FIELD__ROLE_ID + ":(" + Roles.USER.getId() + ")"));
    }

    @Test
    public void convert_When_CategoriesIdIsNotSet_Expect_CategoryIdQueryNotExist() {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        assertFalse(solrQuery.get(CommonParams.Q).contains(DocumentIndex.FIELD__CATEGORY_ID));
    }

    @Test
    public void convert_When_OneCategoryIdIsSet_Expect_CategoryIdQueryExists() {
        final int categoryId = 1;

        final String expectedQuery = String.format("(*:*) AND (category_id:(%d))", categoryId);

        searchQueryDTO.setCategoriesId(Collections.singletonList(1));

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        assertThat(solrQuery.get(CommonParams.Q), is(expectedQuery));
    }

    @Test
    public void convert_When_TwoCategoriesIdIsSet_Expect_CategoryIdQueryExists() {
        final int firstCategoryId = 1;
        final int secondCategoryId = 2;

        final String expectedQuery = String.format("(*:*) AND (category_id:(%d AND %d))",
                firstCategoryId, secondCategoryId);

        searchQueryDTO.setCategoriesId(Arrays.asList(firstCategoryId, secondCategoryId));

        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO);

        assertThat(solrQuery.get(CommonParams.Q), is(expectedQuery));
    }
}