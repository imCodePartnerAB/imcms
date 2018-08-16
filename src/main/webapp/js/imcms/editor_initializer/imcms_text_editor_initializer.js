/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 *
 */
Imcms.define('imcms-text-editor-initializer',
    ['jquery', 'imcms-text-editor-utils', 'imcms-tinymce-text-editor', 'imcms-text-editor', 'imcms-text-editor-types'],
    function ($, textEditorUtils, tinyMceTextEditor, textEditor, editorTypes) {

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

        function initTextEditor() {
            var $textEditor = $(this);
            var type = $textEditor.data('type');

            switch (type) {
                case editorTypes.text:
                    return textEditor.initPlainTextEditor($textEditor);
                case editorTypes.textFromEditor:
                    return textEditor.initTextFromEditor($textEditor);
                case editorTypes.html:
                case editorTypes.cleanHtml:
                    return textEditor.initHtmlEditor($textEditor);
                case editorTypes.htmlFromEditor:
                    return textEditor.initHtmlFromEditor($textEditor);
                default:
                    return tinyMceTextEditor.init($textEditor);
            }
        }

        return {
            initEditor: function () {
                $(document).click(toggleFocusEditArea);

                $('.imcms-editor-content--text').each(initTextEditor);
            }
        };
    }
);
