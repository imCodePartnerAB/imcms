package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.sorted.TypeSort;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static com.imcode.imcms.sorted.TypeSort.TREE_SORT;
import static imcode.server.ImcmsConstants.SWE_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class MenuControllerTest extends AbstractControllerTest {

    private static final int COUNT_MENU_ITEMS = 2;

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

    @Test
    public void getMenu_When_MenuExistInModeSHOWINDEFAULTLANGAndParam_Expect_CorrectEntitiesSizeAndEmptyChildren() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), Imcms.getUser().getLanguage(), menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(resultMenu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndOn_Expect_MenuDtoJson() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueParamSortTree_Expect_CorrectEntities() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TREE_SORT));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndParamSortMANUAL_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MANUAL), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.MANUAL));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueParamSortMANUAL_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MANUAL), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.MANUAL));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndParamSortAlphabeticASC_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.ALPHABETICAL_ASC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.ALPHABETICAL_ASC));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueParamSortAlphabeticDESC_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.ALPHABETICAL_DESC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.ALPHABETICAL_DESC));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenu_When_MenuExistInModeSHOWINDEFAULTLANGAndNoParamSortPublishedASC_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.PUBLISHED_DATE_ASC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.PUBLISHED_DATE_ASC));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndTrueParamSortPublishedDESC_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.PUBLISHED_DATE_DESC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.PUBLISHED_DATE_DESC));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndParamSortModifiedASC_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MODIFIED_DATE_ASC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.MODIFIED_DATE_ASC));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistInModeSHOWINDEFAULTLANGAndParamSortModifiedDESC_Expect_CorrectEntitiesEmptyChild() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MODIFIED_DATE_DESC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", String.valueOf(TypeSort.MODIFIED_DATE_DESC));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenu_When_MenuExistInModeSHOWINDEFAULTLANGAndParamSortInvalid_Expect_CorrectData() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MANUAL), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getDocId());
        document.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        documentService.save(document);

        assertEquals(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE, document.getDisabledLanguageShowMode());

        final String fakeTypeSort = "test";

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", fakeTypeSort);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuExistAndMenuItemsHasModeDONOTSHOW_ParamSortNull_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When_MenuExistAndMenuItemsHasModeDONOTSHOW_ParamTrue_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueParamSortTree_Expect_EmptyResult() throws Exception {

        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TREE_SORT), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWParamSortMANUAL_Expect_EmptyResult() throws Exception {

        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MANUAL), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueParamSortMANUAL_Expect_EmptyResult() throws Exception {

        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MANUAL), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When_MenuExistAndMenuItemsHasModeDONOTSHOWParamSortAlphabeticASC_Expect_EmptyResult() throws Exception {

        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.ALPHABETICAL_ASC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueParamSortAlphabeticDESC_Expect_EmptyResult() throws Exception {

        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.ALPHABETICAL_DESC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWNoParamSortPublishedASC_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.PUBLISHED_DATE_ASC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueParamSortPublishedDESC_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.PUBLISHED_DATE_DESC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWParamSortModifiedASC_Expect_EmptyResult() throws Exception {

        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MODIFIED_DATE_ASC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void getMenuItems_When__MenuExistAndMenuItemsHasModeDONOTSHOWTrueParamSortModifiedDESC_Expect_EmptyResult() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, String.valueOf(TypeSort.MODIFIED_DATE_DESC), COUNT_MENU_ITEMS);
        final DocumentDTO document = documentService.get(menu.getMenuItems().get(0).getDocumentId());
        final DocumentDTO document2 = documentService.get(menu.getMenuItems().get(1).getDocumentId());
        DocumentDTO setUpDocDTO = setUpMenuDoc(document, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        DocumentDTO setUpDocDTO2 = setUpMenuDoc(document2, SWE_CODE, Meta.DisabledLanguageShowMode.DO_NOT_SHOW);


        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO.getDisabledLanguageShowMode());
        assertEquals(Meta.DisabledLanguageShowMode.DO_NOT_SHOW, setUpDocDTO2.getDisabledLanguageShowMode());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()))
                .param("typeSort", menu.getTypeSort());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }


    @Test
    public void getMenuItems_When_UserSuperAdminMenuExistInModeDONOTSHOWHas_Expect_CorrectEntitiesSizeAndEmptyChildren() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        assertTrue(Imcms.getUser().isSuperAdmin());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        MenuDTO resultMenu = menuService.getMenuDTO(
                menu.getDocId(), menu.getMenuIndex(), user.getLanguage(), null);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(resultMenu));
    }

    @Test
    public void getMenu_When_UserSuperAdminMenuExistInModeDONOTSHOWOn_Expect_CorrectEntitiesSize() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        assertTrue(Imcms.getUser().isSuperAdmin());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", String.valueOf(menu.getMenuIndex()))
                .param("docId", String.valueOf(menu.getDocId()));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(menu));
    }

    @Test
    public void getMenuItems_When_MenuMissing_Expect_Expect_EmptyArray() throws Exception {
        final MenuDTO menu = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);
        versionDataInitializer.createData(0, 1001);
        menuService.deleteByDocId(menu.getDocId());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("menuIndex", "1")
                .param("docId", "1001");

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "{}");
    }

    @Test
    public void postMenu_When_MenuExistWithMenuItems_Expect_Ok() throws Exception {
        final MenuDTO menuDTO = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

    @Test
    public void postMenu_When_MenuMissing_Expect_EmptyArray() throws Exception {
        final MenuDTO menuDTO = menuDataInitializer.createData(true, null, COUNT_MENU_ITEMS);

        versionDataInitializer.createData(0, 1001);
        menuService.deleteByDocId(menuDTO.getDocId());

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        performPostWithContentExpectOkAndJsonContentEquals(menuDTO, menuDTO);
    }

    private DocumentDTO setUpMenuDoc(DocumentDTO document, String enableLang, Meta.DisabledLanguageShowMode showMode) {
        for (CommonContent content : document.getCommonContents()) {
            Language language = content.getLanguage();
            language.setEnabled(content.getLanguage().getCode().equals(enableLang));
            languageRepository.save(new LanguageJPA(language));
        }
        document.setDisabledLanguageShowMode(showMode);
        return documentService.save(document);
    }

}
