Imcms.define(
    "imcms-image-editor-body-head-builder",
    ["imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery"],
    function (texts, BEM, components, $) {

        texts = texts.editors.image;

        function showHidePanel(panelOpts) {
            var panelAnimationOpts = {};

            if (panelOpts.$btn.data("state")) {
                panelAnimationOpts[panelOpts.panelSide] = "-" + panelOpts.newPanelSideValue + "px";
                panelOpts.$panel.animate(panelAnimationOpts, 300);
                panelOpts.$btn.data("state", false);
                panelOpts.$btn.text(texts.panels.bottom.show);

            } else {
                panelAnimationOpts[panelOpts.panelSide] = 0;
                panelOpts.$panel.animate(panelAnimationOpts, 300);
                panelOpts.$btn.data("state", true);
                panelOpts.$btn.text(texts.panels.bottom.hide);
            }
        }

        function showHideRightPanel(imageDataContainers) {
            showHidePanel({
                $btn: $(this),
                newPanelSideValue: imageDataContainers.$rightSidePanel.width(),
                $panel: imageDataContainers.$rightSidePanel,
                panelSide: "right",
                textHide: texts.panels.right.hide,
                textShow: texts.panels.right.show
            });
        }

        function showHideBottomPanel(imageDataContainers) {
            showHidePanel({
                $btn: $(this),
                newPanelSideValue: imageDataContainers.$bottomPanel.height(),
                $panel: imageDataContainers.$bottomPanel,
                panelSide: "bottom",
                textHide: texts.panels.bottom.hide,
                textShow: texts.panels.bottom.show
            });
        }

        function buildHeightWidthBlock(imageDataContainers) {
            var $heightBlock = new BEM({
                block: "imcms-img-origin-size",
                elements: {
                    "height-title": components.texts.titleText("<span>", "H:"),
                    "height-value": imageDataContainers.$heightValue = components.texts.titleText("<span>")
                }
            }).buildBlockStructure("<div>");

            var $widthBlock = new BEM({
                block: "imcms-img-origin-size",
                elements: {
                    "width-title": components.texts.titleText("<span>", "W:"),
                    "width-value": imageDataContainers.$widthValue = components.texts.titleText("<span>")
                }
            }).buildBlockStructure("<div>");

            return new BEM({
                block: "imcms-title imcms-image-characteristic",
                elements: {
                    "origin-size": [$heightBlock, $widthBlock]
                }
            }).buildBlockStructure("<div>", {text: "Orig "});
        }

        return {
            build: function (opts) {
                var bodyHeadBEM = new BEM({
                    block: "imcms-image-characteristics",
                    elements: {
                        "button": "imcms-image-characteristic",
                        "img-title": "imcms-title imcms-image-characteristic",
                        "img-url": "imcms-title imcms-image-characteristic",
                        "img-origin-size": "imcms-title imcms-image-characteristic"
                    }
                });

                var $showHideBottomPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.bottom.show,
                    click: function () {
                        showHideBottomPanel.call(this, opts.imageDataContainers);
                    }
                });

                opts.imageDataContainers.$imageTitle = bodyHeadBEM.buildElement("img-title", "<div>");

                var $showHideRightPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.right.show,
                    click: function () {
                        showHideRightPanel.call(this, opts.imageDataContainers);
                    }
                });

                var $imgUrl = bodyHeadBEM.buildElement("img-url", "<div>", {
                    text: "Url: "
                }).append(opts.imageDataContainers.$imgUrl = $("<span>"));

                var $heightWidthBlock = buildHeightWidthBlock(opts.imageDataContainers);

                return bodyHeadBEM.buildBlock("<div>", [
                    {
                        "button": $showHideBottomPanelBtn,
                        modifiers: ["bottom-panel"]
                    }, {
                        "img-title": opts.imageDataContainers.$imageTitle
                    }, {
                        "button": $showHideRightPanelBtn,
                        modifiers: ["right-panel"]
                    }, {
                        "img-url": $imgUrl
                    }, {
                        "img-origin-size": $heightWidthBlock
                    }
                ]);
            }
        }
    }
);
