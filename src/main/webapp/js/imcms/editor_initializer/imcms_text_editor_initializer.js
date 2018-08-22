/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 *
 */
Imcms.define('imcms-text-editor-initializer',
    [
        'jquery', 'imcms-text-editor-utils', 'imcms-tinymce-text-editor', 'imcms-text-editor', 'imcms-uuid-generator',
        'imcms-text-editor-types'
    ],
    function ($, textEditorUtils, tinyMceTextEditor, textEditor, uuidGenerator, editorTypes) {

        function toggleFocusEditArea(e) {
            var $activeTextArea = $(textEditorUtils.ACTIVE_EDIT_AREA_CLASS_$);

            if (!$activeTextArea.length) return;

            var $target = $(e.target);

            if ($target.closest(textEditorUtils.ACTIVE_EDIT_AREA_CLASS_$).length) return;
            if ($target.closest('.text-history').length) return;

            $activeTextArea.removeClass(textEditorUtils.ACTIVE_EDIT_AREA_CLASS)
                .find('.mce-edit-focus')
                .removeClass('mce-edit-focus');

            textEditorUtils.setActiveTextEditor(false);
        }

        function initEditor(type, $textEditor) {
            switch (type) {
                case editorTypes.text:
                    return textEditor.initPlainTextEditor($textEditor);
                case editorTypes.textFromEditor:
                    return textEditor.initTextFromEditor($textEditor);
                case editorTypes.html:
                    return textEditor.initHtmlEditor($textEditor);
                case editorTypes.htmlFromEditor:
                    return textEditor.initHtmlFromEditor($textEditor);
                case editorTypes.editor:
                default:
                    return tinyMceTextEditor.init($textEditor);
            }
        }

        function initTextEditor(opts) {
            var $textEditor = $(this);
            var type = $textEditor.attr('data-type');

            var toolbarId = uuidGenerator.generateUUID();
            var textAreaId = uuidGenerator.generateUUID();

            $textEditor.attr('id', textAreaId)
                .closest('.imcms-editor-area--text')
                .find('.imcms-editor-area__text-toolbar')
                .attr('id', toolbarId);

            var editor = initEditor(type, $textEditor);

            if (opts && opts.autoFocus) {
                editor.then(function (editor) {
                    editor[0].focus();
                })
            }
        }

        return {
            initEditor: function (opts) {
                $(document).click(toggleFocusEditArea);

                $('.imcms-editor-content--text').each(function () {
                    initTextEditor.call(this, opts)
                });
            }
        };
    }
);
