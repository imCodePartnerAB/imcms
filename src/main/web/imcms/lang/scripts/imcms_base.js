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
        Imcms.Editors.Loop = new Imcms.Loop.Loader();
        Imcms.Editors.Menu = new Imcms.Menu.Loader();
        Imcms.Editors.Text = new Imcms.Text.Editor();
        Imcms.Editors.Language = new Imcms.Language.Loader();
        Imcms.Editors.Template = new Imcms.Template.Loader();
        Imcms.Editors.Role = new Imcms.Role.Loader();
        Imcms.Editors.Permission = new Imcms.Permission.Loader();
        Imcms.Editors.Category = new Imcms.Category.Loader();
        Imcms.Editors.Document = new Imcms.Document.Loader();
        Imcms.Editors.File = new Imcms.File.Loader();
        Imcms.Editors.Folder = new Imcms.Folder.Loader();
        Imcms.Editors.Content = new Imcms.Content.Loader();
        Imcms.Editors.Image = new Imcms.Image.Loader();

        Imcms.Admin.Panel.init();
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
        frame.click(this._click).ready(function () {
            setTimeout(this.positioningFrame.bind(this, frame), 50)
        }.bind(this));
        return frame;
    },
    positioningFrame: function ($frame) {
        var changedPosition = false,
            frameOffset = $frame.offset(),
            frameRect = {
                left: frameOffset.left,
                top: frameOffset.top,
                right: frameOffset.left + $frame.width(),
                bottom: frameOffset.top + $frame.height()
            };
        var test = $frame.touching(".editor-frame");
        $frame.touching(".editor-frame").filter("[data-configured]").sort(function (a, b) {
            var $a = $(a), $b = $(b),
                offsetA = $a.offset(),
                offsetB = $b.offset(),
                rightA = offsetA.left + $a.width(),
                rightB = offsetB.left + $b.width();

            if (rightA > rightB) {
                return 1;
            }
            if (rightA < rightB) {
                return -1;
            }
            return 0;
        }).each(function (position, element) {
            var elementRight;
            element = $(element);
            if ((elementRight = element.offset().left + element.width()) > frameRect.left) {
                var diff = elementRight - frameRect.left;
                frameRect.left += diff;
                frameRect.right += diff;
                changedPosition = true;
            }
        });
        $frame.attr("data-configured", "");
        if (changedPosition) {
            $frame.offset(frameRect);
            this.positioningFrame($frame);
        }
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