/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.03.18
 */
define(
    "imcms-image-edit-size-controls",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery", "imcms-image-crop-angles",
        "imcms-image-cropping-elements", "imcms-events"
    ],

    function (BEM, components, texts, $, croppingAngles, cropElements, events) {

        texts = texts.editors.image;

        function setCropAreaHeight(newImageHeight, oldImgHeight) {
            var cropAreaHeight = cropElements.$cropArea.height();
            var cropAreaTop = cropElements.$cropArea.getTop();

            var newCropAreaHeight = (newImageHeight * cropAreaHeight) / oldImgHeight;
            var angleBorderSize = croppingAngles.getBorderSize();
            var cropAngleHeight = croppingAngles.getHeight();
            var delta = 0;

            if ((newImageHeight + angleBorderSize) < (cropAreaTop + newCropAreaHeight)) {
                delta = cropAreaTop + newCropAreaHeight - newImageHeight - angleBorderSize;
            }

            if (newCropAreaHeight !== 0) {
                cropElements.$cropArea.height(newCropAreaHeight - delta);
                var newTop = cropAreaTop + newCropAreaHeight - delta - cropAngleHeight;
                croppingAngles.bottomRight.setTop(newTop);
                croppingAngles.bottomLeft.setTop(newTop);

            } else {
                cropElements.$cropArea.height(newImageHeight + angleBorderSize);
                cropElements.$cropArea.css({"top": angleBorderSize});
                cropElements.$cropImg.css({"top": 0});

                croppingAngles.topLeft.setTop(0);
                croppingAngles.topRight.setTop(0);
                croppingAngles.bottomLeft.setTop(newImageHeight - cropAngleHeight - angleBorderSize);
                croppingAngles.bottomRight.setTop(newImageHeight - cropAngleHeight - angleBorderSize);
            }
            events.trigger("update cropArea");
        }

        function setCropAreaWidth(newImageWidth, oldImgWidth) {
            var cropAreaWidth = cropElements.$cropArea.width();
            var cropAreaLeft = cropElements.$cropArea.getLeft();

            var newCropAreaWidth = (newImageWidth * cropAreaWidth) / oldImgWidth;
            var angleBorderSize = croppingAngles.getBorderSize();
            var cropAngleWidth = croppingAngles.getWidth();
            var delta = 0;

            if ((newImageWidth + angleBorderSize) < (cropAreaLeft + newCropAreaWidth)) {
                delta = cropAreaLeft + newCropAreaWidth - newImageWidth - angleBorderSize;
            }

            if (newCropAreaWidth !== 0) {
                cropElements.$cropArea.width(newCropAreaWidth - delta);
                var newLeft = cropAreaLeft + newCropAreaWidth - delta - cropAngleWidth;

                croppingAngles.bottomRight.setLeft(newLeft);
                croppingAngles.topRight.setLeft(newLeft);

            } else {
                cropElements.$cropArea.width(newImageWidth + angleBorderSize);
                cropElements.$cropArea.css({"left": angleBorderSize});
                cropElements.$cropImg.css({"left": 0});

                croppingAngles.topLeft.setLeft(0);
                croppingAngles.bottomLeft.setLeft(0);
                croppingAngles.topRight.setLeft(newImageWidth - cropAngleWidth - angleBorderSize);
                croppingAngles.bottomRight.setLeft(newImageWidth - cropAngleWidth - angleBorderSize);
            }
            events.trigger("update cropArea");
        }

        function setWidth(newWidth, imageDataContainers) {
            var newShadowWidth = Math.max(
                newWidth + croppingAngles.getDoubleBorderSize(),
                imageDataContainers.$editableImageArea.width() - 6 // 6px to skip scroll bar size
            );

            cropElements.$image.width(newWidth);
            cropElements.$cropImg.width(newWidth);
            imageDataContainers.$shadow.width(newShadowWidth);
        }

        function setHeight(newHeight, imageDataContainers) {
            var newShadowHeight = Math.max(
                newHeight + croppingAngles.getDoubleBorderSize(),
                imageDataContainers.$editableImageArea.height() - 6 // 6px to skip scroll bar size
            );

            cropElements.$image.height(newHeight);
            cropElements.$cropImg.height(newHeight);
            imageDataContainers.$shadow.height(newShadowHeight);
        }

        function updateHeight(newHeight, imageDataContainers) {
            var previousHeight = cropElements.$cropImg.height();
            setHeight(newHeight, imageDataContainers);
            setCropAreaHeight(newHeight, previousHeight);
        }

        function updateWidth(newWidth, imageDataContainers) {
            var previousWidth = cropElements.$cropImg.width();
            setWidth(newWidth, imageDataContainers);
            setCropAreaWidth(newWidth, previousWidth);
        }

        function updateWidthProportionally(newHeight, imageDataContainers) {
            var original = imageDataContainers.original;
            var proportionalWidth = (newHeight * original.width) / original.height;

            imageDataContainers.$widthControlInput.getInput().val(~~proportionalWidth);
            updateWidth(proportionalWidth, imageDataContainers);
        }

        function updateHeightProportionally(newWidth, imageDataContainers) {
            var original = imageDataContainers.original;
            var proportionalHeight = (newWidth * original.height) / original.width;

            imageDataContainers.$heightControlInput.getInput().val(~~proportionalHeight);
            updateHeight(proportionalHeight, imageDataContainers);
        }

        var saveProportions = true; // by default

        return {
            buildEditSizeControls: function (imageDataContainers) {

                var $title = components.texts.titleText("<div>", texts.displaySize);

                function onValidWidthChange() {
                    var newWidth = +$(this).val();

                    if (isNaN(newWidth) || newWidth < 0) {
                        $(this).val($(this).val().replace(/[^0-9]/g, ''));
                        return;
                    }

                    updateWidth(newWidth, imageDataContainers);
                    saveProportions && updateHeightProportionally(newWidth, imageDataContainers);
                }

                function onValidHeightChange() {
                    var newHeight = +$(this).val();

                    if (isNaN(newHeight) || newHeight < 0) {
                        $(this).val($(this).val().replace(/[^0-9]/g, ''));
                        return;
                    }

                    updateHeight(newHeight, imageDataContainers);
                    saveProportions && updateWidthProportionally(newHeight, imageDataContainers);
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
                    title: texts.proportionsButtonTitle,
                    click: function () {
                        saveProportions = !saveProportions;
                        $(this).attr("data-state", saveProportions ? "active" : "passive");
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
