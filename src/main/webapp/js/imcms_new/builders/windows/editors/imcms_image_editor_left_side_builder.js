Imcms.define(
    "imcms-image-editor-left-side-builder",
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery"],
    function (BEM, components, texts, $) {

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

        function changeHeight(step, imageDataContainers) {
            imageDataContainers.$image.height(imageDataContainers.$image.height() + step);
            // imageDataContainers.$shadow.height(imageDataContainers.$shadow.height() + step);
            imageDataContainers.$cropImg.height(imageDataContainers.$cropImg.height() + step);
        }

        function incrementHeight() {
            changeHeight(1, this);
        }

        function decrementHeight() {
            changeHeight(-1, this)
        }

        function changeWidth(step, imageDataContainers) {
            imageDataContainers.$image.width(imageDataContainers.$image.width() + step);
            // imageDataContainers.$shadow.width(imageDataContainers.$shadow.width() + step);
            imageDataContainers.$cropImg.width(imageDataContainers.$cropImg.width() + step);
        }

        function incrementWidth() {
            changeWidth(1, this);
        }

        function decrementWidth() {
            changeWidth(-1, this)
        }

        function buildEditSizeControls(imageDataContainers) {
            var $title = components.texts.titleText("<div>", texts.displaySize);

            imageDataContainers.$heightControlInput = components.texts.textNumber("<div>", {
                name: "height",
                placeholder: texts.height,
                text: "H",
                error: "Error"
            });

            imageDataContainers.$heightControlInput.find(".imcms-button--increment").click(incrementHeight.bind(imageDataContainers));
            imageDataContainers.$heightControlInput.find(".imcms-button--decrement").click(decrementHeight.bind(imageDataContainers));

            var $proportionsBtn = components.buttons.proportionsButton({
                "data-state": "active",
                click: function () {
                    console.log("%c Not implemented: Lock/unlock image proportions!", "color: red");
                }
            });

            imageDataContainers.$widthControlInput = components.texts.textNumber("<div>", {
                name: "width",
                placeholder: texts.width,
                text: "W",
                error: "Error"
            });

            imageDataContainers.$widthControlInput.find(".imcms-button--increment").click(incrementWidth.bind(imageDataContainers));
            imageDataContainers.$widthControlInput.find(".imcms-button--decrement").click(decrementWidth.bind(imageDataContainers));

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

        function resizeImage(newWidth, newHeight, imageDataContainers) {
            imageDataContainers.$image.add(imageDataContainers.$cropImg)
                .add(imageDataContainers.$cropArea)
                .animate({
                    "width": newWidth,
                    "height": newHeight
                }, 200);

            var angleHeight = imageDataContainers.angles.$bottomLeft.height();
            var angleWidth = imageDataContainers.angles.$bottomLeft.width();
            var angleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) || 0;

            imageDataContainers.$cropArea.add(imageDataContainers.$image)
                .animate({
                    "top": angleBorderSize,
                    "left": angleBorderSize
                }, 200);
            imageDataContainers.angles.$topLeft.animate({
                "top": 0,
                "left": 0
            }, 200);
            imageDataContainers.angles.$bottomLeft.animate({
                "top": newHeight - angleHeight + angleBorderSize,
                "left": 0
            }, 200);
            imageDataContainers.angles.$topRight.animate({
                "top": 0,
                "left": newWidth - angleWidth + angleBorderSize
            }, 200);
            imageDataContainers.angles.$bottomRight.animate({
                "top": newHeight - angleHeight + angleBorderSize,
                "left": newWidth - angleWidth + angleBorderSize
            }, 200);
        }

        function zoom(zoomCoefficient, imageDataContainers) {
            var newHeight = ~~(imageDataContainers.$image.height() * zoomCoefficient),
                newWidth = ~~(imageDataContainers.$image.width() * zoomCoefficient)
            ;
            resizeImage(newWidth, newHeight, imageDataContainers);
        }

        function zoomPlus() {
            zoom(1.1, this);
        }

        function zoomMinus() {
            zoom(0.9, this);
        }

        function zoomContain(imageDataContainers) {
            // fixme: save proportions! now image becomes just as editable area
            // only one side should be as area's side and one as needed to save proportions
            var newHeight = imageDataContainers.$editableImageArea.height(),
                newWidth = imageDataContainers.$editableImageArea.width()
            ;
            var twiceAngleBorderSize = parseInt(this.angles.$topLeft.css("border-width")) * 2 || 0;
            resizeImage(newWidth - twiceAngleBorderSize, newHeight - twiceAngleBorderSize);
        }

        var angle = 0;

        function rotate(angleDelta, imageDataContainers) {
            angle += angleDelta;
            imageDataContainers.$image.css({"transform": "rotate(" + angle + "deg)"});
            imageDataContainers.$cropImg.css({"transform": "rotate(" + angle + "deg)"});
        }

        function rotateLeft() {
            rotate(-90, this);
        }

        function rotateRight() {
            rotate(90, this);
        }

        function buildScaleAndRotateControls(imageDataContainers) {
            return new BEM({
                block: "imcms-edit-image",
                elements: {
                    "button": [
                        components.buttons.zoomPlusButton({click: zoomPlus.bind(imageDataContainers)}),
                        components.buttons.zoomMinusButton({click: zoomMinus.bind(imageDataContainers)}),
                        components.buttons.zoomContainButton({click: zoomContain.bind(imageDataContainers)}),
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

        function buildBottomPanel(toggleImgArea, imageDataContainers) {
            return new BEM({
                block: "imcms-editable-img-controls",
                elements: {
                    "control-size": buildEditSizeControls(imageDataContainers),
                    "control-scale-n-rotate": buildScaleAndRotateControls(imageDataContainers),
                    "control-view": buildSwitchViewControls(toggleImgArea, imageDataContainers)
                }
            }).buildBlockStructure("<div>");
        }

        return {
            build: function (opts) {
                opts.imageDataContainers.$editableImageArea = buildEditableImageArea(opts.imageDataContainers);
                opts.imageDataContainers.$previewImageArea = buildPreviewImageArea(opts.imageDataContainers);
                opts.imageDataContainers.$bottomPanel = buildBottomPanel(opts.toggleImgArea, opts.imageDataContainers);

                return $("<div>").append(
                    opts.imageDataContainers.$editableImageArea,
                    opts.imageDataContainers.$previewImageArea,
                    opts.imageDataContainers.$bottomPanel
                );
            }
        };
    }
);
