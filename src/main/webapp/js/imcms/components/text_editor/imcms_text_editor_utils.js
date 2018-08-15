/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.08.18
 */
Imcms.define(
    'imcms-text-editor-utils',
    [
        'tinyMCE', 'imcms-texts-rest-api', 'imcms-events', 'jquery', 'imcms-modal-window-builder',
        'imcms-text-editor-types'
    ],
    function (tinyMCE, textsRestApi, events, $, modalWindowBuilder, editorTypes) {

        var ACTIVE_EDIT_AREA_CLASS = 'imcms-editor-area--active';
        var ACTIVE_EDIT_AREA_CLASS_$ = '.' + ACTIVE_EDIT_AREA_CLASS;

        var activeEditor;

        var blurEnabled = true;

        events.on('disable text editor blur', function () {
            blurEnabled = false;
        });
        events.on('enable text editor blur', function () {
            blurEnabled = true;
        });

        function getActiveTextEditor() {
            return activeEditor || tinyMCE.activeEditor;
        }

        function setActiveTextEditor(activeTextEditor) {
            if (activeEditor && (activeTextEditor !== activeEditor) && activeEditor.triggerBlur) {
                activeEditor.triggerBlur();
            }

            activeEditor = activeTextEditor;
        }

        function saveContent(editor) {
            var textDTO = $(editor.$()).data();
            textDTO.text = editor.getContent();

            if ((textDTO.type === editorTypes.html) || (textDTO.type === editorTypes.cleanHtml)) {
                textDTO.text = textDTO.text.replace(/&lt;/g, '<').replace(/&gt;/g, '>');

            } else if (textDTO.type === editorTypes.text) {
                textDTO.text = textDTO.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
            }

            textsRestApi.create(textDTO).success(function () {
                events.trigger('imcms-version-modified');
                editor.startContent = editor.getContent();
                editor.setDirty(false);
            });
        }

        function setEditorFocus(activeTextEditor) {
            $(activeTextEditor.$()).focus(function () {
                setActiveTextEditor(activeTextEditor);

                $(ACTIVE_EDIT_AREA_CLASS_$).removeClass(ACTIVE_EDIT_AREA_CLASS)
                    .find('.mce-edit-focus')
                    .removeClass('mce-edit-focus');

                $(this).closest('.imcms-editor-area--text').addClass(ACTIVE_EDIT_AREA_CLASS);
            })
        }

        function onEditorBlur(editor) {
            if (!blurEnabled || !editor.isDirty()) {
                return;
            }

            modalWindowBuilder.buildConfirmWindow('Save changes?', function () {
                saveContent(editor);
            })
        }

        function showEditButton($editor) {
            $editor.parents('.imcms-editor-area--text')
                .find('.imcms-control--edit.imcms-control--text')
                .css('display', 'block');
        }

        return {
            ACTIVE_EDIT_AREA_CLASS: ACTIVE_EDIT_AREA_CLASS,
            ACTIVE_EDIT_AREA_CLASS_$: ACTIVE_EDIT_AREA_CLASS_$,
            getActiveTextEditor: getActiveTextEditor,
            setActiveTextEditor: setActiveTextEditor,
            saveContent: saveContent,
            setEditorFocus: setEditorFocus,
            onEditorBlur: onEditorBlur,
            showEditButton: showEditButton
        }
    }
);
