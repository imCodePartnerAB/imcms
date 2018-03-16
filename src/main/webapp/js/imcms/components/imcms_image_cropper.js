/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.09.17
 */
Imcms.define(
    "imcms-image-cropper",
    ["imcms-events", "imcms-image-crop-angles", "imcms-numeric-limiter", "imcms-image-cropping-elements"],
    function (events, angles, Limit, cropElements) {

        var $imageEditor, croppingAreaParams, angleBorderSize, imageCoords,
            imageData;

        function moveCropImage(newTop, newLeft) {
            var cropImgTop = -newTop + angleBorderSize,
                cropImgLeft = -newLeft + angleBorderSize
            ;

            setElementTopLeft(cropElements.$cropImg, cropImgTop, cropImgLeft);
        }

        function setElementTopLeft($element, newTop, newLeft) {
            $element.css({
                top: newTop,
                left: newLeft
            });
        }

        function setElementWidthHeight($element, newWidth, newHeight) {
            $element.width(newWidth);
            $element.height(newHeight);
        }

        function getValidCoordX(coordX) {
            return new Limit().setMin(imageCoords.left)
                .setMax(cropElements.$image.getCurrentWidth() + imageCoords.left + angleBorderSize)
                .forValue(coordX);
        }

        function getValidCoordY(coordY) {
            return new Limit().setMin(imageCoords.top)
                .setMax(cropElements.$image.getCurrentHeight() + imageCoords.top + angleBorderSize)
                .forValue(coordY);
        }

        function getValidLeftOnMove(left) {
            return new Limit().setMin(angleBorderSize)
                .setMax(cropElements.$image.getCurrentWidth() - croppingAreaParams.width + angleBorderSize)
                .forValue(left);
        }

        function getValidTopOnMove(top) {
            return new Limit().setMin(angleBorderSize)
                .setMax(cropElements.$image.getCurrentHeight() - croppingAreaParams.height + angleBorderSize)
                .forValue(top);
        }

        function getValidLeftOnResize(left) {
            return new Limit().setMin(angleBorderSize)
                .setMax(angles.topRight.getLeft() - angles.getWidth()) // could be without angle
                .forValue(left);
        }

        function getValidTopOnResize(top) {
            return new Limit().setMin(angleBorderSize)
                .setMax(parseInt(cropElements.$cropArea.css("top")) + cropElements.$cropArea.getCurrentHeight() - angles.getDoubleHeight())
                .forValue(top);
        }

        function getValidLeftCropWidth(width) {
            return new Limit().setMin(angles.getDoubleWidth())
                .setMax(parseInt(cropElements.$cropArea.css("left")) + cropElements.$cropArea.getCurrentWidth() - angleBorderSize)
                .forValue(width);
        }

        function getValidRightCropWidth(width) {
            return new Limit().setMin(angles.getDoubleWidth())
                .setMax(cropElements.$image.getCurrentWidth() - parseInt(cropElements.$cropArea.css("left")) + angleBorderSize)
                .forValue(width);
        }

        function getValidCropHeightTop(height) {
            return new Limit().setMin(angles.getDoubleHeight())
                .setMax(parseInt(cropElements.$cropArea.css("top")) + cropElements.$cropArea.getCurrentHeight() - angleBorderSize)
                .forValue(height);
        }

        function getValidCropHeightBottom(height) {
            return new Limit().setMin(angles.getDoubleHeight())
                .setMax(cropElements.$image.getCurrentHeight() - parseInt(cropElements.$cropArea.css("top")) + angleBorderSize)
                .forValue(height);
        }

        function moveCropArea(top, left) {
            setElementTopLeft(cropElements.$cropArea, top, left);
            moveCropImage(top, left);
        }

        events.on("update cropArea", function () {
            croppingAreaParams.width = cropElements.$cropArea.width();
            croppingAreaParams.height = cropElements.$cropArea.height();
        });

        function resizeCroppingTopLeft(deltaX, deltaY) {
            var newWidth = (croppingAreaParams.width = cropElements.$cropArea.width() + deltaX);
            var newHeight = (croppingAreaParams.height = cropElements.$cropArea.height() + deltaY);

            var newTop = parseInt(cropElements.$cropArea.css("top")) - deltaY;
            newTop = getValidTopOnResize(newTop);
            var newLeft = parseInt(cropElements.$cropArea.css("left")) - deltaX;
            newLeft = getValidLeftOnResize(newLeft);

            var legalWidth = getValidLeftCropWidth(newWidth);
            var legalHeight = getValidCropHeightTop(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
            setElementTopLeft(cropElements.$cropImg, (angleBorderSize - newTop), (angleBorderSize - newLeft));
            setElementTopLeft(cropElements.$cropArea, newTop, newLeft);
        }

        function resizeCroppingTopRight(deltaX, deltaY) {
            var newWidth = (croppingAreaParams.width = cropElements.$cropArea.width() - deltaX);
            var newHeight = (croppingAreaParams.height = cropElements.$cropArea.height() + deltaY);

            var newTop = parseInt(cropElements.$cropArea.css("top")) - deltaY;
            newTop = getValidTopOnResize(newTop);

            var legalWidth = getValidRightCropWidth(newWidth);
            var legalHeight = getValidCropHeightTop(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
            cropElements.$cropImg.css("top", angleBorderSize - newTop);
            cropElements.$cropArea.css("top", newTop);
        }

        function resizeCroppingBottomRight(deltaX, deltaY) {
            var newWidth = (croppingAreaParams.width = cropElements.$cropArea.width() - deltaX);
            var newHeight = (croppingAreaParams.height = cropElements.$cropArea.height() - deltaY);

            var legalWidth = getValidRightCropWidth(newWidth);
            var legalHeight = getValidCropHeightBottom(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
        }

        function resizeCroppingBottomLeft(deltaX, deltaY) {
            var newWidth = (croppingAreaParams.width = cropElements.$cropArea.width() + deltaX);
            var newHeight = (croppingAreaParams.height = cropElements.$cropArea.height() - deltaY);

            var newLeft = parseInt(cropElements.$cropArea.css("left")) - deltaX;
            newLeft = getValidLeftOnResize(newLeft);

            var legalWidth = getValidLeftCropWidth(newWidth);
            var legalHeight = getValidCropHeightBottom(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
            cropElements.$cropImg.css("left", angleBorderSize - newLeft);
            cropElements.$cropArea.css("left", newLeft);
        }

        function moveCroppingAngles(angleName, deltaX, deltaY) {
            var angle1X = 0, angle1Y = 0;
            var angle2X = 0, angle2Y = 0;
            var angle3X = 0, angle3Y = 0;
            var angle4X = 0, angle4Y = 0;

            switch (angleName) {
                case "topLeft":
                    angle1X = angle4X = deltaX;
                    angle1Y = angle2Y = deltaY;
                    break;
                case "topRight":
                    angle2X = angle3X = deltaX;
                    angle2Y = angle1Y = deltaY;
                    break;
                case "bottomRight":
                    angle3X = angle2X = deltaX;
                    angle3Y = angle4Y = deltaY;
                    break;
                case "bottomLeft":
                    angle4X = angle1X = deltaX;
                    angle4Y = angle3Y = deltaY;
                    break;
            }

            (angle1X || angle1Y) && angles.topLeft.moveAngle(angle1X, angle1Y);
            (angle2X || angle2Y) && angles.topRight.moveAngle(angle2X, angle2Y);
            (angle3X || angle3Y) && angles.bottomRight.moveAngle(angle3X, angle3Y);
            (angle4X || angle4Y) && angles.bottomLeft.moveAngle(angle4X, angle4Y);
        }

        var resizeCropper = {
            topLeft: resizeCroppingTopLeft,
            topRight: resizeCroppingTopRight,
            bottomRight: resizeCroppingBottomRight,
            bottomLeft: resizeCroppingBottomLeft
        };

        function init(imageCropComponents) {
            var isMouseDown = false,
                isResizing = false
            ;

            imageData = imageCropComponents.imageData;
            // cropElements.$image = imageCropComponents.cropElements.$image;
            $imageEditor = imageCropComponents.$imageEditor;
            // $cropImg = imageCropComponents.$cropImg;

            angleBorderSize = angles.getBorderSize();
            imageCoords = cropElements.$image.offset();
            imageCoords.top -= angleBorderSize;
            imageCoords.left -= angleBorderSize;

            var originImageWidth = cropElements.$image.width();
            var originImageHeight = cropElements.$image.height();

            var croppingCoefficientX = imageData.width / originImageWidth;
            var croppingCoefficientY = imageData.height / originImageHeight;

            if (!originImageWidth || !originImageHeight) {
                return;
            }

            if (!imageData.cropRegion) {
                imageData.cropRegion = {
                    cropX1: 0,
                    cropY1: 0,
                    cropX2: cropElements.$cropArea.width(),
                    cropY2: cropElements.$cropArea.height()
                };
            } else {
                imageData.cropRegion.cropX1 /= croppingCoefficientX;
                imageData.cropRegion.cropY1 /= croppingCoefficientY;
                imageData.cropRegion.cropX2 /= croppingCoefficientX;
                imageData.cropRegion.cropY2 /= croppingCoefficientY;
            }

            croppingAreaParams = {
                height: imageData.cropRegion.cropY2 - imageData.cropRegion.cropY1,
                width: imageData.cropRegion.cropX2 - imageData.cropRegion.cropX1
            };

            removeCroppingListeners();
            setElementWidthHeight(cropElements.$cropImg, originImageWidth, originImageHeight);
            setElementWidthHeight(cropElements.$cropArea, croppingAreaParams.width, croppingAreaParams.height);
            setElementTopLeft(cropElements.$cropArea, imageData.cropRegion.cropY1 + angleBorderSize, imageData.cropRegion.cropX1 + angleBorderSize);
            setElementTopLeft(cropElements.$cropImg, -imageData.cropRegion.cropY1, -imageData.cropRegion.cropX1);

            var angleSize = angles.topRight.$angle.width();

            !function setStartCroppingAngles() {
                setCroppingAnglesTopLeft(
                    imageData.cropRegion.cropY1 + angleBorderSize,
                    imageData.cropRegion.cropX1 + angleBorderSize
                );
            }();

            imageData.cropRegion.cropX1 *= croppingCoefficientX;
            imageData.cropRegion.cropY1 *= croppingCoefficientY;
            imageData.cropRegion.cropX2 *= croppingCoefficientX;
            imageData.cropRegion.cropY2 *= croppingCoefficientY;

            angles.showAll();

            cropElements.$cropArea.mousedown(function (event) {
                (isMouseDown = (event.which === 1)) && setCursor("move");
            });

            var resizeAngleName; // topLeft, topRight, bottomRight, bottomLeft

            function getAngleMouseDownEvent(angleName, cursor) {
                return function (event) {
                    if (event.which === 1) {
                        isResizing = true;
                        resizeAngleName = angleName;
                        isMouseDown = true;
                        setCursor(cursor);
                    }
                }
            }

            angles.topLeft.$angle.mousedown(getAngleMouseDownEvent("topLeft", "nw-resize"));
            angles.topRight.$angle.mousedown(getAngleMouseDownEvent("topRight", "ne-resize"));
            angles.bottomRight.$angle.mousedown(getAngleMouseDownEvent("bottomRight", "se-resize"));
            angles.bottomLeft.$angle.mousedown(getAngleMouseDownEvent("bottomLeft", "sw-resize"));

            $imageEditor.mouseup(function () {
                if (event.which === 1) {
                    isMouseDown = false;
                    isResizing = false;
                    setCursor("");
                }
            });

            function setCroppingAnglesTopLeft(top, left) {
                angles.topLeft.setTopLeft(top - angleBorderSize, left - angleBorderSize);
                angles.topRight.setTopLeft(top - angleBorderSize, croppingAreaParams.width + left - angleSize);
                angles.bottomRight.setTopLeft(croppingAreaParams.height + top - angleSize, croppingAreaParams.width + left - angleSize);
                angles.bottomLeft.setTopLeft(croppingAreaParams.height + top - angleSize, left - angleBorderSize);
            }

            function setCursor(cursorValue) {
                [
                    angles.topLeft.$angle,
                    angles.topRight.$angle,
                    angles.bottomRight.$angle,
                    angles.bottomLeft.$angle,
                    cropElements.$cropArea,
                    $imageEditor

                ].forEach(function ($element) {
                    $element.css("cursor", cursorValue);
                });
            }

            var prevX, prevY;

            $imageEditor.mousemove(function (event) {
                if (!isMouseDown) {
                    prevX = undefined;
                    prevY = undefined;
                    return;
                }

                var newX = getValidCoordX(event.clientX);
                var newY = getValidCoordY(event.clientY);

                if (prevX === undefined || prevY === undefined) {
                    prevX = newX;
                    prevY = newY;
                    return;
                }

                var deltaX = prevX - newX;
                var deltaY = prevY - newY;

                prevX = newX;
                prevY = newY;

                if (isResizing) {
                    moveCroppingAngles(resizeAngleName, deltaX, deltaY);
                    resizeCropper[resizeAngleName](deltaX, deltaY);

                } else {
                    var croppingAreaTop = parseInt(cropElements.$cropArea.css("top"));
                    var croppingAreaLeft = parseInt(cropElements.$cropArea.css("left"));

                    var newLeft = croppingAreaLeft - deltaX;
                    var newTop = croppingAreaTop - deltaY;

                    newLeft = getValidLeftOnMove(newLeft);
                    newTop = getValidTopOnMove(newTop);

                    moveCropArea(newTop, newLeft);
                    setCroppingAnglesTopLeft(newTop, newLeft);
                }

                imageData.cropRegion.cropX1 = croppingCoefficientX * (parseInt(cropElements.$cropArea.css("left")) - 2);
                imageData.cropRegion.cropY1 = croppingCoefficientY * (parseInt(cropElements.$cropArea.css("top")) - 2);
                imageData.cropRegion.cropX2 = croppingCoefficientX * (parseInt(cropElements.$cropArea.css("left")) + cropElements.$cropArea.width() - 2);
                imageData.cropRegion.cropY2 = croppingCoefficientY * (parseInt(cropElements.$cropArea.css("top")) + cropElements.$cropArea.height() - 2);
            });

            $imageEditor.on("dragstart", function () {
                return false;
            });
        }

        function removeEventListeners($element, eventsArr) {
            eventsArr.forEach(function (event) {
                $element.off(event);
            });
        }

        function removeCroppingListeners() {
            removeEventListeners(cropElements.$cropArea, ["mousedown", "mouseup"]);
            angles.forEach(function ($angle) {
                removeEventListeners($angle, ["mousedown", "mouseup"]);
            });
            removeEventListeners($imageEditor, ["mousemove", "mouseup", "dragstart"]);
        }

        function destroy() {
            if (!imageData) {
                return;
            }

            moveCropArea(0, 0);

            [
                cropElements.$cropImg,
                cropElements.$image,
                angles.topLeft.$angle,
                angles.topRight.$angle,
                angles.bottomRight.$angle,
                angles.bottomLeft.$angle,
                cropElements.$cropArea

            ].forEach(function ($element) {
                $element.removeAttr("style");
            });

            removeCroppingListeners();
        }

        return {
            initImageCropper: init,
            destroyImageCropper: destroy
        };
    }
);
