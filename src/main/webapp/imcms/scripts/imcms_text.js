/**
 * Text Editor
 *
 * Created by Shadowgun on 13.02.2015.
 * Upgraded by Serhii Maksymchuk in 2016-2017
 */
(function (Imcms) {
    var textApi = new Imcms.REST.API(Imcms.Linker.get("text")),
        validationTextApi = new Imcms.REST.API(Imcms.Linker.get("text.validate")),
        api = {
            get: textApi.get,
            update: textApi.post,
            validate: validationTextApi.post
        };

    Imcms.Text = {};
    Imcms.Text.Editor = function () {
        this.init();
    };

    Imcms.Text.Editor.prototype = {
        _textFrame: {},
        init: function () {
            this._textFrame = new Imcms.FrameBuilder().title("Text Editor");

            CKEDITOR.on('instanceReady', this._onInstanceReady.bind(this));
            CKEDITOR.on('instanceCreated', this._onCreated.bind(this));
            CKEDITOR.on("confirmChangesEvent", this._onConfirm.bind(this));
            CKEDITOR.on("validateText", this._onValidateText.bind(this));
            CKEDITOR.on("getTextHistory", this._onGetTextHistory.bind(this));

            $("[contenteditable='true']").each(this.addEditor.bind(this));
        },
        addEditor: function (position, element) {
            element = $(element);
            CKEDITOR.inline(element[0]);
            element.parents("a").attr("onclick", "return false;");

            $("<div>").insertAfter(element)
                .append(element)
                .css({overflow: "hidden"});

            var loopTitle = "";
            if (element.data("loopentryref")) {
                loopTitle = (element.data("loopentryref")).split("_");
                loopTitle = "L" + loopTitle[0] + "-E" + loopTitle[1] + "-T";
            }

            var title = loopTitle + element.data("no") + " | " + element.data("label"),
                currentFrame = this._textFrame
                    .click(function (e) {
                        currentFrame.hide();
                        element.focus();
                        element.trigger(e);
                        element.blur(function () {
                            currentFrame.show();
                        });
                    })
                    .build()
                    .attr("title", title.replace(/<(?:.|\n)*?>/gim, ''))
                    .insertBefore(element);

            if (element.data("showlabel")) {
                $("<div>").addClass("text-editor-label")
                    .html(element.data("label"))
                    .insertBefore(element);
            }
        },
        _onInstanceReady: function (event) {
            var editor = event.editor,
                selectedImageData = {};

            editor.addCommand('editInternalImageCmd', CKEDITOR.newCommandWithExecution(
                function (editor) {
                    new Imcms.Image
                        .ImageInTextEditor(editor)
                        .onExistingImageEdit(selectedImageData);
                }
            ));
            editor.contextMenu.addListener(function (element, selection) {
                if (element.hasClass("internalImageInTextEditor")) {
                    var selectedElement = selection._.cache.selectedElement.$,
                        $selection = $(selectedElement);

                    selectedImageData = {
                        no: $selection.attr("data-no"),
                        src: $selection.attr("src"),
                        selectedElement: selectedElement
                    };
                    // skipping CKEditor's "image" context menu item that is items[3]
                    editor.contextMenu.items = editor.contextMenu.items.slice(0, 3);
                    return {
                        editInternalImageCmd: CKEDITOR.TRISTATE_OFF
                    };
                }
            });
            editor.addMenuItems({
                editInternalImageCmd: {
                    label: 'Edit Image',
                    command: 'editInternalImageCmd',
                    group: 'image',
                    icon: Imcms.Linker.get("edit.image.in.text.editor.icon"),
                    order: 2
                }
            });
        },
        _onConfirm: function (event) {
            var editor = event.editor,
                data = $(editor.element.$).data(),
                isHtmlContent = CKEDITOR.contentType.isAnyHtml(data.contenttype),
                callFunc = CKEDITOR.contentType.getHtmlOrText(isHtmlContent),
                content = $(editor.element.$)[callFunc]();

            // save only when content is changed or mode is switched
            if ((data.content !== content) || CKEDITOR.switchFormat) {
                data.content = content;
                if (!data.meta) {
                    data.meta = Imcms.document.meta;
                }

                var shouldRefreshPage = false;

                if (CKEDITOR.switchFormat) {
                    var isCleanContent = CKEDITOR.contentType.isClean(data.contenttype);
                    var funcToCall = (isHtmlContent) ? "getSource" : "getHtml";

                    data.contenttype = CKEDITOR.contentType[funcToCall](isCleanContent && "clean");
                    shouldRefreshPage = true;
                    CKEDITOR.switchFormat = false;
                }

                var onTextUpdated = function (savedContent) {
                    editor.resetDirty();
                    $(editor.element.$).html($("<p>"))
                        .children()[callFunc](savedContent.replace(/&nbsp;/g, " "));
                    editor.resetDirty();

                    (event.data.callback || Imcms.BackgroundWorker.createTask({
                        showProcessWindow: true,
                        refreshPage: shouldRefreshPage
                    })).call();
                };

                api.update(data, onTextUpdated);

            } else if (event.data && event.data.callback && (typeof event.data.callback === "function")) {
                event.data.callback()
            }
        },
        _onCreated: function (event) {
            var editor = event.editor;

            // Customize editors.
            // These editors don't need features like smileys, templates, iframes etc.\
            // Customize the editor configurations on "configLoaded" event,
            // which is fired after the configuration file loading and
            // execution. This makes it possible to change the
            // configurations before the editor initialization takes place.
            editor.on('configLoaded', this._onEditorLoaded.bind(this));
        },
        _onGetTextHistory: function (event) {
            var data = jQuery(event.editor.element.$).data();

            data = { // sending only needed data, other can produce errors
                locale: data.locale,
                loopentryref: data.loopentryref,
                meta: Imcms.document.meta,
                no: data.no
            };

            api.get(data, event.data.callback);
        },
        _onValidateText: function (event) {
            api.validate({content: event.editor.getData()}, event.data.callback);
        },
        _onEditorLoaded: function (event) {
            var editor = event.editor;

            // commands definition
            editor.addCommand("switchFormat", CKEDITOR.newCommandWithExecution(function (editor) {
                CKEDITOR.switchFormat = true;
                editor.execCommand("confirmChangesBeforeSwitch");
            }));

            // Remove unnecessary plugins to make the editor simpler.
            editor.config.removePlugins = 'colorbutton,find,forms,newpage,removeformat,specialchar,'
                + 'stylescombo,templates';
            var customExtraPlugins = ",switchFormatToHTML,switchFormatToText,documentSaver,fileBrowser,link,"
                + "textHistory,w3cValidator,maximize,toolbarswitch";

            if (editor.config.extraPlugins.indexOf(customExtraPlugins) === -1) {
                editor.config.extraPlugins = editor.config.extraPlugins + customExtraPlugins;
            }

            var fontPlugins = ['Bold', 'Italic', 'Underline', 'Strike'],
                textParagraphPlugins = [
                    'NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-',
                    'Outdent', 'Indent'
                ],
                linkPlugins = ['Link', 'Unlink', 'Anchor'],
                imagesPlugins = ['Image', 'openBrowser'],
                textPlugins = ['TextColor', 'BGColor'],
                imcmsCustomPlugins = ['w3cValidate', 'textHistory', 'Toolbarswitch', 'saveData', 'confirm', 'cancel'],
                switchFormatToTextPlugin = ['switchFormatToText'],
                switchFormatToHtmlPlugin = ['switchFormatToHTML'],
                plainTextPlugins = ['textHistory', 'Toolbarswitch', 'saveData', 'confirm', 'cancel'],
                imageUtilsAndOtherDefaultPlugins = [
                    'Image', 'openBrowser', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak',
                    'Iframe'
                ],
                advancedFontPlugins = ['Styles', 'Format', 'Font', 'FontSize'],
                sourcePlugin = ['Source'],
                textEditingPlugins = ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'],
                advancedActionsPlugins = ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'];

            editor.config.toolbar_minPlainText = [plainTextPlugins];
            // Custom minimized toolbar config for tag with attribute "formats"="text"

            editor.config.toolbar_maxPlainText = [plainTextPlugins];
            // Custom maximized toolbar config for tag with attribute "formats"="text"

            editor.config.toolbar_minTextToolbar = [
                switchFormatToHtmlPlugin,
                textPlugins,
                plainTextPlugins
            ]; // Custom minimized toolbar config for tag without attribute "formats" but was changed to "Text"

            editor.config.toolbar_minHtmlToolbar = [
                switchFormatToTextPlugin,
                fontPlugins,
                textParagraphPlugins,
                linkPlugins,
                imagesPlugins,
                textPlugins,
                sourcePlugin,
                imcmsCustomPlugins
            ]; // Custom minimized toolbar config for tag without attribute "formats" but was changed to "HTML"

            editor.config.toolbar_maxHtmlToolbar = [
                switchFormatToTextPlugin,
                fontPlugins,
                textParagraphPlugins,
                linkPlugins,
                imageUtilsAndOtherDefaultPlugins,
                advancedFontPlugins,
                textPlugins,
                textEditingPlugins,
                sourcePlugin,
                advancedActionsPlugins,
                imcmsCustomPlugins
            ]; // Custom maximized toolbar config for tag without attribute "formats" but was changed to "HTML"

            editor.config.toolbar_maxTextToolbar = [
                switchFormatToHtmlPlugin,
                advancedActionsPlugins,
                plainTextPlugins
            ]; // Custom maximized toolbar config for tag without attribute "formats" but was changed to "HTML"

            editor.toolbarLocation = "top";
            editor.config.toolbar = CKEDITOR.defineToolbar(editor);
            editor.config.allowedContent = true;
        }
    };

    return Imcms.Text.Editor;
})(Imcms);
