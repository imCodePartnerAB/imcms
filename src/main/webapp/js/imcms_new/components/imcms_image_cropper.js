/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.09.17
 */
Imcms.define("imcms-image-cropper", [], function () {

    var $croppingArea, $imageEditor, $cropImg, croppingAreaParams, angleBorderSize, imageCoords, angleParams,
        $originImg, angles;

    function moveCropImage(newTop, newLeft) {
        var cropImgTop = -newTop + angleBorderSize,
            cropImgLeft = -newLeft + angleBorderSize
        ;

        setElementTopLeft($cropImg, cropImgTop, cropImgLeft);
    }

    function setElementTopLeft($element, newTop, newLeft) {
        $element.css({
            top: newTop,
            left: newLeft
        });
    }

    function setElementWidthHeight($element, newWidth, newHeight) {
        $element.css({
            width: newWidth,
            height: newHeight
        });
    }

    function Limit(min, max) {
        return {
            forValue: function (value) {
                return Math.min(Math.max(value, min), max);
            }
        }
    }

    function getValidCoordX(coordX) {
        return Limit(imageCoords.left, $originImg.width() + imageCoords.left + angleBorderSize).forValue(coordX);
    }

    function getValidCoordY(coordY) {
        return Limit(imageCoords.top, $originImg.height() + imageCoords.top + angleBorderSize).forValue(coordY);
    }

    function getValidLeftOnMove(left) {
        return Limit(angleBorderSize, $originImg.width() - croppingAreaParams.width + angleBorderSize).forValue(left);
    }

    function getValidLeftOnResize(left) {
        return Limit(angleBorderSize, parseInt(angles.$topRight.css("left")) - angleParams.width).forValue(left);
    }

    function getValidTop(top) {
        return Limit(angleBorderSize, $originImg.height() - croppingAreaParams.height + angleBorderSize).forValue(top);
    }

    function getValidTopOnResize(top) {
        return Limit(angleBorderSize, parseInt($croppingArea.css("top")) + $croppingArea.height() - angleParams.height * 2).forValue(top);
    }

    function getValidTopAngleY(top) {
        return Limit(0, parseInt($croppingArea.css("top")) + $croppingArea.height() - angleParams.height * 2 - angleBorderSize).forValue(top);
    }

    function getValidBottomAngleY(top) {
        return Limit(parseInt($croppingArea.css("top")) + angleParams.height, $originImg.height() - angleParams.height + angleBorderSize).forValue(top);
    }

    function getValidLeftAngleX(left) {
        return Limit(0, parseInt($croppingArea.css("left")) + $croppingArea.width() - angleBorderSize - angleParams.width * 2).forValue(left);
    }

    function getValidRightAngleX(left) {
        return Limit(parseInt($croppingArea.css("left")) + angleParams.width, $originImg.width() - angleParams.width + angleBorderSize).forValue(left);
    }

    function getValidLeftCropWidth(width) {
        return Limit(2 * angleParams.width, parseInt($croppingArea.css("left")) + $croppingArea.width() - angleBorderSize).forValue(width);
    }

    function getValidRightCropWidth(width) {
        return Limit(2 * angleParams.width, $originImg.width() - parseInt($croppingArea.css("left")) + angleBorderSize).forValue(width);
    }

    function getValidCropHeightTop(height) {
        return Limit(2 * angleParams.height, parseInt($croppingArea.css("top")) + $croppingArea.height() - angleBorderSize).forValue(height);
    }

    function getValidCropHeightBottom(height) {
        return Limit(2 * angleParams.height, $originImg.height() - parseInt($croppingArea.css("top")) + angleBorderSize).forValue(height);
    }

    function moveCropArea(top, left) {
        setElementTopLeft($croppingArea, top, left);
        moveCropImage(top, left);
    }

    var angleCoordsTransformer = {
        x: {
            topLeft: getValidLeftAngleX,
            topRight: getValidRightAngleX,
            bottomRight: getValidRightAngleX,
            bottomLeft: getValidLeftAngleX
        },
        y: {
            topLeft: getValidTopAngleY,
            topRight: getValidTopAngleY,
            bottomRight: getValidBottomAngleY,
            bottomLeft: getValidBottomAngleY
        }
    };

    function transformCroppingAngleDeltaCoords(angleName, deltaX, deltaY) {
        return {
            x: angleCoordsTransformer.x[angleName](parseInt(angles["$" + angleName].css("left")) - deltaX),
            y: angleCoordsTransformer.y[angleName](parseInt(angles["$" + angleName].css("top")) - deltaY)
        };
    }

    function moveCroppingAngle(angleName, deltaX, deltaY) {
        var fixedCoords = transformCroppingAngleDeltaCoords(angleName, deltaX, deltaY);
        setElementTopLeft(angles["$" + angleName], fixedCoords.y, fixedCoords.x);
    }

    function resizeCroppingTopLeft(deltaX, deltaY) {
        var newWidth = (croppingAreaParams.width = $croppingArea.width() + deltaX);
        var newHeight = (croppingAreaParams.height = $croppingArea.height() + deltaY);

        var newTop = parseInt($croppingArea.css("top")) - deltaY;
        newTop = getValidTopOnResize(newTop);
        var newLeft = parseInt($croppingArea.css("left")) - deltaX;
        newLeft = getValidLeftOnResize(newLeft);

        var legalWidth = getValidLeftCropWidth(newWidth);
        var legalHeight = getValidCropHeightTop(newHeight);

        setElementWidthHeight($croppingArea, legalWidth, legalHeight);
        setElementTopLeft($cropImg, (angleBorderSize - newTop), (angleBorderSize - newLeft));
        setElementTopLeft($croppingArea, newTop, newLeft);
    }

    function resizeCroppingTopRight(deltaX, deltaY) {
        var newWidth = (croppingAreaParams.width = $croppingArea.width() - deltaX);
        var newHeight = (croppingAreaParams.height = $croppingArea.height() + deltaY);

        var newTop = parseInt($croppingArea.css("top")) - deltaY;
        newTop = getValidTopOnResize(newTop);

        var legalWidth = getValidRightCropWidth(newWidth);
        var legalHeight = getValidCropHeightTop(newHeight);

        setElementWidthHeight($croppingArea, legalWidth, legalHeight);
        $cropImg.css("top", angleBorderSize - newTop);
        $croppingArea.css("top", newTop);
    }

    function resizeCroppingBottomRight(deltaX, deltaY) {
        var newWidth = (croppingAreaParams.width = $croppingArea.width() - deltaX);
        var newHeight = (croppingAreaParams.height = $croppingArea.height() - deltaY);

        var legalWidth = getValidRightCropWidth(newWidth);
        var legalHeight = getValidCropHeightBottom(newHeight);

        setElementWidthHeight($croppingArea, legalWidth, legalHeight);
    }

    function resizeCroppingBottomLeft(deltaX, deltaY) {
        var newWidth = (croppingAreaParams.width = $croppingArea.width() + deltaX);
        var newHeight = (croppingAreaParams.height = $croppingArea.height() - deltaY);

        var newLeft = parseInt($croppingArea.css("left")) - deltaX;
        newLeft = getValidLeftOnResize(newLeft);

        var legalWidth = getValidLeftCropWidth(newWidth);
        var legalHeight = getValidCropHeightBottom(newHeight);

        setElementWidthHeight($croppingArea, legalWidth, legalHeight);
        $cropImg.css("left", angleBorderSize - newLeft);
        $croppingArea.css("left", newLeft);
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

        (angle1X || angle1Y) && moveCroppingAngle("topLeft", angle1X, angle1Y);
        (angle2X || angle2Y) && moveCroppingAngle("topRight", angle2X, angle2Y);
        (angle3X || angle3Y) && moveCroppingAngle("bottomRight", angle3X, angle3Y);
        (angle4X || angle4Y) && moveCroppingAngle("bottomLeft", angle4X, angle4Y);
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

        angleBorderSize = imageCropComponents.borderWidth;
        $originImg = imageCropComponents.$originImg;
        angles = imageCropComponents.angles;
        $croppingArea = imageCropComponents.$croppingArea;
        $imageEditor = imageCropComponents.$imageEditor;
        $cropImg = imageCropComponents.$cropImg;
        imageCoords = $originImg.offset();
        imageCoords.top -= angleBorderSize;
        imageCoords.left -= angleBorderSize;

        var originImageWidth = $originImg.width();
        var originImageHeight = $originImg.height();

        setElementWidthHeight($cropImg, originImageWidth, originImageHeight);

        croppingAreaParams = {
            height: $croppingArea.height(),
            width: $croppingArea.width()
        };

        $croppingArea.mousedown(function (event) {
            (isMouseDown = (event.which === 1)) && setCursor("move");
        });

        angleParams = {
            width: angles.$topLeft.width(),
            height: angles.$topLeft.height()
        };

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

        angles.$topLeft.mousedown(getAngleMouseDownEvent("topLeft", "nw-resize"));
        angles.$topRight.mousedown(getAngleMouseDownEvent("topRight", "ne-resize"));
        angles.$bottomRight.mousedown(getAngleMouseDownEvent("bottomRight", "se-resize"));
        angles.$bottomLeft.mousedown(getAngleMouseDownEvent("bottomLeft", "sw-resize"));

        $imageEditor.mouseup(function () {
            if (event.which === 1) {
                isMouseDown = false;
                isResizing = false;
                setCursor("");
            }
        });

        var angleSize = angles.$topRight.width();

        !function setStartCroppingAngles() {
            setCroppingAnglesTopLeft(angleBorderSize, angleBorderSize);
        }();

        function setCroppingAnglesTopLeft(top, left) {
            setElementTopLeft(angles.$topLeft, top - angleBorderSize, left - angleBorderSize);
            setElementTopLeft(angles.$topRight, top - angleBorderSize, croppingAreaParams.width + left - angleSize);
            setElementTopLeft(angles.$bottomRight, croppingAreaParams.height + top - angleSize, croppingAreaParams.width + left - angleSize);
            setElementTopLeft(angles.$bottomLeft, croppingAreaParams.height + top - angleSize, left - angleBorderSize);
        }

        function setCursor(cursorValue) {
            [
                angles.$topLeft,
                angles.$topRight,
                angles.$bottomRight,
                angles.$bottomLeft,
                $croppingArea,
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
                var croppingAreaTop = parseInt($croppingArea.css("top"));
                var croppingAreaLeft = parseInt($croppingArea.css("left"));

                var newLeft = croppingAreaLeft - deltaX;
                var newTop = croppingAreaTop - deltaY;

                newLeft = getValidLeftOnMove(newLeft);
                newTop = getValidTop(newTop);

                moveCropArea(newTop, newLeft);
                setCroppingAnglesTopLeft(newTop, newLeft);
            }
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

    function destroy() {
        moveCropArea(0, 0);

        [
            $cropImg,
            $originImg,
            angles.$topLeft,
            angles.$topRight,
            angles.$bottomRight,
            angles.$bottomLeft,
            $croppingArea
        ].forEach(function ($element) {
            $element.removeAttr("style");
        });

        removeEventListeners($croppingArea, ["mousedown", "mouseup"]);
        removeEventListeners(angles.$bottomRight, ["mousedown", "mouseup"]);
        removeEventListeners($imageEditor, ["mousemove", "mouseup", "dragstart"]);
    }

    return {
        initImageCropper: init,
        destroyImageCropper: destroy
    };
});
