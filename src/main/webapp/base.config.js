/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.08.18
 */

const pathResolver = require('./dependency_path_resolver');

module.exports = {
    entry: {
        imcms_admin: './imcms/js/imcms_admin.js',
        imcms_start: './imcms/js/imcms_start.js',
        imcms_admin_manager_start: './imcms/js/starters/imcms_admin_manager_start.js',
        imcms_content_manager_start: './imcms/js/starters/imcms_content_manager_start.js',
        imcms_doc_info_edit_start: './imcms/js/starters/imcms_doc_info_edit_start.js',
        imcms_doc_manager_start: './imcms/js/starters/imcms_doc_manager_start.js',
        imcms_image_edit_start: './imcms/js/starters/imcms_image_edit_start.js',
        imcms_loop_edit_start: './imcms/js/starters/imcms_loop_edit_start.js',
        imcms_menu_edit_start: './imcms/js/starters/imcms_menu_edit_start.js',
        imcms_text_edit_start: './imcms/js/starters/imcms_text_edit_start.js',
        imcms_login_start: './imcms/js/starters/imcms_login_start.js',
        userCreate: './imcms/js/new_admin/userCreate.js',
        userEditorNew: './imcms/js/new_admin/userEditorNew.js',
        userEditorOld: './imcms/js/old_admin/userEditorOld.js'
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
        alias: pathResolver.resolvePaths('./imcms/js', {
                '': ['imcms'],
                events: 'imcms-events',
                streams: 'imcms-streams',
                i18n: 'imcms-i18n-texts',
                components: {
                    text_editor: [
                        'imcms-image-in-text-plugin',
                        'imcms-text-history-plugin',
                        'imcms-text-validation-plugin',
                        'imcms-text-full-screen-plugin',
                        'imcms-text-discard-changes-plugin',
                        'imcms-text-editor-utils',
                        'imcms-text-editor-toolbar-button-builder',
                        'imcms-tinymce-text-editor',
                        'imcms-text-editor',
                        'imcms-text-editor-types',
                        'imcms-switch-to-plain-text-plugin',
                        'imcms-switch-to-html-mode-plugin',
                        'imcms-switch-to-text-editor-plugin',
                        'imcms-html-filtering-policy-plugin',
                        'imcms-html-filtering-policies',
                        'imcms-text-editor-initializer',
                    ],
                    '': [
                        'imcms-logger',
                        'imcms-authentication',
                        'imcms-document-permission-types',
                        'imcms-document-types',
                        'imcms-document-status',
                        'imcms-calendar',
                        'imcms-date-picker',
                        'imcms-time-picker',
                        'imcms-uuid-generator',
                        'imcms-validator',
                        'imcms-jquery-element-reload',
                        'imcms-jquery-string-selector',
                        'imcms-dom-attributes-extractor',
	                    'imcms-drag-and-scroll',
	                    'imcms-formatters',
                        'imcms-numeric-limiter',
                        'imcms-session-timeout-management',
	                    'imcms-templates-css-versions',
                        'imcms-date-time-validator',
                        'imcms-cookies',
                        'imcms-displacing-array',
                        'imcms-window-keys-controller',
                        'date-format',
                        'imcms-jquery-tag-replacer',
                        'check-browser',
                        'css-utils',
                        'js-utils',
                    ]
                },
                editor_initializer: {
                    '': [
                        'imcms-text-editor-initializer',
                        'imcms-editors-initializer',
                        'imcms-image-editor-initializer',
                        'imcms-loop-editor-initializer',
                        'imcms-menu-editor-initializer',
                        'imcms-editor-labels-initializer',
                        'imcms-editor-init-strategy',
                    ],
                    imcms_editors_init_data: [
                        'imcms-image-editor-init-data',
                        'imcms-menu-editor-init-data',
                        'imcms-loop-editor-init-data',
                    ]
                },
                builders: {
                    components: {
                        image_editor: [
                            'imcms-preview-image-area',
                            'imcms-originally-area',
                            'imcms-originally-image',
                            'imcms-image-rotate',
                            'imcms-image-resize',
                            'imcms-image-edit-size-controls',
                            'imcms-image-cropper',
                            'imcms-image-crop-angles',
                            'imcms-origin-image-width-block',
                            'imcms-origin-image-height-block',
                            'imcms-cropping-area',
                            'imcms-toolbar-view-builder',
                            'imcms-cropping-angle',
                            'imcms-crop-coords-controllers',
                            'imcms-image-locker-button',
                            'imcms-image-active-tab',
                            'imcms-image-zoom',
                        ],
                        page_info_tabs: [
                            'imcms-page-info-tabs-builder',
                            'imcms-appearance-tab-builder',
	                        'imcms-metadata-tab-builder',
                            'imcms-life-cycle-tab-builder',
                            'imcms-templates-tab-builder',
                            'imcms-file-tab-builder',
                            'imcms-url-tab-builder',
                            'imcms-keywords-tab-builder',
                            'imcms-categories-tab-builder',
                            'imcms-access-tab-builder',
                            'imcms-permissions-tab-builder',
                            'imcms-status-tab-builder',
                            'imcms-cache-tab-builder',
                            'imcms-properties-tab-builder',
                            'imcms-all-data-tab-builder'
                        ],
                        super_admin_tabs: [
                            'imcms-super-admin-tabs-builder',
                            'imcms-users-tab-builder',
                            'imcms-roles-tab-builder',
                            'imcms-ip-access-tab-builder',
                            'imcms-doc-versions-tab-builder',
                            'imcms-delete-docs-tab-builder',
                            'imcms-files-tab-builder',
	                        'imcms-templates-css-tab-builder',
                            'imcms-link-validator-tab-builder',
                            'imcms-images-tab-builder',
                            'imcms-categories-admin-tab-builder',
                            'imcms-profiles-tab-builder',
                            'imcms-system-properties-tab-builder',
                            'imcms-temporal-data-tab-builder',
                            'imcms-data-version-admin-tab-builder',
	                        'imcms-import-documents-tab-builder',
                            'imcms-documentation-tab-builder'
                        ],
                        '': [
                            'imcms-title-text-builder',
                            'imcms-buttons-builder',
                            'imcms-flags-builder',
                            'imcms-checkboxes-builder',
                            'imcms-radio-buttons-builder',
                            'imcms-selects-builder',
                            'imcms-texts-builder',
                            'imcms-switch-builder',
                            'imcms-choose-image-builder',
                            'imcms-keywords-builder',
                            'imcms-date-time-builder',
                            'imcms-controls-builder',
                            'imcms-top-panel-visibility-initiator',
                            'imcms-admin-panel-settings-builder',
                            'imcms-admin-panel-state',
                            'imcms-window-tabs-builder',
                            'imcms-page-info-tab',
                            'imcms-window-tab-builder',
                            'imcms-super-admin-tab',
                            'imcms-role-editor',
                            'imcms-role-to-row-transformer',
                            'imcms-profile-editor',
                            'imcms-profile-to-row-transformer',
                            'imcms-file-to-row-transformer',
                            'imcms-document-transformer',
                            'imcms-file-editor',
                            'imcms-rule-editor',
                            'imcms-rule-to-row-transformer',
                            'imcms-field-wrapper',
                            'imcms-overlays-builder',
                        ]
                    },
                    windows: {
                        editors: {
                            image_editor: [
                                'imcms-image-editor-builder',
                                'imcms-image-editor-factory',
                                'imcms-image-editor-right-side-builder',
                                'imcms-image-editor-left-side-builder',
                                'imcms-image-editor-body-head-builder',
                            ],
                            '': [
                                'imcms-menu-editor-builder',
                                'imcms-document-editor-builder',
                                'imcms-loop-editor-builder',
                            ]
                        },
                        '': [
                            'imcms-window-builder',
                            'imcms-modal-window-builder',
                            'imcms-content-manager-builder',
                            'imcms-page-info-builder',
                            'imcms-document-type-select-window-builder',
                            'imcms-document-profile-select-window-builder',
                            'imcms-text-history-window-builder',
                            'imcms-text-validation-result-builder',
                            'imcms-super-admin-page-builder',
                        ]
                    },
                    '': [
                        'imcms-primitives-builder',
                        'imcms-components-builder',
                        'imcms-window-components-builder',
                        'imcms-bem-builder',
                        'imcms-image-content-builder',
                        'imcms-image-metadata-builder',
                        'imcms-admin-panel-builder',
                        'imcms-site-specific-admin-panel',
                        'imcms-standalone-editor-toolbar-builder'
                    ]
                },
                rest: [
                    'imcms-rest-api',
                    'imcms-image-files-rest-api',
                    'imcms-image-folders-rest-api',
                    'imcms-documents-rest-api',
                    'imcms-document-basket-rest-api',
                    'imcms-documents-search-rest-api',
                    'imcms-users-rest-api',
                    'imcms-categories-rest-api',
                    'imcms-roles-rest-api',
                    'imcms-ip-rules-rest-api',
                    'imcms-templates-rest-api',
	                'imcms-templates-css-rest-api',
                    'imcms-template-groups-rest-api',
                    'imcms-category-types-rest-api',
                    'imcms-loops-rest-api',
                    'imcms-menus-rest-api',
                    'imcms-images-rest-api',
                    'imcms-images-history-rest-api',
                    'imcms-languages-rest-api',
                    'imcms-texts-rest-api',
                    'imcms-texts-history-rest-api',
                    'imcms-texts-validation-rest-api',
                    'imcms-file-doc-files-rest-api',
                    'imcms-files-rest-api',
                    'imcms-profiles-rest-api',
                    'imcms-publish-document-rest-api',
                    'imcms-document-validation-rest-api',
                    'imcms-document-copy-rest-api',
                    'imcms-auth-providers-rest-api',
                    'imcms-external-roles-rest-api',
                    'imcms-azure-roles-rest-api',
	                'imcms-cgi-roles-rest-api',
	                'imcms-external-to-local-roles-links-rest-api',
                    'imcms-settings-rest-api',
                    'imcms-link-validator-rest-api',
                    'imcms-temporal-data-rest-api',
                    'imcms-version-data-rest-api',
                    'imcms-cache-document-rest-api',
                    'imcms-doc-view-request-api',
                    'imcms-user-properties-rest-api',
                    'imcms-all-data-document-rest-api',
	                'imcms-meta-tag-rest-api',
	                'imcms-import-documents-rest-api',
	                'imcms-basic-import-documents-info-rest-api',
	                'imcms-import-entity-reference-rest-api'
                ]
            }
        ),
    }
};
