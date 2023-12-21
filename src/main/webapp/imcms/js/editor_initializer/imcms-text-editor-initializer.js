/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 *
 */
define('imcms-text-editor-initializer',
    [
        'jquery', 'imcms-text-editor-utils', 'imcms-tinymce-text-editor', 'imcms-text-editor', 'imcms-uuid-generator',
        'imcms-text-editor-types', 'imcms-modal-window-builder'
    ],
    ($, textEditorUtils, tinyMceTextEditor, textEditor, uuidGenerator, editorTypes, modalWindowBuilder) => {

        function toggleFocusEditArea(e) {
            const $activeTextArea = $(textEditorUtils.ACTIVE_EDIT_AREA_CLASS_$);

            if (!$activeTextArea.length) return;

            const $target = $(e.target);

            if ($target.closest(textEditorUtils.ACTIVE_EDIT_AREA_CLASS_$).length) return;
            if ($target.closest('.text-history').length) return;
            if ($target.closest('.imcms-editor-toolbar').length) return;

            $activeTextArea.removeClass(textEditorUtils.ACTIVE_EDIT_AREA_CLASS)
                .find('.mce-edit-focus')
                .removeClass('mce-edit-focus');

	        const $controlsSmall = $activeTextArea.find(".imcms-editor-area__control-wrap--small");
	        if ($controlsSmall.length) {
		        $activeTextArea.find(".imcms-editor-area__control-wrap--small").show();
		        $activeTextArea.find(".imcms-editor-area__text-label").hide();
	        }

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
            const $textEditor = $(this);
            const editorData = $textEditor.data();

            if (editorData.external) {
                const confirmMessage = `This text (#${editorData.index})`
                    + ` is edited on page ${editorData.external}\nGo to the page?`;

                $textEditor.parent()
                    .find('.imcms-editor-area__control-wrap')
                    .click(() => {
                        modalWindowBuilder.buildConfirmWindow(confirmMessage, () => {
                            const url = window.location.origin + window.location.pathname
                                + '?meta_id=' + editorData.external;

                            window.open(url, '_blank');
                        });
                    });

                $textEditor.attr('disabled', 'disabled');
                textEditorUtils.showControls($textEditor);
                return;
            }

            const type = $textEditor.attr('data-type'); // exactly through the attr, '.data' can cache value

            const toolbarId = uuidGenerator.generateUUID();
            const textAreaId = uuidGenerator.generateUUID();

            $textEditor.attr('id', textAreaId)
                .closest('.imcms-editor-area--text')
                .find('.imcms-editor-area__text-toolbar')
                .attr('id', toolbarId);

	        $textEditor.on("click focus", toggleFocusEditArea);

            const editor = initEditor(type, $textEditor);

            if (opts && opts.autoFocus) {
                editor.then(editor => {
                    editor[0].focus();
                    // Uncover timynce editor
                    // fixme: rewrite
                    if(!type || editorTypes.editor === type) editor[0].buttons.fullscreen.onclick();
                });
            }
        }

        return {
            initEditor: (opts) => {
                $(document).click(toggleFocusEditArea);

                $('.imcms-editor-content--text').each(function () {
                    initTextEditor.call(this, opts);
                });
            }
        };
    }
);
