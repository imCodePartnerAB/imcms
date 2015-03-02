/**
 * Created by Shadowgun on 12.02.2015.
 */
/*
 JQuery Addons Data
 return all attr in tag that started with `data-`
 */

(function ($) {
    var $super = $.fn.data;
    $.fn.data = function () {
        if (arguments.length === 0) {
            if (this.length === 0) {
                console.error("No element");
                return null;
            }

            var uglyResult = {
                prettify: function () {
                    var prettyResult = {};
                    for (var property in this) {
                        if (this.hasOwnProperty(property)) {
                            prettyResult[property.replace("data-", "")] = this[property];
                        }
                    }

                    return prettyResult;
                }
            };
            $.each(this[0].attributes, function () {
                if (this.specified && this.name.indexOf("data-") > -1) {
                    uglyResult[this.name] = this.value;
                }
            });

            return uglyResult;
        }

        return $super.apply(this, arguments);
    };
})(jQuery);
/*
 CKEditor Addons
 */
CKEDITOR.define("confirmChanges", {});
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
        var confirmCommandFunction = function (e) {
            CKEDITOR.fire("confirmChanges", {}, e);
            editor.documentSnapshot = e.getData();
            e.resetDirty();
            e.removeListener('blur', e.blurHandler);
            e.focusManager.unlock();
            e.element.$.blur();
            e.focusManager.blur();
            e.on("focus", e.focusHandler);
        };
        var confirmCommandDefinition =
        {
            // This command works in both editing modes.
            modes: {wysiwyg: 1, source: 1},

            // This command will not auto focus editor before execution.
            editorFocus: false,

            // This command requires no undo snapshot.
            canUndo: false,

            exec: confirmCommandFunction
        };
        editor.addCommand("confirmChanges", confirmCommandDefinition);
        var cancelCommandFunction = function (e) {
            e.setData(e.documentSnapshot);
            e.removeListener('blur', e.blurHandler);
            e.focusManager.unlock();
            e.element.$.blur();
            e.focusManager.blur();
            e.on("focus", e.focusHandler);
        };
        var cancelCommandDefinition =
        {
            // This command works in both editing modes.
            modes: {wysiwyg: 1, source: 1},

            // This command will not auto focus editor before execution.
            editorFocus: false,

            // This command requires no undo snapshot.
            canUndo: false,

            exec: cancelCommandFunction
        };
        editor.on('blur', editor.blurHandler);


        editor.addCommand("cancelChanges", cancelCommandDefinition);
        editor.ui.addButton('confirm',
            {
                label: 'Save all changes',
                command: "confirmChanges",
                icon: "images/ic_apply.png"
            });
        editor.ui.addButton('cancel',
            {
                label: 'Cancel all changes and restore document to previous state',
                command: "cancelChanges",
                icon: "images/ic_cancel.png"
            });
    }
});
CKEDITOR.dialog.add("documentSaver", function (e) {
    return {
        title: 'Save Dialog',
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
                        html: '<div style="text-align:center; line-height:25px">Save document?</div>'
                    }
                ]
            }
        ]
    };
});
