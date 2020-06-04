package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MenuControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private LanguageRepository languageRepository;

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user);

        final Language currentLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage();
        Imcms.setLanguage(currentLanguage);
    }

    @AfterEach
    public void cleanRepos() {
        menuDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
        Imcms.removeUser();
    }

    @Override
    protected String controllerPath() {
        return "/menus";
    }

//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParam_Expect_CorrectEntitiesSizeAndEmptyChildren() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, null);
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()));
//
//        List<MenuItemDTO> menuItems = menuService.getMenuItems(
//                menu.getDocId(), menu.getMenuIndex(), Imcms.getUser().getLanguage(), menu.isNested(), menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menuItems));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNestedOn_Expect_MenuItemsDtosJson() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, true, null);
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParamSortTree_Expect_CorrectException() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.TREE_SORT));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", String.valueOf(TypeSort.TREE_SORT));
//
//        performRequestBuilderExpectException(SortNotSupportedException.class, requestBuilder);
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueNestedParamSortTree_Expect_CorrectEntities() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, true, String.valueOf(TypeSort.TREE_SORT));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()))
//                .param("typeSort", String.valueOf(TypeSort.TREE_SORT));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParamSortMANUAL_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MANUAL));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", String.valueOf(TypeSort.MANUAL));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueNestedParamSortMANUAL_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MANUAL));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true))
//                .param("typeSort", String.valueOf(TypeSort.MANUAL));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParamSortAlphabeticASC_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.ALPHABETICAL_ASC));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", String.valueOf(TypeSort.ALPHABETICAL_ASC));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueNestedParamSortAlphabeticDESC_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.ALPHABETICAL_DESC));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true))
//                .param("typeSort", String.valueOf(TypeSort.ALPHABETICAL_DESC));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNoNestedParamSortPublishedASC_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.PUBLISHED_DATE_ASC));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", String.valueOf(TypeSort.PUBLISHED_DATE_ASC));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueNestedParamSortPublishedDESC_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.PUBLISHED_DATE_DESC));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true))
//                .param("typeSort", String.valueOf(TypeSort.PUBLISHED_DATE_DESC));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParamSortModifiedASC_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MODIFIED_DATE_ASC));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", String.valueOf(TypeSort.MODIFIED_DATE_ASC));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParamSortModifiedDESC_Expect_CorrectEntitiesEmptyChild() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MODIFIED_DATE_DESC));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true))
//                .param("typeSort", String.valueOf(TypeSort.MODIFIED_DATE_DESC));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndNotNestedParamSortInvalid_Expect_Exception() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MANUAL));
//        final DocumentDTO document = documentService.get(menu.getDocId());
//        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
//        documentService.save(document);
//
//        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());
//
//        final String fakeNested = "test";
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", fakeNested);
//
//        performRequestBuilderExpectException(IllegalArgumentException.class, requestBuilder);
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistAndMenuItemsHasModeDONOTSHOW_NotNestedParamSortNull_Expect_EmptyResult() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, null);
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When_MenuExistAndMenuItemsHasModeDONOTSHOW_NestedParamTrue_Expect_EmptyResult() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, true, null);
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWNotNestedParamSortTree_Expect_CorrectEception() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.TREE_SORT));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()))
//                .param("typeSort", String.valueOf(TypeSort.TREE_SORT));
//
//        performRequestBuilderExpectException(SortNotSupportedException.class, requestBuilder);
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueNestedParamSortTree_Expect_EmptyResult() throws Exception {
//
//        final MenuDTO menu = menuDataInitializer.createData(true, true, String.valueOf(TypeSort.TREE_SORT));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWNotNestedParamSortMANUAL_Expect_EmptyResult() throws Exception {
//
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MANUAL));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueNestedParamSortMANUAL_Expect_EmptyResult() throws Exception {
//
//        final MenuDTO menu = menuDataInitializer.createData(true, true, String.valueOf(TypeSort.MANUAL));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWNotNestedParamSortAlphabeticASC_Expect_EmptyResult() throws Exception {
//
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.ALPHABETICAL_ASC));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueNestedParamSortAlphabeticDESC_Expect_EmptyResult() throws Exception {
//
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.ALPHABETICAL_DESC));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWNoNestedParamSortPublishedASC_Expect_EmptyResult() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.PUBLISHED_DATE_ASC));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueNestedParamSortPublishedDESC_Expect_EmptyResult() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.PUBLISHED_DATE_DESC));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWNotNestedParamSortModifiedASC_Expect_EmptyResult() throws Exception {
//
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MODIFIED_DATE_ASC));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueNestedParamSortModifiedDESC_Expect_EmptyResult() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, String.valueOf(TypeSort.MODIFIED_DATE_DESC));
//        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
//        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
//        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
//
//
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
//        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(true))
//                .param("typeSort", menu.getTypeSort());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//
//    @Test
//    public void getMenuItems_When_UserSuperAdminMenuExistInModeDONOTSHOWHasNotNested_Expect_CorrectEntitiesSizeAndEmptyChildren() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, false, null);
//        final UserDomainObject user = new UserDomainObject(1);
//        user.setLanguageIso639_2("eng");
//        user.addRoleId(Roles.SUPER_ADMIN.getId());
//        Imcms.setUser(user);
//
//        assertTrue(Imcms.getUser().isSuperAdmin());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()));
//
//        List<MenuItemDTO> menuItems = menuService.getMenuItems(
//                menu.getDocId(), menu.getMenuIndex(), user.getLanguage(), menu.isNested(), null);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menuItems));
//    }
//
//    @Test
//    public void getMenuItems_When_UserSuperAdminMenuExistInModeDONOTSHOWNestedOn_Expect_CorrectEntitiesSize() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, true, null);
//        final UserDomainObject user = new UserDomainObject(1);
//        user.setLanguageIso639_2("eng");
//        user.addRoleId(Roles.SUPER_ADMIN.getId());
//        Imcms.setUser(user);
//
//        assertTrue(Imcms.getUser().isSuperAdmin());
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
//                .param("docId", String.valueOf(menu.getDocId()))
//                .param("nested", String.valueOf(menu.isNested()));
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu.getMenuItems()));
//    }
//
//    @Test
//    public void getMenuItems_When_MenuMissing_Expect_Expect_EmptyArray() throws Exception {
//        final MenuDTO menu = menuDataInitializer.createData(true, true, null);
//        versionDataInitializer.createData(0, 1001);
//        menuService.deleteByDocId(menu.getDocId());
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("menuIndex", "1")
//                .param("docId", "1001");
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
//    }
//
//    @Test
//    public void postMenu_When_MenuExistWithMenuItems_Expect_Ok() throws Exception {
//        final MenuDTO menuDTO = menuDataInitializer.createData(true, true, null);
//
//        final UserDomainObject user = new UserDomainObject(1);
//        user.setLanguageIso639_2("eng");
//        user.addRoleId(Roles.SUPER_ADMIN.getId());
//        Imcms.setUser(user);
//
//        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
//    }
//
//    @Test
//    public void postMenu_When_MenuMissing_Expect_EmptyArray() throws Exception {
//        final MenuDTO menuDTO = menuDataInitializer.createData(true, true, null);
//
//        versionDataInitializer.createData(0, 1001);
//        menuService.deleteByDocId(menuDTO.getDocId());
//
//        final UserDomainObject user = new UserDomainObject(1);
//        user.setLanguageIso639_2("eng");
//        user.addRoleId(Roles.SUPER_ADMIN.getId());
//        Imcms.setUser(user);
//
//        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
//    }
//
//    private DocumentDTO setUpMenuDoc(DocumentDTO document, String enableLang, Meta.DisabledLanguageShowMode showMode) {
//        for (CommonContent content : document.getCommonContents()) {
//            Language language = content.getLanguage();
//            language.setEnabled(content.getLanguage().getCode().equals(enableLang));
//            languageRepository.save(new LanguageJPA(language));
//        }
//        document.setDisabledLanguageShowMode(showMode);
//        return documentService.save(document);
//    }

}
