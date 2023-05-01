/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.08.18
 */
define(
    'imcms-text-editor-utils',
    [
        'tinymce', 'imcms-texts-rest-api', 'imcms-events', 'jquery', 'imcms-modal-window-builder',
        'imcms-text-editor-types', 'imcms-html-filtering-policies', 'imcms-i18n-texts'
    ],
    function (tinyMCE, textsRestApi, events, $, modal, editorTypes, filteringPolicies, texts) {

        texts = texts.editors.text;

        const ACTIVE_EDIT_AREA_CLASS = 'imcms-editor-area--active';
        const ACTIVE_EDIT_AREA_CLASS_$ = '.' + ACTIVE_EDIT_AREA_CLASS;

        let activeEditor;

        let blurEnabled = true;

        events.on('disable text editor blur', () => {
            blurEnabled = false;
        });
        events.on('enable text editor blur', () => {
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

        function saveContent(editor, onSaved, withFilter) {
            const textDTO = $(editor.$()).data();
            textDTO.text = editor.getContent();

            if (!withFilter) {
                textDTO.htmlFilteringPolicy = filteringPolicies.allowAll;
            }

            switch (textDTO.type) {
                case editorTypes.html:
                case editorTypes.htmlFromEditor: {
                    textDTO.text = textDTO.text.replace(/&lt;/g, '<').replace(/&gt;/g, '>');
                    if (textDTO.type === editorTypes.htmlFromEditor) textDTO.type = editorTypes.html;
                    break;
                }
                case editorTypes.text:
                case editorTypes.textFromEditor: {
                    textDTO.text = textDTO.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
                    if (textDTO.type === editorTypes.textFromEditor) textDTO.type = editorTypes.text;
                }
            }

            textsRestApi.create(textDTO)
                .done(receivedTextDTO => {
                    events.trigger('imcms-version-modified');
                    editor.startContent = editor.getContent();
                    editor.setDirty(false);

                    onSaved && onSaved.call && onSaved.call(null, receivedTextDTO);
                })
                .fail(() => modal.buildErrorWindow(texts.error.createFailed));
        }

        function setEditorFocus(activeTextEditor) {
	        $(activeTextEditor.$()).on("click focus",function (e) {
	            const $this = $(this);
                setActiveTextEditor(activeTextEditor);

	            $(ACTIVE_EDIT_AREA_CLASS_$).find(".imcms-editor-area__text-label").hide();
	            $(ACTIVE_EDIT_AREA_CLASS_$).removeClass(ACTIVE_EDIT_AREA_CLASS)
		            .find('.mce-edit-focus')
		            .removeClass('mce-edit-focus');

	            const $parent = $this.closest(".imcms-editor-area--text");

	            $parent.addClass(ACTIVE_EDIT_AREA_CLASS);
	            $parent.find(".imcms-editor-area__control-wrap--small").hide();
	            $parent.find(".imcms-editor-area__text-label").show();
			});
        }

        function onEditorBlur(editor) {
            if (!blurEnabled || !editor.isDirty()) {
                return;
            }

            modal.buildModalWindow(texts.confirmSave, confirmed => {
                if (!confirmed) {
                    editor.setContent(editor.startContent);
                } else {
                    saveContent(editor);
                }
            });
        }

	    function showControls($editor) {
		    $editor.next()
			    .children()
			    .each((index, control) => {
				    $(control).css('display', 'inline-block');
			    })
	    }

        function filterContent(content, filteringPolicy, onSuccess, onFail) {
            const textDTO = {
                text: content,
                htmlFilteringPolicy: filteringPolicy,
            };

            textsRestApi.filter(textDTO)
                .done(receivedTextDTO => {
                    onSuccess && onSuccess(receivedTextDTO);
                })
                .fail(() => {
                    onFail && onFail();
                    modal.buildErrorWindow(texts.error.filterFailed)
                });
        }

        return {
            ACTIVE_EDIT_AREA_CLASS: ACTIVE_EDIT_AREA_CLASS,
            ACTIVE_EDIT_AREA_CLASS_$: ACTIVE_EDIT_AREA_CLASS_$,
            getActiveTextEditor: getActiveTextEditor,
            setActiveTextEditor: setActiveTextEditor,
            saveContent: saveContent,
            setEditorFocus: setEditorFocus,
            onEditorBlur: onEditorBlur,
	        showControls: showControls,
            filterContent,
        };
    }
);
