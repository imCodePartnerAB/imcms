Imcms.define(
    "imcms-image-editor-left-side-builder",
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery", "imcms-events"],
    function (BEM, components, texts, $, events) {
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

            imageDataContainers.$image = editableImgAreaBEM.buildElement("img", "<img>");
            imageDataContainers.$shadow = editableImgAreaBEM.buildElement("layout", "<div>");
            imageDataContainers.$cropArea = editableImgAreaBEM.buildElement("crop-area", "<div>")
                .append(imageDataContainers.$cropImg = $("<img>", {"class": "imcms-crop-area__crop-img"}));

            var angleAttributes = {
                style: "display: none;"
            };
            imageDataContainers.angles = {
                $topLeft: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["top-left"]),
                $topRight: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["top-right"]),
                $bottomLeft: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["bottom-left"]),
                $bottomRight: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["bottom-right"])
            };

            return editableImgAreaBEM.buildBlock("<div>", [
                {"img": imageDataContainers.$image},
                {"layout": imageDataContainers.$shadow},
                {"crop-area": imageDataContainers.$cropArea},
                {"angle": imageDataContainers.angles.$topLeft},
                {"angle": imageDataContainers.angles.$topRight},
                {"angle": imageDataContainers.angles.$bottomRight},
                {"angle": imageDataContainers.angles.$bottomLeft}
            ]);
        }

        function setCropAreaHeight(newImageHeight, oldImgHeight, imageDataContainers) {
            var cropAreaHeight = imageDataContainers.$cropArea.height();
            var cropAreaTop = imageDataContainers.$cropArea.position().top;

            var newCropAreaHeight = (newImageHeight * cropAreaHeight) / oldImgHeight;
            var delta = 0;

            if ((newImageHeight + 2) < (cropAreaTop + newCropAreaHeight)) {
                delta = cropAreaTop + newCropAreaHeight - newImageHeight - 2;
            }

            if (newCropAreaHeight !== 0) {
                imageDataContainers.$cropArea.height(newCropAreaHeight - delta);
                imageDataContainers.angles.$bottomRight.css({"top": (cropAreaTop + newCropAreaHeight - delta - 18) + "px"});
                imageDataContainers.angles.$bottomLeft.css({"top": (cropAreaTop + newCropAreaHeight - delta - 18) + "px"});
            } else {
                imageDataContainers.$cropArea.height(newImageHeight + 2);
                imageDataContainers.$cropArea.css({"top": 2 + "px"});
                imageDataContainers.$cropImg.css({"top": 0 + "px"});
                imageDataContainers.angles.$topLeft.css({"top": 0 + "px"});
                imageDataContainers.angles.$topRight.css({"top": 0 + "px"});
                imageDataContainers.angles.$bottomLeft.css({"top": (newImageHeight - 16) + "px"});
                imageDataContainers.angles.$bottomRight.css({"top": (newImageHeight - 16) + "px"});
            }
            events.trigger("update cropArea");
        }

        function setCropAreaWidth(newImageWidth, oldImgWidth, imageDataContainers) {
            var cropAreaWidth = imageDataContainers.$cropArea.width();
            var cropAreaLeft = imageDataContainers.$cropArea.offset().left;

            var newCropAreaWidth = (newImageWidth * cropAreaWidth) / oldImgWidth;
            var delta = 0;

            if ((newImageWidth + 2) < (cropAreaLeft + newCropAreaWidth)) {
                delta = cropAreaLeft + newCropAreaWidth - newImageWidth - 2;
            }

            if (newCropAreaWidth !== 0) {
                imageDataContainers.$cropArea.width(newCropAreaWidth - delta);
                imageDataContainers.angles.$bottomRight.css({"left": (cropAreaLeft + newCropAreaWidth - delta - 18) + "px"});
                imageDataContainers.angles.$topRight.css({"left": (cropAreaLeft + newCropAreaWidth - delta - 18) + "px"});
            } else {
                imageDataContainers.$cropArea.width(newImageWidth + 2);
                imageDataContainers.$cropArea.css({"left": 2 + "px"});
                imageDataContainers.$cropImg.css({"left": 0 + "px"});
                imageDataContainers.angles.$topLeft.css({"left": 0 + "px"});
                imageDataContainers.angles.$bottomLeft.css({"left": 0 + "px"});
                imageDataContainers.angles.$topRight.css({"left": (newImageWidth - 16) + "px"});
                imageDataContainers.angles.$bottomRight.css({"left": (newImageWidth - 16) + "px"});
            }
            events.trigger("update cropArea");
        }

        function setWidth(newImageWidth, newCropImageWidth, newShadowWidth, imageDataContainers) {
            newShadowWidth = Math.max(newShadowWidth, imageDataContainers.$editableImageArea.width());

            imageDataContainers.$image.width(newImageWidth);
            imageDataContainers.$cropImg.width(newCropImageWidth);
            imageDataContainers.$shadow.width(newShadowWidth);
        }

        function setHeight(newImageHeight, newCropImageHeight, newShadowHeight, imageDataContainers) {
            newShadowHeight = Math.max(newShadowHeight, imageDataContainers.$editableImageArea.height());

            imageDataContainers.$image.height(newImageHeight);
            imageDataContainers.$cropImg.height(newCropImageHeight);
            imageDataContainers.$shadow.height(newShadowHeight);
        }

        function buildEditSizeControls(imageDataContainers) {
            var $title = components.texts.titleText("<div>", texts.displaySize);

            function onValidHeightChange() {
                var height = +$(this).val();
                var previousHeight = imageDataContainers.$cropImg.height();

                if (isNaN(height)) {
                    return;
                }

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
                var previousWidth = imageDataContainers.$cropImg.width();

                if (isNaN(width)) {
                    return;
                }

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
                    {"number": imageDataContainers.$heightControlInput},
                    {"button": $proportionsBtn},
                    {"number": imageDataContainers.$widthControlInput}
                ]
            }).buildBlockStructure("<div>");
        }

        function resizeImage(newWidth, newHeight, newCropAreaHeight, newCropAreaWeight, newCropAreaLeft, newCropAreaTop, imageDataContainers) {
            imageDataContainers.$image
                .animate({
                    "width": newWidth,
                    "height": newHeight
                }, 200);

            imageDataContainers.$shadow.animate({
                "width": ((isImgRotate) ? newHeight : newWidth) + 4,
                "height": ((isImgRotate) ? newWidth : newHeight) + 4
            }, 200);

            imageDataContainers.$cropImg
                .animate({
                    "left": -newCropAreaLeft,
                    "top": -newCropAreaTop,
                    "width": newWidth,
                    "height": newHeight
                }, 200);

            imageDataContainers.$cropArea
                .animate({
                    "left": newCropAreaLeft,
                    "top": newCropAreaTop,
                    "width": newCropAreaWeight,
                    "height": newCropAreaHeight
                }, 200);

            var angleHeight = imageDataContainers.angles.$bottomLeft.height();
            var angleWidth = imageDataContainers.angles.$bottomLeft.width();
            var angleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) || 0;

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

            imageDataContainers.angles.$topRight.animate({
                "top": positionTopForAngelsTop,
                "left": positionLeftForAngelsRight
            }, 200);

            imageDataContainers.angles.$bottomRight.animate({
                "top": positionTopForAngelsBottom,
                "left": positionLeftForAngelsRight
            }, 200);

            imageDataContainers.angles.$topLeft.animate({
                "top": positionTopForAngelsTop,
                "left": positionLeftForAngelsLeft
            }, 200);

            imageDataContainers.angles.$bottomLeft.animate({
                "top": positionTopForAngelsBottom,
                "left": positionLeftForAngelsLeft
            }, 200);

            setTimeout(function () {
                events.trigger("update cropArea");
            }, 250);
        }

        function zoom(zoomCoefficient, imageDataContainers) {
            var newHeight = ~~(imageDataContainers.$image.height() * zoomCoefficient),
                newWidth = ~~(imageDataContainers.$image.width() * zoomCoefficient),
                newCropAreaHeight = ~~(imageDataContainers.$cropArea.height() * zoomCoefficient),
                newCropAreaWeight = ~~(imageDataContainers.$cropArea.width() * zoomCoefficient),
                newCropAreaLeft = ~~(imageDataContainers.$cropArea.position().left * zoomCoefficient),
                newCropAreaTop = ~~(imageDataContainers.$cropArea.position().top * zoomCoefficient)
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
            var twiceAngleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) * 2 || 0;

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
        }

        var angle = 0;

        function rotate(angleDelta, imageDataContainers) {
            angle += angleDelta;
            angle = ((angle === 360) || (angle === -360)) ? 0 : angle;

            var style = {};

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
            imageDataContainers.$image.css(style);
            imageDataContainers.$cropImg.css(style);

            isImgRotate = !isImgRotate;
            events.trigger("rotate img");

            var newWidth = imageDataContainers.$image.width(),
                newHeight = imageDataContainers.$image.height(),
                newCropAreaHeight = imageDataContainers.$cropArea.width(),
                newCropAreaWeight = imageDataContainers.$cropArea.height(),
                newCropAreaLeft = imageDataContainers.$cropArea.position().left,
                newCropAreaTop = imageDataContainers.$cropArea.position().top;

            // TODO: fix crop area position when it is out of img
            resizeImage(newWidth, newHeight, newCropAreaHeight, newCropAreaWeight, 2, 2, imageDataContainers);
        }

        function rotateLeft() {
            rotate(-90, this);
        }

        function rotateRight() {
            rotate(90, this);
        }

        function buildScaleAndRotateControls(imageDataContainers, $editableImageArea) {
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
