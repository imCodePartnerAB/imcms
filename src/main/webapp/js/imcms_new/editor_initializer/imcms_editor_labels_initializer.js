Imcms.define("imcms-editor-labels-initializer", ["jquery"], function ($) {
    return {
        initEditorLabels: function () {
            var editControls = $(".imcms-editor-area__control-wrap")
            ;

            function getParams(elem) {
                return {
                    width: elem.outerWidth(),
                    height: elem.outerHeight()
                };
            }

            function positioningLabel($ctrl, event) {
                var $label = $ctrl.find(".imcms-editor-area__control-title"),
                    $labelBefore$ctrl = $ctrl.find(".imcms-editor-area__control-title:before"),
                    mouseCoords = {
                        x: event.clientX,
                        y: event.clientY
                    },
                    ctrlParams = getParams($ctrl),
                    labelParam = getParams($label)
                ;

                console.log("$labelBefore$ctrl: ", $labelBefore$ctrl);

                if (mouseCoords.y > (ctrlParams.height + labelParam.height + 5)) { // 5px - height of label:before
                    $label.css({
                        "top": -(labelParam.height + 5) + "px"
                    });
                    $label.removeClass().addClass("imcms-editor-area__control-title")
                }

                if (mouseCoords.y < (ctrlParams.height + labelParam.height + 5)) {
                    $label.css({
                        "top": ctrlParams.height + 5 + "px"
                    });
                    $label.addClass("imcms-editor-area__control-title--top");
                }

                if ((mouseCoords.x + (labelParam.width / 2)) > $(window).width()) {
                    $label.css({
                        "left": $label.position().left - (labelParam.width - ctrlParams.width)
                    });
                    $label.addClass("imcms-editor-area__control-title--right");
                }

                if ((mouseCoords.x - (labelParam.width / 2)) < 0) {
                    $label.css({
                        "left": 0
                    });
                    $label.addClass("imcms-editor-area__control-title--left");
                }

                $label.show();
            }

            editControls.on("mouseenter", function (event) {
                positioningLabel($(this), event);
            });

            editControls.on("mouseleave", function () {
                $(this).find(".imcms-editor-area__control-title").hide();
            })
        }
    }
});
