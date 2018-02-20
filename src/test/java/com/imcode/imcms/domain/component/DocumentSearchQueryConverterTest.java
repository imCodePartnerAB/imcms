package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Imcms.class})
public class DocumentSearchQueryConverterTest {

    private static DocumentSearchQueryConverter documentSearchQueryConverter;
    private SearchQueryDTO searchQueryDTO;

    @BeforeClass
    public static void setConverter() {
        documentSearchQueryConverter = new DocumentSearchQueryConverter();
    }

    @Before
    public void setUp() {
        searchQueryDTO = new SearchQueryDTO();

        PowerMockito.mockStatic(Imcms.class);
        PowerMockito.when(Imcms.getUser()).thenReturn(new UserDomainObject(1));
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
                DocumentIndex.FIELD__META_HEADLINE,
                DocumentIndex.FIELD__META_TEXT,
                DocumentIndex.FIELD__KEYWORD,
                DocumentIndex.FIELD__TEXT,
                DocumentIndex.FIELD__ALIAS})
                .map(field -> String.format("%s:*%s*", field, term))
                .collect(Collectors.joining(" "));

        assertThat(solrQuery.get(CommonParams.Q), is(expected));
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

        final PageRequestDTO pageRequestDTO = new PageRequestDTO(expectedPage, expectedSize);

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
}