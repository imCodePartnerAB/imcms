package imcode.server.document.index.service.impl;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.service.IndexServiceOps;
import imcode.server.document.index.service.SolrClientFactory;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentIndexServiceOpsTest extends WebAppSpringTestConfig {

	private static final DocumentSearchQueryConverter documentSearchQueryConverter = new DocumentSearchQueryConverter();
	private static final File testSolrFolder = new File("WEB-INF/solr").getAbsoluteFile();
	private static final File mainSolrFolder = new File("src/main/webapp/WEB-INF/solr").getAbsoluteFile();
	private static final List<String> mockData = new ArrayList<>();
	private final String testHeadline = "testHeadline";

	private static String titleField;
	private static String titleFieldLower;
	private static String aliasField;
	private static String aliasFieldLower;
	private static int documentSize = 10;
	private static boolean addedInitDocuments;
	private static SolrClient solrClient;

	@InjectMocks
	private IndexServiceOps documentIndexServiceOps;
	@Mock
	private DocumentIndexer documentIndexer;

	private SearchQueryDTO searchQueryDTO;

    @Autowired
    private LanguageService languageService;

    @BeforeAll
    public static void setUp() throws Exception {
	    FileUtils.copyDirectory(mainSolrFolder, testSolrFolder); // assume that test solr folder does not exist

	    solrClient = SolrClientFactory.createEmbeddedSolrClient(testSolrFolder.getAbsolutePath(), , false);

	    final UserDomainObject user = new UserDomainObject(1);
	    user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
	    Imcms.setUser(user);

	    titleField = DocumentIndex.FIELD__META_HEADLINE + "_" + user.getLanguage();
	    titleFieldLower = DocumentIndex.FIELD_META_HEADLINE + "_" + user.getLanguage();
	    aliasField = DocumentIndex.FIELD__META_ALIAS + '_' + user.getLanguage();
	    aliasFieldLower = DocumentIndex.FIELD_META_ALIAS + '_' + user.getLanguage();

	    mockData.add(")_fwEf$");
	    mockData.add("wefGErg(");
	    mockData.add("GEGgw$d%");
	    mockData.add("vfF ENG+ (pp)");
	    mockData.add("DSTE bv");
    }

    @AfterAll
    public static void deleteTestSolrFolder() throws IOException {
        solrClient.close();
        FileUtility.forceDelete(testSolrFolder);
    }

    @BeforeEach
    public void createSolrInputDocuments() throws IOException, SolrServerException {

        searchQueryDTO = new SearchQueryDTO("");

        if (!addedInitDocuments) {
            for (int i = 1; i <= documentSize; i++) {
                indexDocument(i, addRequiredFields(i));
            }

            addedInitDocuments = true;
        }

        Imcms.setLanguage(languageService.getDefaultLanguage());
    }

    @Test
    public void getDocumentWithMaxId_When_DefaultSearchQuerySet_Expect_isFirst() throws Exception {
        final int id = ++documentSize;
        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        addFieldToSolrDocument(solrInputDocument, "headline_lower", testHeadline);
        indexDocument(id, solrInputDocument);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), documentSize);
        assertEquals(
                solrDocumentList.get(0).getFieldValue(titleFieldLower),
                testHeadline.toLowerCase()
        );
    }

    @Test
    public void reindexDocument_When_DocumentIsRemovedAndAdded_Expect_ContentUpdated() throws Exception {
        final int id = ++documentSize;

        final String headlineValue = "Headline_Testing_Value_Remove_Added";
        final String newHeadlineValue = "Updated_Headline_Testing_Value_Remove_Added";

        final SolrInputDocument solrInputDocumentOriginal = addRequiredFields(id);
        solrInputDocumentOriginal.addField(titleField, headlineValue);
        indexDocument(id, solrInputDocumentOriginal);

        searchQueryDTO.setTerm(headlineValue);
        SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), 1);
        assertEquals(solrDocumentList.get(0).getFieldValue(titleField), headlineValue);

        documentIndexServiceOps.deleteFromIndex(solrClient, String.valueOf(getDocId(id)));

        solrDocumentList = getSolrDocumentList(searchQueryDTO);
        assertEquals(solrDocumentList.size(), 0);

        final SolrInputDocument solrInputDocumentUpdated = addRequiredFields(id);
        solrInputDocumentUpdated.addField(titleField, newHeadlineValue);
        indexDocument(id, solrInputDocumentUpdated);

        searchQueryDTO.setTerm(newHeadlineValue);
        solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), 1);
        assertEquals(solrDocumentList.get(0).getFieldValue(titleField), newHeadlineValue);
    }

    @Test
    public void reindexDocument_When_DocumentIsAdded_Expect_ContentUpdated() throws Exception {
        final int id = ++documentSize;

        final String headlineValue = "Headline_Testing_Value_Added";
        final String newHeadlineValue = "Updated_Headline_Testing_Value_Added";

        final SolrInputDocument solrInputDocumentOriginal = addRequiredFields(id);
        solrInputDocumentOriginal.addField(titleField, headlineValue);
        indexDocument(id, solrInputDocumentOriginal);

        searchQueryDTO.setTerm(headlineValue);
        SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), 1);
        assertEquals(solrDocumentList.get(0).getFieldValue(titleField), headlineValue);

        final SolrInputDocument solrInputDocumentUpdated = addRequiredFields(id);
        solrInputDocumentUpdated.addField(titleField, newHeadlineValue);
        indexDocument(id, solrInputDocumentUpdated);

        searchQueryDTO.setTerm(newHeadlineValue);
        solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), 1);
        assertEquals(solrDocumentList.get(0).getFieldValue(titleField), newHeadlineValue);
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

        assertEquals(solrDocumentList.size(), 1);
        assertEquals(solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__META_ID), termValue);
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

        assertEquals(solrDocumentList.size(), documentNumberFirstUser);

        solrDocumentList.forEach(doc -> assertTrue(
                firstUserDocsId.contains(Integer.parseInt((String) doc.getFieldValue(DocumentIndex.FIELD__META_ID)))
        ));

        firstUserDocsId.forEach(id -> then(documentIndexer).should().index(id));

        // checking for first user id
        searchQueryDTO.setUserId(secondUserId);

        solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), documentNumberSecondUser);

        solrDocumentList.forEach(doc -> assertTrue(
                secondUserDocsId.contains(Integer.parseInt((String) doc.getFieldValue(DocumentIndex.FIELD__META_ID)))
        ));

        secondUserDocsId.forEach(id -> then(documentIndexer).should().index(id));
    }

    // these tests are for searching by page request

    @Test
    public void search_When_UseDefaultPageRequest_Expect_Found() throws Exception {
        assertEquals(getSolrDocumentList(searchQueryDTO).size(), documentSize);
    }

    @Test
    public void getDocuments_When_SecondPageIsSet_Expect_Returned() throws Exception {
        final int pageSize = 5;
        final int id = ++documentSize;

        final DocumentPageRequestDTO documentPageRequestDTO = new DocumentPageRequestDTO();
        documentPageRequestDTO.setSkip(pageSize);
        documentPageRequestDTO.setSize(pageSize);

        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        solrInputDocument.addField(titleField, testHeadline);
        solrInputDocument.addField(titleFieldLower, testHeadline.toLowerCase());
        indexDocument(id, solrInputDocument);


        searchQueryDTO.setPage(documentPageRequestDTO);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), pageSize);

        searchQueryDTO.setTerm(testHeadline.toLowerCase());

        SolrDocumentList solrDocuments = getSolrDocumentList(searchQueryDTO);

        solrDocuments.forEach(solrDocument -> {
            final String title = (String) solrDocument.getFieldValue(titleField);
            assertEquals(testHeadline, title);
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

        assertEquals(solrDocumentList.size(), 1);

        then(documentIndexer).should().index(getDocId(id));
    }

    @Test
    public void getDocument_When_HeadLineHasSpaceBetweenWord_Expect_OneDocumentIsReturned() throws Exception {
        final int id = ++documentSize;
        final String headLine = "headLine test";
        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        solrInputDocument.addField(titleField, headLine);
        indexDocument(id, solrInputDocument);

        searchQueryDTO.setTerm(String.format("\"%s\"",headLine));    //looking for a quote

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(1, solrDocumentList.size());

        then(documentIndexer).should().index(getDocId(id));
    }

    @Test
    public void getDocument_When_SearchInLowerCase_Expect_CorrectResult() throws Exception {
        final int id = ++documentSize;
        final String headLine = "HeadLine_test".toUpperCase();
        final String inputTest = "headline_test";
        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        solrInputDocument.addField(titleField, headLine);
        solrInputDocument.addField(titleFieldLower, headLine.toLowerCase());
        indexDocument(id, solrInputDocument);

        searchQueryDTO.setTerm(inputTest);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertFalse(solrDocumentList.isEmpty());
        assertEquals(solrDocumentList.size(), 1);
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

        solrClient.commit();

        // checking for first category id
        searchQueryDTO.setCategoriesId(Collections.singletonList(firstCategoryId));

        SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), documentNumberWithFirstCategory);
        assertEquals(
                solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__META_ID),
                String.valueOf(docIdWithFirstCategory.get(0))
        );

        // checking for second category id
        searchQueryDTO.setCategoriesId(Collections.singletonList(secondCategoryId));

        solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), documentNumberWithSecondCategory);

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

        assertEquals(getSolrDocumentList(searchQueryDTO).getNumFound(), expectedDocumentSize);
    }

    private void testKeywordOrAliasOrHeadlineForOneDocument(String field) throws Exception {
        final int id = ++documentSize;

        final String termValue = "test_term_valueA" + field;

        final SolrInputDocument solrInputDocument = addRequiredFields(id);
        addFieldToSolrDocument(solrInputDocument, field, termValue);
        indexDocument(id, solrInputDocument);

        searchQueryDTO.setTerm(termValue);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        assertEquals(solrDocumentList.size(), 1);
        assertEquals(solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD__ID), String.valueOf(documentSize));

        then(documentIndexer).should().index(getDocId(id));
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

        assertEquals(solrDocumentList.size(), documentNumberWithSpecifiedKeyword);

        solrDocumentList.forEach(doc ->
                assertTrue(ids.contains(Integer.parseInt((String) doc.getFieldValue(DocumentIndex.FIELD__ID))))
        );

        ids.forEach(id -> then(documentIndexer).should().index(getDocId(id)));
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

        final DocumentPageRequestDTO documentPageRequestDTO = new DocumentPageRequestDTO();
        documentPageRequestDTO.setProperty(property);
        documentPageRequestDTO.setDirection(direction);

        searchQueryDTO.setPage(documentPageRequestDTO);

        final SolrDocumentList solrDocumentList = getSolrDocumentList(searchQueryDTO);

        final List<String> actualFieldValues = solrDocumentList.stream()
                .map(solrDocument -> (String) solrDocument.getFieldValue(property))
                .collect(Collectors.toList());

        final List<String> expectedFieldValues = new ArrayList<>(actualFieldValues);

        if (direction == Sort.Direction.ASC)
            expectedFieldValues.sort(Comparator.nullsLast(String::compareTo));
        else
            expectedFieldValues.sort(Comparator.nullsLast(Collections.reverseOrder()));

        assertEquals(actualFieldValues, expectedFieldValues);

        docIds.forEach(docId -> then(documentIndexer).should().index(docId));
    }

    private void indexDocument(int id, SolrInputDocument solrInputDocument) throws IOException, SolrServerException {
        final int docId = getDocId(id);

        when(documentIndexer.index(docId)).thenReturn(solrInputDocument);

        documentIndexServiceOps.addToIndex(solrClient, String.valueOf(docId));
    }

    private SolrInputDocument addRequiredFields(int id) {
        final SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField(DocumentIndex.FIELD__ID, id);
        solrInputDocument.addField(DocumentIndex.FIELD__META_ID, getDocId(id));
        solrInputDocument.addField(DocumentIndex.FIELD__TIMESTAMP, new Date());
        solrInputDocument.addField(DocumentIndex.FIELD__MODIFIED_DATETIME, new Date());
        solrInputDocument.addField(DocumentIndex.FIELD__LANGUAGE_CODE, "test_lang_code");
        solrInputDocument.addField(DocumentIndex.FIELD__VERSION_NO, 0);
        solrInputDocument.addField(DocumentIndex.FIELD__SEARCH_ENABLED, true);
        solrInputDocument.addField(DocumentIndex.FIELD__ROLE_ID, Roles.USER.getId());
        solrInputDocument.addField(DocumentIndex.FIELD__VISIBLE, true);

        return solrInputDocument;
    }

    private int getDocId(int id) {
        return id + 1000;
    }

    private void addFieldToSolrDocument(SolrInputDocument solrInputDocument, String field, String value) {
        switch (field) {
	        case "headline":
		        solrInputDocument.addField(titleField, value);
		        break;
	        case "headline_lower":
		        solrInputDocument.addField(titleFieldLower, value.toLowerCase());
		        break;
	        case "alias":
		        solrInputDocument.addField(aliasField, value);
		        break;
	        case "alias_lower":
		        solrInputDocument.addField(aliasFieldLower, value.toLowerCase());
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

    private SolrDocumentList getSolrDocumentList(SearchQueryDTO queryDTO) throws SolrServerException, IOException {
        final SolrQuery solrQuery = documentSearchQueryConverter.convertToSolrQuery(queryDTO, false);
        final QueryResponse queryResponse = documentIndexServiceOps.query(solrClient, solrQuery);

        return queryResponse.getResults();
    }
}
