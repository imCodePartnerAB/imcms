define(
    "imcms-image-editor-body-head-builder",
    [
        "imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery", 'imcms-image-edit-size-controls',
        "imcms-image-rotate", "imcms-image-resize", "imcms-image-crop-angles", "imcms-image-cropping-elements"
    ],
    function (texts, BEM, components, $, imageEditSizeControls, imageRotate, imageResize, croppingAngles, cropElements) {

        texts = texts.editors.image;

        function showHidePanel(panelOpts) {
            const panelAnimationOpts = {};

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
            const $heightBlock = new BEM({
                block: "imcms-img-origin-size",
                elements: {
                    "height-title": components.texts.titleText("<span>", "H:"),
                    "height-value": imageDataContainers.$heightValue = components.texts.titleText("<span>")
                }
            }).buildBlockStructure("<div>");

            const $widthBlock = new BEM({
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
            imageResize.resize(opts, opts.imageDataContainers, opts.isProportionsInverted);
            opts.imageDataContainers.$heightControlInput.getInput().val(opts.image.height);
            opts.imageDataContainers.$widthControlInput.getInput().val(opts.image.width);
        }

        function zoom(zoomCoefficient, imageDataContainers) {
            const newHeight = ~~(cropElements.$image.height() * zoomCoefficient);
            const newWidth = ~~(cropElements.$image.width() * zoomCoefficient);
            const newCropAreaHeight = ~~(cropElements.$cropArea.height() * zoomCoefficient);
            const newCropAreaWeight = ~~(cropElements.$cropArea.width() * zoomCoefficient);
            let newCropAreaLeft = ~~(cropElements.$cropArea.position().left * zoomCoefficient);
            let newCropAreaTop = ~~(cropElements.$cropArea.position().top * zoomCoefficient);
            const borderSize = croppingAngles.getBorderSize();

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
            const proportionsInverted = imageRotate.isProportionsInverted();
            const newHeight = $editableImageArea.height();
            const newWidth = $editableImageArea.width();
            const newCropAreaHeight = (proportionsInverted) ? $editableImageArea.width() : $editableImageArea.height();
            const newCropAreaWeight = (proportionsInverted) ? $editableImageArea.height() : $editableImageArea.width();
            const newCropAreaLeft = croppingAngles.getBorderSize();
            const newCropAreaTop = croppingAngles.getBorderSize();
            const twiceAngleBorderSize = croppingAngles.getDoubleBorderSize();

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

            const newWidth = imageDataContainers.original.width;
            const newHeight = imageDataContainers.original.height;

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

        function buildSwitchViewControls(toggleImgArea, imageDataContainers) {
            const $preview = components.texts.titleText("<div>", texts.preview, {
                "data-tab": "prev",
                click: toggleImgArea
            });
            const $origin = components.texts.titleText("<div>", texts.original, {
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
            let sizeControls;

            const onCancel = () => {
                imageRotate.rotateImageByDegrees(previousRotateDegrees);
                onApply();
            };

            const onApply = () => {
                $(cancelChangesButton).add(applyChangesButton)
                    .add(rotateLeftButton)
                    .add(rotateRightButton)
                    .slideUp('fast', () => {
                        $(zoomPlusButton).add(zoomMinusButton)
                            .add(zoomContainButton)
                            .add(showImageRotationControls)
                            .add(sizeControls)
                            .add(revertButton)
                            .slideDown();
                    });
            };

            let previousRotateDegrees = null;

            const onRotationActivated = () => {
                previousRotateDegrees = parseInt(cropElements.$image[0].style.transform.split(' ')[0].replace(/\D/g, ''));

                $(zoomPlusButton).add(zoomMinusButton)
                    .add(zoomContainButton)
                    .add(showImageRotationControls)
                    .add(sizeControls)
                    .add(revertButton)
                    .slideUp('fast', () => {
                        $(cancelChangesButton).add(applyChangesButton)
                            .add(rotateLeftButton)
                            .add(rotateRightButton)
                            .slideDown();
                    });
            };

            const cancelChangesButton = components.buttons.negativeButton({
                text: 'Cancel', //todo: localize!!!111
                title: 'Cancel changes', //todo: localize!!!111
                click: onCancel,
                style: 'display: none;',
            });

            const applyChangesButton = components.buttons.saveButton({
                text: 'Apply',//todo: localize!!!111
                title: 'Apply changes', //todo: localize!!!111
                click: onApply,
                style: 'display: none;',
            });

            const showImageRotationControls = components.buttons.rotationButton({
                title: 'Activate rotation controls',//todo: localize!!!111
                click: onRotationActivated,
            });

            // const showImageCropControls = components.buttons.

            const zoomPlusButton = components.buttons.zoomPlusButton({
                title: texts.buttons.zoomIn,
                click: zoomPlus.bind(imageDataContainers),
            });
            const zoomMinusButton = components.buttons.zoomMinusButton({
                title: texts.buttons.zoomOut,
                click: zoomMinus.bind(imageDataContainers),
            });
            const zoomContainButton = components.buttons.zoomContainButton({ // change to "unzoom" or smth like that
                title: texts.buttons.zoomContain,
                click: function () {
                    zoomContain(imageDataContainers, $editableImageArea);
                },
            });
            const rotateLeftButton = components.buttons.rotateLeftButton({
                title: texts.buttons.rotateLeft,
                click: imageRotate.rotateLeft,
                style: 'display: none;',
            });
            const rotateRightButton = components.buttons.rotateRightButton({
                title: texts.buttons.rotateRight,
                click: imageRotate.rotateRight,
                style: 'display: none;',
            });

            const revertButton = components.buttons.negativeButton({
                text: "Revert",
                click: function () {
                    revertImageChanges(imageDataContainers);
                }
            });

            function buildScaleAndRotateControls(imageDataContainers) {
                imageRotate.setDataContainers(imageDataContainers);

                return new BEM({
                    block: "imcms-edit-image",
                    elements: {
                        "button": [
                            zoomPlusButton,
                            zoomMinusButton,
                            zoomContainButton,
                            showImageRotationControls,
                            rotateLeftButton,
                            rotateRightButton,
                        ]
                    }
                }).buildBlockStructure("<div>");
            }

            return new BEM({
                block: "imcms-editable-img-controls",
                elements: {
                    "control-size": '',
                    'control-button': '',
                    "control-scale-n-rotate": '',
                    "control-view": '',
                }
            }).buildBlock("<div>", [
                {"control-size": sizeControls = imageEditSizeControls.buildEditSizeControls(imageDataContainers)},
                {'control-button': cancelChangesButton},
                {"control-scale-n-rotate": buildScaleAndRotateControls(imageDataContainers)},
                {"control-button": applyChangesButton},
                {"control-button": revertButton},
                {"control-view": buildSwitchViewControls(toggleImgArea, imageDataContainers)}
            ]);
        }

        let $imgUrl;

        return {
            build: function (opts, $rightSidePanel) {
                const bodyHeadBEM = new BEM({
                    block: "imcms-image-toolbar",
                    elements: {
                        "button": "imcms-image-characteristic",
                        "img-title": "imcms-title imcms-image-characteristic",
                        "img-url": "imcms-title imcms-image-characteristic",
                        "img-origin-size": "imcms-title imcms-image-characteristic"
                    }
                });

                const $showHideRightPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.right.show,
                    click: function () {
                        showHideRightPanel.call(this, $rightSidePanel);
                    }
                });

                $imgUrl = bodyHeadBEM.buildElement("img-url", "<div>", {
                    text: "Url: "
                }).append(opts.imageDataContainers.$imgUrl = $("<a>"));

                const $heightWidthBlock = buildHeightWidthBlock(opts.imageDataContainers);

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
                return $imgUrl.find("a").attr('data-name')
            }
        }
    }
);
