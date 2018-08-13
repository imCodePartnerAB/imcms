/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 *
 * @namespace tinyMCE.activeEditor.getContent
 */
Imcms.define("imcms-text-editor-initializer",
    [
        "tinyMCE", "imcms-uuid-generator", "jquery", "imcms", "imcms-texts-rest-api", "imcms-events",
        "imcms-text-history-plugin", "imcms-text-validation-plugin", "imcms-image-in-text-plugin",
        "imcms-modal-window-builder", "imcms-text-full-screen-plugin", "imcms-text-discard-changes-plugin",
        'imcms-bem-builder'
    ],
    function (tinyMCE, uuidGenerator, $, imcms, textsRestApi, events, textHistory, textValidation, imageInText,
              modalWindowBuilder, fullScreenPlugin, discardChangesPlugin, BEM) {

        var ACTIVE_EDIT_AREA_CLASS = "imcms-editor-area--active";

        function saveContent(editor) {
            var textDTO = $(editor.$()).data();
            textDTO.text = editor.getContent();

            if (textDTO.type === "HTML" || textDTO.type === "CLEAN_HTML") {
                if (textDTO.text.startsWith("<p>") && textDTO.text.endsWith("</p>")) {
                    textDTO.text = textDTO.text.substring(3, textDTO.text.length - 4);
                }
                textDTO.text = textDTO.text.replace(/&lt;/g, "<").replace(/&gt;/g, ">");
            }

            textsRestApi.create(textDTO).success(function () {
                events.trigger("imcms-version-modified");
                editor.startContent = editor.getContent();
                editor.setDirty(false);
            });
        }

        fullScreenPlugin.initFullScreen();

        var commonConfig = {
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
            save_onsavecallback: saveContent,
            setup: function (editor) {
                textHistory.initTextHistory(editor);
                textValidation.initTextValidation(editor);
                imageInText.initImageInText(editor);
                discardChangesPlugin.initDiscardChanges(editor);
            }
        };

        var inlineEditorConfig = $.extend({
            valid_elements: '*[*]',
            plugins: ['autolink link lists hr code ' + fullScreenPlugin.pluginName + ' save'],
            toolbar: 'code | bold italic underline | bullist numlist | hr |'
                + ' alignleft aligncenter alignright alignjustify | link ' + imageInText.pluginName + ' | '
                + textHistory.pluginName + ' ' + textValidation.pluginName + ' |' + ' ' + fullScreenPlugin.pluginName
                + ' | save ' + discardChangesPlugin.pluginName
        }, commonConfig);

        var textFormatEditorConfig = $.extend({
            paste_as_text: true,
            plugins: [fullScreenPlugin.pluginName + ' save paste'],
            toolbar: textHistory.pluginName + ' ' + textValidation.pluginName + ' | ' + fullScreenPlugin.pluginName
                + ' | save ' + discardChangesPlugin.pluginName
        }, commonConfig);

        var htmlFormatEditorConfig = $.extend({
            paste_as_text: true,
            plugins: [fullScreenPlugin.pluginName + ' save paste'],
            toolbar: textHistory.pluginName + ' ' + textValidation.pluginName + ' | ' + fullScreenPlugin.pluginName
                + ' | save ' + discardChangesPlugin.pluginName
        }, commonConfig);

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
        }

        function onEditorFocus() {
            $('.' + ACTIVE_EDIT_AREA_CLASS).removeClass(ACTIVE_EDIT_AREA_CLASS)
                .find('.mce-edit-focus')
                .removeClass('mce-edit-focus');

            $(this).closest(".imcms-editor-area--text").addClass(ACTIVE_EDIT_AREA_CLASS)
        }

        function setEditorFocus($editor) {
            $editor.focus(onEditorFocus)
        }

        function setEditorFocusOnEditControlClick(editor) {
            editor.$()
                .parents('.imcms-editor-area--text')
                .find('.imcms-control--text')
                .on('click', function () {
                    editor.focus();
                });
        }

        function showEditButton($editor) {
            $editor.parents(".imcms-editor-area--text")
                .find(".imcms-control--edit.imcms-control--text")
                .css("display", "block");
        }

        /** @function event.target.isDirty */
        function onEditorBlur(event) {
            if (!blurEnabled) {
                return;
            }

            if (!event.target.isDirty()) {
                return;
            }

            modalWindowBuilder.buildModalWindow("Save changes?", function (saveChanges) {
                if (saveChanges) {
                    saveContent(event.target);
                }
            })
        }

        var blurEnabled = true;

        function initSaveContentConfirmation(editor) {
            events.on("disable text editor blur", function () {
                blurEnabled = false;
            });
            events.on("enable text editor blur", function () {
                blurEnabled = true;
            });
            editor.on('blur', onEditorBlur);
        }

        function prepareEditor(editor) {
            clearSaveBtnText(editor);
            setEditorFocus($(editor.$()));
            setEditorFocusOnEditControlClick(editor);
            showEditButton($(editor.$()));
            initSaveContentConfirmation(editor);
        }

        function toggleFocusEditArea(e) {
            var $activeTextArea = $("." + ACTIVE_EDIT_AREA_CLASS);

            if (!$activeTextArea.length) return;

            var $target = $(e.target);
            if ($target.closest("." + ACTIVE_EDIT_AREA_CLASS).length) return;

            $activeTextArea.removeClass(ACTIVE_EDIT_AREA_CLASS)
                .find('.mce-edit-focus')
                .removeClass('mce-edit-focus');
        }

        $(document).click(toggleFocusEditArea);

        function initPlainTextEditor($textEditor) {

            function setContentEditable($textEditor) {
                $textEditor.attr('contenteditable', 'true')
                    .attr('spellcheck', 'false')
            }

            function focusEditorOnControlClick($textEditor) {
                $textEditor.parent()
                    .find('.imcms-editor-area__control-wrap')
                    .click(function () {
                        $textEditor[0].focus();
                    })
            }

            function buildTextHistoryButton($textEditor) {
                return new BEM({
                    block: 'text-history-button',
                    elements: {
                        'icon': $('<div>', {
                            'class': 'text-toolbar__icon'
                        })
                    }
                }).buildBlockStructure('<div>', {
                    class: 'text-toolbar__button',
                    click: function () {
                        alert('text history')
                    }
                })
            }

            function buildToolbar($textEditor) {
                var $toolbarWrapper = $('<div>', {
                    'class': 'text-toolbar-wrapper'
                });

                $toolbarWrapper.append([
                    buildTextHistoryButton($textEditor)
                ]);

                $textEditor.parent()
                    .find('.imcms-editor-area__text-toolbar')
                    .append($toolbarWrapper);
            }

            setContentEditable($textEditor);
            focusEditorOnControlClick($textEditor);
            setEditorFocus($textEditor);
            buildToolbar($textEditor);
            showEditButton($textEditor);
        }

        function initHtmlEditor() {
            // todo: implement
        }

        function initTinyMCEEditor() {

        }

        function initTextEditor() {
            var $textEditor = $(this);
            var type = $textEditor.data("type");

            var config;

            switch (type) {
                case 'TEXT':
                    return initPlainTextEditor($textEditor);
                case 'HTML':
                case 'CLEAN_HTML':
                    config = htmlFormatEditorConfig; // return initHtmlEditor();
                    break;
                default:
                    config = inlineEditorConfig; // return initTinyMCEEditor();
            }

            var toolbarId = uuidGenerator.generateUUID();
            var textAreaId = uuidGenerator.generateUUID();

            $textEditor.attr("id", textAreaId)
                .closest(".imcms-editor-area--text")
                .find(".imcms-editor-area__text-toolbar")
                .attr("id", toolbarId);

            var editorConfig = $.extend({
                selector: "#" + textAreaId,
                fixed_toolbar_container: "#" + toolbarId
            }, config);

            // 4.5.7 the last version compatible with IE 10
            if (Imcms.browserInfo.isIE10) {
                tinyMCE.baseURL = "https://cdnjs.cloudflare.com/ajax/libs/tinymce/4.5.7";
                tinyMCE.suffix = ".min";
            }

            tinyMCE.init(editorConfig);
        }

        return {
            initEditor: function () {
                $(".imcms-editor-content--text").each(initTextEditor);
            }
        };
    }
);
