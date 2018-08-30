define(
    "imcms-image-editor-body-head-builder",
    [
        "imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery", 'imcms-image-edit-size-controls',
        "imcms-image-rotate", "imcms-image-resize", "imcms-image-crop-angles", "imcms-image-cropping-elements"
    ],
    function (texts, BEM, components, $, imageEditSizeControls, imageRotate, imageResize, croppingAngles, cropElements) {

        texts = texts.editors.image;

        function showHidePanel(panelOpts) {
            var panelAnimationOpts = {};

            if (panelOpts.$btn.data("state")) {
                panelAnimationOpts[panelOpts.panelSide] = "-" + panelOpts.newPanelSideValue + "px";
                panelOpts.$panel.animate(panelAnimationOpts, 300);
                panelOpts.$btn.data("state", false);
                panelOpts.$btn.text(panelOpts.textShow);

            } else {
                panelAnimationOpts[panelOpts.panelSide] = 0;
                panelOpts.$panel.animate(panelAnimationOpts, 300);
                panelOpts.$btn.data("state", true);
                panelOpts.$btn.text(panelOpts.textHide);
            }
        }

        function showHideRightPanel($rightSidePanel) {
            showHidePanel({
                $btn: $(this),
                newPanelSideValue: $rightSidePanel.width(),
                $panel: $rightSidePanel,
                panelSide: "right",
                textHide: texts.panels.right.hide,
                textShow: texts.panels.right.show
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

        function resizeImage(opts) {
            imageResize.resize(opts, opts.imageDataContainers, false);
            opts.imageDataContainers.$heightControlInput.getInput().val(opts.image.height);
            opts.imageDataContainers.$widthControlInput.getInput().val(opts.image.width);
        }

        function zoom(zoomCoefficient, imageDataContainers) {
            var newHeight = ~~(cropElements.$image.height() * zoomCoefficient),
                newWidth = ~~(cropElements.$image.width() * zoomCoefficient),
                newCropAreaHeight = ~~(cropElements.$cropArea.height() * zoomCoefficient),
                newCropAreaWeight = ~~(cropElements.$cropArea.width() * zoomCoefficient),
                newCropAreaLeft = ~~(cropElements.$cropArea.position().left * zoomCoefficient),
                newCropAreaTop = ~~(cropElements.$cropArea.position().top * zoomCoefficient)
            ;

            var borderSize = croppingAngles.getBorderSize();

            if (newCropAreaLeft <= borderSize) {
                newCropAreaLeft = borderSize;
            }

            if (newCropAreaTop <= borderSize) {
                newCropAreaTop = borderSize;
            }

            resizeImage({
                imageDataContainers: imageDataContainers,
                isProportionsInverted: imageRotate.isProportionsInverted(),
                image: {
                    width: newWidth,
                    height: newHeight
                },
                cropArea: {
                    height: newCropAreaHeight,
                    width: newCropAreaWeight,
                    top: newCropAreaTop,
                    left: newCropAreaLeft
                }
            });
        }

        function zoomPlus() {
            zoom(1.1, this);
        }

        function zoomMinus() {
            zoom(0.9, this);
        }

        function zoomContain(imageDataContainers, $editableImageArea) {
            // fixme: save proportions! now image becomes just as editable area
            // only one side should be as area's side and one as needed to save proportions
            var proportionsInverted = imageRotate.isProportionsInverted();
            var newHeight = $editableImageArea.height(),
                newWidth = $editableImageArea.width(),
                newCropAreaHeight = (proportionsInverted)
                    ? $editableImageArea.width() : $editableImageArea.height(),
                newCropAreaWeight = (proportionsInverted)
                    ? $editableImageArea.height() : $editableImageArea.width(),
                newCropAreaLeft = croppingAngles.getBorderSize(),
                newCropAreaTop = croppingAngles.getBorderSize(),
                twiceAngleBorderSize = croppingAngles.getDoubleBorderSize()
            ;

            resizeImage({
                imageDataContainers: imageDataContainers,
                isProportionsInverted: proportionsInverted,
                image: {
                    width: newWidth - twiceAngleBorderSize,
                    height: newHeight - twiceAngleBorderSize
                },
                cropArea: {
                    height: newCropAreaHeight - twiceAngleBorderSize,
                    width: newCropAreaWeight - twiceAngleBorderSize,
                    top: newCropAreaTop,
                    left: newCropAreaLeft
                }
            });
        }

        function revertImageChanges(imageDataContainers) {
            imageRotate.rotateImage("NORTH");

            var newWidth = imageDataContainers.original.width;
            var newHeight = imageDataContainers.original.height;

            resizeImage({
                imageDataContainers: imageDataContainers,
                isProportionsInverted: false,
                image: {
                    width: newWidth,
                    height: newHeight
                },
                cropArea: {
                    height: newHeight,
                    width: newWidth,
                    top: croppingAngles.getBorderSize(),
                    left: croppingAngles.getBorderSize()
                }
            });
        }

        function buildRevertButton(imageDataContainers) {
            return components.buttons.negativeButton({
                text: "Revert",
                click: function () {
                    revertImageChanges(imageDataContainers);
                }
            })
        }

        function buildScaleAndRotateControls(imageDataContainers, $editableImageArea) {
            imageRotate.setDataContainers(imageDataContainers);

            return new BEM({
                block: "imcms-edit-image",
                elements: {
                    "button": [
                        components.buttons.zoomPlusButton({
                            title: texts.buttons.zoomIn,
                            click: zoomPlus.bind(imageDataContainers)
                        }),
                        components.buttons.zoomMinusButton({
                            title: texts.buttons.zoomOut,
                            click: zoomMinus.bind(imageDataContainers)
                        }),
                        components.buttons.zoomContainButton({
                            title: texts.buttons.zoomContain,
                            click: function () {
                                zoomContain(imageDataContainers, $editableImageArea);
                            }
                        }),
                        components.buttons.rotateLeftButton({
                            title: texts.buttons.rotateLeft,
                            click: imageRotate.rotateLeft
                        }),
                        components.buttons.rotateRightButton({
                            title: texts.buttons.rotateRight,
                            click: imageRotate.rotateRight
                        })
                    ]
                }
            }).buildBlockStructure("<div>");
        }

        function buildSwitchViewControls(toggleImgArea, imageDataContainers) {
            var $preview = components.texts.titleText("<div>", texts.preview, {
                "data-tab": "prev",
                click: toggleImgArea
            });
            var $origin = components.texts.titleText("<div>", texts.original, {
                "data-tab": "origin",
                click: toggleImgArea
            });
            $origin.modifiers = ["active"];

            imageDataContainers.$tabOriginal = $origin;

            return new BEM({
                block: "imcms-editable-img-control-tabs",
                elements: {
                    "tab": [$preview, $origin]
                }
            }).buildBlockStructure("<div>");
        }

        function buildToolbar(toggleImgArea, imageDataContainers, $editableImageArea) {
            return new BEM({
                block: "imcms-editable-img-controls",
                elements: {
                    "control-size": imageEditSizeControls.buildEditSizeControls(imageDataContainers),
                    "control-scale-n-rotate": buildScaleAndRotateControls(imageDataContainers, $editableImageArea),
                    "image-revert": buildRevertButton(imageDataContainers),
                    "control-view": buildSwitchViewControls(toggleImgArea, imageDataContainers)
                }
            }).buildBlockStructure("<div>");
        }

        var $imgUrl;

        return {
            build: function (opts, $rightSidePanel) {
                var bodyHeadBEM = new BEM({
                    block: "imcms-image-toolbar",
                    elements: {
                        "button": "imcms-image-characteristic",
                        "img-title": "imcms-title imcms-image-characteristic",
                        "img-url": "imcms-title imcms-image-characteristic",
                        "img-origin-size": "imcms-title imcms-image-characteristic"
                    }
                });

                opts.imageDataContainers.$imageTitle = bodyHeadBEM.buildElement("img-title", "<div>");

                var $showHideRightPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.right.show,
                    click: function () {
                        showHideRightPanel.call(this, $rightSidePanel);
                    }
                });

                $imgUrl = bodyHeadBEM.buildElement("img-url", "<div>", {
                    text: "Url: "
                }).append(opts.imageDataContainers.$imgUrl = $("<span>"));

                var $heightWidthBlock = buildHeightWidthBlock(opts.imageDataContainers);

                opts.imageDataContainers.$toolbar = buildToolbar(
                    opts.toggleImgArea, opts.imageDataContainers, opts.imageDataContainers.$editableImageArea
                );

                return bodyHeadBEM.buildBlock("<div>", [
                    {
                        "toolbar": opts.imageDataContainers.$toolbar
                    }, {
                        "button": $showHideRightPanelBtn,
                        modifiers: ["right-panel"]
                    }, {
                        "img-url": $imgUrl
                    }, {
                        "img-origin-size": $heightWidthBlock
                    }
                ]);
            },
            getImageUrl: function () {
                return $imgUrl.find("span").text()
            }
        }
    }
);
