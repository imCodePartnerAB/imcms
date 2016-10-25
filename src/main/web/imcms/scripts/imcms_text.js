/**
 * Created by Shadowgun on 13.02.2015.
 */
/**
 Text Editor
 */
Imcms.Text = {};

Imcms.Text.API = function () {

};
Imcms.Text.API.prototype = {
    get: function (request, callback) {
        $.ajax({
            url: Imcms.Linker.get("text"),
            type: "GET",
            data: request,
            success: callback
        })
    },
    update: function (request, callback) {
        $.ajax({
            url: Imcms.Linker.get("text"),
            type: "POST",
            data: request,
            success: callback
        })
    },
    validate: function (request, callback) {
        $.ajax({
            url: Imcms.Linker.get("text.validate"),
            type: "POST",
            data: request,
            success: callback
        })
    }
};

Imcms.Text.Editor = function () {
    this.init();
};

Imcms.Text.Editor.prototype = {
    _api: new Imcms.Text.API(),
    init: function () {
        var textFrame = new Imcms.FrameBuilder().title("Text Editor");

        CKEDITOR.on('instanceCreated', this._onCreated.bind(this));
        CKEDITOR.on("confirmChangesEvent", this._onConfirm.bind(this));
        CKEDITOR.on("validateText", this._onValidateText.bind(this));
        CKEDITOR.on("getTextHistory", this._onGetTextHistory.bind(this));

        $("[contenteditable='true']").each(function (position, element) {
            element = $(element);
            CKEDITOR.inline(element[0]);
            element.parents("a").attr("onclick", "return false;");

            $("<div>").insertAfter(element).append(element).css({overflow: "hidden"});

            var loopTitle = "";
            if (element.data("loopentryref")) {
                loopTitle = (element.data("loopentryref")).split("_");
                loopTitle = "L" + loopTitle[0] + "-E" + loopTitle[1] + "-T";
            }

            var title = loopTitle + element.data("no") + " | " + element.data("label"),
                currentFrame = textFrame.click(function (e) {
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
        });
    },
    _onConfirm: function (event) {
        var editor = event.editor,
            data = $(editor.element.$).data(),
            isHtmlContent = (data.contenttype === "html"),
            callFunc = (isHtmlContent) ? "html" : "text",
            content = $(editor.element.$)[callFunc]();

        // save only when content is changed or mode is switched
        if ((data.content !== content) || CKEDITOR.switchFormat) {
            data.content = content;
            if (!data.meta) {
                data.meta = Imcms.document.meta;
            }

            var shouldRefreshPage = false;

            if (CKEDITOR.switchFormat) {
                data.contenttype = (isHtmlContent)
                    ? "from-html"
                    : "html";

                shouldRefreshPage = true;
                CKEDITOR.switchFormat = false;
            }

            this._api.update(data, event.data.callback || Imcms.BackgroundWorker.createTask({
                    showProcessWindow: true,
                    refreshPage: shouldRefreshPage
                })
            );

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

        this._api.get(data, event.data.callback);
    },
    _onValidateText: function (event) {
        this._api.validate({content: event.editor.getData()}, event.data.callback);
    },
    _onEditorLoaded: function (event) {
        var editor = event.editor;

        // commands definition
        editor.addCommand("switchFormat", CKEDITOR.newCommandWithExecution(function (editor) {
            CKEDITOR.switchFormat = true;
            editor.execCommand("confirmChangesWithoutEvent");
        }));

        // Remove unnecessary plugins to make the editor simpler.
        editor.config.removePlugins = 'colorbutton,find,forms,newpage,removeformat,specialchar,stylescombo,templates';
        var customExtraPlugins = ",switchFormatToHTML,switchFormatToText,documentSaver,fileBrowser,link,textHistory,w3cValidator,maximize,toolbarswitch";

        if (editor.config.extraPlugins.indexOf(customExtraPlugins) == -1) {
            editor.config.extraPlugins = editor.config.extraPlugins + customExtraPlugins;
        }

        var fontPlugins = ['Bold', 'Italic', 'Underline', 'Strike'];
        var textParagraphPlugins = ['NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Outdent', 'Indent'];
        var linkPlugins = ['Link', 'Unlink', 'Anchor'];
        var imagesPlugins = ['Image', 'openBrowser'];
        var textPlugins = ['TextColor', 'BGColor'];
        var imcmsCustomPlugins = ['w3cValidate', 'textHistory', 'Toolbarswitch', 'saveData', 'confirm', 'cancel'];
        var switchFormatToTextPlugin = ['switchFormatToText'];
        var switchFormatToHtmlPlugin = ['switchFormatToHTML'];
        var plainTextPlugins = ['textHistory', 'Toolbarswitch', 'saveData', 'confirm', 'cancel'];
        var imageUtilsAndOtherDefaultPlugins = ['Image', 'openBrowser', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe'];
        var advancedFontPlugins = ['Styles', 'Format', 'Font', 'FontSize'];
        var sourcePlugin = ['Source'];
        var textEditingPlugins = ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'];
        var advancedActionsPlugins = ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'];

        editor.config.toolbar_minPlainText = [
            plainTextPlugins
        ]; // Custom minimized toolbar config for tag with attribute "formats"="text"

        editor.config.toolbar_maxPlainText = [
            plainTextPlugins
        ]; // Custom maximized toolbar config for tag with attribute "formats"="text"

        editor.config.toolbar_minTextToolbar = [
            switchFormatToHtmlPlugin,
            linkPlugins,
            imagesPlugins,
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
            linkPlugins,
            advancedActionsPlugins,
            plainTextPlugins
        ]; // Custom maximized toolbar config for tag without attribute "formats" but was changed to "HTML"

        editor.toolbarLocation = "top";
        editor.config.toolbar = CKEDITOR.defineToolbar(editor);
        editor.config.allowedContent = true;
    }
};
