/**
 * Created by Shadowgun on 12.02.2015.
 */
var Imcms = {};
Imcms.Editors = {};
Imcms.Editors.Text = {};
Imcms.Editors.Menu = {};
Imcms.Utils = {};
Imcms.document = {};

/*
 Imcms bootstraper
 */

Imcms.Bootstrapper = function () {
};
Imcms.Bootstrapper.prototype = {
    bootstrap: function () {
        Imcms.Editors.Text = new Imcms.Text.Editor();
        Imcms.Editors.Menu = new Imcms.Menu.Loader();
        Imcms.Editors.Language = new Imcms.Language.Loader();
        Imcms.Editors.Template = new Imcms.Template.Loader();
        Imcms.Editors.Document = new Imcms.Document.Loader();
        Imcms.Editors.Loop = new Imcms.Loop.Loader();
        Imcms.Editors.File = new Imcms.File.Loader();
        Imcms.Editors.Folder = new Imcms.Folder.Loader();
        Imcms.Editors.Content = new Imcms.Content.Loader();
        Imcms.Editors.Image = new Imcms.Image.Loader();
    }
};

Imcms.FrameBuilder = function () {

};
Imcms.FrameBuilder.prototype = {
    _click: function () {
    },
    _title: "",
    title: function () {
        this._title = arguments[0];
        return this;
    },
    click: function () {
        this._click = arguments[0];
        return this;
    },
    build: function () {
        var frame = $("<div>").addClass("editor-frame");

        this._createHeader().appendTo(frame);
        frame.click(this._click);
        return frame;
    },

    _createHeader: function () {
        var headerPh = $("<div>").addClass("header-ph");
        var header = $("<div>").addClass("header").appendTo(headerPh);

        this._createTitle().appendTo(header);
        return headerPh;
    },

    _createTitle: function () {
        return $("<div>").addClass("title").html(this._title);
    }
};