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

        const sourceCodePlugin = 'code';
        const fontPlugins = ['bold', 'italic', 'underline'].join(' ');
        const listsPlugins = ['bullist', 'numlist'].join(' ');
        const horizontalLinePlugin = 'hr';
        const textAlignPlugins = ['alignleft', 'aligncenter', 'alignright', 'alignjustify'].join(' ');
        const specialInsertsPlugins = ['link', imageInText.pluginName].join(' ');
        const customImcmsTextPlugins = [textHistory.pluginName, textValidation.pluginName].join(' ');
        const fullscreenPlugin = fullScreenPlugin.pluginName;
        const saveAndDiscardPlugins = ['save', discardChangesPlugin.pluginName].join(' ');
        const switchModePlugins = [
            switchToPlainTextPlugin.pluginName,
            switchToHtmlModePlugin.pluginName,
            switchToTextEditorPlugin.pluginName
        ].join(' ');
        const htmlFilteringPolicyPlugins = htmlFilteringPolicyPlugin.pluginName;

        const toolbar = [
            sourceCodePlugin,
            fontPlugins,
            listsPlugins,
            horizontalLinePlugin,
            textAlignPlugins,
            specialInsertsPlugins,
            customImcmsTextPlugins,
            fullscreenPlugin,
            saveAndDiscardPlugins,
            switchModePlugins,
            htmlFilteringPolicyPlugins
        ].join(' | ');

        const inlineEditorConfig = {
            skin_url: imcms.contextPath + '/css/tinymce/skins/white',
            convert_urls: false,
            cache_suffix: '?v=0.0.1',
            branding: false,
            skin: 'white',
            inline: true,
            inline_boundaries: false,
            toolbar_items_size: 'small',
            content_css: imcms.contextPath + '/css/imcms-text_editor.css',
            menubar: false,
            statusbar: false,
            force_br_newlines: false,
            force_p_newlines: true,
            forced_root_block: false,
            init_instance_callback: prepareEditor,
            save_onsavecallback: textEditorUtils.saveContent,
            setup: editor => {
                textHistory.initTextHistory(editor);
                textValidation.initTextValidation(editor);
                fullScreenPlugin.initFullScreen(editor);
                imageInText.initImageInText(editor);
                discardChangesPlugin.initDiscardChanges(editor);
                switchToPlainTextPlugin.initSwitchToPlainText(editor);
                switchToHtmlModePlugin.initSwitchToHtmlMode(editor);
                switchToTextEditorPlugin.initSwitchToTextEditor(editor);
                htmlFilteringPolicyPlugin.initHtmlFilteringPolicy(editor);
            },
            valid_elements: '*[*]',
            plugins: ['autolink link lists hr code ' + fullScreenPlugin.pluginName + ' save'],
            toolbar: toolbar
        };

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
        }

        function setEditorFocusOnEditControlClick(editor) {
            editor.$().parents('.imcms-editor-area--text')
                .find('.imcms-control--text')
                .on('click', () => {
                    editor.focus();
                });
        }

        function initSaveContentConfirmation(editor) {
            editor.on('blur', () => {
                textEditorUtils.onEditorBlur(editor);
            });
        }

        function prepareEditor(editor) {
            clearSaveBtnText(editor);
            textEditorUtils.setEditorFocus(editor);
            setEditorFocusOnEditControlClick(editor);
            textEditorUtils.showEditButton($(editor.$()));
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
