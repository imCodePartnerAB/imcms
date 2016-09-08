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
    bootstrap: function (editmode) {
        if (editmode) {
            $("body").css({paddingLeft: 150, width: $(window).width() - 150});
        } else {
            if ($("body").css('paddingLeft').length > 0){
                   $("body").removeAttr('style');
            }
        }

        //Init of internalization plugin
        $.i18n.properties({
            name: 'imcms',
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
        Imcms.Editors.Loop = new Imcms.Loop.Loader();
        Imcms.Editors.Menu = new Imcms.Menu.Loader();
        Imcms.Editors.Text = new Imcms.Text.Editor();
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
            setTimeout(function () {
                frame.css({left: 3 - frame.offset().left});
                this.positioningFrame(frame)
            }.bind(this), 50)
        }.bind(this)).css({left: 0}).hover(function () {
            frame.parent().css({outline: "1px solid #0091e1"})
        }, function () {
            frame.parent().css({outline: "none"})
        });
        return frame;
    },
    positioningFrame: function ($frame) {
        var changedPosition = false,
            frameOffset = undefined,
            frameRect = undefined;

        do {
            changedPosition = false;
            frameOffset = $frame.offset();
            frameRect = {
                left: frameOffset.left,
                top: frameOffset.top,
                right: frameOffset.left + $frame.width(),
                bottom: frameOffset.top + $frame.height()
            };
            $frame.touching(".editor-frame").filter("[data-configured]:visible").sort(function (a, b) {
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
                var elementTop;
                element = $(element);
                if ((elementTop = element.offset().top + element.height()) > frameRect.top) {
                    var diff = elementTop - frameRect.top;
                    frameRect.top += diff;
                    frameRect.bottom += diff;
                    changedPosition = true;
                }
            });
            $frame.attr("data-configured", "");
            if (changedPosition) {
                $frame.offset(frameRect);
            }
        } while (changedPosition && $frame.is(":visible"))
    },
    _createHeader: function () {
        var headerPh = $("<div>").addClass("header-ph");
        var header = $("<div>").addClass("imcms-header").appendTo(headerPh);

        this._createTitle().appendTo(header);
        return headerPh;
    },
    _createTitle: function () {
        return $("<div>").addClass("imcms-title").html(this._title);
    }
};

Imcms.Events = {
    addEvent: function (name, detail) {
        window.dispatchEvent(new CustomEvent(name), {
            detail: detail
        });
    },

    on: function (name, callback) {
        window.addEventListener(name, callback);
    }
};
