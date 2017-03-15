(function (Imcms) {
    return Imcms.Editors = {
        Language: {},
        Template: {},
        Role: {},
        Permission: {},
        Category: {},
        User: {},
        Document: {},
        Loop: {},
        Menu: {},
        Text: {},
        File: {},
        Folder: {},
        Content: {},
        Image: {},
        /**
         * Initialize all editors
         */
        init: function () {
            this.Language = new Imcms.Language.Loader();
            this.Template = new Imcms.Template.Loader();
            this.Role = new Imcms.Role.Loader();
            this.Permission = new Imcms.Permission.Loader();
            this.Category = new Imcms.Category.Loader();
            this.User = new Imcms.User.Loader();
            this.Document = new Imcms.Document.Loader();
            this.Loop = new Imcms.Loop();
            this.Menu = new Imcms.Menu.Loader();
            this.Text = new Imcms.Text.Editor();
            this.File = new Imcms.File.Loader();
            this.Folder = new Imcms.Folder.Loader();
            this.Content = new Imcms.Content.Loader();
            this.Image = new Imcms.Image.Loader();
        },
        /**
         * Will rebuild editors of specified element.
         * If any child should have editor, it will built.
         * !!Do not forget to bind context of what to buildExtra!!!
         *
         * @param $target jQuery object to be rebuilt
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
})(Imcms);
