/*
 CKEditor Addons
 */
CKEDITOR.define("confirmChanges", {});
CKEDITOR.define("validateText", {});
CKEDITOR.define("getTextHistory", {});

CKEDITOR.newCommandWithExecution = function (executionFunc) {
    return {
        modes: {wysiwyg: 1, source: 1},// This command works in both editing modes.
        editorFocus: false,// This command will not auto focus editor before execution.
        canUndo: false,// This command requires no undo snapshot.
        exec: executionFunc
    }
};

CKEDITOR.plugins.add("documentSaver", {
    init: function (editor) {
        var pluginName = "documentSaver";
        editor.documentSnapshot = editor.getData();
        editor.blurHandler = function (e) {
            if (e.editor.checkDirty()) {
                e.editor.openDialog(pluginName);
            }
        };
        editor.focusHandler = function (e) {
            e.removeListener();
            e.editor.on('blur', e.editor.blurHandler);
        };
        var switchToolbarCommandFunction = function (e) {
            var element = e.element;
            if (e.elementMode == 1) {
                e.execCommand("toolbarswitch");
            }
            for (var key in CKEDITOR.instances) {
                var editor = CKEDITOR.instances[key];
                if (element == editor.element) {
                    return editor;
                }
            }
        };

        editor.on('blur', editor.blurHandler);

        var confirmNoEvent = function (e, callback) {
            if (!callback) {
                e = switchToolbarCommandFunction(e);
            }
            CKEDITOR.fire("confirmChangesEvent", {callback: callback}, e);
            editor.documentSnapshot = e.getData();
            e.resetDirty();
            if (!callback) {
                e.removeListener('blur', e.blurHandler);
                e.focusManager.unlock();
                e.element.$.blur();
                e.focusManager.blur();
                e.on("focus", e.focusHandler);
            }
        };

        var confirmWithEvent = function (e) {
            e = switchToolbarCommandFunction(e);
            var callback = Imcms.Events.getCallback("TextEditorRedirect");
            CKEDITOR.fire("confirmChangesEvent", {callback: callback}, e);
            e.resetDirty();
        };
        var confirmCommandWithEvent = CKEDITOR.newCommandWithExecution(confirmWithEvent);
        editor.addCommand("confirmChanges", confirmCommandWithEvent);
        editor.ui.addButton('confirm', {
            label: 'Save all changes',
            command: "confirmChanges",
            icon: "images/ic_apply.png"
        });

        var confirmCommandNoEvent = CKEDITOR.newCommandWithExecution(confirmNoEvent);
        editor.addCommand("confirmChangesWithoutEvent", confirmCommandNoEvent);

        var cancelCommandFunction = function (e) {
            var newEditor = switchToolbarCommandFunction(e),
                hideCommand = function (e) {
                    setTimeout(function () {
                        e.removeListener('instanceReady', hideCommand);
                        e.setData(e.documentSnapshot);
                        e.removeListener('blur', e.blurHandler);
                        e.focusManager.unlock();
                        e.element.$.blur();
                        e.focusManager.blur();
                        e.on("focus", e.focusHandler);
                    }, 1);
                };
            if (newEditor != e) {
                newEditor.on('instanceReady', hideCommand.bind(null, newEditor));

            } else {
                hideCommand(e);
            }
        };
        var cancelCommandDefinition = CKEDITOR.newCommandWithExecution(
            Imcms.Events.getCallbackOr("TextEditorRedirect", cancelCommandFunction)
        );
        editor.addCommand("cancelChanges", cancelCommandDefinition);
        editor.ui.addButton('cancel', {
            label: 'Cancel all changes and restore document to previous state',
            command: "cancelChanges",
            icon: "images/ic_cancel.png"
        });

        var saveCommandDefinition = CKEDITOR.newCommandWithExecution(function (e) {
            var $button = $('.' + e.id).find('.cke_button__savedata_icon')
                .css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
                });
            confirmNoEvent(e, function () {
                $button.css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_save.png" + ")"
                });
            });
        });
        editor.addCommand("saveChanges", saveCommandDefinition);
        editor.ui.addButton('saveData', {
            label: 'Save all changes',
            command: "saveChanges",
            icon: "images/ic_save.png"
        });
    }
});
CKEDITOR.dialog.add("documentSaver", function (e) {
    return {
        title: 'Save changes',
        width: 200,
        height: 25,
        onOk: function () {
            e.execCommand("confirmChanges");
        },
        onCancel: function () {
            e.execCommand("cancelChanges");
        },
        onHide: function () {
            setTimeout(function () {
                e.focusManager.unlock();
                e.element.$.blur();
                e.focusManager.blur();
                $(e.focusManager.currentActive).blur();
            }, 500);
        },
        contents: [
            {
                id: 'general',
                label: 'Settings',
                elements: [
                    {
                        type: 'html',
                        onLoad: function (e) {
                            var $dialog = $(e.sender.parts.dialog.$);
                            $dialog.find(".cke_resizer.cke_resizer_ltr").hide(); // dialog resize triangle
                            // in Safari close button goes to wrong side,
                            $dialog.find(".cke_dialog_close_button").css("float", "right");
                        },
                        html: '<div style="text-align:center; line-height:25px">You have changed the text. Do you wish to save it?</div>'
                    }
                ]
            }
        ]
    };
});

CKEDITOR.defineToolbar = function (editor) {
    var prefix;

    if (editor.elementMode == 1) { // full-screen editor mode
        editor.config.maxToolbar = true;
        prefix = "max";

    } else {
        editor.config.maxToolbar = false;
        prefix = "min";
    }

    if (editor.element.data("contenttype") === "text") {
        return prefix + "PlainText";

    } else if (editor.element.data("contenttype") === "from-html") {
        return prefix + "TextToolbar";

    } else {
        return prefix + "HtmlToolbar";
    }
};

CKEDITOR.switchFormat = false;

CKEDITOR.plugins.add("switchFormatToHTML", {
    init: function (editor) {
        editor.ui.addButton('switchFormatToHTML', {
            label: 'Switch format to HTML',
            command: "switchFormat",
            icon: "images/switch-between-html-and-text-mode.png"
        });
    }
});

CKEDITOR.plugins.add("switchFormatToText", {
    init: function (editor) {
        editor.ui.addButton('switchFormatToText', {
            label: 'Switch format to text',
            command: "switchFormat",
            icon: "images/plain_text.png"
        });
    }
});

CKEDITOR.plugins.add("fileBrowser", {
    init: function (editor) {

        var onChooseFile = function () {
            Imcms.Editors.Content.showDialog({
                onApply: $.proxy(onFileChosen, this),
                onCancel: $.proxy(onFileChosen, this)
            });
            editor.focusManager.blur();
            editor.element.$.blur();
        };
        var onFileChosen = function (data) {
            if (data) {
                editor.insertHtml('<img class="captionedImage" src="' + data.urlPathRelativeToContextPath + '" alt="" /><br>', 'unfiltered_html');
            }
            editor.focusManager.focus();
            editor.element.$.focus();
        };

        var openBrowserCommandDefinition = CKEDITOR.newCommandWithExecution(onChooseFile);
        editor.addCommand("openBrowser", openBrowserCommandDefinition);

        editor.ui.addButton('openBrowser', {
            label: 'Open Image Browser',
            command: "openBrowser",
            icon: "images/ic_images.png"
        });
    }
});

CKEDITOR.plugins.add("w3cValidator", {
    init: function (editor) {
        var w3cValidateCommand = function (e) {
            var $button = $('.' + e.id).find('.cke_button__w3cvalidate_icon')
                .css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
                });
            //todo: Add message if content is invalid
            CKEDITOR.fire("validateText", {
                callback: function (data) {
                    $button.css({
                        backgroundImage: "url(" + CKEDITOR.basePath + (data.result ? "images/ic_valid.png" : "images/ic_invalid.png") + ")"
                    });

                    if (!data.result) {
                        $(e.element.$).data("w3cValidateData", data);
                        e.execCommand("w3cValidateDialog", data);
                    }
                }
            }, e);
        };
        var w3cValidateCommandDefinition = CKEDITOR.newCommandWithExecution(w3cValidateCommand);
        editor.addCommand("w3cValidate", w3cValidateCommandDefinition);
        editor.addCommand("w3cValidateDialog", new CKEDITOR.dialogCommand("w3cValidationResultDialog"));

        editor.ui.addButton('w3cValidate', {
            label: 'Validate Content over W3C',
            command: "w3cValidate",
            icon: "images/ic_w3c.png"
        });
    }
});
CKEDITOR.dialog.add("w3cValidationResultDialog", function (e) {
    var $wrapper = $("<div>"),
        $content = $("<div>").addClass("imcms-w3c-errors").appendTo($wrapper),
        data = $(e.element.$).data("w3cValidateData");

    $("<div>").append($("<h2>").text("Validation Output: " + data.data.errors.length + " Errors")).appendTo($content);

    data.data.errors.forEach(function (item, pos) {
        var $container = $("<div>").addClass("imcms-w3c-error").appendTo($content),
            $errorMessage = $("<div>").text(pos + 1 + ". " + item.message.charAt(0).toUpperCase() + item.message.slice(1)).appendTo($container),
            $sourceContainer = $("<div>").appendTo($container),
            $source = $("<code>").addClass("language-html").html(item.line + ": " + item.source.replace(/(<([^>]+)>)/ig, "")).appendTo($sourceContainer);

        Prism.highlightElement($source[0]);
    });

    return {
        title: 'Validation Result Dialog',
        width: 600,
        minHeight: 250,
        contents: [
            {
                id: 'w3cValidation',
                label: 'Validation',
                elements: [
                    {
                        type: 'html',
                        html: $("<div>").append($wrapper).html()
                    }
                ]
            }
        ]
    };
});

CKEDITOR.plugins.add("textHistory", {
    init: function (editor) {
        var textHistoryCommand = function (e) {
            var $button = $('.' + e.id).find('.cke_button__texthistory_icon')
                .css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
                });
            //todo: Add message if content is invalid
            CKEDITOR.fire("getTextHistory", {
                callback: function (data) {
                    $button.css({
                        backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_history.png" + ")"
                    });

                    $(e.element.$).data("textHistoryData", data);
                    e.execCommand("textHistoryDialog", data);
                }
            }, e);
        };
        var textHistoryCommandDefinition = CKEDITOR.newCommandWithExecution(textHistoryCommand);
        editor.addCommand("textHistory", textHistoryCommandDefinition);
        editor.addCommand("textHistoryDialog", new CKEDITOR.dialogCommand("textHistory"));

        editor.ui.addButton('textHistory', {
            label: 'Text History',
            command: "textHistory",
            icon: "images/ic_history.png"
        });
    }
});

CKEDITOR.dialog.add("textHistory", function (e) {
    var $wrapper = $("<div>").addClass("imcms-text-history"),
        $leftPanel = $("<div>").addClass("imcms-left-panel").appendTo($wrapper),

        $content = $("<div>").addClass("imcms-content")
            .css({
                "max-width": "600px",
                "float": "right"
            })
            .appendTo($wrapper),

        data = $(e.element.$).data("textHistoryData")
            .map(function (textHistoryData) {
                textHistoryData.modifiedDate = new Date(textHistoryData.modifiedDate);
                textHistoryData.type = (textHistoryData.type == "HTML")
                    ? "html"
                    : "from-html";
                return textHistoryData;
            })
            .sort(function (a, b) {
                return (a.modifiedDate > b.modifiedDate ? -1 : (a.modifiedDate == b.modifiedDate ? 0 : 1));
            }),

        groupedData = {},
        $selected = undefined,
        selectedItem,
        previousType;

    data.forEach(function (it) {
        var key = it.modifiedDate.format("dd/m/yy");

        if (!(key in groupedData)) {
            groupedData[key] = [];
        }

        groupedData[key].push(it);
    });

    var buttonsShowed = false;

    function addTextHistoryButton(text, onClick) {
        return $("<button>")
            .addClass("imcms-neutral")
            .css("float", "right")
            .html(text)
            .appendTo($wrapper)
            .click(onClick);
    }

    $.each(groupedData, function (key, list) {
        $("<div>").addClass("imcms-separator").text(key).appendTo($leftPanel);

        list.forEach(function (item) {
            $("<div>").appendTo($leftPanel)
                .append(item.modifiedDate.format("HH:MM:ss") + " | " + item.modifiedBy)
                .click(function () {
                    if (!buttonsShowed) {
                        addTextHistoryButton("View Page", function () {
                            if (selectedItem.type !== "html") {
                                $content.html($content.text());
                                selectedItem.type = "html";
                            }
                        });
                        addTextHistoryButton("View Source", function () {
                            if (selectedItem.type === "html") {
                                $content.text($content.html());
                                selectedItem.type = "from-html";
                            }
                        });
                        buttonsShowed = true;
                    }

                    if ($selected) {
                        $selected.removeClass("selected");
                    }

                    var callFunc = (item.type === "html")
                        ? "html"
                        : "text";

                    $content[callFunc](item.text);
                    $selected = $(this).addClass("selected");
                    selectedItem = item;
                    previousType = item.type;
                });
        })
    });

    return {
        title: 'Text History Dialog',
        width: 600,
        height: 400,
        contents: [
            {
                id: 'textHistory',
                label: 'Text History',
                elements: [
                    {
                        type: 'html',
                        onLoad: function () {
                            $("#" + this.domId).append($wrapper)
                        },
                        html: ""
                    }
                ]
            }
        ],
        buttons: [
            {
                id: "text history OK button",
                type: "button",
                label: "Write to textfield",
                title: "Write to textfield",
                "class":"cke_dialog_ui_button_ok",
                disabled: false,
                onClick: function () {
                    if (selectedItem) {
                        e.setData(""); // clear previous text
                        var contentType = $(e.element.$).data("contenttype");

                        var callFunc = (contentType === "html")
                            ? "insertHtml"
                            : "insertText";

                        e[callFunc](selectedItem.text);

                        if (selectedItem.type !== contentType) {
                            e.execCommand("switchFormat");
                        }
                    }
                }
            },
            CKEDITOR.dialog.cancelButton
        ]
    };
});