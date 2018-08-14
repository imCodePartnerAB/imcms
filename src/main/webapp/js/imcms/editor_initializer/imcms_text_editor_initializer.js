/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 *
 */
Imcms.define("imcms-text-editor-initializer",
    [
        "tinyMCE", "imcms-uuid-generator", "jquery", "imcms", "imcms-texts-rest-api", "imcms-events",
        "imcms-text-history-plugin", "imcms-text-validation-plugin", "imcms-image-in-text-plugin",
        "imcms-modal-window-builder", "imcms-text-full-screen-plugin", "imcms-text-discard-changes-plugin",
        'imcms-text-editor', 'imcms-text-editor-toolbar-button-builder'
    ],
    function (tinyMCE, uuidGenerator, $, imcms, textsRestApi, events, textHistory, textValidation, imageInText,
              modalWindowBuilder, fullScreenPlugin, discardChangesPlugin, textEditor, toolbarButtonBuilder) {

        var ACTIVE_EDIT_AREA_CLASS = "imcms-editor-area--active";

        function saveContent(editor) {
            var textDTO = $(editor.$()).data();
            textDTO.text = editor.getContent();

            if (textDTO.type === 'HTML' || textDTO.type === 'CLEAN_HTML') {
                textDTO.text = textDTO.text.replace(/&lt;/g, '<').replace(/&gt;/g, '>');

            } else if (textDTO.type === 'TEXT') {
                textDTO.text = textDTO.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
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

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
        }

        function setEditorFocus(activeTextEditor) {
            $(activeTextEditor.$()).focus(function () {
                textEditor.setActiveTextEditor(activeTextEditor);

                $('.' + ACTIVE_EDIT_AREA_CLASS).removeClass(ACTIVE_EDIT_AREA_CLASS)
                    .find('.mce-edit-focus')
                    .removeClass('mce-edit-focus');

                $(this).closest(".imcms-editor-area--text").addClass(ACTIVE_EDIT_AREA_CLASS);
            })
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

        events.on("disable text editor blur", function () {
            blurEnabled = false;
        });
        events.on("enable text editor blur", function () {
            blurEnabled = true;
        });

        function initSaveContentConfirmation(editor) {
            editor.on('blur', onEditorBlur);
        }

        function prepareEditor(editor) {
            clearSaveBtnText(editor);
            setEditorFocus(editor);
            setEditorFocusOnEditControlClick(editor);
            showEditButton($(editor.$()));
            initSaveContentConfirmation(editor);
        }

        function toggleFocusEditArea(e) {
            var $activeTextArea = $("." + ACTIVE_EDIT_AREA_CLASS);

            if (!$activeTextArea.length) return;

            var $target = $(e.target);
            if ($target.closest("." + ACTIVE_EDIT_AREA_CLASS).length) return;

            if ($target.closest('.text-history').length) return;

            $activeTextArea.removeClass(ACTIVE_EDIT_AREA_CLASS)
                .find('.mce-edit-focus')
                .removeClass('mce-edit-focus');

            textEditor.setActiveTextEditor(false);
        }

        $(document).click(toggleFocusEditArea);

        function initPlainTextEditor($textEditor) {

            function autoGrow() {
                var el = this;

                setTimeout(function () {
                    el.style.cssText = 'height:auto';
                    el.style.cssText = 'height:' + el.scrollHeight + 'px';
                });
            }

            autoGrow.call($textEditor[0]);
            $textEditor.on('keydown', autoGrow);

            function setRows($textEditor) {
                var rows = $textEditor.attr('data-rows');

                if (rows) {
                    $textEditor.attr('rows', rows);
                }
            }

            function focusEditorOnControlClick($textEditor) {
                $textEditor.parent()
                    .find('.imcms-editor-area__control-wrap')
                    .click(function () {
                        $textEditor[0].focus();
                    })
            }

            function buildSaveButton(activeTextEditor) {
                var onClick = function () {
                    if (!activeTextEditor.isDirty()) return;
                    saveContent(activeTextEditor);
                };

                var $saveButton = toolbarButtonBuilder.buildButton('text-editor-save-button', 'Save', onClick, true);

                activeTextEditor.$().on('change keyup paste', function () {
                    $saveButton.removeClass('text-toolbar__button--disabled');
                });

                return $saveButton
            }

            function buildToolbar(activeTextEditor) {
                var $textEditor = activeTextEditor.$();

                var $toolbarWrapper = $('<div>', {
                    'class': 'text-toolbar-wrapper'
                });

                $toolbarWrapper.append([
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(activeTextEditor),
                    discardChangesPlugin.buildPlainTextButton(activeTextEditor)
                ]);

                $textEditor.parent()
                    .find('.imcms-editor-area__text-toolbar')
                    .append($toolbarWrapper);
            }

            function wrapAsTextEditor($textEditor) {
                var TextEditor = function ($textEditor) {
                    this.$editor = $textEditor;
                    this.dirty = false;
                    this.startContent = $textEditor.val();

                    this.$editor.on('change keyup paste', function () {
                        this.dirty = true;
                    }.bind(this));
                };

                TextEditor.prototype = {
                    $: function () {
                        return this.$editor
                    },
                    setContent: function (content) {
                        this.$editor.val(content);
                        this.setDirty(true);
                    },
                    getContent: function () {
                        return this.$editor.val()
                    },
                    setDirty: function (isDirty) {
                        this.dirty = isDirty;

                        var $parent = this.$editor.parent();
                        var $discard = $parent.find('.text-editor-discard-changes-button');
                        var $save = $parent.find('.text-editor-save-button');

                        if (isDirty) {
                            $discard.removeClass('text-toolbar__button--disabled');
                            $save.removeClass('text-toolbar__button--disabled');

                        } else {
                            $discard.addClass('text-toolbar__button--disabled');
                            $save.addClass('text-toolbar__button--disabled');
                        }
                    },
                    isDirty: function () {
                        return this.dirty;
                    },
                    triggerBlur: function () {
                        onEditorBlur({target: this})
                    }
                };

                return new TextEditor($textEditor);
            }

            setRows($textEditor);

            var activeTextEditor = wrapAsTextEditor($textEditor);

            focusEditorOnControlClick($textEditor);
            setEditorFocus(activeTextEditor);
            buildToolbar(activeTextEditor);
            showEditButton($textEditor);
        }

        function initHtmlEditor($textEditor) {
            initPlainTextEditor($textEditor)
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
                    return initHtmlEditor($textEditor);
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
