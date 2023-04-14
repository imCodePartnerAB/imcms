package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.DataIsNotValidException;
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
import com.imcode.imcms.enums.TypeSort;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.imcode.imcms.enums.TypeSort.*;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.DO_NOT_SHOW;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static imcode.server.ImcmsConstants.*;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class MenuServiceTest extends WebAppSpringTestConfig {

    static final int WORKING_VERSION_NO = 0;
    private static final int DOC_ID = 1001;

    @Autowired
    private MenuService menuService;

    @Autowired
    private Function<Menu, MenuDTO> menuToMenuDTO;

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

        final Language currentLanguage = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(currentLanguage);
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
    public void saveFrom_When_ItemHasEmptySortOrder_Expect_CorrectException() {
        final MenuDTO menu = menuDataInitializer.createData(true, TREE_SORT.toString(), 5);
        versionDataInitializer.createData(WORKING_VERSION_NO, menu.getDocId());
        final List<MenuItemDTO> menuItems = menu.getMenuItems();

        final MenuItemDTO newMenuItem = menuDataInitializer.createMenuItemDTO("");
        menuItems.add(newMenuItem);

        assertTrue(menuItems.contains(newMenuItem));

        menu.setMenuItems(menuItems);

        assertThrows(DataIsNotValidException.class, () -> menuService.saveFrom(menu));
    }

    @Test
    public void saveFrom_When_ItemHasNullSortOrder_Expect_CorrectException() {
        final MenuDTO menu = menuDataInitializer.createData(true, TREE_SORT.toString(), 5);
        versionDataInitializer.createData(WORKING_VERSION_NO, menu.getDocId());
        final List<MenuItemDTO> menuItems = menu.getMenuItems();

        final MenuItemDTO newMenuItem = menuDataInitializer.createMenuItemDTO(null);
        menuItems.add(newMenuItem);

        assertTrue(menuItems.contains(newMenuItem));

        menu.setMenuItems(menuItems);

        assertThrows(DataIsNotValidException.class, () -> menuService.saveFrom(menu));
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
    public void getMenus_Expected_EqualsResult() {
        final MenuDTO newMenu = getCreatedNewMenu();
        List<MenuDTO> resultMenus = menuService.getByDocId(newMenu.getDocId())
                .stream()
                .map(menuToMenuDTO)
                .collect(Collectors.toList());

        assertFalse(resultMenus.isEmpty());
        assertEquals(1, resultMenus.size());
    }

    @Test
    public void getMenus_WhenMenuNotExist_Expected_EmptyResultResult() {
        final MenuDTO newMenu = getCreatedNewMenu();
        final Integer menuId = newMenu.getDocId();

        final Integer newDocId = documentDataInitializer.createData().getId();

        assertNotEquals(menuId, newDocId);
        List<MenuDTO> resultMenus = menuService.getByDocId(newDocId)
                .stream()
                .map(menuToMenuDTO)
                .collect(Collectors.toList());

        assertTrue(resultMenus.isEmpty());
    }


    @Test
    public void saveMenu_When_ExistBefore_Expect_NoDuplicatedDataAndCorrectSave() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO menuDTO = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 4);
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
    public void saveMenu_When_SomeChildrenHasSortNumbersIsEmpties_Expect_NoDuplicatedDataAndCorrectSave() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO expectedMenuDTO = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 3);
        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(TREE_SORT), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));

        testMenuDTO.setMenuItems(newMenuItems);

        final List<MenuItemDTO> expectedMenuItems = expectedMenuDTO.getMenuItems();

        MenuDTO savedMenu = menuService.saveFrom(testMenuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(expectedMenuItems.size(), menuItemAfter.size());

        final List<MenuItemDTO> menuItems = testMenuDTO.getMenuItems();
        final MenuItemDTO removed = menuItems.remove(0);
        final int newSize = menuItems.size();
        assertNotNull(removed);
        savedMenu = menuService.saveFrom(testMenuDTO);

        menuItemAfter = savedMenu.getMenuItems();
        assertEquals(newSize, menuItemAfter.size());
    }

    @Test
    public void saveMenu_When_TypeSortNotTREE_SORTAndSort_Order_NotNumber_Expect_CorrectException() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(MANUAL), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2.1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));

        testMenuDTO.setMenuItems(newMenuItems);

        assertThrows(DataIsNotValidException.class, () -> menuService.saveFrom(testMenuDTO));
    }


    @Test
    public void saveMenu_When_ManualTypeSort_Expect_NoDuplicatedDataAndCorrectSave() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO expectedMenuDTO = menuDataInitializer.createData(true, String.valueOf(MANUAL), 5);
        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(MANUAL), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("4"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("5"));

        testMenuDTO.setMenuItems(newMenuItems);

        final List<MenuItemDTO> expectedMenuItems = expectedMenuDTO.getMenuItems();

        MenuDTO savedMenu = menuService.saveFrom(testMenuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(expectedMenuItems.size(), menuItemAfter.size());
        assertEquals(expectedMenuItems.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList()),

                menuItemAfter.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList())
        );

    }

    @Test
    public void saveMenu_When_TreeSortAndHaveSameOrder_Expect_NoDuplicatedDataAndCorrectSave() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO menuDTO = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 3);
        final List<MenuItemDTO> menuItems = menuDTO.getMenuItems();
        menuItems.get(1).setSortOrder("1");
        final MenuDTO expectedMenuDTO = menuService.saveFrom(menuDTO);
        final List<MenuItemDTO> expectedMenuItems = expectedMenuDTO.getMenuItems();

        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(TREE_SORT), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));

        testMenuDTO.setMenuItems(newMenuItems);

        MenuDTO savedMenu = menuService.saveFrom(testMenuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(expectedMenuItems.size(), menuItemAfter.size());
        assertEquals(expectedMenuItems.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList()),

                menuItemAfter.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void saveMenu_When_TreeSortAndSomeChildrenHasEmptySortNumber_Expect_NoDuplicatedDataAndCorrectSave() {

        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO expectedMenuDTO = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 3);
        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(TREE_SORT), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));

        testMenuDTO.setMenuItems(newMenuItems);

        final List<MenuItemDTO> expectedMenuItems = expectedMenuDTO.getMenuItems();

        MenuDTO savedMenu = menuService.saveFrom(testMenuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(expectedMenuItems.size(), menuItemAfter.size());
        assertEquals(expectedMenuItems.get(0).getSortOrder(), menuItemAfter.get(0).getSortOrder());
        assertEquals(expectedMenuItems.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList()),

                menuItemAfter.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList())
        );
    }


    @Test
    public void saveMenu_When_ManualAndSameSortOrder_Expect_NoDuplicatedDataAndCorrectSave() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO menuDTO = menuDataInitializer.createData(true, String.valueOf(MANUAL), 5);
        final List<MenuItemDTO> menuItems = menuDTO.getMenuItems();
        menuItems.get(4).setSortOrder("1");
        final MenuDTO expectedMenuDTO = menuService.saveFrom(menuDTO);

        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(MANUAL), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("4"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));

        testMenuDTO.setMenuItems(newMenuItems);

        final List<MenuItemDTO> expectedMenuItems = expectedMenuDTO.getMenuItems();

        MenuDTO savedMenu = menuService.saveFrom(testMenuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(expectedMenuItems.size(), menuItemAfter.size());
        assertEquals(expectedMenuItems.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList()),

                menuItemAfter.stream()
                        .map(MenuItemDTO::getSortOrder)
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void saveMenu_When_FlatMenuAndItemHasSubLevel_Expect_CorretException() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO expectedMenuDTO = menuDataInitializer.createData(true, String.valueOf(MANUAL), 5);
        final MenuDTO testMenuDTO = menuDataInitializer.createData(false, String.valueOf(MANUAL), 0);
        final List<MenuItemDTO> newMenuItems = new ArrayList<>();

        newMenuItems.add(menuDataInitializer.createMenuItemDTO("3"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("2"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1.1"));
        newMenuItems.add(menuDataInitializer.createMenuItemDTO("1.1.1"));

        testMenuDTO.setMenuItems(newMenuItems);

        assertThrows(DataIsNotValidException.class, () -> menuService.saveFrom(testMenuDTO));
    }

    @Test
    public void saveMenu_When_SortNumbersCorrect_Expect_NoDuplicatedDataAndCorrectSaveAndCorrectSort() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

        final MenuDTO expectedMenuDTO = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 3);

        final List<MenuItemDTO> menuItemBefore = expectedMenuDTO.getMenuItems();

        MenuDTO savedMenu = menuService.saveFrom(expectedMenuDTO);

        List<MenuItemDTO> menuItemAfter = savedMenu.getMenuItems();
        assertEquals(menuItemBefore.size(), menuItemAfter.size());

        final List<MenuItemDTO> menuItems = expectedMenuDTO.getMenuItems();
        final MenuItemDTO removed = menuItems.remove(0);
        final int newSize = menuItems.size();
        assertNotNull(removed);
        savedMenu = menuService.saveFrom(expectedMenuDTO);

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
                    menuDataInitializer.createData(true, menuIndex, versionIndex, docId, String.valueOf(TREE_SORT), 3)
            );
        });

        assertFalse(menuRepository.findAll().isEmpty());

        menuService.deleteByDocId(docId);

        assertTrue(menuRepository.findAll().isEmpty());
    }

    @Test
    public void createVersionedContent() {
        final boolean withMenuItems = true;
        final Menu workingVersionMenu = menuDataInitializer.createDataEntity(withMenuItems, 3);

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
    public void getMenuDTO_MenuNoAndDocIdAndUserLanguage_Expect_CorrectSizeAndData() {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final String language = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService
                .getMenuDTO(menu.getDocId(), menu.getMenuIndex(), language, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(expectedMenuItems.size(), resultMenuItems.size());
        assertEquals(expectedMenuItems, resultMenuItems);
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());
    }

    @Test
    public void getMenuDTO_When_UserSetEnLangAndMenuDisableEn_ShowModeSHOW_IN_DEFAULT_LANGUAGE_Expect_CorrectEntitiesSizeAndChildrenEmpty() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TypeSort.MANUAL), 3);

        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(expectedMenuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(expectedMenuItems.size(), resultMenuItems.size());
        assertEquals(expectedMenuItems, resultMenuItems);
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());

        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenuDTO_When_TypeSortMANUAL_UserSetEnLangAndMenuDisableEn_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSizeAndChildrenEmpty() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TypeSort.MANUAL), 3);
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

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(menuItems.size() - 1, resultMenuItems.size()); // -1 because 1 doc is do not show mode
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());

        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenu_When_TypeSortTREE_SORT_UserSetEnLangAndMenuDisableEn_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 2);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(expectedMenuItems.get(1).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(expectedMenuItems.size() - 1, resultMenuItems.size());

        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());
    }

    @Test
    public void getMenu_When_TypeSortTREE_SORT_UserSetEnLangAndMenuDisableEn_ShowModeSHOW_IN_DEFAULT_LANGUAGE_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);

        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(expectedMenuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(expectedMenuItems.size(), resultMenuItems.size());
        assertEquals(expectedMenuItems, resultMenuItems);
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());
        assertEquals(expectedMenuItems.size(), resultMenuItems.size());
    }

    @Test
    public void getMenu_When_UserSetEnLangAndMenuDisableEn_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(expectedMenuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(expectedMenuItems.size() - 1, resultMenuItems.size());
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());
    }

    @Test
    public void getMenu_When_UserSetSvLangAndMenuDisableEN_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSizeAndChildrenEmpty() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(SWE_CODE_ISO_639_2);
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TypeSort.MANUAL), 3);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(expectedMenuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, DO_NOT_SHOW);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());
        assertFalse(resultMenuItems.isEmpty());
        assertEquals(expectedMenuItems.size(), resultMenuItems.size());
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenu_When_UserSetSvLangAndMenuDisableEN_ShowModeSHOW_ON_DEFAULT_Expect_CorrectEntitiesSize() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(SWE_CODE_ISO_639_2);
        Imcms.setUser(user);
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        final String langUser = Imcms.getUser().getLanguage();
        final List<MenuItemDTO> expectedMenuItems = menu.getMenuItems();
        final DocumentDTO menuItemDoc = documentService.get(expectedMenuItems.get(0).getDocumentId());
        final DocumentDTO changedMenuItemDoc = setUpMenuItem(menuItemDoc, SWE_CODE, SHOW_IN_DEFAULT_LANGUAGE);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);

        documentService.publishDocument(changedMenuItemDoc.getId(), Imcms.getUser().getId());
        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());
        menuService.saveFrom(menu);

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(SHOW_IN_DEFAULT_LANGUAGE, changedMenuItemDoc.getDisabledLanguageShowMode());

        assertFalse(resultMenuItems.isEmpty());

        assertEquals(expectedMenuItems.size(), resultMenuItems.size());
        assertEquals(expectedMenuItems, resultMenuItems);
        assertEquals(menu.getTypeSort(), resultMenu.getTypeSort());
    }

    @Test
    public void getPublicMenuItems_When_TypeSortMANUAL_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TypeSort.MANUAL), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        List<MenuItemDTO> publicMenuItems = menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser);

        assertEquals(menu.getMenuItems().size(), publicMenuItems.size());
    }

    @Test
    public void getPublicMenuItems_When_TypeSortTREE_SORT_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertEquals(menu.getMenuItems().size(), menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser).size());
    }

    @Test
    public void getVisibleMenuItems_When_TypeSortTREE_SORT_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertEquals(menu.getMenuItems().size(), menuService.getVisibleMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser).size());
    }

    @Test
    public void getVisibleMenuItems_When_TypeSortMANUAL_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TypeSort.MANUAL), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        final String langUser = Imcms.getUser().getLanguage();
        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        assertEquals(menu.getMenuItems().size(), menuService.getVisibleMenuItems(
                menu.getDocId(), menu.getMenuIndex(), langUser).size());
    }

    @Test
    public void getVisibleMenuItems_When_PassedVersion_Expected_MenuItemsOfSpecificVersion(){
        final int index = 1;

        final int version1 = 1;
        final int version2 = 2;

        menuDataInitializer.createData(true, index, WORKING_VERSION_NO, DOC_ID, String.valueOf(ALPHABETICAL_ASC), 3);
        final MenuDTO menuVersion1 = menuDataInitializer.createData(true, index, version1, DOC_ID, String.valueOf(TREE_SORT), 5);
        menuDataInitializer.createData(true, index, version2, DOC_ID, String.valueOf(PUBLISHED_DATE_ASC), 7);

        assertEquals(menuVersion1.getMenuItems(), menuService.getVisibleMenuItems(DOC_ID, index, version1, Imcms.getUser().getLanguage()));
    }

    @Test
    public void getVisibleMenuItems_When_NoMenuItemsOfSpecificVersion_Expected_EmptyList(){
        final int index = 1;

        final int version1 = 1;
        final int version2 = 2;

        menuDataInitializer.createData(true, index, WORKING_VERSION_NO, DOC_ID, String.valueOf(ALPHABETICAL_ASC), 3);
        menuDataInitializer.createData(true, index, version2, DOC_ID, String.valueOf(PUBLISHED_DATE_ASC), 7);

        assertTrue(menuService.getVisibleMenuItems(DOC_ID, index, version1, Imcms.getUser().getLanguage()).isEmpty());
    }

    @Test
    public void getMenuItems_When_UserSetEnLangAndMenuDisableSv_TypeSortTREE_SORT_ShowModeSHOW_ON_DEFAULT_Expect_CorrectEntitiesSize() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
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
        assertEquals(menuItems.size(), menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), langUser).size());
    }

    @Test
    public void getMenuItems_When_UserSetSvLangAndMenuDisableSv_TypeSortTREE_SORT_ShowModeDO_NOT_SHOW_Expect_CorrectEntitiesSize() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2(SWE_CODE_ISO_639_2);
        Imcms.setUser(user);

        final Language currentLanguage = languageDataInitializer.createData().get(1);
        Imcms.setLanguage(currentLanguage);

        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
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

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), langUser, menu.getTypeSort());

        assertNotNull(resultMenu);

        MenuItemDTO menuItemDTO = resultMenu.getMenuItems().get(0);

        assertEquals(DO_NOT_SHOW, changedMenuItemDoc.getDisabledLanguageShowMode());

        assertNotEquals(documentService.get(menuItemDTO.getDocumentId()).getCommonContents().get(0).getLanguage(), Imcms.getLanguage());

        assertEquals(2, resultMenu.getMenuItems().size());
    }


    @Test
    public void getMenu_Expect_CorrectEntities() {
        menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        menuDataInitializer.createData(true, 2, String.valueOf(TREE_SORT), 3);

        List<Menu> foundMenus = menuService.getAll();

        assertNotNull(foundMenus);
        assertEquals(2, foundMenus.size());
        assertEquals(TREE_SORT.toString(), foundMenus.get(0).getTypeSort());
        assertEquals(TREE_SORT.toString(), foundMenus.get(1).getTypeSort());
    }

    @Test
    public void getMenu_When_TypeSortTREE_SORT_Expected_CorrectSizeEntities() {
        final MenuDTO menuDTO = setUpMenu(String.valueOf(TREE_SORT), 3);
        final String langUser = Imcms.getUser().getLanguage();

        MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );

        assertNotNull(resultMenu);
        assertEquals(menuDTO.getMenuItems().size(), resultMenu.getMenuItems().size());
    }

    @Test
    public void getMenu_When_TypeNull_Expected_TypeSortCorrectAndCorrectSize() {
        final MenuDTO menuDTO = setUpMenu(null, 3);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );

        assertNotNull(resultMenu);
        assertEquals(TREE_SORT.toString(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
    }

    @Test
    public void getMenu_When_TypeAlphabeticalASCAndTypeSortTREE_SORT_Expected_CorrectSizeEntitiesAndEmptyChild() {
        final MenuDTO menuDTO = setUpMenu(String.valueOf(TypeSort.ALPHABETICAL_ASC), 5);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );
        assertNotNull(resultMenu);
        assertEquals(menuDTO.getTypeSort(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenu_When_TypeAlphabeticalDESCAndTypeSortTREE_SORT_Expected_CorrectSizeEntitiesAndEmptyChild() {
        final MenuDTO menuDTO = setUpMenu(String.valueOf(TypeSort.ALPHABETICAL_DESC), 4);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );
        assertNotNull(resultMenu);
        assertEquals(menuDTO.getTypeSort(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenu_When_TypeManual_Expected_CorrectSizeEntitiesAndEmptyChild() {
        final MenuDTO menuDTO = setUpMenu(String.valueOf(TypeSort.MANUAL), 4);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );
        assertNotNull(resultMenu);
        assertEquals(menuDTO.getTypeSort(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));

    }

    @Test
    public void getMenu_When_TypeNull_Expected_CorrectSizeEntitiesAndCorrectTypeSort() {
        final MenuDTO menuDTO = setUpMenu(null, 5);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );
        assertNotNull(resultMenu);
        assertEquals(TREE_SORT.toString(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));
    }

    @Test
    public void getMenu_When_TypeAlphabeticalASC_Expected_CorrectSizeEntitiesAndEmptyChild() {
        final MenuDTO menuDTO = setUpMenu(String.valueOf(TypeSort.ALPHABETICAL_ASC), 4);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );
        assertNotNull(resultMenu);
        assertEquals(menuDTO.getTypeSort(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));

    }

    @Test
    public void getMenu_When_TypeAlphabeticalDESC_Expected_CorrectSizeEntitiesAndEmptyChild() {
        final MenuDTO menuDTO = setUpMenu(String.valueOf(TypeSort.ALPHABETICAL_DESC), 4);
        final String langUser = Imcms.getUser().getLanguage();

        final MenuDTO resultMenu = menuService.getMenuDTO(
                menuDTO.getDocId(), menuDTO.getMenuIndex(), langUser, menuDTO.getTypeSort()
        );
        assertNotNull(resultMenu);
        assertEquals(menuDTO.getTypeSort(), resultMenu.getTypeSort());

        final List<MenuItemDTO> resultMenuItems = resultMenu.getMenuItems();

        assertEquals(menuDTO.getMenuItems().size(), resultMenuItems.size());
        resultMenu.getMenuItems().forEach(item -> assertTrue(item.getChildren().isEmpty()));

    }

    @Test
    public void setAsWorkingVersion_Expected_CopyMenuFromSpecificVersionToWorkingVersion(){
        final int index = 1;

        final int version1 = 1;
        final int version2 = 2;

        menuDataInitializer.createData(true, index, WORKING_VERSION_NO, DOC_ID, String.valueOf(ALPHABETICAL_ASC), 3);
        menuDataInitializer.createData(true, index, version1, DOC_ID, String.valueOf(TREE_SORT), 5);
        menuDataInitializer.createData(true, index, version2, DOC_ID, String.valueOf(PUBLISHED_DATE_ASC), 7);

        final List<Menu> menuWorkingVersion = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO));
        final List<Menu> menuVersion1 = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, version1));
        final List<Menu> menuVersion2 = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, version2));

        menuService.setAsWorkingVersion(versionService.findByDocIdAndNo(DOC_ID, version1));

        final List<Menu> menuWorkingVersionAfterReset = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO));
        final List<Menu> menuVersion1AfterReset = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, version1));
        final List<Menu> menuVersion2AfterReset = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, version2));

        assertFalse(equalsIgnoreIdAndVersion(menuWorkingVersion, menuWorkingVersionAfterReset));
        assertTrue(equalsIgnoreIdAndVersion(menuVersion1, menuWorkingVersionAfterReset));
        assertTrue(equalsIgnoreIdAndVersion(menuVersion1, menuVersion1AfterReset));
        assertTrue(equalsIgnoreIdAndVersion(menuVersion2, menuVersion2AfterReset));
    }

    @Test
    public void setAsWorkingVersion_When_NoMenuWithSpecificVersion_Expected_WorkingVersionHasNoMenu(){
        final int index = 1;

        final int version1 = 1;
        final int version2 = 2;

        menuDataInitializer.createData(true, index, WORKING_VERSION_NO, DOC_ID, String.valueOf(ALPHABETICAL_ASC), 3);
        versionDataInitializer.createData(version1, DOC_ID);
        menuDataInitializer.createData(true, index, version2, DOC_ID, String.valueOf(PUBLISHED_DATE_ASC), 7);

        final List<Menu> menuWorkingVersion = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO));
        assertFalse(menuWorkingVersion.isEmpty());

        menuService.setAsWorkingVersion(versionService.findByDocIdAndNo(DOC_ID, version1));

        final List<Menu> menuWorkingVersionAfterReset = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO));
        assertTrue(menuWorkingVersionAfterReset.isEmpty());
    }

    @Test
    public void setAsWorkingVersion_When_SpecificVersionHasMenuWithoutItems_Expected_WorkingVersionHasMenuWithoutItems(){
        final int index = 1;

        final int version1 = 1;
        final int version2 = 2;

        menuDataInitializer.createData(true, index, WORKING_VERSION_NO, DOC_ID, String.valueOf(ALPHABETICAL_ASC), 3);
        menuDataInitializer.createData(true, index, version1, DOC_ID, String.valueOf(TREE_SORT), 0);
        menuDataInitializer.createData(true, index, version2, DOC_ID, String.valueOf(PUBLISHED_DATE_ASC), 7);

        final List<Menu> menuVersion1 = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, version1));

        menuService.setAsWorkingVersion(versionService.findByDocIdAndNo(DOC_ID, version1));

        final List<Menu> menuVersion1AfterReset = menuRepository.findByVersion(versionService.findByDocIdAndNo(DOC_ID, WORKING_VERSION_NO));

        assertTrue(equalsIgnoreIdAndVersion(menuVersion1, menuVersion1AfterReset));
    }

    private boolean equalsIgnoreIdAndVersion(List<Menu> a, List<Menu> b){
        Function<Menu, MenuDTO> mapMenuToMenuDTO = menu -> {
            MenuDTO menuDTO = menuToMenuDTO.apply(menu);
            menuDTO.setMenuItems(menuService.getSortedMenuItems(menuDTO, null));
            return menuDTO;
        };

        List<MenuDTO> aDTO = a.stream().map(mapMenuToMenuDTO).collect(Collectors.toList());
        List<MenuDTO> bDTO = b.stream().map(mapMenuToMenuDTO).collect(Collectors.toList());
        return aDTO.equals(bDTO);
    }

    private MenuDTO getCreatedNewMenu() {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        return menuService.saveFrom(menu);
    }


    private void getMenuItemsOf_When_MenuNoAndDocId_Expect_ResultEqualsExpectedMenuItems(boolean isAll) {
        final MenuDTO menu = menuDataInitializer.createData(true, 1, String.valueOf(TREE_SORT), 3);
        final DocumentDTO documentDTO = documentService.get(menu.getDocId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(documentDTO);

        final Version workingVersion = versionService.findByDocIdAndNo(menu.getDocId(), WORKING_VERSION_NO);
        final Version latestVersionDoc = versionService.getLatestVersion(menu.getDocId());

        menuService.createVersionedContent(workingVersion, latestVersionDoc);
        menuService.saveFrom(menu);

        final String code = languageDataInitializer.createData().get(0).getCode();
        final List<MenuItemDTO> menuItemDtosOfMenu = isAll
                ? menuService.getVisibleMenuItems(menu.getDocId(), menu.getMenuIndex(), code)
                : menuService.getPublicMenuItems(menu.getDocId(), menu.getMenuIndex(), code);

        assertEquals(menuDataInitializer.getMenuItemDtoList().size(), menuItemDtosOfMenu.size());

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
                ? menuService.getVisibleMenuItems(DOC_ID, WORKING_VERSION_NO, code)
                : menuService.getPublicMenuItems(DOC_ID, WORKING_VERSION_NO, code);
        assertTrue(menuItems.isEmpty());
    }

    private void saveFrom_Expect_SameSizeAndResultsEquals(boolean menuExist) {
        final MenuDTO menuDTO = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), 3);
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

    private MenuDTO setUpMenu(String typeSort, int count) {
        final MenuDTO menu = menuDataInitializer.createData(true, typeSort, count);

        versionDataInitializer.createData(WORKING_VERSION_NO, menu.getDocId());

        documentService.publishDocument(menu.getDocId(), Imcms.getUser().getId());

        return menuService.saveFrom(menu);
    }
}