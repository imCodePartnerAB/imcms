/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 *
 * @namespace tinyMCE.activeEditor
 * @namespace tinyMCE.activeEditor.getContent
 */
Imcms.define("imcms-text-editor-initializer",
    [
        "tinyMCE", "imcms-uuid-generator", "jquery", "imcms", "imcms-texts-rest-api", "imcms-events",
        "imcms-text-history-window-builder", "imcms-texts-validation-rest-api", "imcms-text-validation-result-builder"
    ],
    function (tinyMCE, uuidGenerator, $, imcms, textsRestApi, events, textHistoryBuilder, textValidationAPI,
              textValidationBuilder) {
        var ACTIVE_EDIT_AREA_CLASS = "imcms-editor-area--active";

        function saveContent(editor) {
            var textDTO = $(editor.$()).data();
            textDTO.text = editor.getContent();

            textsRestApi.create(textDTO).success(function () { // todo: unfocus current editor, maybe
                events.trigger("imcms-version-modified");
            });
        }

        function addTextHistoryButton(editor) {

            editor.addButton('text_history', {
                icon: 'imcms-text-history-icon',
                tooltip: 'Show text history',
                onclick: function () {
                    var textDTO = $(this.$el).parents(".imcms-editor-area--text")
                        .find(".imcms-editor-content--text")
                        .data();

                    textHistoryBuilder.buildTextHistory(textDTO);
                }
            });
        }

        function addW3cValidationButton(editor) {

            editor.addButton('w3c_validation', {
                icon: 'imcms-w3c-text-validation-icon',
                tooltip: 'Validate Content over W3C',
                onclick: function () {
                    var content = tinyMCE.activeEditor.getContent();
                    var $icon = $(this.$el).find(".mce-ico")
                        .removeAttr("class")
                        .attr("class", "mce-ico mce-i-imcms-w3c-text-validation-processing-icon");

                    textValidationAPI.validate({content: content}).done(function (validationResult) {
                        var iconClass = validationResult.valid
                            ? "mce-i-imcms-w3c-text-validation-valid-icon"
                            : "mce-i-imcms-w3c-text-validation-invalid-icon";

                        $icon.removeAttr("class").attr("class", "mce-ico " + iconClass);

                        if (!validationResult.valid) {
                            textValidationBuilder.buildTextValidationFailWindow(validationResult);
                        }
                    });
                }
            });

        }

        function addImageEditorButton(editor) {

            editor.addButton('image_editor', {
                icon: 'imcms-image--in-text-editor-icon',
                tooltip: 'Add Image',
                onclick: function () {
                    // todo: implement here
                }
            });

        }

        var inlineEditorConfig = {
            skin_url: imcms.contextPath + '/js/libs/tinymce/skins/white',
            cache_suffix: '?v=0.0.1',
            branding: false,
            skin: 'white',
            inline: true,
            toolbar_items_size: 'small',
            content_css: imcms.contextPath + '/css_new/imcms-text_editor.css',
            plugins: ['autolink link lists hr code fullscreen save table contextmenu'],
            toolbar: 'code | bold italic underline | bullist numlist | hr |' +
            ' alignleft aligncenter alignright alignjustify | link image_editor | text_history w3c_validation |' +
            ' fullscreen | save',
            menubar: false,
            statusbar: false,
            init_instance_callback: prepareEditor,
            save_onsavecallback: saveContent,
            setup: function (editor) {
                addTextHistoryButton(editor);
                addW3cValidationButton(editor);
                addImageEditorButton(editor);
            }
        };

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
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
            $editor.parent(".imcms-editor-area--text")
                .find(".imcms-control--edit.imcms-control--text")
                .css("display", "block");
        }

        function prepareEditor(editor) {
            clearSaveBtnText(editor);
            setEditorFocusOnEditControlClick(editor);
            showEditButton(editor.$());
        }

        function toggleFocusEditArea(e) {
            var $activeTextArea = $("." + ACTIVE_EDIT_AREA_CLASS);

            if ($activeTextArea.find(".mce-edit-focus").length) {
                return;
            }

            var $closestTextArea = $(e.target).closest(".imcms-editor-area--text");
            $activeTextArea.removeClass(ACTIVE_EDIT_AREA_CLASS);

            if ($closestTextArea.length && $closestTextArea.find(".mce-edit-focus").length) {
                $closestTextArea.addClass(ACTIVE_EDIT_AREA_CLASS);
            }
        }

        $(document).click(toggleFocusEditArea);

        function initTextEditor() {
            var toolbarId = uuidGenerator.generateUUID();
            var textAreaId = uuidGenerator.generateUUID();

            $(this).attr("id", textAreaId)
                .closest(".imcms-editor-area--text")
                .find(".imcms-editor-area__text-toolbar")
                .attr("id", toolbarId);

            var config = $.extend({
                selector: "#" + textAreaId,
                fixed_toolbar_container: "#" + toolbarId
            }, inlineEditorConfig);

            // 4.5.7 the last version compatible with IE 10
            if (Imcms.browserInfo.isIE10) {
                tinyMCE.baseURL = "https://cdnjs.cloudflare.com/ajax/libs/tinymce/4.5.7";
                tinyMCE.suffix = ".min";
            }

            tinyMCE.init(config);
        }

        return {
            initEditor: function () {
                $(".imcms-editor-content--text").each(initTextEditor);
            }
        };
    }
);
