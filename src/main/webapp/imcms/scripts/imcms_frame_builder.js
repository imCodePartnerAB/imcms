(function (Imcms) {
    Imcms.FrameBuilder = function () {
    };

    Imcms.FrameBuilder.prototype = {
        _click: function () {
        },
        _title: "",
        _tooltip: "",
        title: function () {
            this._title = arguments[0];
            return this;
        },
        tooltip: function () {
            this._tooltip = arguments[0];
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
            var $title = $("<div>").addClass("imcms-title").html(this._title);

            return (this._tooltip !== "")
                ? $title.attr("title", this._tooltip)
                : $title;
        }
    };

    return Imcms;
})(Imcms);
