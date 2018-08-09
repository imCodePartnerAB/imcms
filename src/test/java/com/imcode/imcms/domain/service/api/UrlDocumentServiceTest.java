package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UrlDocumentServiceTest {

    private static final int superAdminId = 1;

    @Autowired
    private DocumentDtoFactory documentDtoFactory;

    @Autowired
    private DocumentService<UrlDocumentDTO> urlDocumentService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentUrlRepository documentUrlRepository;

    @Autowired
    private DocumentUrlService documentUrlService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private UrlDocumentDataInitializer urlDocumentDataInitializer;

    @Autowired
    private UserService userService;

    private UrlDocumentDTO emptyUrlDocumentDTO;

    @Before
    public void setUp() {
        Imcms.setUser(new UserDomainObject(superAdminId));
        this.emptyUrlDocumentDTO = documentDtoFactory.createEmptyUrlDocument();
    }

    @Test
    public void createFromParent() {
        final Integer parentDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();
        final UrlDocumentDTO newDoc = urlDocumentService.createFromParent(parentDocId);

        assertNull(newDoc.getId());
        assertEquals(newDoc.getType(), Meta.DocumentType.URL);
        assertNotNull(newDoc.getDocumentURL());
        assertEquals(newDoc.getDocumentURL().getUrl(), "");
        assertNull(newDoc.getDocumentURL().getDocId());
        assertNull(newDoc.getDocumentURL().getId());
    }

    @Test
    public void saveUrlDocument_When_documentDoesNotExist_Expect_Saved() {
        emptyUrlDocumentDTO.setDocumentURL(null);
        urlDocumentService.save(emptyUrlDocumentDTO);

        assertEquals(2, metaRepository.findAll().size()); // one doc with 1001 id
    }

    @Test
    public void saveUrlDocument_When_DocumentUrlDTOIsNotSet_Expect_DocumentUrlDtoIsNotSaved() {
        emptyUrlDocumentDTO.setDocumentURL(null);
        urlDocumentService.save(emptyUrlDocumentDTO);

        assertEquals(0, documentUrlRepository.findAll().size());
    }

    @Test
    public void saveUrlDocument_When_DocumentUrlDtoIsSet_Expect_DocumentUrlDtoIsSaved() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();
        final List<DocumentUrlJPA> all = documentUrlRepository.findAll();

        assertEquals(1, all.size());

        final DocumentURL actualDocumentUrl = documentUrlService.getByDocId(savedDocId);

        final DocumentUrlDTO expectedDocumentUrl = emptyUrlDocumentDTO.getDocumentURL();
        expectedDocumentUrl.setId(actualDocumentUrl.getId());

        assertEquals(expectedDocumentUrl, expectedDocumentUrl);
    }

    @Test
    public void getUrlDocumentByDocId_When_DocumentExists_Expect_Found() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();
        final UrlDocumentDTO actualUrlDocumentDTO = urlDocumentService.get(savedDocId);

        assertNotNull(actualUrlDocumentDTO);
    }

    @Test(expected = DocumentNotExistException.class)
    public void deleteUrlDocument_When_Expect_Deleted() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();

        assertNotNull(urlDocumentService.get(savedDocId));

        urlDocumentService.deleteByDocId(savedDocId);
        metaRepository.flush();
        urlDocumentService.get(savedDocId);
    }

    @Test
    public void deleteUrlDocumentWithItsDocumentUrl_When_DocumentUrlExists_Expect_DocumentUrlIsDeleted() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();

        assertEquals(1, documentUrlRepository.findAll().size());

        urlDocumentService.deleteByDocId(savedDocId);
        metaRepository.flush();

        assertEquals(0, documentUrlRepository.findAll().size());
    }

    @Test
    public void publishUrlDocument_Expect_Published() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();
        final boolean published = urlDocumentService.publishDocument(savedDocId, 1);

        assertEquals(true, published);
    }

    @Test
    public void publishUrlDocument_Expect_NotPublished() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO).getId();
        final int nextVersionIndex = Version.WORKING_VERSION_INDEX + 1;

        // create another version
        versionDataInitializer.createData(nextVersionIndex, savedDocId);

        final boolean published = urlDocumentService.publishDocument(savedDocId, superAdminId);

        assertEquals(false, published);
    }

    @Test
    public void copyUrlDocument_Expect_Copied() {
        final List<CategoryJPA> categories = categoryDataInitializer.createData(3);

        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument();
        urlDocumentDTO.setCategories(new HashSet<>(categories));
        urlDocumentDTO.setKeywords(new HashSet<>(Arrays.asList("1", "2", "3")));

        final UrlDocumentDTO originalUrlDocument = urlDocumentService.save(urlDocumentDTO);

        final UrlDocumentDTO clonedUrlDocument = urlDocumentService.copy(originalUrlDocument.getId());

        assertThat(metaRepository.findAll(), hasSize(3));

        assertThat(clonedUrlDocument.getId(), is(not(originalUrlDocument.getId())));

        assertThat(clonedUrlDocument.getDocumentURL().getDocId(),
                is(not(originalUrlDocument.getDocumentURL().getDocId())));

        final List<CommonContent> originalCommonContents = originalUrlDocument.getCommonContents();
        final List<CommonContent> copiedCommonContents = clonedUrlDocument.getCommonContents();

        IntStream.range(0, originalCommonContents.size())
                .forEach(i -> {
                    final CommonContent originalCommonContent = originalCommonContents.get(i);
                    final CommonContent copiedCommonContent = copiedCommonContents.get(i);

                    assertThat(copiedCommonContent.getId(), is(not(originalCommonContent.getId())));
                    assertThat(copiedCommonContent.getDocId(), is(not(originalCommonContent.getDocId())));
                    assertThat(copiedCommonContent.getHeadline(), is(not(originalCommonContent.getHeadline())));
                    assertThat(copiedCommonContent.getVersionNo(), is(Version.WORKING_VERSION_INDEX));
                });

        checkExistingAuditDTO(clonedUrlDocument.getCreated());
        checkExistingAuditDTO(clonedUrlDocument.getModified());

        checkNotExistingAuditDTO(clonedUrlDocument.getPublished());
        checkNotExistingAuditDTO(clonedUrlDocument.getPublicationEnd());
        checkNotExistingAuditDTO(clonedUrlDocument.getArchived());

        final Set<Category> originalCategories = originalUrlDocument.getCategories();
        final Set<Category> copiedCategories = clonedUrlDocument.getCategories();

        assertThat(copiedCategories.size(), is(originalCategories.size()));
        assertTrue(originalCategories.containsAll(copiedCategories));

        assertThat(clonedUrlDocument.getKeywords(), is(originalUrlDocument.getKeywords()));
    }

    private void checkExistingAuditDTO(AuditDTO auditDTO) {
        assertThat(auditDTO.getId(), is(superAdminId));
        assertThat(auditDTO.getBy(), is(userService.getUser(superAdminId).getLogin()));
        assertNotNull(auditDTO.getDate());
        assertNotNull(auditDTO.getTime());
    }

    private void checkNotExistingAuditDTO(AuditDTO auditDTO) {
        assertNull(auditDTO.getId());
        assertNull(auditDTO.getBy());
        assertNull(auditDTO.getDate());
        assertNull(auditDTO.getTime());
    }
}