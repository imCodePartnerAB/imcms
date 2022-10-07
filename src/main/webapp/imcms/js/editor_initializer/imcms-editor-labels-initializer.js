define("imcms-editor-labels-initializer", ["jquery", "imcms-components-builder"], function ($, components) {
    return {
        initEditorLabels: function () {
            const editControls = $(".imcms-editor-area__control-wrap");

            function getParams(elem) {
                return {
                    width: elem.outerWidth(),
                    height: elem.outerHeight()
                };
            }

			//unused because of tippy usage
            function positioningLabel(event) {
                const $ctrl = $(this);
                const $label = $ctrl.children().first(),
                    mouseCoords = {
                        x: event.clientX,
                        y: event.clientY
                    },
                    ctrlParams = getParams($ctrl),
                    labelParam = getParams($label);

                const labelHeight = (ctrlParams.height + labelParam.height + 5);
                if (mouseCoords.y > labelHeight) { // 5px - height of label:before
                    $label.css({
                        "top": -(labelParam.height + 5) + "px"
                    });
                    $label.removeClass().addClass("imcms-editor-area__control-title")

                } else if (mouseCoords.y < labelHeight) {
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

                } else if ((mouseCoords.x - (labelParam.width / 2)) < 0) {
                    $label.css({
                        "left": 0
                    });
                    $label.addClass("imcms-editor-area__control-title--left");
                }

                $label.show();
            }

			function showLabel() {
				const $control = $(this);
				const label = $control.data("label");

				components.overlays.defaultTooltip($control, label, {delay: 0})
			}

	        editControls.children().each((index, control) => {
		        $(control).on("mouseenter", showLabel);
	        });

	        editControls.children().each((index, control) => {
		        $(control).on("mouseleave", function () {
			        $(this).children().first().hide();
		        });
	        });
        }
    };
});
