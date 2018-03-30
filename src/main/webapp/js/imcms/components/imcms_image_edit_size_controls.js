/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.03.18
 */
Imcms.define(
    "imcms-image-edit-size-controls",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery", "imcms-image-crop-angles",
        "imcms-image-cropping-elements", "imcms-events"
    ],

    function (BEM, components, texts, $, croppingAngles, cropElements, events) {

        texts = texts.editors.image;

        function setCropAreaHeight(newImageHeight, oldImgHeight) {
            var cropAreaHeight = cropElements.$cropArea.height();
            var cropAreaTop = cropElements.$cropArea.position().top; // note: position and offset works different

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
            var cropAreaLeft = cropElements.$cropArea.offset().left; // note: position and offset works different

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

        return {
            buildEditSizeControls: function (imageDataContainers) {

                var $title = components.texts.titleText("<div>", texts.displaySize);

                function onValidWidthChange() {
                    var width = +$(this).val();

                    if (isNaN(width)) {
                        return;
                    }

                    var previousWidth = cropElements.$cropImg.width();
                    setWidth(width, width, width + 4, imageDataContainers);
                    setCropAreaWidth(width, previousWidth, imageDataContainers);
                }

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
        }
    }
);
