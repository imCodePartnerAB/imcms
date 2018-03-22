Imcms.define(
    "imcms-image-editor-left-side-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery", "imcms-events",
        "imcms-image-crop-angles", "imcms-image-cropping-elements"
    ],

    function (BEM, components, texts, $, events, croppingAngles, cropElements) {
        var isImgRotate = false;
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

        function setCropAreaHeight(newImageHeight, oldImgHeight) {
            var cropAreaHeight = cropElements.$cropArea.height();
            var cropAreaTop = cropElements.$cropArea.position().top;

            var newCropAreaHeight = (newImageHeight * cropAreaHeight) / oldImgHeight;
            var delta = 0;

            if ((newImageHeight + 2) < (cropAreaTop + newCropAreaHeight)) {
                delta = cropAreaTop + newCropAreaHeight - newImageHeight - 2;
            }

            if (newCropAreaHeight !== 0) {
                cropElements.$cropArea.height(newCropAreaHeight - delta);
                var newTop = cropAreaTop + newCropAreaHeight - delta - 18;
                croppingAngles.bottomRight.setTop(newTop);
                croppingAngles.bottomLeft.setTop(newTop);
            } else {
                cropElements.$cropArea.height(newImageHeight + 2);
                cropElements.$cropArea.css({"top": 2});
                cropElements.$cropImg.css({"top": 0});

                croppingAngles.topLeft.setTop(0);
                croppingAngles.topRight.setTop(0);
                croppingAngles.bottomLeft.setTop(newImageHeight - 16);
                croppingAngles.bottomRight.setTop(newImageHeight - 16);
            }
            events.trigger("update cropArea");
        }

        function setCropAreaWidth(newImageWidth, oldImgWidth) {
            var cropAreaWidth = cropElements.$cropArea.width();
            var cropAreaLeft = cropElements.$cropArea.offset().left;

            var newCropAreaWidth = (newImageWidth * cropAreaWidth) / oldImgWidth;
            var delta = 0;

            if ((newImageWidth + 2) < (cropAreaLeft + newCropAreaWidth)) {
                delta = cropAreaLeft + newCropAreaWidth - newImageWidth - 2;
            }

            if (newCropAreaWidth !== 0) {
                cropElements.$cropArea.width(newCropAreaWidth - delta);
                var newLeft = cropAreaLeft + newCropAreaWidth - delta - 18;

                croppingAngles.bottomRight.setLeft(newLeft);
                croppingAngles.topRight.setLeft(newLeft);

            } else {
                cropElements.$cropArea.width(newImageWidth + 2);
                cropElements.$cropArea.css({"left": 2});
                cropElements.$cropImg.css({"left": 0});

                croppingAngles.topLeft.setLeft(0);
                croppingAngles.bottomLeft.setLeft(0);
                croppingAngles.topRight.setLeft(newImageWidth - 16);
                croppingAngles.bottomRight.setLeft(newImageWidth - 16);
            }
            events.trigger("update cropArea");
        }

        function setWidth(newImageWidth, newCropImageWidth, newShadowWidth, imageDataContainers) {
            newShadowWidth = Math.max(newShadowWidth, imageDataContainers.$editableImageArea.width());

            cropElements.$image.width(newImageWidth);
            cropElements.$cropImg.width(newCropImageWidth);
            imageDataContainers.$shadow.width(newShadowWidth);
        }

        function setHeight(newImageHeight, newCropImageHeight, newShadowHeight, imageDataContainers) {
            newShadowHeight = Math.max(newShadowHeight, imageDataContainers.$editableImageArea.height());

            cropElements.$image.height(newImageHeight);
            cropElements.$cropImg.height(newCropImageHeight);
            imageDataContainers.$shadow.height(newShadowHeight);
        }

        function buildEditSizeControls(imageDataContainers) {
            var $title = components.texts.titleText("<div>", texts.displaySize);

            function onValidHeightChange() {
                var height = +$(this).val();

                if (isNaN(height)) {
                    return;
                }

                var previousHeight = cropElements.$cropImg.height();
                setHeight(height, height, height + 4, imageDataContainers);
                setCropAreaHeight(height, previousHeight, imageDataContainers);
            }

            imageDataContainers.$heightControlInput = components.texts.textNumber("<div>", {
                name: "height",
                placeholder: texts.height,
                text: "H",
                error: "Error",
                onValidChange: onValidHeightChange
            });

            var $proportionsBtn = components.buttons.proportionsButton({
                "data-state": "active",
                click: function () {
                    console.log("%c Not implemented: Lock/unlock image proportions!", "color: red");
                }
            });

            function onValidWidthChange() {
                var width = +$(this).val();

                if (isNaN(width)) {
                    return;
                }

                var previousWidth = cropElements.$cropImg.width();
                setWidth(width, width, width + 4, imageDataContainers);
                setCropAreaWidth(width, previousWidth, imageDataContainers);
            }

            imageDataContainers.$widthControlInput = components.texts.textNumber("<div>", {
                name: "width",
                placeholder: texts.width,
                text: "W",
                error: "Error",
                onValidChange: onValidWidthChange
            });

            return new BEM({
                block: "imcms-edit-size",
                elements: [
                    {"title": $title},
                    {"number": imageDataContainers.$widthControlInput},
                    {"button": $proportionsBtn},
                    {"number": imageDataContainers.$heightControlInput}
                ]
            }).buildBlockStructure("<div>");
        }

        function resizeImage(newWidth, newHeight, newCropAreaHeight, newCropAreaWeight, newCropAreaLeft, newCropAreaTop, imageDataContainers) {
            cropElements.$image.animate({
                "width": newWidth,
                "height": newHeight
            }, 200);

            imageDataContainers.$shadow.animate({
                "width": ((isImgRotate) ? newHeight : newWidth) + 4,
                "height": ((isImgRotate) ? newWidth : newHeight) + 4
            }, 200);

            cropElements.$cropImg.animate({
                "left": -newCropAreaLeft + 2,
                "top": -newCropAreaTop + 2,
                "width": newWidth,
                "height": newHeight
            }, 200);

            cropElements.$cropArea.animate({
                "left": newCropAreaLeft,
                "top": newCropAreaTop,
                "width": newCropAreaWeight,
                "height": newCropAreaHeight
            }, 200);

            var angleHeight = croppingAngles.bottomLeft.$angle.height();
            var angleWidth = croppingAngles.bottomLeft.$angle.width();
            var angleBorderSize = parseInt(croppingAngles.topLeft.$angle.css("border-width")) || 0;

            var positionTopForAngelsTop = newCropAreaTop - angleBorderSize;
            var positionTopForAngelsBottom = newCropAreaTop + newCropAreaHeight - angleHeight;
            var positionLeftForAngelsLeft = newCropAreaLeft - angleBorderSize;
            var positionLeftForAngelsRight = newCropAreaLeft + newCropAreaWeight - angleWidth;

            if (newCropAreaLeft <= 2) {
                positionLeftForAngelsLeft = 0;
            }

            if (newCropAreaTop <= 2) {
                positionTopForAngelsTop = 0;
            }

            croppingAngles.topRight.animate({
                "top": positionTopForAngelsTop,
                "left": positionLeftForAngelsRight
            }, 200);

            croppingAngles.bottomRight.animate({
                "top": positionTopForAngelsBottom,
                "left": positionLeftForAngelsRight
            }, 200);

            croppingAngles.topLeft.animate({
                "top": positionTopForAngelsTop,
                "left": positionLeftForAngelsLeft
            }, 200);

            croppingAngles.bottomLeft.animate({
                "top": positionTopForAngelsBottom,
                "left": positionLeftForAngelsLeft
            }, 200);

            setTimeout(function () {
                events.trigger("update cropArea");
            }, 250);
        }

        function zoom(zoomCoefficient, imageDataContainers) {
            var newHeight = ~~(cropElements.$image.height() * zoomCoefficient),
                newWidth = ~~(cropElements.$image.width() * zoomCoefficient),
                newCropAreaHeight = ~~(cropElements.$cropArea.height() * zoomCoefficient),
                newCropAreaWeight = ~~(cropElements.$cropArea.width() * zoomCoefficient),
                newCropAreaLeft = ~~(cropElements.$cropArea.position().left * zoomCoefficient),
                newCropAreaTop = ~~(cropElements.$cropArea.position().top * zoomCoefficient)
            ;

            if (newCropAreaLeft <= 2) {
                newCropAreaLeft = 2;
            }

            if (newCropAreaTop <= 2) {
                newCropAreaTop = 2;
            }

            resizeImage(newWidth, newHeight, newCropAreaHeight, newCropAreaWeight, newCropAreaLeft, newCropAreaTop, imageDataContainers);
            var heightControlInput = imageDataContainers.$heightControlInput.getInput();
            var widthControlInput = imageDataContainers.$widthControlInput.getInput();

            heightControlInput.val(newHeight);
            widthControlInput.val(newWidth);
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
            var newHeight = $editableImageArea.height(),
                newWidth = $editableImageArea.width(),
                newCropAreaHeight = $editableImageArea.height(),
                newCropAreaWeight = $editableImageArea.width(),
                newCropAreaLeft = 2,
                newCropAreaTop = 2
            ;
            var twiceAngleBorderSize = parseInt(croppingAngles.topLeft.$angle.css("border-width")) * 2 || 0;

            if (isImgRotate) {
                newCropAreaHeight = $editableImageArea.width();
                newCropAreaWeight = $editableImageArea.height();
            }

            resizeImage(
                newWidth - twiceAngleBorderSize,
                newHeight - twiceAngleBorderSize,
                newCropAreaHeight - twiceAngleBorderSize,
                newCropAreaWeight - twiceAngleBorderSize,
                newCropAreaLeft,
                newCropAreaTop,
                imageDataContainers
            );
            var heightControlInput = imageDataContainers.$heightControlInput.getInput();
            var widthControlInput = imageDataContainers.$widthControlInput.getInput();

            heightControlInput.val(newHeight);
            widthControlInput.val(newWidth);
        }

        var angle = 0;

        function rotateOnAngle(newAngle, imageDataContainers) {
            var style = {};
            imageDataContainers.rotateAngle = angle = newAngle;

            switch (angle) {
                case 90:
                case -270:
                    style = {
                        "transform": "rotate(" + angle + "deg) translateY(-100%)",
                        "transform-origin": "top left"
                    };
                    break;
                case 180:
                case -180:
                    style = {
                        "transform": "rotate(" + angle + "deg) translate(-100%, -100%)",
                        "transform-origin": "top left"
                    };
                    break;
                case 270:
                case -90:
                    style = {
                        "transform": "rotate(" + angle + "deg) translateX(-100%)",
                        "transform-origin": "top left"
                    };
                    break;
                default:
                    style = {
                        "transform": "rotate(" + angle + "deg)",
                        "transform-origin": "top left"
                    };
            }
            cropElements.$image.css(style);
            cropElements.$cropImg.css(style);

            isImgRotate = !isImgRotate;
            events.trigger("image rotated");

            var newWidth = cropElements.$image.width(),
                newHeight = cropElements.$image.height(),
                newCropAreaHeight = cropElements.$cropArea.width(),
                newCropAreaWeight = cropElements.$cropArea.height();

            // TODO: fix crop area position when it is out of img
            resizeImage(newWidth, newHeight, newCropAreaHeight, newCropAreaWeight, 2, 2, imageDataContainers);

            var heightControlInput = imageDataContainers.$heightControlInput.getInput();
            var widthControlInput = imageDataContainers.$widthControlInput.getInput();

            if (isImgRotate) {
                heightControlInput.val(newWidth);
                widthControlInput.val(newHeight);
            } else {
                heightControlInput.val(newHeight);
                widthControlInput.val(newWidth);
            }
        }

        function rotate(angleDelta, imageDataContainers) {
            angle += angleDelta;
            angle = ((angle === 360) || (angle === -360)) ? 0 : angle;

            rotateOnAngle(angle, imageDataContainers);
        }

        function rotateLeft() {
            rotate(-90, this);
        }

        function rotateRight() {
            rotate(90, this);
        }

        function buildScaleAndRotateControls(imageDataContainers, $editableImageArea) {
            events.on("rotate image NORTH", rotateOnAngle.bindArgs(0, imageDataContainers));
            events.on("rotate image EAST", rotateOnAngle.bindArgs(90, imageDataContainers));
            events.on("rotate image SOUTH", rotateOnAngle.bindArgs(180, imageDataContainers));
            events.on("rotate image WEST", rotateOnAngle.bindArgs(-90, imageDataContainers));

            return new BEM({
                block: "imcms-edit-image",
                elements: {
                    "button": [
                        components.buttons.zoomPlusButton({click: zoomPlus.bind(imageDataContainers)}),
                        components.buttons.zoomMinusButton({click: zoomMinus.bind(imageDataContainers)}),
                        components.buttons.zoomContainButton({
                            click: function () {
                                zoomContain(imageDataContainers, $editableImageArea);
                            }
                        }),
                        components.buttons.rotateLeftButton({click: rotateLeft.bind(imageDataContainers)}),
                        components.buttons.rotateRightButton({click: rotateRight.bind(imageDataContainers)})
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
                    "control-size": buildEditSizeControls(imageDataContainers),
                    "control-scale-n-rotate": buildScaleAndRotateControls(imageDataContainers, $editableImageArea),
                    "control-view": buildSwitchViewControls(toggleImgArea, imageDataContainers)
                }
            }).buildBlockStructure("<div>");
        }

        return {
            build: function (opts) {
                opts.imageDataContainers.$editableImageArea = buildEditableImageArea(opts.imageDataContainers);
                var $previewImageArea = buildPreviewImageArea(opts.imageDataContainers);

                opts.imageDataContainers.$bottomPanel = buildBottomPanel(
                    opts.toggleImgArea, opts.imageDataContainers, opts.imageDataContainers.$editableImageArea
                );

                return $("<div>").append(
                    opts.imageDataContainers.$editableImageArea,
                    $previewImageArea,
                    opts.imageDataContainers.$bottomPanel
                );
            }
        };
    }
);
