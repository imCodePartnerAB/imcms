Imcms.define(
    "imcms-image-editor-left-side-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-image-crop-angles", "imcms-image-cropping-elements", "imcms-image-rotate", "imcms-image-resize",
        "imcms-image-edit-size-controls"
    ],

    function (BEM, components, texts, $, croppingAngles, cropElements, imageRotate, imageResize, imageEditSizeControls) {
        texts = texts.editors.image;

        function buildPreviewImageArea(imageDataContainers) {
            var previewImageAreaBEM = new BEM({
                block: "imcms-preview-img-area",
                elements: {
                    "container": "imcms-preview-img-container",
                    "img": "imcms-preview-img"
                }
            });

            imageDataContainers.$previewImgContainer = previewImageAreaBEM.buildElement("container", "<div>");
            imageDataContainers.$previewImg = previewImageAreaBEM.buildElement("img", "<img>");
            imageDataContainers.$previewImg.appendTo(imageDataContainers.$previewImgContainer);

            return previewImageAreaBEM.buildBlock("<div>", [
                {"container": imageDataContainers.$previewImgContainer}
            ]);
        }

        function buildEditableImageArea(imageDataContainers) {
            var editableImgAreaBEM = new BEM({
                block: "imcms-editable-img-area",
                elements: {
                    "img": "imcms-editable-img",
                    "layout": "",
                    "crop-area": "imcms-crop-area",
                    "angle": "imcms-angle"
                }
            });

            imageDataContainers.$shadow = editableImgAreaBEM.buildElement("layout", "<div>");

            return editableImgAreaBEM.buildBlock("<div>", [
                {"img": cropElements.buildImage()},
                {"layout": imageDataContainers.$shadow},
                {"crop-area": cropElements.buildCropArea()},
                {"angle": croppingAngles.topLeft.buildAngle()},
                {"angle": croppingAngles.topRight.buildAngle()},
                {"angle": croppingAngles.bottomRight.buildAngle()},
                {"angle": croppingAngles.bottomLeft.buildAngle()}
            ]);
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

        function buildBottomPanel(toggleImgArea, imageDataContainers, $editableImageArea) {
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

        return {
            build: function (opts) {
                opts.imageDataContainers.$editableImageArea = buildEditableImageArea(opts.imageDataContainers);
                var $previewImageArea = buildPreviewImageArea(opts.imageDataContainers);

                opts.imageDataContainers.$toolbar = buildBottomPanel(
                    opts.toggleImgArea, opts.imageDataContainers, opts.imageDataContainers.$editableImageArea
                );

                return $("<div>").append(
                    opts.imageDataContainers.$editableImageArea,
                    $previewImageArea,
                    opts.imageDataContainers.$toolbar
                );
            }
        };
    }
);
