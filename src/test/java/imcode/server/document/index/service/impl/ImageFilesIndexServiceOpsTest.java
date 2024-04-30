//package imcode.server.document.index.service.impl;
//
//import com.imcode.imcms.WebAppSpringTestConfig;
//import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
//import com.imcode.imcms.domain.component.ImageFileSearchQueryConverter;
//import com.imcode.imcms.domain.dto.SearchImageFileQueryDTO;
//import com.imcode.imcms.domain.dto.SearchQueryDTO;
//import com.imcode.imcms.domain.service.LanguageService;
//import com.imcode.imcms.model.Roles;
//import imcode.server.Imcms;
//import imcode.server.ImcmsConstants;
//import imcode.server.document.index.DocumentIndex;
//import imcode.server.document.index.ImageFileIndex;
//import imcode.server.document.index.service.SolrClientFactory;
//import imcode.server.user.UserDomainObject;
//import imcode.util.io.FileUtility;
//import org.apache.commons.io.FileUtils;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.common.SolrInputDocument;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ImageFilesIndexServiceOpsTest extends WebAppSpringTestConfig {
//	private static final ImageFileSearchQueryConverter searchQueryConverter = new ImageFileSearchQueryConverter();
//	private static final File testSolrFolder = new File("WEB-INF/solr/" + SolrClientFactory.DEFAULT_IMAGE_FILE_METADATA_CORE_NAME).getAbsoluteFile();
//	private static final File mainSolrFolder = new File("src/main/webapp/WEB-INF/solr").getAbsoluteFile();
//	private static final List<String> mockData = new ArrayList<>();
//	private final String testHeadline = "testHeadline";
//
//	private static boolean addedInitDocuments;
//	private static int documentSize = 10;
//
//	private static SolrClient solrClient;
//
//	@InjectMocks
//	private ImageFileIndexServiceOps imageFileIndexServiceOps;
//
//	private SearchImageFileQueryDTO searchQueryDTO;
//
//	@BeforeAll
//	public static void setUp() throws Exception {
//		FileUtils.copyDirectory(mainSolrFolder, testSolrFolder); // assume that test solr folder does not exist
//
//		solrClient = SolrClientFactory.createEmbeddedSolrClient(testSolrFolder.getAbsolutePath(), false);
//
//		mockData.add(")_fwEf$");
//		mockData.add("wefGErg(");
//		mockData.add("GEGgw$d%");
//		mockData.add("vfF ENG+ (pp)");
//		mockData.add("DSTE bv");
//	}
//
//	@AfterAll
//	public static void deleteTestSolrFolder() throws IOException {
//		solrClient.close();
//		FileUtility.forceDelete(testSolrFolder);
//	}
//
//	@BeforeEach
//	public void createSolrInputDocuments() throws IOException, SolrServerException {
//
//		searchQueryDTO = new SearchImageFileQueryDTO("");
//
//		if (!addedInitDocuments) {
//			for (int i = 1; i <= documentSize; i++) {
//				indexDocument(i, addRequiredFields(i));
//			}
//
//			addedInitDocuments = true;
//		}
//	}
//
//	private void indexDocument(int id, SolrInputDocument solrInputDocument) throws IOException, SolrServerException {
//		final int docId = getDocId(id);
//
//		when(imageFileIndexServiceOps..index(docId)).thenReturn(solrInputDocument);
//
//		documentIndexServiceOps.addToIndex(solrClient, String.valueOf(docId));
//	}
//
//	private SolrInputDocument addRequiredFields(int id) {
//		final SolrInputDocument solrInputDocument = new SolrInputDocument();
//		solrInputDocument.addField(ImageFileIndex.FIELD__ID, id);
//		solrInputDocument.addField(ImageFileIndex.FIELD__NAME, getDocId(id));
//		solrInputDocument.addField(ImageFileIndex.FIELD__PATH, new Date());
//		solrInputDocument.addField(ImageFileIndex.FIELD__UPLOADED, new Date());
//		solrInputDocument.addField(ImageFileIndex.FIELD__SIZE, "test_lang_code");
//		solrInputDocument.addField(ImageFileIndex.FIELD__WIDTH, 0);
//		solrInputDocument.addField(ImageFileIndex.FIELD__HEIGHT, true);
//
//		return solrInputDocument;
//	}
//}
