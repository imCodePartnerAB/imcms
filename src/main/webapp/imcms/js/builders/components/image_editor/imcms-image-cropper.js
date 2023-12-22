/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.09.17
 */
const angles = require('imcms-image-crop-angles');
const Limit = require('imcms-numeric-limiter');
const $ = require('jquery');
const cropArea = require('imcms-cropping-area');
const imageResize = require('imcms-image-resize');
const previewImageArea = require('imcms-preview-image-area');
const imageZoom = require('imcms-image-zoom');

let $imageEditor, angleBorderSize, imageCoords, imageData;
let croppingAreaParams= {};

function moveCropImage(newTop, newLeft) {
    const cropImgTop = -newTop;
    const cropImgLeft = -newLeft;

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
    return new Limit().setMin(0)
        .setMax(cropArea.getImage().getCurrentWidth() + imageCoords.left + angleBorderSize)
        .forValue(coordX);
}

function getValidCoordY(coordY) {
    return new Limit().setMin(0)
        .setMax(cropArea.getImage().getCurrentHeight() + imageCoords.top + angleBorderSize)
        .forValue(coordY);
}

function getValidLeftOnMove(left) {
    return new Limit().setMin(0)
        .setMax(cropArea.getImage().getCurrentWidth() - croppingAreaParams.width)
        .forValue(left);
}

function getValidTopOnMove(top) {
    return new Limit().setMin(0)
        .setMax(cropArea.getImage().getCurrentHeight() - croppingAreaParams.height)
        .forValue(top);
}

function getValidLeftOnResize(left) {
    return new Limit().setMin(0)
        .setMax(
            cropArea.getCroppingArea().getLeft()
            + cropArea.getCroppingArea().getCurrentWidth()
            - angles.getDoubleWidth()
        )
        .forValue(left);
}

function getValidTopOnResize(top) {
    return new Limit().setMin(0)
        .setMax(
            cropArea.getCroppingArea().getTop()
            + cropArea.getCroppingArea().getCurrentHeight()
            - angles.getDoubleHeight()
        )
        .forValue(top);
}

function getValidLeftCropWidth(width) {
    return new Limit().setMin(angles.getDoubleWidth())
        .setMax(
            cropArea.getCroppingArea().getLeft()
            + cropArea.getCroppingArea().getCurrentWidth()
        )
        .forValue(width);
}

function getValidRightCropWidth(width) {
    return new Limit().setMin(angles.getDoubleWidth())
        .setMax(
            cropArea.getImage().getCurrentWidth()
            - cropArea.getCroppingArea().getLeft()
        )
        .forValue(width);
}

function getValidCropHeightTop(height) {
    return new Limit().setMin(angles.getDoubleHeight())
        .setMax(
            cropArea.getCroppingArea().getTop()
            + cropArea.getCroppingArea().getCurrentHeight()
        )
        .forValue(height);
}

function getValidCropHeightBottom(height) {
    return new Limit().setMin(angles.getDoubleHeight())
        .setMax(
            cropArea.getImage().getCurrentHeight()
            - cropArea.getCroppingArea().getTop()
        )
        .forValue(height);
}

function moveCropArea(top, left) {
    setElementTopLeft(cropArea.getCroppingArea(), top, left);
    moveCropImage(top, left);

    const angleTop = top - angleBorderSize;
    const angleLeft = left - angleBorderSize;

    const angleRight = imageResize.getOriginal().width
        - cropArea.getCroppingArea().getLeft()
        - cropArea.getCroppingArea().getCurrentWidth()
        - angleBorderSize;

    const angleBottom = imageResize.getOriginal().height
        - cropArea.getCroppingArea().getTop()
        - cropArea.getCroppingArea().getCurrentHeight()
        - angleBorderSize;

    angles.topLeft.setTopLeft(angleTop, angleLeft);
    angles.topRight.setTopRight(angleTop, angleRight);
    angles.bottomRight.setBottomRight(angleBottom, angleRight);
    angles.bottomLeft.setBottomLeft(angleBottom, angleLeft);
}

function isOversize(width, height) {
    return (height > imageResize.getOriginal().height) || (width > imageResize.getOriginal().width)
}

function resizeCroppingTopLeft(deltaX, deltaY) {
    if ((deltaX === 0) && (deltaY === 0)|| croppingAreaParams === null) return;

	const oldWidth = cropArea.getCroppingArea().width();
	const oldHeight = cropArea.getCroppingArea().height();

	let newWidth = oldWidth + deltaX;
	let newHeight = oldHeight + deltaY;

	newWidth = getValidLeftCropWidth(newWidth);
	newHeight = getValidCropHeightTop(newHeight);

	if (imageResize.isSaveProportionsEnabled()) {
		const proportionsK = imageResize.getProportionsCoefficient();
		const newProportionsK = (newWidth / newHeight);

		if (proportionsK !== newProportionsK) {
			if (deltaY === 0) newHeight = ~~(newWidth / proportionsK);
			else newWidth = ~~(proportionsK * newHeight);
			deltaX = newWidth - oldWidth;
			deltaY = newHeight - oldHeight;

			if (isOversize(newWidth, newHeight)) return;
		}
	}

    croppingAreaParams.width = newWidth;
    croppingAreaParams.height = newHeight;

    let newTop = cropArea.getCroppingArea().getTop() - deltaY;
    newTop = getValidTopOnResize(newTop);
    let newLeft = cropArea.getCroppingArea().getLeft() - deltaX;
    newLeft = getValidLeftOnResize(newLeft);

    setElementWidthHeight(cropArea.getCroppingArea(), newWidth, newHeight);
    setElementTopLeft(cropArea.getCroppingImage(), -newTop, -newLeft);
    setElementTopLeft(cropArea.getCroppingArea(), newTop, newLeft);

    const angleTop = newTop - angleBorderSize;
    const angleLeft = newLeft - angleBorderSize;

    const angleRight = imageResize.getOriginal().width
        - cropArea.getCroppingArea().getLeft()
        - newWidth
        - angleBorderSize;

    const angleBottom = imageResize.getOriginal().height
        - cropArea.getCroppingArea().getTop()
        - newHeight
        - angleBorderSize;

    angles.topLeft.setTopLeft(angleTop, angleLeft);
    angles.topRight.setTopRight(angleTop, angleRight);
    angles.bottomLeft.setBottomLeft(angleBottom, angleLeft);
    angles.bottomRight.setBottomRight(angleBottom, angleRight);
}

function resizeCroppingTopRight(deltaX, deltaY) {
    if ((deltaX === 0) && (deltaY === 0) || croppingAreaParams === null) return;

    const oldWidth = cropArea.getCroppingArea().width();
    const oldHeight = cropArea.getCroppingArea().height();

    let newWidth = oldWidth - deltaX;
    let newHeight = oldHeight + deltaY;

    newWidth = getValidRightCropWidth(newWidth);
    newHeight = getValidCropHeightTop(newHeight);

    if (imageResize.isSaveProportionsEnabled()) {
	    const proportionsK = imageResize.getProportionsCoefficient();
	    const newProportionsK = (newWidth / newHeight);

	    if (proportionsK !== newProportionsK) {
		    if (deltaY === 0) newHeight = ~~(newWidth / proportionsK);
		    else newWidth = ~~(proportionsK * newHeight);
		    deltaY = newHeight - oldHeight;

		    if (isOversize(newWidth, newHeight)) return;
	    }
    }

    croppingAreaParams.width = newWidth;
    croppingAreaParams.height = newHeight;

    const allWidth = cropArea.getCroppingArea().getLeft() + newWidth;

    if (allWidth > imageResize.getOriginal().width) {
        const deltaW = allWidth - imageResize.getOriginal().width;

        cropArea.getCroppingArea().setLeft(cropArea.getCroppingArea().getLeft() - deltaW);
        cropArea.getCroppingImage().setLeft(cropArea.getCroppingImage().getLeft() + deltaW);

        angles.topLeft.setLeft(angles.topLeft.getLeft() - deltaW);
        angles.bottomLeft.setLeft(angles.bottomLeft.getLeft() - deltaW);
    }

    let newTop = cropArea.getCroppingArea().getTop() - deltaY;
    newTop = getValidTopOnResize(newTop);

    setElementWidthHeight(cropArea.getCroppingArea(), newWidth, newHeight);
    cropArea.getCroppingImage().setTop(-newTop);
    cropArea.getCroppingArea().setTop(newTop);

    const angleTop = newTop - angleBorderSize;
    const angleRight = imageResize.getOriginal().width
        - cropArea.getCroppingArea().getLeft()
        - newWidth
        - angleBorderSize;

    const angleBottom = imageResize.getOriginal().height
        - cropArea.getCroppingArea().getTop()
        - newHeight
        - angleBorderSize;

    angles.topLeft.setTop(angleTop);
    angles.topRight.setTopRight(angleTop, angleRight);
    angles.bottomRight.setBottomRight(angleBottom, angleRight);
    angles.bottomLeft.setBottom(angleBottom);
}

function resizeCroppingBottomRight(deltaX, deltaY) {
    if ((deltaX === 0) && (deltaY === 0) || croppingAreaParams === null) return;

    const oldWidth = cropArea.getCroppingArea().width();
    const oldHeight = cropArea.getCroppingArea().height();

    let newWidth = oldWidth - deltaX;
    let newHeight = oldHeight - deltaY;

    newWidth = getValidRightCropWidth(newWidth);
    newHeight = getValidCropHeightBottom(newHeight);

    if (imageResize.isSaveProportionsEnabled()) {
	    const proportionsK = imageResize.getProportionsCoefficient();
	    const newProportionsK = (newWidth / newHeight);

	    if (proportionsK !== newProportionsK) {
		    if (deltaY === 0) newHeight = ~~(newWidth / proportionsK);
		    else newWidth = ~~(proportionsK * newHeight);

		    if (isOversize(newWidth, newHeight)) return;
	    }
    }

    croppingAreaParams.width = newWidth;
    croppingAreaParams.height = newHeight;

    const allHeight = cropArea.getCroppingArea().getTop() + newHeight;

    if (allHeight > imageResize.getOriginal().height) {
        const deltaH = allHeight - imageResize.getOriginal().height;

        cropArea.getCroppingArea().setTop(cropArea.getCroppingArea().getTop() - deltaH);
        cropArea.getCroppingImage().setTop(cropArea.getCroppingImage().getTop() + deltaH);

        angles.topLeft.setTop(angles.topLeft.getTop() - deltaH);
        angles.topRight.setTop(angles.topRight.getTop() - deltaH);
    }

    const allWidth = cropArea.getCroppingArea().getLeft() + newWidth;

    if (allWidth > imageResize.getOriginal().width) {
        const deltaW = allWidth - imageResize.getOriginal().width;

        cropArea.getCroppingArea().setLeft(cropArea.getCroppingArea().getLeft() - deltaW);
        cropArea.getCroppingImage().setLeft(cropArea.getCroppingImage().getLeft() + deltaW);

        angles.topLeft.setLeft(angles.topLeft.getLeft() - deltaW);
        angles.bottomLeft.setLeft(angles.bottomLeft.getLeft() - deltaW);
    }

    setElementWidthHeight(cropArea.getCroppingArea(), newWidth, newHeight);

    const angleBottom = imageResize.getOriginal().height
        - cropArea.getCroppingArea().getTop()
        - newHeight
        - angleBorderSize;

    const angleRight = imageResize.getOriginal().width
        - cropArea.getCroppingArea().getLeft()
        - newWidth
        - angleBorderSize;

    angles.topRight.setRight(angleRight);
    angles.bottomLeft.setBottom(angleBottom);
    angles.bottomRight.setBottomRight(angleBottom, angleRight);
}

function resizeCroppingBottomLeft(deltaX, deltaY) {
    if ((deltaX === 0) && (deltaY === 0) || croppingAreaParams === null) return;

    const oldWidth = cropArea.getCroppingArea().width();
    const oldHeight = cropArea.getCroppingArea().height();

    let newWidth = oldWidth + deltaX;
    let newHeight = oldHeight - deltaY;

    newWidth = getValidLeftCropWidth(newWidth);
    newHeight = getValidCropHeightBottom(newHeight);

    if (imageResize.isSaveProportionsEnabled()) {
	    const proportionsK = imageResize.getProportionsCoefficient();
	    const newProportionsK = (newWidth / newHeight);

	    if (proportionsK !== newProportionsK) {
		    if (deltaY === 0) newHeight = ~~(newWidth / proportionsK);
		    else newWidth = ~~(proportionsK * newHeight);
		    deltaX = newWidth - oldWidth;

		    if (isOversize(newWidth, newHeight)) return;
	    }
    }

    croppingAreaParams.width = newWidth;
    croppingAreaParams.height = newHeight;

    let newLeft = cropArea.getCroppingArea().getLeft() - deltaX;
    newLeft = getValidLeftOnResize(newLeft);

    const allHeight = cropArea.getCroppingArea().getTop() + newHeight;

    if (allHeight > imageResize.getOriginal().height) {
        const deltaH = allHeight - imageResize.getOriginal().height;

        cropArea.getCroppingArea().setTop(cropArea.getCroppingArea().getTop() - deltaH);
        cropArea.getCroppingImage().setTop(cropArea.getCroppingImage().getTop() + deltaH);

        angles.topLeft.setTop(angles.topLeft.getTop() - deltaH);
        angles.topRight.setTop(angles.topRight.getTop() - deltaH);
    }

    setElementWidthHeight(cropArea.getCroppingArea(), newWidth, newHeight);
    cropArea.getCroppingImage().setLeft(-newLeft);
    cropArea.getCroppingArea().setLeft(newLeft);

    const angleBottom = imageResize.getOriginal().height
        - cropArea.getCroppingArea().getTop()
        - newHeight
        - angleBorderSize;

    const angleRight = imageResize.getOriginal().width
        - cropArea.getCroppingArea().getLeft()
        - newWidth
        - angleBorderSize;

    const angleLeft = newLeft - angleBorderSize;

    angles.topRight.setRight(angleRight);
    angles.topLeft.setLeft(angleLeft);
    angles.bottomLeft.setBottomLeft(angleBottom, angleLeft);
    angles.bottomRight.setBottomRight(angleBottom, angleRight);
}

const resizeCropper = {
    topLeft: resizeCroppingTopLeft,
    topRight: resizeCroppingTopRight,
    bottomRight: resizeCroppingBottomRight,
    bottomLeft: resizeCroppingBottomLeft
};

function init(_imageData) {

    if (!_imageData || !_imageData.path) return;

    const $croppingBlock = cropArea.getCroppingBlock().css({
        "z-index": 50,
    });

    const $image = previewImageArea.getPreviewImage();
    const src = $image.attr('data-src');

    cropArea.getCroppingArea().css({
        top: 0,
        left: 0,
    });
    cropArea.getImage().attr('src', src);
    cropArea.getCroppingImage().attr('src', src);

    let isMouseDown = false;
    let isResizing = false;

    imageData = _imageData;
    $imageEditor = $('.imcms-image_editor');

    angleBorderSize = angles.getBorderSize();

    imageCoords = cropArea.getImage().offset();
    imageCoords.top -= angleBorderSize;
    imageCoords.left -= angleBorderSize;

    const original = imageResize.getOriginal();
    const originImageWidth = original.width;
    const originImageHeight = original.height;

    if (!originImageWidth || !originImageHeight) return;

    const $croppingArea = cropArea.getCroppingArea();
    let cropRegion = imageData.cropRegion && {...imageData.cropRegion};

    if (cropRegion) {
        if (cropRegion.cropX1 < 0) cropRegion.cropX1 = 0;
        if (cropRegion.cropY1 < 0) cropRegion.cropY1 = 0;
        if (cropRegion.cropX2 <= 1) cropRegion.cropX2 = originImageWidth;
        if (cropRegion.cropY2 <= 1) cropRegion.cropY2 = originImageHeight;
    } else {
        cropRegion = {
            cropX1: 0,
            cropY1: 0,
            cropX2: originImageWidth,
            cropY2: originImageHeight
        };
    }

    croppingAreaParams = {
        height: cropRegion.cropY2 - cropRegion.cropY1,
        width: cropRegion.cropX2 - cropRegion.cropX1
    };

    removeCroppingListeners();

    setElementWidthHeight(cropArea.getCroppingImage(), originImageWidth, originImageHeight);
    setElementWidthHeight($croppingArea, croppingAreaParams.width, croppingAreaParams.height);
    setElementTopLeft($croppingArea, cropRegion.cropY1, cropRegion.cropX1);
    setElementTopLeft(cropArea.getCroppingImage(), -cropRegion.cropY1, -cropRegion.cropX1);

    const fixedTop = cropRegion.cropY1 - angleBorderSize;
    const fixedLeft = cropRegion.cropX1 - angleBorderSize;
    const fixedRight = originImageWidth - cropRegion.cropX2 - angleBorderSize;
    const fixedBottom = originImageHeight - cropRegion.cropY2 - angleBorderSize;

    const zoomVal = parseFloat(imageZoom.getRelativeZoomValueByOriginalImg());
    if (!isNaN(zoomVal)) {
	    let angleWidthHeight = Math.round(1000 / Number(zoomVal * 100));
	    let angleBorderWidth = Math.round(100 / Number(zoomVal * 100));
	    $croppingBlock.find('.imcms-angle').each(function () {
		    const $angle = $(this).first();
		    $angle.css({
			    'width': angleWidthHeight,
			    'height': angleWidthHeight,
			    'border-width': angleBorderWidth,
		    })
	    })
    }

    angles.showAll();

    angles.topLeft.setTopLeft(fixedTop, fixedLeft);
    angles.topRight.setTopRight(fixedTop, fixedRight);
    angles.bottomLeft.setBottomLeft(fixedBottom, fixedLeft);
    angles.bottomRight.setBottomRight(fixedBottom, fixedRight);

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
	    const zoomValue = parseFloat(imageZoom.getRelativeZoomValueByOriginalImg());
	    const newX = ~~getValidCoordX(event.clientX / zoomValue);
	    const newY = ~~getValidCoordY(event.clientY / zoomValue);

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
            resizeCropper[resizeAngleName](deltaX, deltaY);

        } else {
            const croppingAreaTop = cropArea.getCroppingArea().getTop();
            const croppingAreaLeft = cropArea.getCroppingArea().getLeft();

            let newLeft = croppingAreaLeft - deltaX;
            let newTop = croppingAreaTop - deltaY;

            newLeft = getValidLeftOnMove(newLeft);
            newTop = getValidTopOnMove(newTop);

            moveCropArea(newTop, newLeft);
        }
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

    if (!imageData) return;

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

    imageZoom.updateZoomGradeValue();

    croppingAreaParams = null;
    imageCoords = null;
    imageData = null;
}

function setCropX(newX) {
    if (!croppingAreaParams) return;

    const deltaX = cropArea.getCroppingArea().getLeft() - newX;
    resizeCroppingTopLeft(deltaX, 0);
}

function setCropX1(newX1) {
    if (!croppingAreaParams) return;

    const oldLeft = cropArea.getCroppingArea().getLeft();
    const oldWidth = cropArea.getCroppingArea().width();
    const oldX1 = oldLeft + oldWidth;

    if (oldX1 === newX1) {
        return;
    }

    const deltaX1 = oldX1 - newX1;

    resizeCroppingTopRight(deltaX1, 0);
}

function setCropY(newY) {
    if (!croppingAreaParams) return;

    const oldTop = cropArea.getCroppingArea().getTop();
    const deltaY = oldTop - newY;

    resizeCroppingTopLeft(0, deltaY);
}

function setCropY1(newY1) {
    if (!croppingAreaParams) return;

    const oldHeight = cropArea.getCroppingArea().height();
    const oldTop = cropArea.getCroppingArea().getTop();
    const oldY1 = oldHeight + oldTop;

    if (oldY1 === newY1) {
        return;
    }

    const deltaY1 = oldY1 - newY1;

    resizeCroppingBottomLeft(0, deltaY1);
}

module.exports = {
    initImageCropper: init,
    setCropX: setCropX,
    setCropX1: setCropX1,
    setCropY: setCropY,
    setCropY1: setCropY1,
    refreshCropping() {
        resizeCroppingTopLeft(-1, 0);
        resizeCroppingTopLeft(1, 0);

        resizeCroppingBottomRight(0, -1);
        resizeCroppingBottomRight(0, 1);
    },
    applyCropping() {

        if (!imageData || !imageData.path) return;

	    const $croppingArea = cropArea.getCroppingArea();
	    const cropRegion = imageData.cropRegion || (imageData.cropRegion = {});
	    const croppedWidth = $croppingArea.getCurrentWidth();
	    const croppedHeight = $croppingArea.getCurrentHeight();

	    cropRegion.cropX1 = $croppingArea.getLeft();
	    cropRegion.cropY1 = $croppingArea.getTop();
	    cropRegion.cropX2 = croppedWidth + cropRegion.cropX1;
	    cropRegion.cropY2 = croppedHeight + cropRegion.cropY1;

	    imageResize.enableResetToOriginalFlag(); // => need to correct set background W/H in strictW/strictH
	    imageResize.checkCropRegionExist(imageData);
	    imageResize.setWidthStrict(cropRegion.cropX1, croppedWidth, false);
	    imageResize.setHeightStrict(cropRegion.cropY1, croppedHeight, false);
	    imageResize.setCurrentPreviewSize(croppedWidth, croppedHeight);

	    imageResize.updateSizing(imageData, true, false);
	    imageResize.disabledResetToOriginalFlag(); //did default state
    },
    destroyImageCropper: destroy
};
