package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional
public class MenuServiceTest extends WebAppSpringTestConfig {

    private static final int WORKING_VERSION_NO = 0;
    private static final int DOC_ID = 1001;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private CommonContentService commonContentService;

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);
    }

    @AfterEach
    public void cleanUpData() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
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

        final Version workingVersion = versionRepository.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO);

        final int latestVersionNo = WORKING_VERSION_NO + 1;
        final Version latestVersion = versionDataInitializer.createData(latestVersionNo, DOC_ID);

        menuService.createVersionedContent(workingVersion, latestVersion);

        final List<Menu> menuByVersion = menuRepository.findByVersion(latestVersion);

        assertEquals(1, menuByVersion.size());

        final Menu menu = menuByVersion.get(0);

        assertEquals(menu.getNo(), workingVersionMenu.getNo());
        assertEquals(menu.getMenuItems().size(), workingVersionMenu.getMenuItems().size());
    }

    @Test
    public void getMenuItems_MenuNoAndDocIdAndUserLanguage_Expect_MenuItemsIsReturnedWithTitleOfUsersLanguage() {
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);
        final String language = Imcms.getUser().getLanguage();

        final List<MenuItemDTO> menuItems = menuService
                .getMenuItems(menu.getMenuIndex(), menu.getDocId(), language);

        assertEquals(menu.getMenuItems().size(), menuItems.size());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableEn_ShowModeSHOW_IN_DEFAULT_LANGUAGE_Expect_CorrectEntities() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final String enableLanguage = languageDataInitializer.createData().get(1).getCode(); //0 -en

        setUpMenuItem(menu, SHOW_IN_DEFAULT_LANGUAGE, enableLanguage);

        assertFalse(menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).isEmpty());
        assertEquals(2, menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).size());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableEn_ShowModeDONOTSHOW_Expect_CorrectEntities() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final String enableLanguage = languageDataInitializer.createData().get(1).getCode(); //0 -en

        setUpMenuItem(menu, DO_NOT_SHOW, enableLanguage);

        assertFalse(menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).isEmpty());
        assertEquals(2, menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).size());
    }

    @Test
    public void getMenuItems_When_UserSetSvLangAndMenuDisableEN_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("swe");
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final String enableLanguage = languageDataInitializer.createData().get(1).getCode(); //0 -en

        setUpMenuItem(menu, DO_NOT_SHOW, enableLanguage);

        assertFalse(menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).isEmpty());
        assertEquals(2, menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).size());
    }

    @Test
    public void getMenuItems_When_UserSetSvLangAndMenuDisableEN_ShowModeSHOW_ON_DEFAULT_Expect_CorrectEntitiesSize() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("swe");
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1);
        final String langUser = Imcms.getUser().getLanguage();
        final String enableLanguage = languageDataInitializer.createData().get(1).getCode(); //0 -en

        setUpMenuItem(menu, SHOW_IN_DEFAULT_LANGUAGE, enableLanguage);

        assertFalse(menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).isEmpty());
        assertEquals(2, menuService.getMenuItems(menu.getMenuIndex(), menu.getDocId(), langUser).size());
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
        final MenuDTO menu = menuDataInitializer.createData(true);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final String code = languageDataInitializer.createData().get(0).getCode();

        final List<MenuItemDTO> menuItemDtosOfMenu = isAll
                ? menuService.getVisibleMenuItems(menu.getMenuIndex(), menu.getDocId(), code)
                : menuService.getPublicMenuItems(menu.getMenuIndex(), menu.getDocId(), code);

        assertEquals(menuDataInitializer.getMenuItemDtoList().size(), menuItemDtosOfMenu.size());
        assertEquals(menuDataInitializer.getMenuItemDtoList().get(0).getChildren().size(), menuItemDtosOfMenu.get(0).getChildren().size());
        assertEquals(menuDataInitializer.getMenuItemDtoList().get(0).getChildren().get(0).getChildren().size(),
                menuItemDtosOfMenu.get(0).getChildren().get(0).getChildren().size());

    }

    private void setUpMenuItem(MenuDTO menu, Meta.DisabledLanguageShowMode showMode, String enableLang) {
        final List<MenuItemDTO> menuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(menuItems.get(0).getDocumentId());
        menuItemDoc.setDisabledLanguageShowMode(showMode);
        for (CommonContent content : menuItemDoc.getCommonContents()) {
            content.setEnabled(content.getLanguage().getCode().equals(enableLang));
        }

        commonContentService.save(menuItemDoc.getId(), menuItemDoc.getCommonContents());
    }


    private void getMenuItemsOf_When_MenuDoesntExist_Expect_EmptyList(boolean isAll) {
        final String code = languageDataInitializer.createData().get(0).getCode();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);
        final List<MenuItemDTO> menuItems = isAll
                ? menuService.getVisibleMenuItems(WORKING_VERSION_NO, DOC_ID, code)
                : menuService.getPublicMenuItems(WORKING_VERSION_NO, DOC_ID, code);
        assertTrue(menuItems.isEmpty());
    }

    private void saveFrom_Expect_SameSizeAndResultsEquals(boolean menuExist) {
        final MenuDTO menuDTO = menuDataInitializer.createData(true);
        final List<MenuItemDTO> menuItemBefore = menuDTO.getMenuItems();

        if (!menuExist) {
            menuDataInitializer.cleanRepositories();
            versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);
        }

        final MenuDTO savedMenu = menuService.saveFrom(menuDTO);

        final List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(menuItemBefore.size(), menuItemAfter.size());
        assertEquals(menuItemBefore, menuItemAfter);
    }

}