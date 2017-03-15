Imcms.Editors = {
    /**
     * Will rebuild editors of specified element.
     * If any child should have editor, it will built.
     * Do not forget to bind context of what to buildExtra
     *
     * @param $target target element to rebuild
     */
    rebuildEditorsIn: function ($target) {
        this.buildExtra();

        $target.find($(".editor-image")).each(
            Imcms.Editors.Image.initEditor.bind(Imcms.Editors.Image)
        );

        $target.find("[contenteditable='true']").each(
            Imcms.Editors.Text.addEditor.bind(Imcms.Editors.Text)
        );
    }
};
Imcms.Editors.Text = {};
Imcms.Editors.Menu = {};
Imcms.Utils = {};

Imcms.Bootstrapper = function () {
};
Imcms.Bootstrapper.prototype = {
    bootstrap: function (editmode) {
        var $body = $("body");
        if (editmode) {
            $body.css({paddingLeft: 150, width: $(window).width() - 150});
        } else {
            if ($body.css('paddingLeft').length > 0) {
                $body.removeAttr('style');
            }
        }

        //Init of internationalization plugin
        $.i18n.properties({
            name: 'imcms_jquery_i18n',
            path: Imcms.Linker.get('admin.localization.config'),
            mode: 'both'
        });

        Imcms.Editors.Language = new Imcms.Language.Loader();
        Imcms.Editors.Template = new Imcms.Template.Loader();
        Imcms.Editors.Role = new Imcms.Role.Loader();
        Imcms.Editors.Permission = new Imcms.Permission.Loader();
        Imcms.Editors.Category = new Imcms.Category.Loader();
        Imcms.Editors.User = new Imcms.User.Loader();
        Imcms.Editors.Document = new Imcms.Document.Loader();
        Imcms.Editors.Loop = new Imcms.Loop();
        Imcms.Editors.Menu = new Imcms.Menu.Loader();
        Imcms.Editors.Text = new Imcms.Text.Editor();
        Imcms.Editors.File = new Imcms.File.Loader();
        Imcms.Editors.Folder = new Imcms.Folder.Loader();
        Imcms.Editors.Content = new Imcms.Content.Loader();
        Imcms.Editors.Image = new Imcms.Image.Loader();

        Imcms.Admin.Panel.init();
    }
};
