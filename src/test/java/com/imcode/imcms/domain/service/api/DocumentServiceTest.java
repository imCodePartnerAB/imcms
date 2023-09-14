package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.*;
import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.component.ImageCacheManager;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.*;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import com.imcode.imcms.persistence.repository.*;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.image.Format;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.imcode.imcms.enums.TypeSort.TREE_SORT;
import static com.imcode.imcms.model.Text.Type.TEXT;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.DO_NOT_SHOW;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Transactional
public class DocumentServiceTest extends WebAppSpringTestConfig {

    private static File testSolrFolder;
    private final DocumentsCache documentsCache = mock(DocumentsCache.class);
    private DocumentDTO createdDoc;
    private DocumentService<DocumentDTO> documentService;
    @Autowired
    private UserDataInitializer userDataInitializer;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;
	@Autowired
	private CommonContentDataInitializer commonContentDataInitializer;
    @Autowired
    private CommonContentService commonContentService;
    @Autowired
    private TextService textService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private LoopService loopService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private DocumentWasteBasketService documentWasteBasketService;
    @Autowired
    private LoopDataInitializer loopDataInitializer;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MenuDataInitializer menuDataInitializer;
    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private TextRepository textRepository;// instead of initializer :)
    @Autowired
    private VersionService versionService;
    @Autowired
    private MetaRepository metaRepository;
    @Autowired
    private ImageCacheManager imageCacheManager;
    @Autowired
    private TextDocumentTemplateRepository textDocumentTemplateRepository;
    @Autowired
    private TemplateDataInitializer templateDataInitializer;
    @Autowired
    private BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO;
    @Autowired
    private Function<DocumentDTO, Meta> documentDtoToMeta;
    @Autowired
    @Qualifier("versionedContentServices")
    private List<VersionedContentService> versionedContentServices;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private LanguageDataInitializer languageDataInitializer;
    @Autowired
    private BasicImportDocumentInfoService basicImportDocumentInfoService;

    @Autowired
    private Config config;

    @Autowired
    private Function<Menu, MenuDTO> menuToMenuDTO;

    @Autowired
    private Function<ImageJPA, ImageDTO> imageJPAImageDTO;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @AfterAll
    public static void shutDownSolr() {
        try {
            FileUtility.forceDelete(testSolrFolder);
        } catch (Exception e) {
            // windows user may receive it
        }
    }

    @PostConstruct
    private void init() {
        documentService = new DefaultDocumentService(
                textDocumentTemplateRepository,
                metaRepository,
                metaToDocumentDTO,
                documentDtoToMeta,
                commonContentService,
                versionService,
                textService,
                imageService,
                loopService,
                documentsCache,
                versionedContentServices,
                menuService,
                menuToMenuDTO,
                imageJPAImageDTO,
                imageCacheManager,
                basicImportDocumentInfoService);
        ((DefaultDocumentService) documentService).init();
    }

    @BeforeEach
    public void setUp() throws Exception {
        documentDataInitializer.cleanRepositories();
        templateDataInitializer.cleanRepositories();
        userDataInitializer.cleanRepositories();
		commonContentDataInitializer.cleanRepositories();

        createdDoc = documentDataInitializer.createData();

        testSolrFolder = new File(config.getSolrHome());

        if (testSolrFolder.mkdirs()) {
            FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);
        }

        final UserDomainObject user = new UserDomainObject(1);
        user.setLogin("admin");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user); // means current user is admin now

        final Language currentLanguage = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(currentLanguage);
    }

    @Test
    public void createFromParent_When_ParentExist_Expect_Created() {
        final DocumentDTO childDoc = documentService.createFromParent(createdDoc.getId());

        assertNotNull(childDoc);

        // equal things
        assertEquals(childDoc.getType(), createdDoc.getType());
        assertEquals(childDoc.getKeywords(), createdDoc.getKeywords());
        assertEquals(childDoc.getCategories(), createdDoc.getCategories());
        assertEquals(childDoc.getRestrictedPermissions(), createdDoc.getRestrictedPermissions());
        assertEquals(childDoc.getRoleIdToPermission(), createdDoc.getRoleIdToPermission());
        assertEquals(childDoc.getProperties(), createdDoc.getProperties());
        assertEquals(childDoc.getTarget(), createdDoc.getTarget());
        assertEquals(childDoc.getDisabledLanguageShowMode(), createdDoc.getDisabledLanguageShowMode());
        assertEquals(childDoc.getDisabledLanguageShowMode(), createdDoc.getDisabledLanguageShowMode());
        assertEquals(childDoc.getCommonContents().size(), createdDoc.getCommonContents().size());
        assertEquals(childDoc.getLatestVersion().getId(), createdDoc.getCurrentVersion().getId());

        // special things
        assertNull(childDoc.getId());
        assertEquals(childDoc.getPublicationStatus(), PublicationStatus.NEW);
        assertEquals(childDoc.getPublicationEnd(), new AuditDTO());
        assertEquals(childDoc.getPublished(), new AuditDTO());
        assertEquals(childDoc.getArchived(), new AuditDTO());
        assertEquals(childDoc.getCreated(), new AuditDTO());
        assertEquals(childDoc.getModified(), new AuditDTO());
        assertEquals(childDoc.getCurrentVersion().getId(), Integer.valueOf(Version.WORKING_VERSION_INDEX));

        final List<CommonContent> childCommonContents = childDoc.getCommonContents();
        final List<CommonContent> commonContents = createdDoc.getCommonContents();

        for (int i = 0; i < childCommonContents.size(); i++) {
            final CommonContent childCommonContent = childCommonContents.get(i);
            final CommonContent commonContent = commonContents.get(i);

            assertEquals(childCommonContent.getLanguage(), commonContent.getLanguage());
	        assertEquals(childCommonContent.getHeadline(), commonContent.getHeadline());
	        assertEquals(childCommonContent.getAlias(), commonContent.getAlias());
	        assertEquals(childCommonContent.getMenuText(), commonContent.getMenuText());

            assertNull(childCommonContent.getId());
            assertNull(childCommonContent.getDocId());
            assertEquals(childCommonContent.getVersionNo(), Integer.valueOf(Version.WORKING_VERSION_INDEX));
        }
    }

    @Test
    public void get() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        assertEquals(documentDTO, createdDoc);
    }

    @Test
    public void get_When_DocumentNotExist_Expect_CorrectException() {
        assertThrows(DocumentNotExistException.class,
                () -> documentService.get(((Long) System.currentTimeMillis()).intValue()));
    }

    @Test
    public void save_With_Target_Expect_Saved() {
        final String testTarget = "_test";
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        documentDTO.setTarget(testTarget);

        documentService.save(documentDTO);

        final DocumentDTO documentDTO1 = documentService.get(documentDTO.getId());

        assertEquals(documentDTO1, documentDTO);
    }

    @Test
    public void save_When_CustomCommonContentsSet_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        final List<CommonContent> commonContents = documentDTO.getCommonContents();

        for (int i = 0; i < commonContents.size(); i++) {
            CommonContent commonContent = commonContents.get(i);
            commonContent.setHeadline("Test headline " + i);
            commonContent.setMenuText("Test menu text " + i);
            commonContent.setEnabled((i % 2) == 0);
        }

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getCommonContents(), commonContents);
    }

    @Test
    public void save_When_TargetAndAliasChanged_Expect_Saved() {
	    final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
	    final String newTarget = "_blank";
	    final String newAlias = "test-alias";

	    documentDTO.setTarget(newTarget);
	    documentDTO.getCommonContents().stream().filter(content -> Objects.equals(content.getLanguage(), Imcms.getLanguage()))
			    .findAny().ifPresent(content -> content.setAlias(newAlias));
	    documentService.save(documentDTO);

	    final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

	    final CommonContent commonContent = savedDocumentDTO.getCommonContents().stream()
			    .filter(content -> Objects.equals(content.getLanguage(), Imcms.getLanguage()))
			    .findAny().get();

	    assertEquals(savedDocumentDTO.getTarget(), newTarget);
	    assertEquals(commonContent.getAlias(), newAlias);
    }

	@Test
	public void save_When_DuplicateAlias_Expect_SavedAndEmptyAlias() {
		final String newAlias = "test-alias";
		final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
		final DocumentDTO dto = documentService.createFromParent(documentDTO.getId());

		dto.getCommonContents().stream().filter(content -> Objects.equals(content.getLanguage(), Imcms.getLanguage()))
				.findAny().ifPresent(content -> content.setAlias(newAlias));

		documentDTO.getCommonContents().stream().filter(content -> Objects.equals(content.getLanguage(), Imcms.getLanguage()))
				.findAny().ifPresent(content -> content.setAlias(newAlias));

		documentService.save(documentDTO);
		documentService.save(dto);

		final DocumentDTO savedDocumentDTO = documentService.get(dto.getId());

		final CommonContent commonContent = savedDocumentDTO.getCommonContents().stream()
				.filter(content -> Objects.equals(content.getLanguage(), Imcms.getLanguage()))
				.findAny().get();

		assertEquals("", commonContent.getAlias());
	}

	@Test
	public void save_When_DifferentPublicationStatusSet_Expect_Saved() {
		final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
		final PublicationStatus statusApproved = PublicationStatus.APPROVED;
		final PublicationStatus statusDisapproved = PublicationStatus.DISAPPROVED;
		final PublicationStatus statusNew = PublicationStatus.NEW;

		// approved
		documentDTO.setPublicationStatus(statusApproved);
        documentService.save(documentDTO);

        DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO.getPublicationStatus(), statusApproved);

        // disapproved
        documentDTO.setPublicationStatus(statusDisapproved);
        documentService.save(documentDTO);

        savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO.getPublicationStatus(), statusDisapproved);

        // new
        documentDTO.setPublicationStatus(statusNew);
        documentService.save(documentDTO);

        savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO.getPublicationStatus(), statusNew);
    }

    @Test
    public void save_When_CreatedAndModifiedAndArchivedAndPublishedAndDepublishedAttributesSet_Expect_Saved() {
	    final UserDomainObject currentUser = Imcms.getUser();

	    final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final Supplier<AuditDTO> auditCreator = () -> {
            final AuditDTO auditDTO = new AuditDTO();
            auditDTO.setDateTime(new Date());
	        auditDTO.setId(currentUser.getId());
	        auditDTO.setBy(currentUser.getLogin());
            return auditDTO;
        };

        final AuditDTO createdAudit = auditCreator.get();
        final AuditDTO modifiedAudit = auditCreator.get();
        final AuditDTO archivedAudit = auditCreator.get();
        final AuditDTO publishedAudit = auditCreator.get();
        final AuditDTO depublishedAudit = auditCreator.get();

        documentDTO.setCreated(createdAudit);
        documentDTO.setModified(modifiedAudit);
        documentDTO.setArchived(archivedAudit);
        documentDTO.setPublished(publishedAudit);
        documentDTO.setPublicationEnd(depublishedAudit);

        documentService.save(documentDTO);
        DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(createdAudit, savedDocumentDTO.getCreated());
        assertEquals(modifiedAudit, savedDocumentDTO.getModified());
        assertEquals(archivedAudit, savedDocumentDTO.getArchived());
        assertEquals(publishedAudit, savedDocumentDTO.getPublished());
        assertEquals(depublishedAudit, savedDocumentDTO.getPublicationEnd());

        // only for nullable things
        final AuditDTO emptyArchivedAudit = new AuditDTO();
        final AuditDTO emptyPublishedAudit = new AuditDTO();
        final AuditDTO emptyDepublishedAudit = new AuditDTO();

        documentDTO.setArchived(emptyArchivedAudit);
        documentDTO.setPublished(emptyPublishedAudit);
        documentDTO.setPublicationEnd(emptyDepublishedAudit);

        documentService.save(documentDTO);
        savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(emptyArchivedAudit, savedDocumentDTO.getArchived());
        assertEquals(emptyPublishedAudit, savedDocumentDTO.getPublished());
        assertEquals(emptyDepublishedAudit, savedDocumentDTO.getPublicationEnd());
    }

    @Test
    public void save_When_CustomMissingLanguagePropertySet_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);

        documentService.save(documentDTO);
        DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getDisabledLanguageShowMode(), SHOW_IN_DEFAULT_LANGUAGE);

        documentDTO.setDisabledLanguageShowMode(DO_NOT_SHOW);

        documentService.save(documentDTO);
        savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getDisabledLanguageShowMode(), DO_NOT_SHOW);
    }

    @Test
    public void save_When_CustomKeywordsSet_Expect_Saved() {
        final Set<String> keywords = new HashSet<>();
        keywords.add("test keyword 1");
        keywords.add("test keyword 2");
        keywords.add("test keyword 3");
        keywords.add("test keyword 4");
        keywords.add("test keyword 5");
        keywords.add("test keyword 6");

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setKeywords(keywords);

        documentService.save(documentDTO);

        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(keywords, savedDocumentDTO.getKeywords());

        final int prevSize = keywords.size();
        keywords.remove("test keyword 1");
        assertEquals(keywords.size() + 1, prevSize);

        savedDocumentDTO.setKeywords(keywords);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertEquals(keywords, savedDocumentDTO1.getKeywords());
    }

    @Test
    public void save_When_SearchEnabledAndDisabled_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        documentDTO.setSearchDisabled(true);
        documentService.save(documentDTO);

        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertTrue(savedDocumentDTO.isSearchDisabled());

        savedDocumentDTO.setSearchDisabled(false);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertFalse(savedDocumentDTO1.isSearchDisabled());
    }

    @Test
    public void save_When_CategoriesIsSet_Expect_Saved() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        categoryDataInitializer.createData(20);

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        final Set<Category> categories = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 0)
                .collect(Collectors.toSet());

        documentDTO.setCategories(categories);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(categories, savedDocumentDTO.getCategories());

        final Set<Category> categories1 = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 1)
                .collect(Collectors.toSet());

        documentDTO.setCategories(categories1);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());

        assertEquals(categories1, savedDocumentDTO1.getCategories());

    }

    @Test
    public void save_When_CustomAccessRulesSet_Expect_Saved() {
        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();

        for (Permission permission : Permission.values()) {
            final Role role = roleService.save(new RoleDTO("test_role_" + permission));
            roleIdToPermission.put(role.getId(), permission);
        }

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setRoleIdToPermission(roleIdToPermission);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertTrue(savedDocumentDTO.getRoleIdToPermission().entrySet().containsAll(roleIdToPermission.entrySet()));

        final Map<Integer, Permission> roleIdToPermission1 = new HashMap<>();
        savedDocumentDTO.setRoleIdToPermission(roleIdToPermission1);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO1.getRoleIdToPermission(), roleIdToPermission1);
    }

    @Test
    public void save_When_AccessRuleWithSimpleUser_Expect_RuleNotSaved() {
        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();

        for (Permission permission : Permission.values()) {
            roleIdToPermission.put(Roles.USER.getId(), permission);
        }

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setRoleIdToPermission(roleIdToPermission);
        documentService.save(documentDTO);

        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertTrue(savedDocumentDTO.getRoleIdToPermission().isEmpty());
    }

    @Test
    public void save_When_RestrictedPermissionsSet_Expect_Saved() {

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final Set<RestrictedPermission> restrictedPermissions = new HashSet<>();

        final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
        restricted1.setPermission(Permission.RESTRICTED_1);
        restricted1.setEditDocInfo(true);
        restricted1.setEditImage(false);
        restricted1.setEditLoop(true);
        restricted1.setEditMenu(false);
        restricted1.setEditText(true);

        final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
        restricted2.setPermission(Permission.RESTRICTED_2);
        restricted2.setEditDocInfo(false);
        restricted2.setEditImage(true);
        restricted2.setEditLoop(false);
        restricted2.setEditMenu(true);
        restricted2.setEditText(false);

        restrictedPermissions.add(restricted1);
        restrictedPermissions.add(restricted2);

        documentDTO.setRestrictedPermissions(restrictedPermissions);

        documentService.save(documentDTO);

        final DocumentDTO documentDTO1 = documentService.get(documentDTO.getId());

        assertTrue(restrictedPermissions.containsAll(documentDTO1.getRestrictedPermissions()));
        assertEquals(documentDTO1, documentDTO);
    }

    @Test
    public void deleteById_Expect_DocumentNotExistExceptionAfterDeletion() {
        final Integer docId = createdDoc.getId();

        final DocumentDTO documentDTO = documentService.get(docId);

        assertNotNull(documentDTO);
        assertEquals(documentDTO.getId(), docId);

        documentService.deleteByDocId(docId);
        metaRepository.flush();

        assertThrows(DocumentNotExistException.class, () -> documentService.get(docId));
    }


    @Test
    public void deleteByIds_Expect_DocumentsNotExistExceptionAfterDeletion() {
        final List<DocumentDTO> createdDocuments = documentDataInitializer.createDocumentsData(2, true, true);

        assertEquals(2, createdDocuments.size());
        assertEquals(4, documentService.countDocuments()); // 4 because we had created 2 document above before test with default 1001

        final List<Integer> docIds = createdDocuments.stream()
                .map(DocumentDTO::getId)
                .collect(Collectors.toList());

        docIds.forEach(id -> assertNotNull(documentService.get(id)));

        documentService.deleteByIds(docIds);
        metaRepository.flush();

        docIds.forEach(docId -> assertThrows(DocumentNotExistException.class, () -> documentService.get(docId)));
    }

    @Test
    public void delete_When_UserAdminAndDocExistWithContent_Expect_DocumentNotExistExceptionAfterDeletion() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user); // means current user is admin now

        final Integer createdDocId = createdDoc.getId();
        final DocumentDTO documentDTO = documentService.get(createdDocId);
        assertNotNull(documentDTO);

        addContentForDocument(documentDTO);

        documentService.deleteByDocId(documentDTO.getId());
        metaRepository.flush();

        assertThrows(DocumentNotExistException.class, () -> documentService.get(createdDocId));
    }

    @Test
    public void delete_When_DocumentInWasteBasket_Expect_DocumentNotExistExceptionAfterDeletion_And_WasteBasketDoesNotContainDocument() {
        final EntityManager entityManager = Imcms.getServices().getManagedBean(EntityManager.class);

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user); // means current user is admin now

        final Integer createdDocId = createdDoc.getId();
        final DocumentDTO documentDTO = documentService.get(createdDocId);
        assertNotNull(documentDTO);

        documentWasteBasketService.putToWasteBasket(documentDTO.getId());
        entityManager.flush();
        entityManager.clear();

        documentService.deleteByDocId(documentDTO.getId());
        metaRepository.flush();

        assertThrows(DocumentNotExistException.class, () -> documentService.get(documentDTO.getId()));
        assertFalse(documentWasteBasketService.getAllIdsFromWasteBasket().contains(createdDocId));
    }

    private void addContentForDocument(DocumentDTO documentDTO) {
        final Integer createdDocId = documentDTO.getId();
        final int testIndex = 1;

        final LoopEntryRefDTO[] loopEntryRefDTOS = {
                new LoopEntryRefDTO(testIndex, testIndex),
                null,
        };

        final List<LoopEntryDTO> loopEntryDTOS = new ArrayList<>(Collections.singletonList(
                LoopEntryDTO.createEnabled(testIndex)
        ));
        final LoopDTO loopDTO = new LoopDTO(createdDocId, testIndex, loopEntryDTOS);
        loopService.saveLoop(loopDTO);

        for (LoopEntryRefDTO loopEntryRefDTO : loopEntryRefDTOS) {

            documentDTO.getCommonContents().forEach(commonContentDTO -> {
                final String langCode = commonContentDTO.getLanguage().getCode();
                final TextDTO textDTO = new TextDTO();
                textDTO.setText("test");
                textDTO.setType(TEXT);
                textDTO.setDocId(createdDocId);
                textDTO.setIndex(testIndex);
                textDTO.setLoopEntryRef(loopEntryRefDTO);
                textDTO.setLangCode(langCode);
                textService.save(textDTO);

                final ImageDTO imageDTO = new ImageDTO(testIndex, createdDocId, loopEntryRefDTO, langCode);
                imageDTO.setFormat(Format.JPEG);

                imageService.saveImage(imageDTO);
            });
        }

        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setDocId(createdDocId);
        menuDTO.setMenuIndex(testIndex);
        menuDTO.setTypeSort(TREE_SORT + "");
        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(createdDocId);
        menuItemDTO.setSortOrder("1");
        menuDTO.setMenuItems(new ArrayList<>(Collections.singletonList(menuItemDTO)));

        menuService.saveFrom(menuDTO);
    }

    @Test
    public void publishDocument_When_hasOnlyWorkingVersion() {
        // initial data
        final Version workingVersion = versionRepository.findWorking(createdDoc.getId());

        final Integer index = 1;
        final LoopDTO testLoop = new LoopDTO(createdDoc.getId(), index, Collections.emptyList());
        loopDataInitializer.createData(testLoop, workingVersion);

        menuDataInitializer.createData(false, index, workingVersion, String.valueOf(TREE_SORT), 2);

        final ImageJPA image = imageDataInitializer.createData(index, workingVersion);

        //get language from image to not use repo.
        createText(index, image.getLanguage(), workingVersion);

        //invoke test

        final boolean isPublished = documentService.publishDocument(createdDoc.getId(), Imcms.getUser().getId());

        //checking

        assertTrue(isPublished);

        final Version latestVersion = versionRepository.findLatest(createdDoc.getId());

        assertNotEquals(workingVersion, latestVersion);

        final Set<Loop> loopByVersion = loopService.getByVersion(latestVersion);
        assertNotNull(loopByVersion);
        assertEquals(1, loopByVersion.size());
        assertEquals(index, new ArrayList<>(loopByVersion).get(0).getIndex());

        final List<Menu> menuByVersion = menuRepository.findByVersion(latestVersion);
        assertNotNull(menuByVersion);
        assertEquals(1, menuByVersion.size());
        assertEquals(index, new ArrayList<>(menuByVersion).get(0).getNo());

        final List<ImageJPA> imageByVersion = imageRepository.findByVersion(latestVersion);
        assertNotNull(imageByVersion);
        assertEquals(1, imageByVersion.size());
        assertEquals(index, new ArrayList<>(imageByVersion).get(0).getIndex());

        final List<TextJPA> textByVersion = textRepository.findByVersion(latestVersion);
        assertNotNull(textByVersion);
        assertEquals(1, textByVersion.size());
        assertEquals(index, new ArrayList<>(textByVersion).get(0).getIndex());

        final Set<CommonContent> commonContentByVersion = commonContentService.getByVersion(latestVersion);
        assertNotNull(commonContentByVersion);
        assertEquals(2, commonContentByVersion.size());
        assertEquals(Integer.valueOf(1), new ArrayList<>(commonContentByVersion).get(0).getVersionNo());
        assertEquals(Integer.valueOf(1), new ArrayList<>(commonContentByVersion).get(1).getVersionNo());

    }

    @Test
    public void publishDocument_When_oneVersionAndWorkingVersionModifiedBeforeLatestVersionCreated() {
        // initial data
        final Version workingVersion = versionRepository.findWorking(createdDoc.getId());
        workingVersion.setCreatedDt(new Date(0L));
        versionRepository.save(workingVersion);
        versionService.create(createdDoc.getId(), Imcms.getUser().getId());
        final boolean isPublished = documentService.publishDocument(createdDoc.getId(), Imcms.getUser().getId());
        assertFalse(isPublished);
    }

    @Test
    public void publishDocument_When_oneVersionAndWorkingVersionModifiedAfterLatestVersionCreated() {
        // initial data
        final Version workingVersion = versionRepository.findWorking(createdDoc.getId());

        final Version latestVersion = versionService.create(createdDoc.getId(), Imcms.getUser().getId());
        latestVersion.setCreatedDt(new Date(0L));
        versionRepository.save(latestVersion);

        final Integer index = 1;
        final LoopDTO testLoop = new LoopDTO(createdDoc.getId(), index, Collections.emptyList());
        loopDataInitializer.createData(testLoop, workingVersion);

        menuDataInitializer.createData(false, index, workingVersion, String.valueOf(TREE_SORT), 2);

        final ImageJPA image = imageDataInitializer.createData(index, workingVersion);

        //get language from image to not use repo.
        createText(index, image.getLanguage(), workingVersion);

        //invoke test

        final boolean isPublished = documentService.publishDocument(createdDoc.getId(), Imcms.getUser().getId());

        //checking

        assertTrue(isPublished);

        final Version newVersion = versionRepository.findLatest(createdDoc.getId());

        assertNotEquals(workingVersion, latestVersion);
        assertNotEquals(latestVersion, newVersion);

        final Set<Loop> loopByVersion = loopService.getByVersion(newVersion);
        assertNotNull(loopByVersion);
        assertEquals(1, loopByVersion.size());
        assertEquals(index, new ArrayList<>(loopByVersion).get(0).getIndex());

        final List<Menu> menuByVersion = menuRepository.findByVersion(newVersion);
        assertNotNull(menuByVersion);
        assertEquals(1, menuByVersion.size());
        assertEquals(index, new ArrayList<>(menuByVersion).get(0).getNo());

        final List<ImageJPA> imageByVersion = imageRepository.findByVersion(newVersion);
        assertNotNull(imageByVersion);
        assertEquals(1, imageByVersion.size());
        assertEquals(index, new ArrayList<>(imageByVersion).get(0).getIndex());

        final List<TextJPA> textByVersion = textRepository.findByVersion(newVersion);
        assertNotNull(textByVersion);
        assertEquals(1, textByVersion.size());
        assertEquals(index, new ArrayList<>(textByVersion).get(0).getIndex());

        final Set<CommonContent> commonContentByVersion = commonContentService.getByVersion(newVersion);
        assertNotNull(commonContentByVersion);
        assertEquals(2, commonContentByVersion.size());
        assertEquals(Integer.valueOf(2), new ArrayList<>(commonContentByVersion).get(0).getVersionNo());
        assertEquals(Integer.valueOf(2), new ArrayList<>(commonContentByVersion).get(1).getVersionNo());

    }

    @Test
    public void publishNewDocVersion_When_StatusIsNew_Expect_StatusSetToApproved() {
        createdDoc = documentDataInitializer.createData(PublicationStatus.NEW, true, true);

        assertEquals(PublicationStatus.NEW, createdDoc.getPublicationStatus());

        final Integer docId = createdDoc.getId();
        final boolean isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());
        final DocumentDTO publishedDoc = documentService.get(docId);

        assertTrue(isPublished);
        assertEquals(PublicationStatus.APPROVED, publishedDoc.getPublicationStatus());
    }

    @Test
    public void publishNewDocVersion_When_PublishDateIsInFuture_Expect_DateNotChanged() {
        final Integer docId = createdDoc.getId();
        final AuditDTO auditDTO = new AuditDTO();
        auditDTO.setDateTime(new Date(new Date().getTime() + 150000000000L)); // date in future
        final Date dateInFuture = auditDTO.getFormattedDate();

        createdDoc.getPublished().setDateTime(dateInFuture);

        documentService.save(createdDoc);

        final boolean isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());
        final DocumentDTO publishedDoc = documentService.get(docId);

        assertTrue(isPublished);
        assertEquals(dateInFuture, publishedDoc.getPublished().getFormattedDate());
    }

    @Test
    public void publishNewDocVersion_When_PublishDateIsNotSet_Expect_CurrentDateSet() {
        assertNull(createdDoc.getPublished().getFormattedDate());

        final Integer docId = createdDoc.getId();
        final boolean isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());
        final DocumentDTO publishedDoc = documentService.get(docId);

        assertTrue(isPublished);
        assertNotNull(publishedDoc.getPublished().getFormattedDate());
    }

    @Test
    public void publishNewDocVersion_When_PublishDateIsInPast_Expect_DateNotChanged() {
        assertNull(createdDoc.getPublished().getFormattedDate());

        final AuditDTO auditDTO = new AuditDTO();
        auditDTO.setDateTime(new Date(new Date().getTime() - 150000000000L)); // date in past
        final Date dateInPast = auditDTO.getFormattedDate();

        createdDoc.getPublished().setDateTime(dateInPast);

        documentService.save(createdDoc);

        final Integer docId = createdDoc.getId();
        final boolean isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());
        final DocumentDTO publishedDoc = documentService.get(docId);

        assertTrue(isPublished);
        assertEquals(dateInPast, publishedDoc.getPublished().getFormattedDate());
    }

    @Test
    public void publishNewDocVersion_When_SomePublishedVersionAlreadyExist_ExpectCommonContentChangesSaved() {
        final Integer docId = createdDoc.getId();
        boolean isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());

        assertTrue(isPublished);

        DocumentDTO publishedDoc = documentService.get(docId);
        AtomicBoolean isEnabledSwitcher = new AtomicBoolean(false);

        for (int i = 1; i <= 2; i++) {
            final String head = "head" + i;
            final String menuText = "menu text" + i;
	        final String alias = "alias" + i;
            final boolean isEnabled = isEnabledSwitcher.getAndSet(!isEnabledSwitcher.get());

            publishedDoc.getCommonContents().forEach(commonContent -> {
                commonContent.setHeadline(head);
                commonContent.setEnabled(isEnabled);
                commonContent.setMenuText(menuText);
	            commonContent.setAlias(alias);
            });

            documentService.save(publishedDoc);
            final DocumentDTO savedDoc = documentService.get(docId);

            savedDoc.getCommonContents().forEach(commonContent -> {
                assertEquals(head, commonContent.getHeadline());
                assertEquals(isEnabled, commonContent.isEnabled());
                assertEquals(menuText, commonContent.getMenuText());
				assertEquals(alias, commonContent.getAlias());
            });

            isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());
            assertTrue(isPublished);

            publishedDoc = documentService.get(docId);

            publishedDoc.getCommonContents().forEach(commonContent -> {
                assertEquals(head, commonContent.getHeadline());
                assertEquals(isEnabled, commonContent.isEnabled());
                assertEquals(menuText, commonContent.getMenuText());
				assertEquals(alias, commonContent.getAlias());
            });
        }
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateNameExist_Expected_CorrectListDocuments() {
        final DocumentDTO document = documentDataInitializer.createData();

        TextDocumentTemplate saved = new TextDocumentTemplateDTO(
                templateDataInitializer.createData(document.getId(), "test", "test")
        );

        List<Document> listWithExpectedDocument = Collections.singletonList(document);

        assertEquals(listWithExpectedDocument, documentService.getDocumentsByTemplateName(saved.getTemplateName()));
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateNameUnknown_Expected_EmptyList() {
        final String fakeName = "unknown";
        assertEquals(Collections.emptyList(), documentService.getDocumentsByTemplateName(fakeName));
    }

    @Test
    public void countDocumentsByTemplateName_When_TemplateNameExist_Expected_CountDocs() {
        final DocumentDTO document = documentDataInitializer.createData();

        TextDocumentTemplate saved = new TextDocumentTemplateDTO(
                templateDataInitializer.createData(document.getId(), "test", "test")
        );

        List<Document> listWithExpectedDocument = Collections.singletonList(document);

        assertEquals(listWithExpectedDocument.size(), documentService.countDocumentsByTemplateName(saved.getTemplateName()));
    }

    @Test
    public void countDocumentsByTemplateName_When_TemplateNameUnknown_Expected_EmptyCountDocs() {
        final String fakeName = "unknown";
        assertEquals(0, documentService.countDocumentsByTemplateName(fakeName));
    }

    private void createText(int index, LanguageJPA language, Version version) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText("test");
        text.setType(TEXT);
        text.setVersion(version);

        textRepository.saveAndFlush(text);
    }

    @Test
    public void getUniqueAlias_When_Alias_NotExists_Existed_NotUpdatedAlias() {
        final String aliasName = "test-alias";

	    createdDoc.getCommonContents().stream().filter(commonContent -> commonContent.getLanguage().equals(Imcms.getLanguage()))
			    .forEach(commonContent -> commonContent.setAlias(aliasName + 1));

	    commonContentService.save(createdDoc.getId(), createdDoc.getCommonContents());

        assertEquals(aliasName, documentService.getUniqueAlias(aliasName));
    }

    @Test
    public void getUniqueAlias_When_Alias_Exists_Existed_AliasWithCounter() {
        final String aliasName = "test-alias";

	    createdDoc.getCommonContents().stream().filter(commonContent -> commonContent.getLanguage().equals(Imcms.getLanguage()))
			    .forEach(commonContent -> commonContent.setAlias(aliasName));

	    commonContentService.save(createdDoc.getId(), createdDoc.getCommonContents());

        assertEquals(aliasName + "-1", documentService.getUniqueAlias(aliasName));
    }

    private Meta createAndSaveMeta() {
        final Meta meta = new Meta();
        meta.setCreatedDatetime(new Date());
        meta.setModifiedDatetime(new Date());
        meta.setCreatorId(Imcms.getUser().getId());
        meta.setModifierId(Imcms.getUser().getId());
        meta.setDocumentType(Meta.DocumentType.URL);
        meta.setLinkableByOtherUsers(true);
        meta.setLinkedForUnauthorizedUsers(true);
        meta.setVisible(false);
        meta.setTarget("");
        meta.setPublicationStatus(PublicationStatus.NEW);
        return metaRepository.save(meta);
    }

    @Test
    public void getUniqueAlias_When_AliasWithCounter_Exists_Existed_AliasWithAnotherCounter() {
        final String aliasName = "test-alias";
	    final List<LanguageDTO> languages = languageDataInitializer.createData();

	    createdDoc.getCommonContents().stream().filter(commonContent -> commonContent.getLanguage().equals(languages.get(0)))
			    .forEach(commonContent -> commonContent.setAlias(aliasName));

	    createdDoc.getCommonContents().stream().filter(commonContent -> commonContent.getLanguage().equals(languages.get(1)))
			    .forEach(commonContent -> commonContent.setAlias(aliasName + "-1"));

	    commonContentService.save(createdDoc.getId(), createdDoc.getCommonContents());

        assertEquals(aliasName + "-2", documentService.getUniqueAlias(aliasName));
    }

}
