/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.09.17
 */
const events = require('imcms-events');
const angles = require('imcms-image-crop-angles');
const Limit = require('imcms-numeric-limiter');
const $ = require('jquery');
const cropArea = require('imcms-cropping-area');
const editableImage = require('imcms-editable-image');
const imageResize = require('imcms-image-resize');

const editableAreaBorderSize = cropArea.getEditableAreaBorderWidth();

let $imageEditor, croppingAreaParams, angleBorderSize, doubleAngleBorderSize, imageCoords, imageData;

function moveCropImage(newTop, newLeft) {
    const cropImgTop = -newTop + editableAreaBorderSize;
    const cropImgLeft = -newLeft + editableAreaBorderSize;

    setElementTopLeft(cropArea.getCroppingImage(), cropImgTop, cropImgLeft);
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
    return new Limit().setMin(editableAreaBorderSize)
        .setMax(cropArea.getImage().getCurrentWidth() + imageCoords.left + angleBorderSize + editableAreaBorderSize)
        .forValue(coordX);
}

function getValidCoordY(coordY) {
    return new Limit().setMin(editableAreaBorderSize)
        .setMax(cropArea.getImage().getCurrentHeight() + imageCoords.top + angleBorderSize + editableAreaBorderSize)
        .forValue(coordY);
}

function getValidLeftOnMove(left) {
    return new Limit().setMin(editableAreaBorderSize)
        .setMax(cropArea.getImage().getCurrentWidth() - croppingAreaParams.width + editableAreaBorderSize)
        .forValue(left);
}

function getValidTopOnMove(top) {
    return new Limit().setMin(editableAreaBorderSize)
        .setMax(cropArea.getImage().getCurrentHeight() - croppingAreaParams.height + editableAreaBorderSize)
        .forValue(top);
}

function getValidLeftOnResize(left) {
    return new Limit().setMin(editableAreaBorderSize)
        .setMax(angles.topRight.getLeft() - angles.getWidth())
        .forValue(left);
}

function getValidTopOnResize(top) {
    return new Limit().setMin(editableAreaBorderSize)
        .setMax(cropArea.getCroppingArea().getTop() + cropArea.getCroppingArea().getCurrentHeight() - angles.getDoubleHeight())
        .forValue(top);
}

function getValidLeftCropWidth(width) {
    return new Limit().setMin(angles.getDoubleWidth())
        .setMax(cropArea.getCroppingArea().getLeft() + cropArea.getCroppingArea().getCurrentWidth() - editableAreaBorderSize)
        .forValue(width);
}

function getValidRightCropWidth(width) {
    return new Limit().setMin(angles.getDoubleWidth())
        .setMax(cropArea.getImage().getCurrentWidth() - cropArea.getCroppingArea().getLeft() + editableAreaBorderSize)
        .forValue(width);
}

function getValidCropHeightTop(height) {
    return new Limit().setMin(angles.getDoubleHeight())
        .setMax(cropArea.getCroppingArea().getTop() + cropArea.getCroppingArea().getCurrentHeight() - editableAreaBorderSize)
        .forValue(height);
}

function getValidCropHeightBottom(height) {
    return new Limit().setMin(angles.getDoubleHeight())
        .setMax(cropArea.getImage().getCurrentHeight() - cropArea.getCroppingArea().getTop() + editableAreaBorderSize)
        .forValue(height);
}

function moveCropArea(top, left) {
    setElementTopLeft(cropArea.getCroppingArea(), top, left);
    moveCropImage(top, left);
}

events.on("update cropArea", () => {
    croppingAreaParams.width = cropArea.getCroppingArea().width();
    croppingAreaParams.height = cropArea.getCroppingArea().height();
});

function resizeCroppingTopLeft(deltaX, deltaY) {
    const newWidth = cropArea.getCroppingArea().width() + deltaX;
    const newHeight = cropArea.getCroppingArea().height() + deltaY;

    let newTop = cropArea.getCroppingArea().getTop() - deltaY;
    newTop = getValidTopOnResize(newTop);
    let newLeft = cropArea.getCroppingArea().getLeft() - deltaX;
    newLeft = getValidLeftOnResize(newLeft);

    const legalWidth = croppingAreaParams.width = getValidLeftCropWidth(newWidth);
    const legalHeight = croppingAreaParams.height = getValidCropHeightTop(newHeight);

    setElementWidthHeight(cropArea.getCroppingArea(), legalWidth, legalHeight);
    setElementTopLeft(cropArea.getCroppingImage(), (editableAreaBorderSize - newTop), (editableAreaBorderSize - newLeft));
    setElementTopLeft(cropArea.getCroppingArea(), newTop, newLeft);
}

function resizeCroppingTopRight(deltaX, deltaY) {
    const newWidth = cropArea.getCroppingArea().width() - deltaX;
    const newHeight = cropArea.getCroppingArea().height() + deltaY;

    let newTop = cropArea.getCroppingArea().getTop() - deltaY;
    newTop = getValidTopOnResize(newTop);

    const legalWidth = croppingAreaParams.width = getValidRightCropWidth(newWidth);
    const legalHeight = croppingAreaParams.height = getValidCropHeightTop(newHeight);

    setElementWidthHeight(cropArea.getCroppingArea(), legalWidth, legalHeight);
    cropArea.getCroppingImage().css("top", editableAreaBorderSize - newTop);
    cropArea.getCroppingArea().css("top", newTop);
}

function resizeCroppingBottomRight(deltaX, deltaY) {
    const newWidth = cropArea.getCroppingArea().width() - deltaX;
    const newHeight = cropArea.getCroppingArea().height() - deltaY;

    const legalWidth = croppingAreaParams.width = getValidRightCropWidth(newWidth);
    const legalHeight = croppingAreaParams.height = getValidCropHeightBottom(newHeight);

    setElementWidthHeight(cropArea.getCroppingArea(), legalWidth, legalHeight);
}

function resizeCroppingBottomLeft(deltaX, deltaY) {
    const newWidth = cropArea.getCroppingArea().width() + deltaX;
    const newHeight = cropArea.getCroppingArea().height() - deltaY;

    let newLeft = cropArea.getCroppingArea().getLeft() - deltaX;
    newLeft = getValidLeftOnResize(newLeft);

    const legalWidth = croppingAreaParams.width = getValidLeftCropWidth(newWidth);
    const legalHeight = croppingAreaParams.height = getValidCropHeightBottom(newHeight);

    setElementWidthHeight(cropArea.getCroppingArea(), legalWidth, legalHeight);
    cropArea.getCroppingImage().css("left", editableAreaBorderSize - newLeft);
    cropArea.getCroppingArea().css("left", newLeft);
}

function moveCroppingAngles(angleName, deltaX, deltaY) {
    let angle1X = 0, angle1Y = 0;
    let angle2X = 0, angle2Y = 0;
    let angle3X = 0, angle3Y = 0;
    let angle4X = 0, angle4Y = 0;

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

const resizeCropper = {
    topLeft: resizeCroppingTopLeft,
    topRight: resizeCroppingTopRight,
    bottomRight: resizeCroppingBottomRight,
    bottomLeft: resizeCroppingBottomLeft
};

function init(_imageData) {
    cropArea.getCroppingBlock().css("z-index", "50");

    const $image = editableImage.getImage();
    const src = $image.attr('src');

    cropArea.getCroppingArea().css({
        top: editableAreaBorderSize,
        left: editableAreaBorderSize,
    });
    cropArea.getImage().attr('src', src);
    cropArea.getCroppingImage().attr('src', src);

    let isMouseDown = false;
    let isResizing = false;

    imageData = _imageData;
    $imageEditor = $('.imcms-image_editor');

    angleBorderSize = angles.getBorderSize();
    doubleAngleBorderSize = angles.getDoubleBorderSize();
    imageCoords = cropArea.getImage().offset();
    imageCoords.top -= angleBorderSize;
    imageCoords.left -= angleBorderSize;

    const original = imageResize.getOriginal();
    const originImageWidth = original.width;
    const originImageHeight = original.height;

    const cropCoefficientX = imageData.width / originImageWidth;
    const cropCoefficientY = imageData.height / originImageHeight;

    if (!originImageWidth || !originImageHeight) {
        return;
    }

    const $croppingArea = cropArea.getCroppingArea();
    let cropRegion = $.extend({}, imageData.cropRegion);

    if (!cropRegion) {
        cropRegion = {
            cropX1: 0,
            cropY1: 0,
            cropX2: $croppingArea.width(),
            cropY2: $croppingArea.height()
        };
    } else {
        if (cropRegion.cropX1 < 0) {
            cropRegion.cropX1 = 0;
        } else {
            cropRegion.cropX1 /= cropCoefficientX;
        }

        if (cropRegion.cropY1 < 0) {
            cropRegion.cropY1 = 0;
        } else {
            cropRegion.cropY1 /= cropCoefficientY;
        }

        if (cropRegion.cropX2 < 0) {
            cropRegion.cropX2 = $croppingArea.width();
        }
        if (cropRegion.cropY2 < 0) {
            cropRegion.cropY2 = $croppingArea.height();
        }

        cropRegion.cropX2 /= cropCoefficientX;
        cropRegion.cropY2 /= cropCoefficientY;
    }

    croppingAreaParams = {
        height: cropRegion.cropY2 - cropRegion.cropY1,
        width: cropRegion.cropX2 - cropRegion.cropX1
    };

    removeCroppingListeners();

    setElementWidthHeight(cropArea.getCroppingImage(), originImageWidth, originImageHeight);
    setElementWidthHeight($croppingArea, croppingAreaParams.width, croppingAreaParams.height);
    setElementTopLeft($croppingArea, cropRegion.cropY1 + editableAreaBorderSize, cropRegion.cropX1 + editableAreaBorderSize);
    setElementTopLeft(cropArea.getCroppingImage(), -cropRegion.cropY1, -cropRegion.cropX1);

    setCroppingAnglesTopLeft(
        cropRegion.cropY1 + editableAreaBorderSize,
        cropRegion.cropX1 + editableAreaBorderSize,
    );

    cropRegion.cropX1 *= cropCoefficientX;
    cropRegion.cropY1 *= cropCoefficientY;
    cropRegion.cropX2 *= cropCoefficientX;
    cropRegion.cropY2 *= cropCoefficientY;

    angles.showAll();

    $croppingArea.on('mousedown', event => (isMouseDown = (event.which === 1)) && setCursor("move"));

    let resizeAngleName; // topLeft, topRight, bottomRight, bottomLeft

    function getAngleMouseDownEvent(angleName, cursor) {
        return event => {
            if (event.which === 1) {
                isResizing = true;
                resizeAngleName = angleName;
                isMouseDown = true;
                setCursor(cursor);
            }
        }
    }

    angles.topLeft.$angle.on('mousedown', getAngleMouseDownEvent("topLeft", "nw-resize"));
    angles.topRight.$angle.on('mousedown', getAngleMouseDownEvent("topRight", "ne-resize"));
    angles.bottomRight.$angle.on('mousedown', getAngleMouseDownEvent("bottomRight", "se-resize"));
    angles.bottomLeft.$angle.on('mousedown', getAngleMouseDownEvent("bottomLeft", "sw-resize"));

    $imageEditor.on('mouseup', event => {
        if ((event.which === 1) && isMouseDown) {
            isMouseDown = false;
            isResizing = false;
            setCursor("");
        }
    });

    function setCroppingAnglesTopLeft(top, left) {
        const fixedTop = top - angleBorderSize;
        const fixedLeft = left - angleBorderSize;
        const fixedWidth = croppingAreaParams.width + left - angles.getWidth();
        const fixedHeight = croppingAreaParams.height + top - angles.getWidth();

        angles.topRight.setTopLeft(fixedTop, fixedWidth);
        angles.topLeft.setTopLeft(fixedTop, fixedLeft);
        angles.bottomRight.setTopLeft(fixedHeight, fixedWidth);
        angles.bottomLeft.setTopLeft(fixedHeight, fixedLeft);
    }

    function setCursor(cursorValue) {
        [
            angles.topLeft.$angle,
            angles.topRight.$angle,
            angles.bottomRight.$angle,
            angles.bottomLeft.$angle,
            cropArea.getCroppingArea(),
            $imageEditor

        ].forEach($element => $element.css("cursor", cursorValue));
    }

    let prevX, prevY;

    $imageEditor.on('mousemove', event => {
        if (!isMouseDown) {
            prevX = undefined;
            prevY = undefined;
            return;
        }

        const newX = getValidCoordX(event.clientX);
        const newY = getValidCoordY(event.clientY);

        if (prevX === undefined || prevY === undefined) {
            prevX = newX;
            prevY = newY;
            return;
        }

        const deltaX = prevX - newX;
        const deltaY = prevY - newY;

        prevX = newX;
        prevY = newY;

        if (isResizing) {
            moveCroppingAngles(resizeAngleName, deltaX, deltaY);
            resizeCropper[resizeAngleName](deltaX, deltaY);

        } else {
            const croppingAreaTop = cropArea.getCroppingArea().getTop();
            const croppingAreaLeft = cropArea.getCroppingArea().getLeft();

            let newLeft = croppingAreaLeft - deltaX;
            let newTop = croppingAreaTop - deltaY;

            newLeft = getValidLeftOnMove(newLeft);
            newTop = getValidTopOnMove(newTop);

            moveCropArea(newTop, newLeft);
            setCroppingAnglesTopLeft(newTop, newLeft);
        }

        const cropAreaLeft = cropArea.getCroppingArea().getLeft() - 2;
        const cropAreaTop = cropArea.getCroppingArea().getTop() - 2;

        cropRegion.cropX1 = cropCoefficientX * (cropAreaLeft);
        cropRegion.cropY1 = cropCoefficientY * (cropAreaTop);
        cropRegion.cropX2 = cropCoefficientX * (cropAreaLeft + cropArea.getCroppingArea().width());
        cropRegion.cropY2 = cropCoefficientY * (cropAreaTop + cropArea.getCroppingArea().height());
    });

    $imageEditor.on("dragstart", () => false);
}

function removeEventListeners($element, eventsArr) {
    eventsArr.forEach(event => $element.off(event));
}

function removeCroppingListeners() {
    removeEventListeners(cropArea.getCroppingArea(), ["mousedown", "mouseup"]);
    angles.forEach($angle => removeEventListeners($angle, ["mousedown", "mouseup"]));
    removeEventListeners($imageEditor, ["mousemove", "mouseup", "dragstart"]);
}

function destroy() {
    cropArea.getCroppingBlock().css("z-index", "10");

    if (!imageData) {
        return;
    }

    moveCropArea(0, 0);

    [
        cropArea.getCroppingImage(),
        cropArea.getImage(),
        angles.topLeft.$angle,
        angles.topRight.$angle,
        angles.bottomRight.$angle,
        angles.bottomLeft.$angle,
        cropArea.getCroppingArea()

    ].forEach($element => $element.removeAttr("style"));

    removeCroppingListeners();
    angles.hideAll();
}

function setCropAreaX(newX) {
    const newLeft = getValidLeftOnResize(newX);
    const oldLeft = cropArea.getCroppingArea().getLeft();
    const oldWidth = cropArea.getCroppingArea().width();
    const deltaX = oldLeft - newLeft;
    const legalWidth = croppingAreaParams.width = getValidLeftCropWidth(oldWidth + deltaX);

    cropArea.getCroppingArea().width(legalWidth);
    cropArea.getCroppingArea().css("left", newLeft);
    cropArea.getCroppingImage().css("left", editableAreaBorderSize - newLeft);
}

function setCropX(newX) {
    angles.topLeft.setNewX(newX + editableAreaBorderSize - angleBorderSize);
    angles.bottomLeft.setNewX(newX + editableAreaBorderSize - angleBorderSize);

    setCropAreaX(newX + editableAreaBorderSize);
}

function setCropAreaX1(newX1) {
    const oldLeft = cropArea.getCroppingArea().getLeft();
    const oldWidth = cropArea.getCroppingArea().width();
    const oldX1 = oldLeft + oldWidth;

    if (oldX1 === newX1) {
        return;
    }

    const deltaX1 = oldX1 - newX1;
    const newWidth = oldWidth - deltaX1;
    const legalWidth = croppingAreaParams.width = getValidRightCropWidth(newWidth);

    cropArea.getCroppingArea().width(legalWidth);
}

function setCropX1(newX1) {
    angles.topRight.setNewX(newX1 - angles.getWidth() + editableAreaBorderSize);
    angles.bottomRight.setNewX(newX1 - angles.getWidth() + editableAreaBorderSize);

    setCropAreaX1(newX1 + editableAreaBorderSize);
}

function setCropAreaY(newY) {
    const oldTop = cropArea.getCroppingArea().getTop();
    let newTop = newY + editableAreaBorderSize;

    newTop = getValidTopOnResize(newTop);

    const deltaY = newTop - oldTop;
    const newHeight = cropArea.getCroppingArea().height() - deltaY;
    const legalHeight = croppingAreaParams.height = getValidCropHeightTop(newHeight);

    cropArea.getCroppingArea().height(legalHeight);
    cropArea.getCroppingImage().css("top", editableAreaBorderSize - newTop);
    cropArea.getCroppingArea().css("top", newTop);
}

function setCropY(newY) {
    angles.topRight.setNewY(newY + editableAreaBorderSize - angleBorderSize);
    angles.topLeft.setNewY(newY + editableAreaBorderSize - angleBorderSize);

    setCropAreaY(newY);
}

function setCropAreaY1(newY1) {
    const oldHeight = cropArea.getCroppingArea().height();
    const oldTop = cropArea.getCroppingArea().getTop();
    const oldY1 = oldHeight + oldTop;

    if (oldY1 === newY1) {
        return;
    }

    const deltaY1 = newY1 - oldY1;
    const newHeight = oldHeight + deltaY1;
    const legalHeight = croppingAreaParams.height = getValidCropHeightBottom(newHeight);

    cropArea.getCroppingArea().height(legalHeight);
}

function setCropY1(newY1) {
    angles.bottomRight.setNewY(newY1 - angles.getWidth() + editableAreaBorderSize);
    angles.bottomLeft.setNewY(newY1 - angles.getWidth() + editableAreaBorderSize);

    setCropAreaY1(newY1 + editableAreaBorderSize);
}

module.exports = {
    initImageCropper: init,
    setCropX: setCropX,
    setCropX1: setCropX1,
    setCropY: setCropY,
    setCropY1: setCropY1,
    applyCropping() {
        const $croppingArea = cropArea.getCroppingArea();
        const cropRegion = imageData.cropRegion;

        cropRegion.cropX1 = $croppingArea.getLeft() - editableAreaBorderSize;
        cropRegion.cropY1 = $croppingArea.getTop() - editableAreaBorderSize;
        cropRegion.cropX2 = $croppingArea.getCurrentWidth() + cropRegion.cropX1;
        cropRegion.cropY2 = $croppingArea.getCurrentHeight() + cropRegion.cropY1;
    },
    destroyImageCropper: destroy
};
