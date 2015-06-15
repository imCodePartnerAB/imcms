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
    path: "/edit",

    update: function (sender, callback) {
        var data = jQuery(sender).data().prettify();
        data.meta = Imcms.document.meta;
        data.content = jQuery(sender).html();
        console.info(data);
        $.ajax({
            url: this.path,
            type: "POST",
            data: data,
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
        $("[contenteditable='true']").each(function (position, element) {
            element = $(element);
            var parent = element.parent().css({position: "relative"});
            var currentFrame = textFrame.click(function (e) {
                currentFrame.hide();
                element.focus();
                element.trigger(e);
                element.blur(function () {
                    currentFrame.show();
                });
            }).build().prependTo(parent);
        });
        CKEDITOR.on('instanceCreated', $.proxy(this, "_onCreated"));
        CKEDITOR.on("confirmChanges", $.proxy(this, "_onConfirm"));
    },
    _onConfirm: function (event) {
        this._api.update(event.editor.element.$, null);
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
    _onEditorLoaded: function (event) {
        var editor = event.editor;

        // Remove unnecessary plugins to make the editor simpler.
        editor.config.removePlugins = 'colorbutton,find,' +
        'forms,newpage,removeformat,' +
        'specialchar,stylescombo,templates';
        editor.config.extraPlugins = editor.config.extraPlugins + ",documentSaver";

        editor.config.toolbar = 'MyToolbar';
        editor.config.toolbar_MyToolbar =
            [
                ['Bold', 'Italic', 'Underline', 'Strike'],
                ['NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Outdent', 'Indent'],
                [ 'Link', 'Unlink', 'Anchor' ],
                [ 'Image', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe' ],
                [ 'Styles', 'Format', 'Font', 'FontSize' ],
                [ 'TextColor', 'BGColor' ],
                ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'],
                ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'],
                ['confirm', 'cancel']
            ];
    }
};
