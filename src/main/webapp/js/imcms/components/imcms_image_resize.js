/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
Imcms.define(
    "imcms-image-resize",
    ["imcms-image-crop-angles", "imcms-image-cropping-elements", "imcms-events"],
    function (croppingAngles, cropElements, events) {
        return {
            resize: function (opts, imageDataContainers, isImageProportionsInverted) {
                var angleHeight = croppingAngles.getHeight();
                var angleWidth = croppingAngles.getWidth();
                var angleBorderSize = croppingAngles.getBorderSize();
                var doubleAngleBorderSize = croppingAngles.getDoubleBorderSize();
                var newWidth = opts.image.width;
                var newHeight = opts.image.height;
                var cropArea = opts.cropArea;

                cropElements.$image.animate(opts.image, 200);

                var displayWidth = ((isImageProportionsInverted) ? newHeight : newWidth) + doubleAngleBorderSize;
                var displayHeight = ((isImageProportionsInverted) ? newWidth : newHeight) + doubleAngleBorderSize;

                imageDataContainers.$shadow.animate({
                    "width": displayWidth,
                    "height": displayHeight
                }, 200);

                if ((croppingAngles.topRight.getLeft() + angleWidth + angleBorderSize) > displayWidth) {
                    cropArea.left = angleBorderSize;
                }
                if ((croppingAngles.bottomRight.getTop() + angleWidth + angleBorderSize) > displayHeight) {
                    cropArea.top = angleBorderSize;
                }

                cropElements.$cropImg.animate({
                    "left": -cropArea.left + angleBorderSize,
                    "top": -cropArea.top + angleBorderSize,
                    "width": newWidth,
                    "height": newHeight
                }, 200);

                cropElements.$cropArea.animate(cropArea, 200);

                var positionTopForAngelsTop = cropArea.top - angleBorderSize;
                var positionTopForAngelsBottom = cropArea.top + cropArea.height - angleHeight;
                var positionLeftForAngelsLeft = cropArea.left - angleBorderSize;
                var positionLeftForAngelsRight = cropArea.left + cropArea.width - angleWidth;

                if (cropArea.left <= angleBorderSize) {
                    positionLeftForAngelsLeft = 0;
                }

                if (cropArea.top <= angleBorderSize) {
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
        };
    }
);
