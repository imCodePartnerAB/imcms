/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-tinymce-text-editor',
    [
        'tinyMCE', 'imcms-uuid-generator', 'jquery', 'imcms', 'imcms-text-editor-utils', 'imcms-text-history-plugin',
        'imcms-text-validation-plugin', 'imcms-image-in-text-plugin', 'imcms-text-discard-changes-plugin',
        'imcms-text-full-screen-plugin', 'imcms-switch-to-plain-text', 'imcms-switch-to-html-mode',
        'imcms-switch-to-text-editor'
    ],
    function (
        tinyMCE, uuidGenerator, $, imcms, textEditorUtils, textHistory, textValidation, imageInText,
        discardChangesPlugin, fullScreenPlugin, switchToPlainTextPlugin, switchToHtmlModePlugin,
        switchToTextEditorPlugin
    ) {
        var sourceCodePlugin = 'code';
        var fontPlugins = ['bold', 'italic', 'underline'].join(' ');
        var listsPlugins = ['bullist', 'numlist'].join(' ');
        var horizontalLinePlugin = 'hr';
        var textAlignPlugins = ['alignleft', 'aligncenter', 'alignright', 'alignjustify'].join(' ');
        var specialInsertsPlugins = ['link', imageInText.pluginName].join(' ');
        var customImcmsTextPlugins = [textHistory.pluginName, textValidation.pluginName].join(' ');
        var fullscreenPlugin = fullScreenPlugin.pluginName;
        var saveAndDiscardPlugins = ['save', discardChangesPlugin.pluginName].join(' ');
        var switchModePlugins = [
            switchToPlainTextPlugin.pluginName,
            switchToHtmlModePlugin.pluginName,
            switchToTextEditorPlugin.pluginName
        ].join(' ');

        var toolbar = [
            sourceCodePlugin,
            fontPlugins,
            listsPlugins,
            horizontalLinePlugin,
            textAlignPlugins,
            specialInsertsPlugins,
            customImcmsTextPlugins,
            fullscreenPlugin,
            saveAndDiscardPlugins,
            switchModePlugins
        ].join(' | ');

        var inlineEditorConfig = {
            skin_url: imcms.contextPath + '/js/libs/tinymce/skins/white',
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
            forced_root_block: false,
            init_instance_callback: prepareEditor,
            save_onsavecallback: textEditorUtils.saveContent,
            setup: function (editor) {
                textHistory.initTextHistory(editor);
                textValidation.initTextValidation(editor);
                imageInText.initImageInText(editor);
                discardChangesPlugin.initDiscardChanges(editor);
                switchToPlainTextPlugin.initSwitchToPlainText(editor);
                switchToHtmlModePlugin.initSwitchToHtmlMode(editor);
                switchToTextEditorPlugin.initSwitchToTextEditor(editor);
            },
            valid_elements: '*[*]',
            plugins: ['autolink link lists hr code ' + fullScreenPlugin.pluginName + ' save'],
            toolbar: toolbar
        };

        fullScreenPlugin.initFullScreen();

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
        }

        function setEditorFocusOnEditControlClick(editor) {
            editor.$().parents('.imcms-editor-area--text')
                .find('.imcms-control--text')
                .on('click', function () {
                    editor.focus();
                });
        }

        function initSaveContentConfirmation(editor) {
            editor.on('blur', function (e) {
                textEditorUtils.onEditorBlur(e.target)
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
            init: function ($textEditor) {
                var toolbarId = uuidGenerator.generateUUID();
                var textAreaId = uuidGenerator.generateUUID();

                $textEditor.attr('id', textAreaId)
                    .closest('.imcms-editor-area--text')
                    .find('.imcms-editor-area__text-toolbar')
                    .attr('id', toolbarId);

                var editorConfig = $.extend({
                    selector: '#' + textAreaId,
                    fixed_toolbar_container: '#' + toolbarId
                }, inlineEditorConfig);

                // 4.5.7 the last version compatible with IE 10
                if (imcms.browserInfo.isIE10) {
                    tinyMCE.baseURL = 'https://cdnjs.cloudflare.com/ajax/libs/tinymce/4.5.7';
                    tinyMCE.suffix = '.min';
                }

                tinyMCE.init(editorConfig);
            }
        };
    }
);
