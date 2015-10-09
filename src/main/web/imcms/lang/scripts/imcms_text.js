/**
 * Created by Shadowgun on 13.02.2015.
 */
/*
 Text Editer
 */
Imcms.Text = {};

Imcms.Text.API = function () {

};
Imcms.Text.API.prototype = {
    path: "/" + Imcms.contextPath + "api/text",

    get: function (request, callback) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: callback
        })
    },
    update: function (request, callback) {
        $.ajax({
            url: this.path,
            type: "POST",
            data: request,
            success: callback
        })
    },
    validate: function (request, callback) {
        $.ajax({
            url: this.path + "/validate",
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

        CKEDITOR.on('instanceCreated', $.proxy(this, "_onCreated"));
        CKEDITOR.on("confirmChanges", $.proxy(this, "_onConfirm"));
        CKEDITOR.on("validateText", $.proxy(this, "_onValidateText"));
        CKEDITOR.on("getTextHistory", $.proxy(this, "_onGetTextHistory"));

        $("[contenteditable='true']").each(function (position, element) {
            element = $(element);

            CKEDITOR.inline(element[0]);


            element.parents("a").attr("onclick", "return false;");

            $("<div>").insertAfter(element).append(element).css({overflow: "hidden"});

            var currentFrame = textFrame.click(function (e) {
                currentFrame.hide();
                element.focus();
                element.trigger(e);
                element.blur(function () {
                    currentFrame.show();
                });
            }).build().insertBefore(element);
        });
    },
    _onConfirm: function (event) {
        var data = jQuery(event.editor.element.$).data().prettify();
        data.meta = Imcms.document.meta;
        data.content = event.editor.getData();

        this._api.update(data, event.data.callback || Imcms.BackgroundWorker.createTask({
                showProcessWindow: true,
                refreshPage: true
            }));
    },
    _onCreated: function (event) {
        var editor = event.editor;

        // Customize editors.
        // These editors don't need features like smileys, templates, iframes etc.\
        // Customize the editor configurations on "configLoaded" event,
        // which is fired after the configuration file loading and
        // execution. This makes it possible to change the
        // configurations before the editor initialization takes place.
        editor.on('configLoaded', $.proxy(this, "_onEditorLoaded"));

    },
    _onGetTextHistory: function (event) {
        var data = jQuery(event.editor.element.$).data().prettify();

        data.meta = Imcms.document.meta;

        this._api.get(data, event.data.callback);
    },
    _onValidateText: function (event) {
        this._api.validate({content: event.editor.getData()}, event.data.callback);
    },
    _onEditorLoaded: function (event) {
        var editor = event.editor;

        // Remove unnecessary plugins to make the editor simpler.
        editor.config.removePlugins = 'colorbutton,find,' +
            'forms,newpage,removeformat,' +
            'specialchar,stylescombo,templates';
        editor.config.extraPlugins = editor.config.extraPlugins + ",documentSaver,fileBrowser,link,textHistory,w3cValidator,maximize,toolbarswitch";
        editor.config.toolbar_minToolbar = [
            ['Bold', 'Italic', 'Underline', 'Strike'],
            ['NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Outdent', 'Indent'],
            ['Link', 'Unlink', 'Anchor'],
            ['Image', 'openBrowser'/*, 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe'*/],
            //['Styles', 'Format', 'Font', 'FontSize'],
            ['TextColor', 'BGColor'],
            // ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'],
            // ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'],
            ['w3cValidate', 'textHistory', 'Toolbarswitch', 'saveData', 'confirm', 'cancel']
        ]; // Custom minimized toolbar config
        editor.config.toolbar_plain = [
            ['saveData', 'confirm', 'cancel']
        ]; // Custom minimized toolbar config
        editor.config.toolbar_maxToolbar = [
            ['Bold', 'Italic', 'Underline', 'Strike'],
            ['NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Outdent', 'Indent'],
            ['Link', 'Unlink', 'Anchor'],
            ['Image', 'openBrowser', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe'],
            ['Styles', 'Format', 'Font', 'FontSize'],
            ['TextColor', 'BGColor'],
            ['Source'],
            ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'],
            ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'],
            ['w3cValidate', 'textHistory', 'Toolbarswitch', 'saveData', 'confirm', 'cancel']
        ];
        editor.toolbarLocation = "top";
        editor.config.toolbar = editor.elementMode == 3 ? (editor.element.data("contenttype") === "html" ? 'minToolbar' : "plain") : 'maxToolbar';
        editor.config.smallToolbar = 'minToolbar';
        editor.config.maximizedToolbar = 'maxToolbar';
        editor.config.allowedContent = true;
        //editor.config.toolbar = 'MyToolbar';
        //editor.config.toolbar_MyToolbar =
        //    [
        //        ['Bold', 'Italic', 'Underline', 'Strike'],
        //        ['NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Outdent', 'Indent'],
        //        ['Link', 'Unlink', 'Anchor'],
        //        ['Image', 'openBrowser'/*, 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe'*/],
        //        //['Styles', 'Format', 'Font', 'FontSize'],
        //        ['TextColor', 'BGColor'],
        //        // ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'],
        //        // ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'],
        //        ['resize', 'confirm', 'cancel']
        //    ];
    }
};
