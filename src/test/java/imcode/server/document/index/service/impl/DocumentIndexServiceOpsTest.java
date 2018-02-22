package imcode.server.document.index.service.impl;

import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.service.SolrServerFactory;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DocumentIndexServiceOpsTest {

    private static final DocumentSearchQueryConverter documentSearchQueryConverter = new DocumentSearchQueryConverter();

    @InjectMocks
    private DocumentIndexServiceOps documentIndexServiceOps;
    private static final File testSolrFolder = new File("WEB-INF/solr").getAbsoluteFile();
    private static final File mainSolrFolder = new File("src/main/webapp/WEB-INF/solr").getAbsoluteFile();
    private static final String aliasField = DocumentIndex.FIELD__ALIAS;
    private static final List<String> mockData = new ArrayList<>();
    private static String titleField;
    private static int documentSize = 10;
    private static boolean addedInitDocuments;
    private static SolrServer solrServer;
    @Mock
    private DocumentIndexer documentIndexer;
    private SearchQueryDTO searchQueryDTO;

    @BeforeClass
    public static void setUp() throws Exception {
        FileUtils.copyDirectory(mainSolrFolder, testSolrFolder); // assume that test solr folder does not exist

        solrServer = SolrServerFactory.createEmbeddedSolrServer(testSolrFolder.getAbsolutePath());

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user);

        titleField = DocumentIndex.FIELD__META_HEADLINE + "_" + user.getLanguage();

        mockData.add(")_fwEf$");
        mockData.add("wefGErg(");
        mockData.add("GEGgw$d%");
        mockData.add("vfF ENG+ (pp)");
        mockData.add("DSTE bv");
    }

    @AfterClass
    public static void deleteTestSolrFolder() throws IOException {
        FileUtility.forceDelete(testSolrFolder);
    }

    @Before
    public void createSolrInputDocuments() throws IOException, SolrServerException {

        searchQueryDTO = new SearchQueryDTO();

        if (!addedInitDocuments) {
            for (int i = 1; i <= documentSize; i++) {
                indexDocument(i, addRequiredFields(i));
            }

            addedInitDocuments = true;
        }
    }

    @Test
    public void getDocumentWithMaxId_When_DefaultSearchQuerySet_Expect_isFirst() throws Exception {
        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(documentSize));

        assertThat(solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__META_ID),
                is(String.valueOf(getDocId(documentSize))));
    }

    // these tests are for searching by term

    @Test
    public void getDocument_When_TermIsSetAsKeyword_Expect_Returned() throws Exception {
        testKeywordOrAliasOrHeadlineForOneDocument("keyword");
    }

    @Test
    public void getDocument_When_TermIsSetAsAlias_Expect_Returned() throws Exception {
        testKeywordOrAliasOrHeadlineForOneDocument("alias");
    }

    @Test
    public void getDocument_When_TermIsSetAsHeadline_Expect_Returned() throws Exception {
        testKeywordOrAliasOrHeadlineForOneDocument("headline");
    }

    @Test
    public void getDocuments_When_TermIsSetAsKeyword_Expect_DocumentsAreReturned() throws Exception {
        testKeywordOrAliasOrHeadlineForMultipleDocuments("keyword");
    }

    @Test
    public void getDocuments_When_TermIsSetAsAlias_Expect_DocumentsAreReturned() throws Exception {
        testKeywordOrAliasOrHeadlineForMultipleDocuments("alias");
    }

    @Test
    public void getDocuments_When_TermIsSetAsTitle_Expect_DocumentsAreReturned() throws Exception {
        testKeywordOrAliasOrHeadlineForMultipleDocuments("headline");
    }

    @Test
    public void getDocument_When_TermIsSetAsSpecifiedDocId_Expect_Returned() throws Exception {
        final int id = documentSize;

        final String termValue = String.valueOf(getDocId(id));

        searchQueryDTO.setTerm(termValue);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(1));
        assertThat(solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__META_ID), is(termValue));
    }

    @Test
    public void getDocuments_When_TermIsSetAsLastDigitOfSpecifiedId_Expect_FoundDocuments() throws Exception {
        testForLastDigits(1);
    }

    @Test
    public void getDocuments_When_TermIsSetAsLastTwoDigitsOfSpecifiedId_Expect_FoundDocuments()
            throws Exception {

        testForLastDigits(2);
    }

    @Test
    public void getDocuments_When_TermIsSetAsLastThreeDigitsOfSpecifiedId_Expect_FoundDocuments()
            throws Exception {

        testForLastDigits(3);
    }

    @Test
    public void getDocuments_When_TermIsSetAsLastFourDigitsOfSpecifiedId_Expect_FoundDocuments()
            throws Exception {

        testForLastDigits(4);
    }

    // these tests are for searching by user id

    @Test
    public void getDocuments_When_UserIdSet_Expect_Returned() throws Exception {
        final int firstUserId = 1;
        final int documentNumberFirstUser = 5;
        final List<Integer> firstUserDocsId = new ArrayList<>();

        final int secondUserId = 2;
        final int documentNumberSecondUser = 7;
        final List<Integer> secondUserDocsId = new ArrayList<>();

        indexDocuments(documentNumberFirstUser, firstUserDocsId, DocumentIndex.FIELD__CREATOR_ID, firstUserId);
        indexDocuments(documentNumberSecondUser, secondUserDocsId, DocumentIndex.FIELD__CREATOR_ID, secondUserId);

        // checking for first user id
        searchQueryDTO.setUserId(firstUserId);

        SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(documentNumberFirstUser));

        solrDocumentList.forEach(doc -> assertTrue(
                firstUserDocsId.contains(Integer.parseInt((String) doc.getFieldValue(DocumentIndex.FIELD__META_ID)))
        ));

        firstUserDocsId.forEach(id -> verify(documentIndexer, times(1)).index(id));

        // checking for first user id
        searchQueryDTO.setUserId(secondUserId);

        solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(documentNumberSecondUser));

        solrDocumentList.forEach(doc -> assertTrue(
                secondUserDocsId.contains(Integer.parseInt((String) doc.getFieldValue(DocumentIndex.FIELD__META_ID)))
        ));

        secondUserDocsId.forEach(id -> verify(documentIndexer, times(1)).index(id));
    }

    // these tests are for searching by page request

    @Test
    public void search_When_UseDefaultPageRequest_Expect_Found() throws Exception {
        assertThat(getSolrDocumentList(searchQueryDTO).size(), is(documentSize));
    }

    @Test
    public void getDocuments_When_SecondPageIsSet_Expect_Returned() throws Exception {
        final int pageSize = 5;

        searchQueryDTO.setPage(new PageRequestDTO(1, pageSize));

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(pageSize));

        final List<Integer> expectedDocId = IntStream
                .rangeClosed(documentSize - 2 * pageSize + 1, documentSize - pageSize)
                .map(this::getDocId)
                .boxed()
                .collect(Collectors.toList());

        solrDocumentList.forEach(solrDocument -> {
            final int docID = Integer.parseInt((String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_ID));
            assertTrue(expectedDocId.contains(docID));
        });
    }

    @Test
    public void getDocuments_When_SpecifiedPageRequest_Expect_Returned() throws Exception {
        final int pageSize = 3;

        searchQueryDTO.setPage(new PageRequestDTO(0, pageSize));

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(pageSize));

        final List<Integer> expectedDocId = IntStream
                .rangeClosed(documentSize - pageSize + 1, documentSize)
                .map(this::getDocId)
                .boxed()
                .collect(Collectors.toList());

        solrDocumentList.forEach(solrDocument -> {
            final int docID = Integer.parseInt((String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_ID));
            assertTrue(expectedDocId.contains(docID));
        });
    }

    @Test
    public void getDocuments_When_SortingByTitleASC_Expect_CorrectData() throws Exception {
        checkSorting(titleField, Sort.Direction.ASC);
    }

    @Test
    public void getDocuments_When_SortingByTitleDESC_Expect_CorrectData() throws Exception {
        checkSorting(titleField, Sort.Direction.DESC);
    }

    @Test
    public void getDocuments_When_SortingByAliasASC_Expect_CorrectData() throws Exception {
        checkSorting(aliasField, Sort.Direction.ASC);
    }

    @Test
    public void getDocuments_When_SortingByAliasDESC_Expect_CorrectData() throws Exception {
        checkSorting(aliasField, Sort.Direction.DESC);
    }

    // these tests are for searching by categories id

    @Test
    public void getDocument_When_CategoryIsSet_Expect_OneDocumentIsReturned() throws Exception {
        final int id = ++documentSize;
        final int categoryId = 1;

        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        solrInputDocument.addField(DocumentIndex.FIELD__CATEGORY_ID, categoryId);
        indexDocument(id, solrInputDocument);

        searchQueryDTO.setCategoriesId(Collections.singletonList(categoryId));

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(1));

        verify(documentIndexer, times(1)).index(getDocId(id));
    }

    @Test
    public void getDocuments_When_SpecifiedCategorySet_Expect_DocumentsAreReturned() throws Exception {

        final int firstCategoryId = 2;
        final int documentNumberWithFirstCategory = 1;
        final List<Integer> docIdWithFirstCategory = new ArrayList<>();

        final int secondCategoryId = 3;
        final int documentNumberWithSecondCategory = 3;
        final List<Integer> docIdWithSecondCategory = new ArrayList<>();

        indexDocuments(
                documentNumberWithFirstCategory, docIdWithFirstCategory,
                DocumentIndex.FIELD__CATEGORY_ID, firstCategoryId
        );

        indexDocuments(
                documentNumberWithSecondCategory, docIdWithSecondCategory,
                DocumentIndex.FIELD__CATEGORY_ID, secondCategoryId
        );

        solrServer.commit();

        // checking for first category id
        searchQueryDTO.setCategoriesId(Collections.singletonList(firstCategoryId));

        SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(documentNumberWithFirstCategory));

        assertThat(solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__META_ID),
                is(String.valueOf(docIdWithFirstCategory.get(0))));

        // checking for second category id
        searchQueryDTO.setCategoriesId(Collections.singletonList(secondCategoryId));

        solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(documentNumberWithSecondCategory));

        solrDocumentList.forEach(solrDocument -> {
            final int docID = Integer.parseInt((String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_ID));
            assertTrue(docIdWithSecondCategory.contains(docID));
        });
    }

    private void testForLastDigits(int lastDigitsNumber) throws Exception {
        final String docId = String.valueOf(getDocId(new Random().nextInt(documentSize) + 1));

        final String termValue = docId.substring(docId.length() - lastDigitsNumber);

        searchQueryDTO.setTerm(termValue);

        final long expectedDocumentSize = IntStream.rangeClosed(1, documentSize)
                .map(this::getDocId)
                .mapToObj(String::valueOf)
                .filter(docIdString -> docIdString.contains(termValue))
                .count();

        assertThat(getSolrDocumentList(searchQueryDTO).getNumFound(), is(expectedDocumentSize));
    }

    private void testKeywordOrAliasOrHeadlineForOneDocument(String field) throws Exception {
        final int id = ++documentSize;

        final String termValue = "test_term_valueA" + field;

        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        addFieldToSolrDocument(solrInputDocument, field, termValue);
        indexDocument(id, solrInputDocument);

        searchQueryDTO.setTerm(termValue);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(1));
        assertThat(solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__ID), is(String.valueOf(documentSize)));

        verify(documentIndexer, times(1)).index(getDocId(id));
    }

    private void testKeywordOrAliasOrHeadlineForMultipleDocuments(String field) throws Exception {
        final List<Integer> ids = new ArrayList<>();

        final String termValue = "test_term_valueB" + field;

        final int documentNumberWithSpecifiedKeyword = 5;

        for (int i = 0; i < documentNumberWithSpecifiedKeyword; i++) {
            final int id = ++documentSize;
            ids.add(id);

            final SolrInputDocument solrInputDocument = addRequiredFields(id);
            addFieldToSolrDocument(solrInputDocument, field, termValue);
            indexDocument(id, solrInputDocument);
        }

        searchQueryDTO.setTerm(termValue);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertThat(solrDocumentList.size(), is(documentNumberWithSpecifiedKeyword));

        solrDocumentList.forEach(doc ->
                assertTrue(ids.contains(Integer.parseInt((String) doc.getFieldValue(DocumentIndex.FIELD__ID))))
        );

        ids.forEach(id -> verify(documentIndexer, times(1)).index(getDocId(id)));
    }

    private void checkSorting(String property, Sort.Direction direction) throws Exception {

        final List<Integer> docIds = new ArrayList<>();

        for (String data : mockData) {
            final int id = ++documentSize;
            docIds.add(getDocId(id));

            final SolrInputDocument solrInputDocument = addRequiredFields(id);

            data += property.substring(0, 1) + direction;
            solrInputDocument.addField(titleField, data);
            solrInputDocument.addField(aliasField, data);

            indexDocument(id, solrInputDocument);
        }

        final PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setProperty(property);
        pageRequestDTO.setDirection(direction);

        searchQueryDTO.setPage(pageRequestDTO);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        final List<String> actualFieldValues = solrDocumentList.stream()
                .map(solrDocument -> (String) solrDocument.getFieldValue(property))
                .collect(Collectors.toList());

        final List<String> expectedFieldValues = new ArrayList<>(actualFieldValues);

        if (direction == Sort.Direction.ASC)
            expectedFieldValues.sort(Comparator.nullsLast(String::compareTo));
        else
            expectedFieldValues.sort(Comparator.nullsLast(Collections.reverseOrder()));

        assertThat(actualFieldValues, is(expectedFieldValues));

        docIds.forEach(docId -> verify(documentIndexer, times(1)).index(docId));
    }

    private void indexDocument(int id, SolrInputDocument solrInputDocument) throws IOException, SolrServerException {
        final int docId = getDocId(id);

        when(documentIndexer.index(docId)).thenReturn(solrInputDocument);

        documentIndexServiceOps.addDocsToIndex(solrServer, docId);
    }

    private SolrInputDocument addRequiredFields(int id) {
        final SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField(DocumentIndex.FIELD__ID, id);
        solrInputDocument.addField(DocumentIndex.FIELD__META_ID, getDocId(id));
        solrInputDocument.addField(DocumentIndex.FIELD__TIMESTAMP, new Date());
        solrInputDocument.addField(DocumentIndex.FIELD__LANGUAGE_CODE, "test_lang_code");
        solrInputDocument.addField(DocumentIndex.FIELD__VERSION_NO, 0);
        solrInputDocument.addField(DocumentIndex.FIELD__SEARCH_ENABLED, true);
        solrInputDocument.addField(DocumentIndex.FIELD__ROLE_ID, 2);

        return solrInputDocument;
    }

    private int getDocId(int id) {
        return id + 1000;
    }

    private void addFieldToSolrDocument(SolrInputDocument solrInputDocument, String field, String value) {
        switch (field) {
            case "headline":
                final String userLanguage = Imcms.getUser().getLanguage();
                solrInputDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_" + userLanguage, value);
                break;
            case "alias":
                solrInputDocument.addField(DocumentIndex.FIELD__ALIAS, value);
                break;
            case "keyword":
                solrInputDocument.addField(DocumentIndex.FIELD__KEYWORD, value);
                break;
        }
    }

    private void indexDocuments(int size, List<Integer> docsId, String field, int value) throws Exception {
        for (int i = 0; i < size; i++) {
            final int id = ++documentSize;
            docsId.add(getDocId(id));

            final SolrInputDocument solrInputDocument = addRequiredFields(id);
            solrInputDocument.addField(field, value);
            indexDocument(id, solrInputDocument);
        }
    }

    private SolrDocumentList getSolrDocumentList(SearchQueryDTO queryDTO) throws SolrServerException {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(queryDTO);
        final QueryResponse queryResponse = documentIndexServiceOps.query(solrServer, solrQuery);

        return queryResponse.getResults();
    }
}