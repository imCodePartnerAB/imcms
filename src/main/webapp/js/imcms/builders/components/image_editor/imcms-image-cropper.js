/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.09.17
 */
define(
    "imcms-image-cropper",
    ["imcms-events", "imcms-image-crop-angles", "imcms-numeric-limiter", "imcms-image-cropping-elements"],
    function (events, angles, Limit, cropElements) {

        var $imageEditor, croppingAreaParams, angleBorderSize, doubleAngleBorderSize, imageCoords, imageData;

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
                .setMax(cropElements.$cropArea.getTop() + cropElements.$cropArea.getCurrentHeight() - angles.getDoubleHeight())
                .forValue(top);
        }

        function getValidLeftCropWidth(width) {
            return new Limit().setMin(angles.getDoubleWidth())
                .setMax(cropElements.$cropArea.getLeft() + cropElements.$cropArea.getCurrentWidth() - angleBorderSize)
                .forValue(width);
        }

        function getValidRightCropWidth(width) {
            return new Limit().setMin(angles.getDoubleWidth())
                .setMax(cropElements.$image.getCurrentWidth() - cropElements.$cropArea.getLeft() + angleBorderSize)
                .forValue(width);
        }

        function getValidCropHeightTop(height) {
            return new Limit().setMin(angles.getDoubleHeight())
                .setMax(cropElements.$cropArea.getTop() + cropElements.$cropArea.getCurrentHeight() - angleBorderSize)
                .forValue(height);
        }

        function getValidCropHeightBottom(height) {
            return new Limit().setMin(angles.getDoubleHeight())
                .setMax(cropElements.$image.getCurrentHeight() - cropElements.$cropArea.getTop() + angleBorderSize)
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

        function setCropAreaX(newX) {
            var newLeft = getValidLeftOnResize(newX + angleBorderSize);
            var oldLeft = cropElements.$cropArea.getLeft();

            if (newLeft === oldLeft) {
                return;
            }

            var oldWidth = cropElements.$cropArea.width();
            var deltaX = oldLeft - newLeft;
            var legalWidth = croppingAreaParams.width = getValidLeftCropWidth(oldWidth + deltaX);

            cropElements.$cropArea.width(legalWidth);
            cropElements.$cropArea.css("left", newLeft);
            cropElements.$cropImg.css("left", angleBorderSize - newLeft);
        }

        function setCropAreaX1(newX1) {
            var oldLeft = cropElements.$cropArea.getLeft();
            var oldWidth = cropElements.$cropArea.width();
            var oldX1 = oldLeft + oldWidth - angleBorderSize;

            if (oldX1 === newX1) {
                return;
            }

            var deltaX1 = oldX1 - newX1;
            var newWidth = oldWidth - deltaX1;
            var legalWidth = croppingAreaParams.width = getValidRightCropWidth(newWidth);

            cropElements.$cropArea.width(legalWidth);
        }

        function setCropAreaY(newY) {
            var oldTop = cropElements.$cropArea.getTop();
            var newTop = newY + angleBorderSize;

            newTop = getValidTopOnResize(newTop);

            if (oldTop === newTop) {
                return;
            }

            var deltaY = newTop - oldTop;
            var newHeight = cropElements.$cropArea.height() - deltaY;
            var legalHeight = croppingAreaParams.height = getValidCropHeightTop(newHeight);

            cropElements.$cropArea.height(legalHeight);
            cropElements.$cropImg.css("top", (angleBorderSize - newTop));
            cropElements.$cropArea.css("top", newTop);
        }

        function setCropAreaY1(newY1) {
            var oldHeight = cropElements.$cropArea.height();
            var oldTop = cropElements.$cropArea.getTop();
            var oldY1 = oldHeight + oldTop - angleBorderSize;

            if (oldY1 === newY1) {
                return;
            }

            var newHeight = newY1 - oldTop + angleBorderSize;
            var legalHeight = croppingAreaParams.height = getValidCropHeightBottom(newHeight);

            cropElements.$cropArea.height(legalHeight);
        }

        function resizeCroppingTopLeft(deltaX, deltaY) {
            var newWidth = cropElements.$cropArea.width() + deltaX;
            var newHeight = cropElements.$cropArea.height() + deltaY;

            var newTop = cropElements.$cropArea.getTop() - deltaY;
            newTop = getValidTopOnResize(newTop);
            var newLeft = cropElements.$cropArea.getLeft() - deltaX;
            newLeft = getValidLeftOnResize(newLeft);

            var legalWidth = croppingAreaParams.width = getValidLeftCropWidth(newWidth);
            var legalHeight = croppingAreaParams.height = getValidCropHeightTop(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
            setElementTopLeft(cropElements.$cropImg, (angleBorderSize - newTop), (angleBorderSize - newLeft));
            setElementTopLeft(cropElements.$cropArea, newTop, newLeft);
        }

        function resizeCroppingTopRight(deltaX, deltaY) {
            var newWidth = cropElements.$cropArea.width() - deltaX;
            var newHeight = cropElements.$cropArea.height() + deltaY;

            var newTop = cropElements.$cropArea.getTop() - deltaY;
            newTop = getValidTopOnResize(newTop);

            var legalWidth = croppingAreaParams.width = getValidRightCropWidth(newWidth);
            var legalHeight = croppingAreaParams.height = getValidCropHeightTop(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
            cropElements.$cropImg.css("top", angleBorderSize - newTop);
            cropElements.$cropArea.css("top", newTop);
        }

        function resizeCroppingBottomRight(deltaX, deltaY) {
            var newWidth = cropElements.$cropArea.width() - deltaX;
            var newHeight = cropElements.$cropArea.height() - deltaY;

            var legalWidth = croppingAreaParams.width = getValidRightCropWidth(newWidth);
            var legalHeight = croppingAreaParams.height = getValidCropHeightBottom(newHeight);

            setElementWidthHeight(cropElements.$cropArea, legalWidth, legalHeight);
        }

        function resizeCroppingBottomLeft(deltaX, deltaY) {
            var newWidth = cropElements.$cropArea.width() + deltaX;
            var newHeight = cropElements.$cropArea.height() - deltaY;

            var newLeft = cropElements.$cropArea.getLeft() - deltaX;
            newLeft = getValidLeftOnResize(newLeft);

            var legalWidth = croppingAreaParams.width = getValidLeftCropWidth(newWidth);
            var legalHeight = croppingAreaParams.height = getValidCropHeightBottom(newHeight);

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
            $imageEditor = imageCropComponents.$imageEditor;

            angleBorderSize = angles.getBorderSize();
            doubleAngleBorderSize = angles.getDoubleBorderSize();
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

            var cropRegion = imageData.cropRegion;

            if (!cropRegion) {
                cropRegion = imageData.cropRegion = {
                    cropX1: 0,
                    cropY1: 0,
                    cropX2: cropElements.$cropArea.width(),
                    cropY2: cropElements.$cropArea.height()
                };
            } else {
                if (cropRegion.cropX1 === -1) {
                    cropRegion.cropX1 = 0;
                } else {
                    cropRegion.cropX1 /= croppingCoefficientX;
                }

                if (cropRegion.cropY1 === -1) {
                    cropRegion.cropY1 = 0;
                } else {
                    cropRegion.cropY1 /= croppingCoefficientY;
                }

                if (cropRegion.cropX2 === -1) {
                    cropRegion.cropX2 = cropElements.$cropArea.width();
                }
                if (cropRegion.cropY2 === -1) {
                    cropRegion.cropY2 = cropElements.$cropArea.height();
                }

                cropRegion.cropX2 /= croppingCoefficientX;
                cropRegion.cropY2 /= croppingCoefficientY;
            }

            croppingAreaParams = {
                height: cropRegion.cropY2 - cropRegion.cropY1,
                width: cropRegion.cropX2 - cropRegion.cropX1
            };

            removeCroppingListeners();
            setElementWidthHeight(cropElements.$cropImg, originImageWidth, originImageHeight);
            setElementWidthHeight(cropElements.$cropArea, croppingAreaParams.width, croppingAreaParams.height);
            setElementTopLeft(cropElements.$cropArea, cropRegion.cropY1 + angleBorderSize, cropRegion.cropX1 + angleBorderSize);
            setElementTopLeft(cropElements.$cropImg, -cropRegion.cropY1, -cropRegion.cropX1);

            (function setStartCroppingAngles() {
                setCroppingAnglesTopLeft(
                    imageData.cropRegion.cropY1 + angleBorderSize,
                    imageData.cropRegion.cropX1 + angleBorderSize
                );
            })();

            cropRegion.cropX1 *= croppingCoefficientX;
            cropRegion.cropY1 *= croppingCoefficientY;
            cropRegion.cropX2 *= croppingCoefficientX;
            cropRegion.cropY2 *= croppingCoefficientY;

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

            $imageEditor.mouseup(function (event) {
                if ((event.which === 1) && isMouseDown) {
                    isMouseDown = false;
                    isResizing = false;
                    setCursor("");
                }
            });

            function setCroppingAnglesTopLeft(top, left) {
                angles.topLeft.setTopLeft(top - angleBorderSize, left - angleBorderSize);
                angles.topRight.setTopLeft(top - angleBorderSize, croppingAreaParams.width + left - angles.getWidth());
                angles.bottomRight.setTopLeft(croppingAreaParams.height + top - angles.getWidth(), croppingAreaParams.width + left - angles.getWidth());
                angles.bottomLeft.setTopLeft(croppingAreaParams.height + top - angles.getWidth(), left - angleBorderSize);
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
                    var croppingAreaTop = cropElements.$cropArea.getTop();
                    var croppingAreaLeft = cropElements.$cropArea.getLeft();

                    var newLeft = croppingAreaLeft - deltaX;
                    var newTop = croppingAreaTop - deltaY;

                    newLeft = getValidLeftOnMove(newLeft);
                    newTop = getValidTopOnMove(newTop);

                    moveCropArea(newTop, newLeft);
                    setCroppingAnglesTopLeft(newTop, newLeft);
                }

                imageData.cropRegion.cropX1 = croppingCoefficientX * (cropElements.$cropArea.getLeft() - 2);
                imageData.cropRegion.cropY1 = croppingCoefficientY * (cropElements.$cropArea.getTop() - 2);
                imageData.cropRegion.cropX2 = croppingCoefficientX * (cropElements.$cropArea.getLeft() + cropElements.$cropArea.width() - 2);
                imageData.cropRegion.cropY2 = croppingCoefficientY * (cropElements.$cropArea.getTop() + cropElements.$cropArea.height() - 2);
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

        function setCropX(newX) {
            angles.topLeft.setNewX(newX);
            angles.bottomLeft.setNewX(newX);

            setCropAreaX(newX);
        }

        function setCropX1(newX1) {
            angles.topRight.setNewX(newX1 - angles.getWidth() + angles.getBorderSize());
            angles.bottomRight.setNewX(newX1 - angles.getWidth() + angles.getBorderSize());

            setCropAreaX1(newX1);
        }

        function setCropY(newY) {
            angles.topRight.setNewY(newY);
            angles.topLeft.setNewY(newY);

            setCropAreaY(newY);
        }

        function setCropY1(newY1) {
            angles.bottomRight.setNewY(newY1 - angles.getWidth() + angles.getBorderSize());
            angles.bottomLeft.setNewY(newY1 - angles.getWidth() + angles.getBorderSize());

            setCropAreaY1(newY1);
        }

        return {
            initImageCropper: init,
            setCropX: setCropX,
            setCropX1: setCropX1,
            setCropY: setCropY,
            setCropY1: setCropY1,
            destroyImageCropper: destroy
        };
    }
);
