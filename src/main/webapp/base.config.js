/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.08.18
 */

const path = require('path');

module.exports = {
    entry: {
        imcms_admin: './js/imcms/imcms_admin.js',
        imcms_start: './js/imcms/imcms_start.js',
        imcms_admin_manager_start: './js/imcms/starters/imcms_admin_manager_start.js',
        imcms_content_manager_start: './js/imcms/starters/imcms_content_manager_start.js',
        imcms_doc_info_edit_start: './js/imcms/starters/imcms_doc_info_edit_start.js',
        imcms_doc_manager_start: './js/imcms/starters/imcms_doc_manager_start.js',
        imcms_image_edit_start: './js/imcms/starters/imcms_image_edit_start.js',
        imcms_loop_edit_start: './js/imcms/starters/imcms_loop_edit_start.js',
        imcms_menu_edit_start: './js/imcms/starters/imcms_menu_edit_start.js',
        imcms_text_edit_start: './js/imcms/starters/imcms_text_edit_start.js',
        imcms_login_start: './js/imcms/starters/imcms_login_start.js',
        userCreate: './js/imcms/new_admin/userCreate.js',
        userEditorNew: './js/imcms/new_admin/userEditorNew.js',
        userEditorOld: './js/imcms/old_admin/userEditorOld.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: "babel-loader",
                    options: {
                        presets: ["@babel/preset-env"]
                    }
                }
            }, {
                test: /\.css$/,
                use: [
                    'style-loader',
                    {
                        loader: 'css-loader',
                        options: {
                            url: false
                        }
                    }
                ]
            }
        ]
    },
    resolve: {
        extensions: [".js", ".jsx"],
        alias: {
            'imcms': path.resolve(
                __dirname, 'js/imcms/imcms.js'
            ),
            'imcms-authentication': path.resolve(
                __dirname, 'js/imcms/components/imcms_authentication.js'
            ),
            'imcms-events': path.resolve(
                __dirname, 'js/imcms/events/imcms_events.js'
            ),
            'imcms-streams': path.resolve(
                __dirname, 'js/imcms/streams/imcms_streams.js'
            ),
            'imcms-i18n-texts': path.resolve(
                __dirname, 'js/imcms/i18n/imcms_i18n_texts.js'
            ),
            'imcms-document-types': path.resolve(
                __dirname, 'js/imcms/components/imcms_document_types.js'
            ),
            'imcms-calendar': path.resolve(
                __dirname, 'js/imcms/components/imcms_calendar.js'
            ),
            'imcms-date-picker': path.resolve(
                __dirname, 'js/imcms/components/imcms_date_picker.js'
            ),
            'imcms-time-picker': path.resolve(
                __dirname, 'js/imcms/components/imcms_time_picker.js'
            ),
            'imcms-uuid-generator': path.resolve(
                __dirname, 'js/imcms/components/imcms_uuid_generator.js'
            ),
            'imcms-validator': path.resolve(
                __dirname, 'js/imcms/components/imcms_validator.js'
            ),
            'imcms-jquery-element-reload': path.resolve(
                __dirname, 'js/imcms/components/imcms_jquery_element_reload.js'
            ),
            'imcms-jquery-string-selector': path.resolve(
                __dirname, 'js/imcms/components/imcms_jquery_string_selector.js'
            ),
            'imcms-dom-attributes-extractor': path.resolve(
                __dirname, 'js/imcms/components/imcms_dom_attributes_extractor.js'
            ),
            'imcms-numeric-limiter': path.resolve(
                __dirname, 'js/imcms/components/imcms_numeric_limiter.js'
            ),
            'imcms-session-timeout-management': path.resolve(
                __dirname, 'js/imcms/components/imcms_session_timeout_management.js'
            ),
            'imcms-date-time-validator': path.resolve(
                __dirname, 'js/imcms/components/imcms_date_time_validator.js'
            ),
            'imcms-cookies': path.resolve(
                __dirname, 'js/imcms/components/imcms_cookies.js'
            ),
            'imcms-displacing-array': path.resolve(
                __dirname, 'js/imcms/components/imcms_displacing_array.js'
            ),
            'imcms-window-keys-controller': path.resolve(
                __dirname, 'js/imcms/components/imcms_window_keys_controller.js'
            ),
            'date-format': path.resolve(
                __dirname, 'js/imcms/components/date_format.js'
            ),
            'imcms-tag-replacer': path.resolve(
                __dirname, 'js/imcms/components/imcms_jquery_tag_replacer.js'
            ),
            'imcms-image-in-text-plugin': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_image_in_text_plugin.js'
            ),
            'imcms-text-history-plugin': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_history_plugin.js'
            ),
            'imcms-text-validation-plugin': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_validation_plugin.js'
            ),
            'imcms-text-full-screen-plugin': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_full_screen_plugin.js'
            ),
            'imcms-text-discard-changes-plugin': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_discard_changes_plugin.js'
            ),
            'imcms-text-editor-utils': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_editor_utils.js'
            ),
            'imcms-text-editor-toolbar-button-builder': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_editor_toolbar_button_builder.js'
            ),
            'imcms-tinymce-text-editor': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_tinymce_text_editor.js'
            ),
            'imcms-text-editor': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_editor.js'
            ),
            'imcms-text-editor-types': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_text_editor_types.js'
            ),
            'imcms-switch-to-plain-text': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_switch_to_plain_text_plugin.js'
            ),
            'imcms-switch-to-html-mode': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_switch_to_html_mode_plugin.js'
            ),
            'imcms-switch-to-text-editor': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_switch_to_text_editor_plugin.js'
            ),
            'imcms-html-filtering-policy-plugin': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_html_filtering_policy_plugin.js'
            ),
            'imcms-html-filtering-policies': path.resolve(
                __dirname, 'js/imcms/components/text_editor/imcms_html_filtering_policies.js'
            ),
            'imcms-text-editor-initializer': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_text_editor_initializer.js'
            ),
            'imcms-editors-initializer': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_editors_initializer.js'
            ),
            'imcms-image-editor-initializer': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_image_editor_initializer.js'
            ),
            'imcms-loop-editor-initializer': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_loop_editor_initializer.js'
            ),
            'imcms-menu-editor-initializer': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_menu_editor_initializer.js'
            ),
            'imcms-editor-labels-initializer': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_editor_labels_initializer.js'
            ),
            'imcms-image-editor-init-data': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_editors_init_data/imcms_image_editor_init_data.js'
            ),
            'imcms-menu-editor-init-data': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_editors_init_data/imcms_menu_editor_init_data.js'
            ),
            'imcms-loop-editor-init-data': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_editors_init_data/imcms_loop_editor_init_data.js'
            ),
            'imcms-editor-init-strategy': path.resolve(
                __dirname, 'js/imcms/editor_initializer/imcms_editor_init_strategy.js'
            ),
            'imcms-buttons-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_buttons_builder.js'
            ),
            'imcms-flags-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_flags_builder.js'
            ),
            'imcms-checkboxes-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_checkboxes_builder.js'
            ),
            'imcms-radio-buttons-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_radio_buttons_builder.js'
            ),
            'imcms-selects-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_selects_builder.js'
            ),
            'imcms-texts-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_texts_builder.js'
            ),
            'imcms-switch-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_switch_builder.js'
            ),
            'imcms-choose-image-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_choose_image_builder.js'
            ),
            'imcms-keywords-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_keywords_builder.js'
            ),
            'imcms-date-time-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_date_time_builder.js'
            ),
            'imcms-controls-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_controls_builder.js'
            ),
            'imcms-top-panel-visibility-initiator': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_top_panel_visibility_initiator.js'
            ),
            'imcms-admin-panel-settings-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_admin_panel_settings_builder.js'
            ),
            'imcms-admin-panel-state': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_admin_panel_state.js'
            ),
            'imcms-window-tabs-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_window_tabs_builder.js'
            ),
            'imcms-page-info-tab': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_page_info_tab.js'
            ),
            'imcms-window-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_window_tab_builder.js'
            ),
            'imcms-super-admin-tab': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_super_admin_tab.js'
            ),
            'imcms-role-editor': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_role_editor.js'
            ),
            'imcms-role-to-row-transformer': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_role_to_row_transformer.js'
            ),
            'imcms-field-wrapper': path.resolve(
                __dirname, 'js/imcms/builders/components/imcms_field_wrapper.js'
            ),
            'imcms-image-rotate': path.resolve(
                __dirname, 'js/imcms/builders/components/image_editor/imcms_image_rotate.js'
            ),
            'imcms-image-resize': path.resolve(
                __dirname, 'js/imcms/builders/components/image_editor/imcms_image_resize.js'
            ),
            'imcms-image-edit-size-controls': path.resolve(
                __dirname, 'js/imcms/builders/components/image_editor/imcms_image_edit_size_controls.js'
            ),
            'imcms-image-cropper': path.resolve(
                __dirname, 'js/imcms/builders/components/image_editor/imcms_image_cropper.js'
            ),
            'imcms-image-cropping-elements': path.resolve(
                __dirname, 'js/imcms/builders/components/image_editor/imcms_image_cropping_elements.js'
            ),
            'imcms-image-crop-angles': path.resolve(
                __dirname, 'js/imcms/builders/components/image_editor/imcms_image_crop_angles.js'
            ),
            'imcms-page-info-tabs-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_page_info_tabs_builder.js'
            ),
            'imcms-appearance-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_appearance_tab_builder.js'
            ),
            'imcms-life-cycle-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_life_cycle_tab_builder.js'
            ),
            'imcms-templates-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_templates_tab_builder.js'
            ),
            'imcms-file-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_file_tab_builder.js'
            ),
            'imcms-url-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_url_tab_builder.js'
            ),
            'imcms-keywords-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_keywords_tab_builder.js'
            ),
            'imcms-categories-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_categories_tab_builder.js'
            ),
            'imcms-access-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_access_tab_builder.js'
            ),
            'imcms-permissions-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_permissions_tab_builder.js'
            ),
            'imcms-status-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/page_info_tabs/imcms_status_tab_builder.js'
            ),
            'imcms-super-admin-tabs-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_super_admin_tabs_builder.js'
            ),
            'imcms-users-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_users_tab_builder.js'
            ),
            'imcms-roles-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_roles_tab_builder.js'
            ),
            'imcms-ip-access-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_ip_access_tab_builder.js'
            ),
            'imcms-ip-white-list-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_ip_white_list_tab_builder.js'
            ),
            'imcms-delete-docs-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_delete_docs_tab_builder.js'
            ),
            'imcms-templates-admin-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_templates_tab_builder.js'
            ),
            'imcms-files-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_files_tab_builder.js'
            ),
            'imcms-search-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_search_tab_builder.js'
            ),
            'imcms-link-validator-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_link_validator_tab_builder.js'
            ),
            'imcms-categories-admin-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_categories_tab_builder.js'
            ),
            'imcms-profiles-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_profiles_tab_builder.js'
            ),
            'imcms-system-properties-tab-builder': path.resolve(
                __dirname, 'js/imcms/builders/components/super_admin_tabs/imcms_system_properties_tab_builder.js'
            ),
            'imcms-window-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_window_builder.js'
            ),
            'imcms-modal-window-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_modal_window_builder.js'
            ),
            'imcms-content-manager-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_content_manager_builder.js'
            ),
            'imcms-page-info-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_page_info_builder.js'
            ),
            'imcms-document-type-select-window-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_document_type_select_window_builder.js'
            ),
            'imcms-document-profile-select-window-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_document_profile_select_window_builder.js'
            ),
            'imcms-text-history-window-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_text_history_window_builder.js'
            ),
            'imcms-text-validation-result-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_text_validation_result_builder.js'
            ),
            'imcms-super-admin-page-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/imcms_super_admin_page_builder.js'
            ),
            'imcms-menu-editor-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/imcms_menu_editor_builder.js'
            ),
            'imcms-document-editor-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/imcms_document_editor_builder.js'
            ),
            'imcms-image-editor-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/image_editor/imcms_image_editor_builder.js'
            ),
            'imcms-image-editor-factory': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/image_editor/imcms_image_editor_factory.js'
            ),
            'imcms-image-editor-right-side-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/image_editor/imcms_image_editor_right_side_builder.js'
            ),
            'imcms-image-editor-left-side-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/image_editor/imcms_image_editor_left_side_builder.js'
            ),
            'imcms-image-editor-body-head-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/image_editor/imcms_image_editor_body_head_builder.js'
            ),
            'imcms-loop-editor-builder': path.resolve(
                __dirname, 'js/imcms/builders/windows/editors/imcms_loop_editor_builder.js'
            ),
            'imcms-primitives-builder': path.resolve(
                __dirname, 'js/imcms/builders/imcms_primitives_builder.js'
            ),
            'imcms-components-builder': path.resolve(
                __dirname, 'js/imcms/builders/imcms_components_builder.js'
            ),
            'imcms-window-components-builder': path.resolve(
                __dirname, 'js/imcms/builders/imcms_window_components_builder.js'
            ),
            'imcms-bem-builder': path.resolve(
                __dirname, 'js/imcms/builders/imcms_bem_builder.js'
            ),
            'imcms-image-content-builder': path.resolve(
                __dirname, 'js/imcms/builders/imcms_image_content_builder.js'
            ),
            'imcms-admin-panel-builder': path.resolve(
                __dirname, 'js/imcms/builders/imcms_admin_panel_builder.js'
            ),
            'imcms-site-specific': path.resolve(
                __dirname, 'js/imcms/builders/imcms_site_specific_admin_panel.js'
            ),
            'imcms-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_rest_api.js'
            ),
            'imcms-image-files-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_image_files_rest_api.js'
            ),
            'imcms-image-folders-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_image_folders_rest_api.js'
            ),
            'imcms-documents-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_documents_rest_api.js'
            ),
            'imcms-documents-search-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_documents_search_rest_api.js'
            ),
            'imcms-users-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_users_rest_api.js'
            ),
            'imcms-categories-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_categories_rest_api.js'
            ),
            'imcms-roles-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_roles_rest_api.js'
            ),
            'imcms-templates-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_templates_rest_api.js'
            ),
            'imcms-category-types-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_category_types_rest_api.js'
            ),
            'imcms-loops-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_loops_rest_api.js'
            ),
            'imcms-menus-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_menus_rest_api.js'
            ),
            'imcms-images-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_images_rest_api.js'
            ),
            'imcms-languages-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_languages_rest_api.js'
            ),
            'imcms-texts-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_texts_rest_api.js'
            ),
            'imcms-texts-history-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_texts_history_rest_api.js'
            ),
            'imcms-texts-validation-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_texts_validation_rest_api.js'
            ),
            'imcms-file-doc-files-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_file_doc_files_rest_api.js'
            ),
            'imcms-profiles-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_profiles_rest_api.js'
            ),
            'imcms-document-validation-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_document_validation_rest_api.js'
            ),
            'imcms-document-copy-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_document_copy_rest_api.js'
            ),
            'imcms-auth-providers-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_auth_providers_rest_api.js'
            ),
            'imcms-external-roles-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_external_roles_rest_api.js'
            ),
            'imcms-azure-roles-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_azure_roles_rest_api.js'
            ),
            'imcms-external-to-local-roles-links-rest-api': path.resolve(
                __dirname, 'js/imcms/rest/imcms_external_to_local_roles_links_rest_api.js'
            )
        }
    }
};
