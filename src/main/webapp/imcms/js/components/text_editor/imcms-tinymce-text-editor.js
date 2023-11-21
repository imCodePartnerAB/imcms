/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
define(
    'imcms-tinymce-text-editor',
    [
        'tinymce', 'jquery', 'imcms', 'imcms-text-editor-utils', 'imcms-text-history-plugin',
        'imcms-text-validation-plugin', 'imcms-image-in-text-plugin', 'imcms-text-discard-changes-plugin',
        'imcms-text-full-screen-plugin', 'imcms-switch-to-plain-text-plugin', 'imcms-switch-to-html-mode-plugin',
        'imcms-switch-to-text-editor-plugin', 'imcms-html-filtering-policy-plugin'
    ],
    function (
        tinyMCE, $, imcms, textEditorUtils, textHistory, textValidation, imageInText, discardChangesPlugin,
        fullScreenPlugin, switchToPlainTextPlugin, switchToHtmlModePlugin, switchToTextEditorPlugin,
        htmlFilteringPolicyPlugin
    ) {

        require('tinymce/themes/modern/theme');
        require('tinymce/plugins/autolink');
        require('tinymce/plugins/link');
        require('tinymce/plugins/lists');
        require('tinymce/plugins/hr');
        require('tinymce/plugins/code');
        require('tinymce/plugins/save');
        require('tinymce/plugins/fullscreen');
        require('tinymce/plugins/paste');

        const sourceCodePlugin = 'code';
        const fontPlugins = ['bold', 'italic', 'underline', 'styleselect'].join(' ');
        const listsPlugins = ['bullist', 'numlist'].join(' ');
        const horizontalLinePlugin = 'hr';
        const textAlignPlugins = ['alignleft', 'aligncenter', 'alignright', 'alignjustify'].join(' ');
        const specialInsertsPlugins = ['link', imageInText.pluginName].join(' ');
        const customImcmsTextPlugins = [textHistory.pluginName, textValidation.pluginName].join(' ');
        const fullscreenPlugin = fullScreenPlugin.pluginName;
        const saveAndDiscardPlugins = ['save', discardChangesPlugin.pluginName].join(' ');
        const switchModePlugins = [
            switchToTextEditorPlugin.pluginName,
            switchToPlainTextPlugin.pluginName,
            switchToHtmlModePlugin.pluginName
        ].join(' ');
        const htmlFilteringPolicyPlugins = htmlFilteringPolicyPlugin.pluginName;

        const toolbar = [
            fullscreenPlugin,
            fontPlugins,
            listsPlugins,
            horizontalLinePlugin,
            textAlignPlugins,
            specialInsertsPlugins,
            switchModePlugins,
            sourceCodePlugin,
            customImcmsTextPlugins,
            htmlFilteringPolicyPlugins,
            saveAndDiscardPlugins
        ].join(' | ');

        const styleFormats = [
            {title: 'Heading 2', block: 'h2'},
            {title: 'Heading 3', block: 'h3'},
            {title: 'Heading 4', block: 'h4'},
            {title: 'Heading 5', block: 'h5'},
            {title: 'Heading 6', block: 'h6'},
            {title: 'Normal', block: 'p'},
        ];

        const inlineEditorConfig = {
            skin_url: imcms.contextPath + '/imcms/css/tinymce/skins/white',
            convert_urls: false,
            cache_suffix: '?v=0.0.1',
            branding: false,
            skin: 'white',
            inline: true,
            inline_boundaries: false,
            toolbar_items_size: 'small',
            content_css: imcms.contextPath + '/dist/imcms-text_editor.css',
            menubar: false,
            statusbar: false,
            force_br_newlines: false,
            force_p_newlines: true,
            forced_root_block: false,
            init_instance_callback: prepareEditor,
            save_onsavecallback: textEditorUtils.saveContent,
            setup: editor => {
                fullScreenPlugin.initFullScreen(editor);
                imageInText.initImageInText(editor);
                switchToTextEditorPlugin.initSwitchToTextEditor(editor);
                switchToPlainTextPlugin.initSwitchToPlainText(editor);
                switchToHtmlModePlugin.initSwitchToHtmlMode(editor);
                textHistory.initTextHistory(editor);
                textValidation.initTextValidation(editor);
                htmlFilteringPolicyPlugin.initHtmlFilteringPolicy(editor);
                discardChangesPlugin.initDiscardChanges(editor);
            },
            valid_elements: '*[*]',
            plugins: ['autolink link lists hr code ' + fullScreenPlugin.pluginName + ' save paste'],
            toolbar: toolbar,
            style_formats: styleFormats,
            paste_postprocess: (plugin, args) => htmlFilteringPolicyPlugin.buildPoliciesModal(args.node),
        };

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
        }

        function setEditorFocusOnEditControlClick(editor) {
            editor.$().parents('.imcms-editor-area--text')
                .find('.imcms-control--text')
                .on('click focus', () => {
                    editor.focus();
                });
        }

        function initSaveContentConfirmation(editor) {
            editor.on('blur', () => {
                if (!$('.imcms-modal-buttons-window').length) {
                    textEditorUtils.onEditorBlur(editor);
                }
            });
        }

        function prepareEditor(editor) {
            clearSaveBtnText(editor);
            textEditorUtils.setEditorFocus(editor);
            setEditorFocusOnEditControlClick(editor);
            textEditorUtils.showControls($(editor.$()));
            initSaveContentConfirmation(editor);
        }

        return {
            init: $textEditor => {
                const textAreaId = $textEditor.attr('id');
                const toolbarId = $textEditor.closest('.imcms-editor-area--text')
                    .find('.imcms-editor-area__text-toolbar')
                    .attr('id');

                const editorConfig = $.extend({
                    selector: '#' + textAreaId,
                    fixed_toolbar_container: '#' + toolbarId
                }, inlineEditorConfig);

                // 4.5.7 the last version compatible with IE 10
                if (imcms.browserInfo.isIE10) {
                    tinyMCE.baseURL = 'https://cdnjs.cloudflare.com/ajax/libs/tinymce/4.5.7';
                    tinyMCE.suffix = '.min';
                }

                return tinyMCE.init(editorConfig);
            }
        };
    }
);
