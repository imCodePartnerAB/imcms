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
        var pluginName = "documentSaver",
            publisherName = Imcms.Admin.Panel.publisherName;

        function dialogCallback(editor) {
            var publisher = Imcms.CallbackConnector.getPublisher(publisherName);

            if (publisher && publisher.hasSubscriber(editor)) {
                publisher.unSubscribe(editor);
                publisher.callback();
                publisher.callback = function () {
                }
            }
        }

        editor.documentSnapshot = editor.getData();
        editor.blurHandler = function (event) {
            var editor = event.editor;
            Imcms.Events.on("TextEditorRedirect", dialogCallback.bind(this, editor));
            if (editor.checkDirty()) {
                editor.openDialog(pluginName);

            } else {
                dialogCallback(editor);
            }
        };

        editor.on("focus", function (event) {
            Imcms.CallbackConnector
                .getPublisher(publisherName)
                .addSubscriber(event.editor);
        });

        editor.focusHandler = function (event) {
            event.editor.on('blur', event.editor.blurHandler);
        };
        var switchToolbarCommandFunction = function (editor) {
            var element = editor.element;
            if (editor.elementMode == 1) {
                editor.execCommand("toolbarswitch");
            }
            for (var key in CKEDITOR.instances) {
                var newEditor = CKEDITOR.instances[key];
                if (element == newEditor.element) {
                    return newEditor;
                }
            }
        };

        editor.on('blur', editor.blurHandler);

        function saveData(event, noCallback) {
            editor.documentSnapshot = event.getData();
            event.resetDirty();
            if (noCallback) {
                event.removeListener('blur', event.blurHandler);
                event.focusManager.unlock();
                event.element.$.blur();
                event.focusManager.blur();
                event.on("focus", event.focusHandler);
            }
        }

        function switchModeCallback(e) {
            return Imcms.BackgroundWorker.createTask({
                showProcessWindow: true,
                refreshPage: true,
                callbackFunc: function () {
                    var loopref = $(e.element.$).attr("data-loopentryref");
                    var no = $(e.element.$).attr("data-no");
                    var meta = $(e.element.$).attr("data-meta");
                    var element = $("div[data-loopentryref=" + (loopref ? loopref : "''") + "]" +
                        "[data-no=" + no + "][data-meta=" + meta + "]");

                    //Getting current ckeditor instance
                    for (var key in CKEDITOR.instances) {
                        var editor = CKEDITOR.instances[key];
                        if (element[0] === $(editor.element.$)[0]) {
                            //Moving cursor and focusing it
                            editor.focus();
                            var range = editor.createRange();
                            range.moveToElementEditEnd(range.root);
                            editor.getSelection().selectRanges([range]);
                            break;
                        }
                    }
                }
            });
        }

        var confirmBeforeSwitch = function (event) {
            var callback = switchModeCallback(event);
            event = switchToolbarCommandFunction(event);
            CKEDITOR.fireOnce("confirmChangesEvent", {callback: callback}, event);
            saveData(event, !callback);
        };

        var confirmWithEvent = function (event) {
            event = switchToolbarCommandFunction(event);
            var callback = Imcms.Events.getCallback("TextEditorRedirect");
            CKEDITOR.fire("confirmChangesEvent", {callback: callback}, event);
            saveData(event, !callback);
        };
        var confirmCommandWithEvent = CKEDITOR.newCommandWithExecution(confirmWithEvent);
        editor.addCommand("confirmChanges", confirmCommandWithEvent);
        editor.ui.addButton('confirm', {
            label: 'Save all changes',
            command: "confirmChanges",
            icon: "images/ic_apply.png"
        });

        var confirmCommandBeforeSwitch = CKEDITOR.newCommandWithExecution(confirmBeforeSwitch);
        editor.addCommand("confirmChangesBeforeSwitch", confirmCommandBeforeSwitch);

        var cancelCommandFunction = function (editor) {
            dialogCallback(editor);
            var newEditor = switchToolbarCommandFunction(editor),
                hideCommand = function (event) {
                    setTimeout(function () {
                        event.removeListener('instanceReady', hideCommand);
                        event.setData(event._.previousValue);
                        event.removeListener('blur', event.blurHandler);
                        event.focusManager.unlock();
                        event.element.$.blur();
                        event.focusManager.blur();
                        event.on("focus", event.focusHandler);
                    }, 1);
                };
            if (newEditor != editor) {
                newEditor.on('instanceReady', hideCommand.bind(null, newEditor));

            } else {
                hideCommand(editor);
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

        var saveCommandDefinition = CKEDITOR.newCommandWithExecution(function (event) {
            var $button = $('.' + event.id).find('.cke_button__savedata_icon').css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
                }),
                callback = function () {
                    $button.css({
                        backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_save.png" + ")"
                    });
                };

            CKEDITOR.fire("confirmChangesEvent", {
                callback: callback
            }, event);
            saveData(event, !callback);
        });
        editor.addCommand("saveChanges", saveCommandDefinition);
        editor.ui.addButton('saveData', {
            label: 'Save all changes',
            command: "saveChanges",
            icon: "images/ic_save.png"
        });
    }
});
CKEDITOR.dialog.add("documentSaver", function (event) {
    var textChangedMessage = "You have changed the text. Do you wish to save it?";
    return {
        title: 'Save changes',
        width: 200,
        height: 25,
        onOk: function () {
            event.execCommand("confirmChanges");
        },
        onCancel: function () {
            event.execCommand("cancelChanges");
        },
        onHide: function () {
            setTimeout(function () {
                event.focusManager.unlock();
                if (event.element) {
                    event.element.$.blur();
                }
                event.focusManager.blur();
                $(event.focusManager.currentActive).blur();
            }, 500);
        },
        contents: [
            {
                id: 'general',
                label: 'Settings',
                elements: [
                    {
                        type: 'html',
                        onLoad: function (event) {
                            var $dialog = $(event.sender.parts.dialog.$);
                            $dialog.find(".cke_resizer.cke_resizer_ltr").hide(); // dialog resize triangle
                            // in Safari close button goes to wrong side,
                            $dialog.find(".cke_dialog_close_button").css("float", "right");
                        },
                        html: $("<div>")
                            .css({
                                "text-align": "center",
                                "line-height": "25px"
                            })
                            .text(textChangedMessage)[0]
                            .outerHTML
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
        var imageInTextEditor = new Imcms.Image.ImageInTextEditor(editor),
            openBrowserCommandDefinition = CKEDITOR.newCommandWithExecution(
                imageInTextEditor.onBrowserOpen.bind(imageInTextEditor)
            );
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
        var w3cValidateCommand = function (event) {
            var $button = $('.' + event.id).find('.cke_button__w3cvalidate_icon').css({
                backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
            });
            //todo: Add message if content is invalid
            CKEDITOR.fire("validateText", {
                callback: function (data) {
                    $button.css({
                        backgroundImage: "url(" + CKEDITOR.basePath
                        + (data.result ? "images/ic_valid.png" : "images/ic_invalid.png") + ")"
                    });

                    if (!data.result) {
                        $(event.element.$).data("w3cValidateData", data);
                        event.execCommand("w3cValidateDialog", data);
                    }
                }
            }, event);
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
CKEDITOR.dialog.add("w3cValidationResultDialog", function (event) {
    var $wrapper = $("<div>"),
        $content = $("<div>").addClass("imcms-w3c-errors").appendTo($wrapper),
        data = $(event.element.$).data("w3cValidateData");

    $("<div>").append($("<h2>").text("Validation Output: " + data.data.errors.length + " Errors")).appendTo($content);

    data.data.errors.forEach(function (item, pos) {
        var $container = $("<div>").addClass("imcms-w3c-error").appendTo($content),
            $errorMessage = $("<div>")
                .text(pos + 1 + ". " + item.message.charAt(0).toUpperCase()
                    + item.message.slice(1))
                .appendTo($container),
            $sourceContainer = $("<div>").appendTo($container),
            $source = $("<code>").addClass("language-html")
                .html(item.line + ": " + item.source.replace(/(<([^>]+)>)/ig, ""))
                .appendTo($sourceContainer);

        Prism.highlightElement($source[0]);
    });

    return {
        title: 'Validation Result Dialog',
        width: 600,
        minHeight: 250,
        contents: [{
            id: 'w3cValidation',
            label: 'Validation',
            elements: [{
                type: 'html',
                html: $("<div>").append($wrapper).html()
            }]
        }]
    };
});

CKEDITOR.plugins.add("textHistory", {
    init: function (editor) {
        var textHistoryCommand = function (event) {
            var $button = $('.' + event.id).find('.cke_button__texthistory_icon')
                .css({
                    backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_loader.gif" + ")"
                });
            //todo: Add message if content is invalid
            CKEDITOR.fire("getTextHistory", {
                callback: function (data) {
                    $button.css({
                        backgroundImage: "url(" + CKEDITOR.basePath + "images/ic_history.png" + ")"
                    });

                    $(event.element.$).data("textHistoryData", data);
                    event.execCommand("textHistoryDialog", data);
                }
            }, event);
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

CKEDITOR.dialog.add("textHistory", function (event) {
    var $wrapper = $("<div>").addClass("imcms-text-history"),
        $leftPanel = $("<div>").addClass("imcms-left-panel").appendTo($wrapper),

        $content = $("<div>").addClass("imcms-content")
            .css({
                "max-width": "600px",
                "float": "right"
            })
            .appendTo($wrapper),

        data = $(event.element.$).data("textHistoryData")
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
        contents: [{
            id: 'textHistory',
            label: 'Text History',
            elements: [{
                type: 'html',
                onLoad: function () {
                    $("#" + this.domId).append($wrapper)
                },
                html: ""
            }]
        }],
        buttons: [
            {
                id: "text history OK button",
                type: "button",
                label: "Write to textfield",
                title: "Write to textfield",
                "class": "cke_dialog_ui_button_ok",
                disabled: false,
                onClick: function () {
                    if (selectedItem) {
                        event.setData(""); // clear previous text
                        var contentType = $(event.element.$).data("contenttype");

                        var callFunc = (contentType === "html")
                            ? "insertHtml"
                            : "insertText";

                        event[callFunc](selectedItem.text);

                        if (selectedItem.type !== contentType) {
                            event.execCommand("switchFormat");
                        }
                    }
                }
            },
            CKEDITOR.dialog.cancelButton
        ]
    };
});
