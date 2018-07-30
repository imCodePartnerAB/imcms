${"<!--"}<%@ page trimDirectiveWhitespaces="true" %>${"-->"}
    <%@ page contentType="text/javascript" pageEncoding="UTF-8" %>

    Imcms = {
        expiredSessionTimeInMillis: ${pageContext.session.maxInactiveInterval * 1000},
        userLanguage: "${userLanguage}",
        contextPath: "${pageContext.request.contextPath}",
        imagesPath: "${imagesPath}",
        version: "${version}",
        isEditMode: ${isEditMode or false},
        isPreviewMode: ${isPreviewMode or false},
        isVersioningAllowed: ${isVersioningAllowed or false},
        isAdmin: ${isAdmin or false},
        editOptions: {
            isEditDocInfo: ${editOptions.editDocInfo or false},
            isEditContent: ${
                    editOptions.editText or editOptions.editMenu or editOptions.editImage or editOptions.editLoop
            }
        },
        document: {
            id: ${empty currentDocument.id ? 'null' : currentDocument.id},
            type: ${empty currentDocument.documentTypeId ? 'null' : currentDocument.documentTypeId},
            hasNewerVersion: ${hasNewerVersion or false},
            headline: "${currentDocument.headline}"
        },
        language: {
            name: "${currentDocument.language.name}",
            nativeName: "${currentDocument.language.nativeName}",
            code: "${empty currentDocument.language.code ? userLanguage : currentDocument.language.code}"
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
        basePath: Imcms.contextPath + "/js/imcms",
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
            "imcms-events": "events/imcms_events.js",
            "imcms-streams": "streams/imcms_streams.js",
            "imcms-i18n-texts": "i18n/imcms_i18n_texts.js", // languages support
            // <components>
            'imcms-authentication': 'components/imcms_authentication.js',
            "imcms-document-types": "components/imcms_document_types.js",
            "imcms-calendar": "components/imcms_calendar.js",
            "imcms-date-picker": "components/imcms_date_picker.js",
            "imcms-time-picker": "components/imcms_time_picker.js",
            "imcms-uuid-generator": "components/imcms_uuid_generator.js",
            "imcms-image-cropper": "components/imcms_image_cropper.js",
            "imcms-validator": "components/imcms_validator.js",
            "imcms-jquery-element-reload": "components/imcms_jquery_element_reload.js",
            "imcms-jquery-string-selector": "components/imcms_jquery_string_selector.js",
            "imcms-dom-attributes-extractor": "components/imcms_dom_attributes_extractor.js",
            "imcms-image-crop-angles": "components/imcms_image_crop_angles.js",
            "imcms-numeric-limiter": "components/imcms_numeric_limiter.js",
            "imcms-image-cropping-elements": "components/imcms_image_cropping_elements.js",
            "imcms-image-rotate": "components/imcms_image_rotate.js",
            "imcms-image-resize": "components/imcms_image_resize.js",
            "imcms-image-edit-size-controls": "components/imcms_image_edit_size_controls.js",
            "imcms-session-timeout-management": "components/imcms_session_timeout_management.js",
            "imcms-date-time-validator": "components/imcms_date_time_validator.js",
            "imcms-cookies": "components/imcms_cookies.js",
            'imcms-displacing-array': "components/imcms_displacing_array.js",
            "mousetrap": "components/mousetrap.js",
            "imcms-window-keys-controller": "components/imcms_window_keys_controller.js",
            "date-format": "components/date_format.js",
            //  <text editor components>
            "imcms-image-in-text-plugin": "components/text_editor/imcms_image_in_text_plugin.js",
            "imcms-text-history-plugin": "components/text_editor/imcms_text_history_plugin.js",
            "imcms-text-validation-plugin": "components/text_editor/imcms_text_validation_plugin.js",
            "imcms-text-full-screen-plugin": "components/text_editor/imcms_text_full_screen_plugin.js",
            "imcms-text-discard-changes-plugin": "components/text_editor/imcms_text_discard_changes_plugin.js",
            //  </text editor components>
            // <components>
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
            //  <builder components>
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
            "imcms-top-panel-visibility-initiator": "builders/components/imcms_top_panel_visibility_initiator.js",
            "imcms-admin-panel-settings-builder": "builders/components/imcms_admin_panel_settings_builder.js",
            "imcms-admin-panel-state": "builders/components/imcms_admin_panel_state.js",
            "imcms-window-tabs-builder": "builders/components/imcms_window_tabs_builder.js",
            "imcms-page-info-tab": "builders/components/imcms_page_info_tab.js",
            "imcms-window-tab-builder": "builders/components/imcms_window_tab_builder.js",
            'imcms-super-admin-tab': 'builders/components/imcms_super_admin_tab.js',
            'imcms-role-editor': 'builders/components/imcms_role_editor.js',
            'imcms-role-to-row-transformer': 'builders/components/imcms_role_to_row_transformer.js',
            //   <page_info_tabs>
            "imcms-page-info-tabs-builder": "builders/components/page_info_tabs/imcms_page_info_tabs_builder.js",
            "imcms-appearance-tab-builder": "builders/components/page_info_tabs/imcms_appearance_tab_builder.js",
            "imcms-life-cycle-tab-builder": "builders/components/page_info_tabs/imcms_life_cycle_tab_builder.js",
            "imcms-templates-tab-builder": "builders/components/page_info_tabs/imcms_templates_tab_builder.js",
            "imcms-file-tab-builder": "builders/components/page_info_tabs/imcms_file_tab_builder.js",
            "imcms-url-tab-builder": "builders/components/page_info_tabs/imcms_url_tab_builder.js",
            "imcms-keywords-tab-builder": "builders/components/page_info_tabs/imcms_keywords_tab_builder.js",
            "imcms-categories-tab-builder": "builders/components/page_info_tabs/imcms_categories_tab_builder.js",
            "imcms-access-tab-builder": "builders/components/page_info_tabs/imcms_access_tab_builder.js",
            "imcms-permissions-tab-builder": "builders/components/page_info_tabs/imcms_permissions_tab_builder.js",
            "imcms-status-tab-builder": "builders/components/page_info_tabs/imcms_status_tab_builder.js",
            //   </page_info_tabs>
            //   <super_admin_tabs>
            'imcms-super-admin-tabs-builder': "builders/components/super_admin_tabs/imcms_super_admin_tabs_builder.js",
            'imcms-users-tab-builder': "builders/components/super_admin_tabs/imcms_users_tab_builder.js",
            'imcms-roles-tab-builder': 'builders/components/super_admin_tabs/imcms_roles_tab_builder.js',
            'imcms-ip-access-tab-builder': 'builders/components/super_admin_tabs/imcms_ip_access_tab_builder.js',
            'imcms-ip-white-list-tab-builder': 'builders/components/super_admin_tabs/imcms_ip_white_list_tab_builder.js',
            'imcms-delete-docs-tab-builder': 'builders/components/super_admin_tabs/imcms_delete_docs_tab_builder.js',
            'imcms-templates-admin-tab-builder': 'builders/components/super_admin_tabs/imcms_templates_tab_builder.js',
            'imcms-files-tab-builder': 'builders/components/super_admin_tabs/imcms_files_tab_builder.js',
            'imcms-search-tab-builder': 'builders/components/super_admin_tabs/imcms_search_tab_builder.js',
            'imcms-link-validator-tab-builder': 'builders/components/super_admin_tabs/imcms_link_validator_tab_builder.js',
            'imcms-categories-admin-tab-builder': 'builders/components/super_admin_tabs/imcms_categories_tab_builder.js',
            'imcms-profiles-tab-builder': 'builders/components/super_admin_tabs/imcms_profiles_tab_builder.js',
            'imcms-system-properties-tab-builder': 'builders/components/super_admin_tabs/imcms_system_properties_tab_builder.js',
            //   </super_admin_tabs>
            //  </builder components>
            //  <windows>
            "imcms-window-builder": "builders/windows/imcms_window_builder.js",
            "imcms-modal-window-builder": "builders/windows/imcms_modal_window_builder.js",
            "imcms-content-manager-builder": "builders/windows/imcms_content_manager_builder.js",
            "imcms-page-info-builder": "builders/windows/imcms_page_info_builder.js",
            "imcms-document-type-select-window-builder": "builders/windows/imcms_document_type_select_window_builder.js",
            "imcms-document-profile-select-window-builder": "builders/windows/imcms_document_profile_select_window_builder.js",
            "imcms-text-history-window-builder": "builders/windows/imcms_text_history_window_builder.js",
            "imcms-text-validation-result-builder": "builders/windows/imcms_text_validation_result_builder.js",
            "imcms-super-admin-page-builder": "builders/windows/imcms_super_admin_page_builder.js",
            //   <editors>
            "imcms-menu-editor-builder": "builders/windows/editors/imcms_menu_editor_builder.js",
            "imcms-document-editor-builder": "builders/windows/editors/imcms_document_editor_builder.js",
            "imcms-image-editor-builder": "builders/windows/editors/imcms_image_editor_builder.js",
            "imcms-image-editor-factory": "builders/windows/editors/imcms_image_editor_factory.js",
            "imcms-image-editor-right-side-builder": "builders/windows/editors/imcms_image_editor_right_side_builder.js",
            "imcms-image-editor-left-side-builder": "builders/windows/editors/imcms_image_editor_left_side_builder.js",
            "imcms-image-editor-body-head-builder": "builders/windows/editors/imcms_image_editor_body_head_builder.js",
            "imcms-loop-editor-builder": "builders/windows/editors/imcms_loop_editor_builder.js",
            //   </editors>
            //  </windows>
            // other builders
            "imcms-primitives-builder": "builders/imcms_primitives_builder.js",
            "imcms-components-builder": "builders/imcms_components_builder.js",
            "imcms-window-components-builder": "builders/imcms_window_components_builder.js",
            "imcms-bem-builder": "builders/imcms_bem_builder.js",
            "imcms-image-content-builder": "builders/imcms_image_content_builder.js",
            "imcms-admin-panel-builder": "builders/imcms_admin_panel_builder.js",
            "imcms-site-specific": "builders/imcms_site_specific_admin_panel.js",
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
            "imcms-texts-rest-api": "rest/imcms_texts_rest_api.js",
            "imcms-texts-history-rest-api": "rest/imcms_texts_history_rest_api.js",
            "imcms-texts-validation-rest-api": "rest/imcms_texts_validation_rest_api.js",
            "imcms-file-doc-files-rest-api": "rest/imcms_file_doc_files_rest_api.js",
            "imcms-profiles-rest-api": "rest/imcms_profiles_rest_api.js",
            "imcms-document-validation-rest-api": "rest/imcms_document_validation_rest_api.js",
            "imcms-document-copy-rest-api": "rest/imcms_document_copy_rest_api.js",
            'imcms-auth-providers-rest-api': 'rest/imcms_auth_providers_rest_api.js'
        }
    };

    <%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
    <%--@elvariable id="isVersioningAllowed" type="boolean"--%>
    <%--@elvariable id="isEditMode" type="boolean"--%>
    <%--@elvariable id="isPreviewMode" type="boolean"--%>
    <%--@elvariable id="hasNewerVersion" type="boolean"--%>
    <%--@elvariable id="version" type="java.lang.String"--%>
    <%--@elvariable id="imagesPath" type="java.lang.String"--%>
    <%--@elvariable id="userLanguage" type="java.lang.String"--%>
    <%--@elvariable id="isAdmin" type="boolean"--%>
    <%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>