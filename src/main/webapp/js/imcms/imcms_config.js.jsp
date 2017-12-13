<%@ page contentType="text/javascript" pageEncoding="UTF-8" %>
    <%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
    <%--@elvariable id="isVersioningAllowed" type="boolean"--%>
    <%--@elvariable id="isEditMode" type="boolean"--%>
    <%--@elvariable id="isPreviewMode" type="boolean"--%>
    <%--@elvariable id="hasNewerVersion" type="boolean"--%>
    <%--@elvariable id="version" type="java.lang.String"--%>
    <%--@elvariable id="imagesPath" type="java.lang.String"--%>

    Imcms = {
        contextPath: "${pageContext.request.contextPath}",
        imagesPath: "${imagesPath}",
        version: "${version}",
        isEditMode: ${isEditMode},
        isPreviewMode: ${isPreviewMode},
        isVersioningAllowed: ${isVersioningAllowed},
        document: {
            id: ${currentDocument.id},
            type: ${currentDocument.documentTypeId},
            hasNewerVersion: ${hasNewerVersion},
            headline: "${currentDocument.headline}"
        },
        language: {
            name: "${currentDocument.language.name}",
            nativeName: "${currentDocument.language.nativeName}",
            code: "${currentDocument.language.code}"
        },
        loadedDependencies: {},
        dependencyTree: {
            imcms: []
        },
        requiresQueue: [],
        browserInfo: {
            isIE10: (window.navigator.userAgent.indexOf("Mozilla/5.0 (compatible; MSIE 10.0;") === 0)
        }
    };
    Imcms.modules = {
        imcms: Imcms // default module
    };
    Imcms.config = {
        basePath: Imcms.contextPath + "/js/imcms_new",
        dependencies: {
            "jquery": {
                path: "//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js",
                onLoad: function ($) {
                    return $.noConflict(true);
                }
            },
            "tinyMCE": {
                path: (Imcms.browserInfo.isIE10)
                    ? "//cdnjs.cloudflare.com/ajax/libs/tinymce/4.5.7/tinymce.min.js"
                    : "//cdn.tinymce.com/4/tinymce.min.js",
                moduleName: "tinyMCE",
                onLoad: function () {
                    var tinyMCE = window.tinyMCE;

                    // TinyMCE version for IE 10 plugins require "tinymce" in global scope
                    if (!Imcms.browserInfo.isIE10) {
                        delete window.tinyMCE;
                        delete window.tinymce;
                    }

                    return tinyMCE;
                }
            },
            "imcms-tests": "imcms_tests.js",
            // components
            "imcms-calendar": "components/imcms_calendar.js",
            "imcms-date-picker": "components/imcms_date_picker.js",
            "imcms-time-picker": "components/imcms_time_picker.js",
            "imcms-uuid-generator": "components/imcms_uuid_generator.js",
            "imcms-image-cropper": "components/imcms_image_cropper.js",
            "imcms-validator": "components/imcms_validator.js",
            "imcms-jquery-element-reload": "components/imcms_jquery_element_reload.js",
            "imcms-jquery-string-selector": "components/imcms_jquery_string_selector.js",
            "imcms-dom-attributes-extractor": "components/imcms_dom_attributes_extractor.js",
            // editors initializer
            "imcms-text-editor-initializer": "editor_initializer/imcms_text_editor_initializer.js",
            "imcms-editors-initializer": "editor_initializer/imcms_editors_initializer.js",
            "imcms-image-editor-initializer": "editor_initializer/imcms_image_editor_initializer.js",
            "imcms-loop-editor-initializer": "editor_initializer/imcms_loop_editor_initializer.js",
            "imcms-menu-editor-initializer": "editor_initializer/imcms_menu_editor_initializer.js",
            "imcms-editor-labels-initializer": "editor_initializer/imcms_editor_labels_initializer.js",
            // editors init data modules
            "imcms-image-editor-init-data": "editor_initializer/imcms_editors_init_data/imcms_image_editor_init_data.js",
            "imcms-menu-editor-init-data": "editor_initializer/imcms_editors_init_data/imcms_menu_editor_init_data.js",
            "imcms-loop-editor-init-data": "editor_initializer/imcms_editors_init_data/imcms_loop_editor_init_data.js",
            // init strategy
            "imcms-editor-init-strategy": "editor_initializer/imcms_editor_init_strategy.js",
            // <builders>
            // basic components builders
            "imcms-buttons-builder": "builders/components/imcms_buttons_builder.js",
            "imcms-flags-builder": "builders/components/imcms_flags_builder.js",
            "imcms-checkboxes-builder": "builders/components/imcms_checkboxes_builder.js",
            "imcms-radio-buttons-builder": "builders/components/imcms_radio_buttons_builder.js",
            "imcms-selects-builder": "builders/components/imcms_selects_builder.js",
            "imcms-texts-builder": "builders/components/imcms_texts_builder.js",
            "imcms-switch-builder": "builders/components/imcms_switch_builder.js",
            "imcms-choose-image-builder": "builders/components/imcms_choose_image_builder.js",
            "imcms-keywords-builder": "builders/components/imcms_keywords_builder.js",
            "imcms-date-time-builder": "builders/components/imcms_date_time_builder.js",
            "imcms-controls-builder": "builders/components/imcms_controls_builder.js",
            "imcms-primitives-builder": "builders/imcms_primitives_builder.js",
            "imcms-components-builder": "builders/imcms_components_builder.js",
            "imcms-window-components-builder": "builders/imcms_window_components_builder.js",
            // <windows>
            "imcms-window-builder": "builders/windows/imcms_window_builder.js",
            "imcms-modal-window-builder": "builders/windows/imcms_modal_window_builder.js",
            "imcms-content-manager-builder": "builders/windows/imcms_content_manager_builder.js",
            "imcms-page-info-builder": "builders/windows/imcms_page_info_builder.js",
            //  <page_info_tabs>
            "imcms-page-info-tabs-builder": "builders/windows/page_info_tabs/imcms_page_info_tabs_builder.js",
            "imcms-page-info-tab-form-builder": "builders/windows/page_info_tabs/imcms_page_info_tab_form_builder.js",
            "imcms-appearance-tab-builder": "builders/windows/page_info_tabs/imcms_appearance_tab_builder.js",
            "imcms-life-cycle-tab-builder": "builders/windows/page_info_tabs/imcms_life_cycle_tab_builder.js",
            "imcms-keywords-tab-builder": "builders/windows/page_info_tabs/imcms_keywords_tab_builder.js",
            "imcms-categories-tab-builder": "builders/windows/page_info_tabs/imcms_categories_tab_builder.js",
            "imcms-access-tab-builder": "builders/windows/page_info_tabs/imcms_access_tab_builder.js",
            "imcms-permissions-tab-builder": "builders/windows/page_info_tabs/imcms_permissions_tab_builder.js",
            "imcms-templates-tab-builder": "builders/windows/page_info_tabs/imcms_templates_tab_builder.js",
            "imcms-status-tab-builder": "builders/windows/page_info_tabs/imcms_status_tab_builder.js",
            //  </page_info_tabs>
            //  <editors>
            "imcms-menu-editor-builder": "builders/windows/editors/imcms_menu_editor_builder.js",
            "imcms-document-editor-builder": "builders/windows/editors/imcms_document_editor_builder.js",
            "imcms-image-editor-builder": "builders/windows/editors/imcms_image_editor_builder.js",
            "imcms-loop-editor-builder": "builders/windows/editors/imcms_loop_editor_builder.js",
            //  </editors>
            // </windows>
            // other builders
            "imcms-bem-builder": "builders/imcms_bem_builder.js",
            "imcms-image-content-builder": "builders/imcms_image_content_builder.js",
            "imcms-admin-panel-builder": "builders/imcms_admin_panel_builder.js",
            // </builders>
            // rest api
            "imcms-rest-api": "rest/imcms_rest_api.js",
            "imcms-image-files-rest-api": "rest/imcms_image_files_rest_api.js",
            "imcms-image-folders-rest-api": "rest/imcms_image_folders_rest_api.js",
            "imcms-documents-rest-api": "rest/imcms_documents_rest_api.js",
            "imcms-documents-search-rest-api": "rest/imcms_documents_search_rest_api.js",
            "imcms-users-rest-api": "rest/imcms_users_rest_api.js",
            "imcms-categories-rest-api": "rest/imcms_categories_rest_api.js",
            "imcms-roles-rest-api": "rest/imcms_roles_rest_api.js",
            "imcms-templates-rest-api": "rest/imcms_templates_rest_api.js",
            "imcms-category-types-rest-api": "rest/imcms_category_types_rest_api.js",
            "imcms-loops-rest-api": "rest/imcms_loops_rest_api.js",
            "imcms-menus-rest-api": "rest/imcms_menus_rest_api.js",
            "imcms-images-rest-api": "rest/imcms_images_rest_api.js",
            "imcms-languages-rest-api": "rest/imcms_languages_rest_api.js",
            "imcms-texts-rest-api": "rest/imcms_texts_rest_api.js"
        }
    };
