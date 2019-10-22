package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.DO_NOT_SHOW;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.ENG_CODE_ISO_639_2;
import static imcode.server.ImcmsConstants.SWE_CODE;
import static imcode.server.ImcmsConstants.SWE_CODE_ISO_639_2;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class MenuServiceTest extends WebAppSpringTestConfig {

    private static final int WORKING_VERSION_NO = 0;
    private static final int DOC_ID = 1001;

    @Autowired
    private MenuService menuService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionService versionService;

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(ENG_CODE_ISO_639_2);
        Imcms.setUser(user);
    }

    @AfterEach
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Test
    public void getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems() {
        getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(true);
    }

    @Test
    public void getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList() {
        getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(true);
    }

    @Test
    public void saveFrom_When_MenuWithItems_Expect_SameSizeAndResultsEquals() {
        saveFrom_Expect_SameSizeAndResultsEquals(true);
    }

    @Test
    public void saveFrom_When_MenuDoesntExist_Expect_SameSizeAndResultsEquals() {
        saveFrom_Expect_SameSizeAndResultsEquals(false);
    }

    @Test
    public void getPublicMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems() {
        getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(false);
    }


    @Test
    public void getPublicMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList() {
        getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(false);
    }


    @Test
    public void saveMenu_When_ExistBefore_Expect_NoDuplicatedDataAndCorrectSave() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO menuDTO = menuDataInitializer.createData(true);
        final List<MenuItemDTO> menuItemBefore = menuDTO.getMenuItems();

        MenuDTO savedMenu = menuService.saveFrom(menuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(menuItemBefore.size(), menuItemAfter.size());
        assertEquals(menuItemBefore, menuItemAfter);

        final List<MenuItemDTO> menuItems = menuDTO.getMenuItems();
        final MenuItemDTO removed = menuItems.remove(0);
        final int newSize = menuItems.size();
        assertNotNull(removed);
        savedMenu = menuService.saveFrom(menuDTO);

        menuItemAfter = savedMenu.getMenuItems();
        assertEquals(newSize, menuItemAfter.size());
    }

    @Test
    public void deleteByDocId() {
        assertTrue(menuRepository.findAll().isEmpty());

        final int docId = 1001;

        IntStream.range(0, 3).forEach((versionIndex) -> {
            versionDataInitializer.createData(versionIndex, docId);

            IntStream.range(1, 5).forEach((menuIndex) ->
                    menuDataInitializer.createData(true, menuIndex, versionIndex, docId)
            );
        });

        assertFalse(menuRepository.findAll().isEmpty());

        menuService.deleteByDocId(docId);

        assertTrue(menuRepository.findAll().isEmpty());
    }

    @Test
    public void createVersionedContent() {
        final boolean withMenuItems = true;
        final Menu workingVersionMenu = menuDataInitializer.createDataEntity(withMenuItems);

        final Version workingVersion = versionService.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(DOC_ID);

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        final List<Menu> menuByVersion = menuRepository.findByVersion(latestVersionDoc);

        assertEquals(1, menuByVersion.size());

        final Menu menu = menuByVersion.get(0);

        assertEquals(menu.getNo(), workingVersionMenu.getNo());
        assertEquals(menu.getMenuItems().size(), workingVersionMenu.getMenuItems().size());
    }

    @Test
    public void getMenuItems_MenuNoAndDocIdAndUserLanguageNestedOn_Expect_MenuItemsIsReturnedWithTitleOfUsersLanguage() {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);
        final String language = Imcms.getUser().getLanguage();

        final List<MenuItemDTO> menuItems = menuService
                .getMenuItems(menu.getDocId(), menu.getMenuIndex(), language, true, "");

        assertEquals(menu.getMenuItems().size(), menuItems.size());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableEnNestedOff_ShowModeSHOW_IN_DEFAULT_LANGUAGE_Expect_CorrectEntitiesSizeAndChildrenEmpty() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);

        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());
        List<MenuItemDTO> expectedMenuItems = menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, false, "");
        assertEquals(8, expectedMenuItems.size());
        expectedMenuItems.forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenuItems_When_NestedOff_UserSetEnLangAndMenuDisableEn_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSizeAndChildrenEmpty() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(1).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        List<MenuItemDTO> expectedMenuItems = menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, false, "");
        assertEquals(7, expectedMenuItems.size());
        expectedMenuItems.forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenuItems_When_NestedOn_UserSetEnLangAndMenuDisableEn_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(1).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertEquals(1, menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true, "").size());
    }

    @Test
    public void getMenuItems_When_NestedOn_UserSetEnLangAndMenuDisableEn_ShowModeSHOW_IN_DEFAULT_LANGUAGE_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);

        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertEquals(2, menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true, "").size());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableEn_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertEquals(1, menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true, "").size());
    }

    @Test
    public void getMenuItems_When_UserSetSvLangAndMenuDisableEN_NestedOff_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSizeAndChildrenEmpty() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(SWE_CODE_ISO_639_2);
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        List<MenuItemDTO> expectedMenuItems = menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, false, "");
        assertEquals(8, expectedMenuItems.size());
        expectedMenuItems.forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenuItems_When_UserSetSvLangAndMenuDisableEN_NestedOn_ShowModeSHOW_ON_DEFAULT_Expect_CorrectEntitiesSize() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(SWE_CODE_ISO_639_2);
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertEquals(2, menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true, "").size());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableSv_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, ENG_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertEquals(2, menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true).size());
    }

    @Test
    public void getPublicMenuItems_When_NestedMenuItemsDisable_Expect_CorrectEntitiesSizeAndEmptyChildren() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertFalse(menu.getMenuItems().get(0).getChildren().isEmpty());

        List<MenuItemDTO> publicMenuItems = menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, false);

        assertEquals(8, publicMenuItems.size());
        publicMenuItems.forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getPublicMenuItems_When_NestedMenuItemsEnable_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertEquals(2, menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true).size());
    }

    @Test
    public void getVisibleMenuItems_When_NestedMenuItemsEnable_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertEquals(2, menuService.getVisibleMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true).size());
    }

    @Test
    public void getVisibleMenuItems_When_NestedMenuItemsDisable_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertEquals(8, menuService.getVisibleMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, false).size());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableSv_NestedOn_ShowModeSHOW_ON_DEFAULT_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, ENG_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertEquals(2, menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true).size());
    }

    @Test
    public void getMenuItems_When_UserSetSvLangAndMenuDisableSv_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(SWE_CODE_ISO_639_2);
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, ENG_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        MenuItemDTO menuItemDTO = menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true, "").get(0);
        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertNotEquals(documentService.get(menuItemDTO.getDocumentId()).getCommonContents().get(0).getLanguage(), Imcms.getLanguage());
        assertEquals(1, menuService.getMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser, true, "").size());
    }


    @Test
    public void getMenu_Expect_CorrectEntities() {
        menuDataInitializer.createData(true, 1);
        menuDataInitializer.createData(true, 2);

        List<Menu> foundMenus = menuService.getAll();

        assertNotNull(foundMenus);
        assertEquals(2, foundMenus.size());
    }

    private void getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(boolean isAll) {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        final String code = languageDataInitializer.createData().get(0).getCode();
        final List<MenuItemDTO> menuItemDtosOfMenu = isAll
                ? menuService.getVisibleMenuItems(menu.getDocId(), menu.getMenuIndex(), code, true)
                : menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), code, true);

        assertEquals(menuDataInitializer.getMenuItemDtoList().size(), menuItemDtosOfMenu.size());
        assertEquals(menuDataInitializer.getMenuItemDtoList().get(0).getChildren().size(), menuItemDtosOfMenu.get(0).getChildren().size());
        assertEquals(menuDataInitializer.getMenuItemDtoList().get(0).getChildren().get(0).getChildren().size(),
                menuItemDtosOfMenu.get(0).getChildren().get(0).getChildren().size());
    }

    private DocumentDTO setUpMenuItem(DocumentDTO document, String enableLang, Meta.DisabledLanguageShowMode showMode) {
        for (CommonContent content : document.getCommonContents()) {
            Language language = content.getLanguage();
            language.setEnabled(content.getLanguage().getCode().equals(enableLang));
            languageRepository.save(new LanguageJPA(language));
        }
        document.setDisabledLanguageShowMode(showMode);
        return documentService.save(document);
    }

    private void getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(boolean isAll) {
        final String code = languageDataInitializer.createData().get(0).getCode();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);
        final List<MenuItemDTO> menuItems = isAll
                ? menuService.getVisibleMenuItems(DOC_ID, WORKING_VERSION_NO, code, false)
                : menuService.getPublicMenuItems(DOC_ID, WORKING_VERSION_NO, code, false);
        assertTrue(menuItems.isEmpty());
    }

    private void saveFrom_Expect_SameSizeAndResultsEquals(boolean menuExist) {
        final MenuDTO menuDTO = menuDataInitializer.createData(true);
        final List<MenuItemDTO> menuItemBefore = menuDTO.getMenuItems();

        if (!menuExist) {
            versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);
            menuService.deleteByDocId(menuDTO.getDocId());
        }

        final MenuDTO savedMenu = menuService.saveFrom(menuDTO);

        final List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(menuItemBefore.size(), menuItemAfter.size());
        assertEquals(menuItemBefore, menuItemAfter);
    }
}