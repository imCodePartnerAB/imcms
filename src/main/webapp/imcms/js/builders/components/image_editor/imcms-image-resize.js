/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
const originImageHeightBlock = require('imcms-origin-image-height-block');
const originImageWidthBlock = require('imcms-origin-image-width-block');
const editableImage = require('imcms-editable-image');
const previewImage = require('imcms-preview-image-area');

let saveProportions = true; // by default
let resetToOriginal = false; // by default
const original = {};
const preview = {};
const currentSize = {};
const currentPrevSize = {};
const finalImageStylesPosition = {};
let currentFinalPrevImg = {};
let proportionsCoefficient;

let maxWidth, maxHeight, minWidth, minHeight;

function trimToMaxMinWidth(newWidth) {
    if (maxWidth) newWidth = Math.min(newWidth, maxWidth);
    if (minWidth) newWidth = Math.max(newWidth, minWidth);

    return newWidth;
}

function trimToMaxMinHeight(newHeight) {
    if (maxHeight) newHeight = Math.min(newHeight, maxHeight);
    if (minHeight) newHeight = Math.max(newHeight, minHeight);

    return newHeight;
}

function setWidth(newWidth, isOriginal) {
    if (isOriginal) {
        const $image = editableImage.getImage();
        $image.width(original.width);
        $widthControl.val(original.width);
    } else {
        const $image = previewImage.getPreviewImage();
        const oldWidth = $image.width();
        const k = newWidth / oldWidth;

        const newImageLeft = k * previewImage.getBackgroundPositionX();
        const newImageBackgroundWidth = k * previewImage.getBackgroundWidth();

        $image.width(newWidth);
        previewImage.setBackgroundWidth(newImageBackgroundWidth);
        previewImage.setBackgroundPositionX(newImageLeft);

        $widthPreviewControl.val(newWidth);
    }

}

function setHeight(newHeight, isOriginal) {
    if (isOriginal) {
        const $image = editableImage.getImage();
        $image.height(original.height);
        $heightControl.val(original.height);
    } else {
        const $image = previewImage.getPreviewImage();
        const oldHeight = $image.height();
        const k = newHeight / oldHeight;

        const newImageTop = k * previewImage.getBackgroundPositionY();
        const newImageBackgroundHeight = k * previewImage.getBackgroundHeight();

        $image.height(newHeight);
        previewImage.setBackgroundHeight(newImageBackgroundHeight);
        previewImage.setBackgroundPositionY(newImageTop);

        $heightPreviewControl.val(newHeight);
    }
}

function setHeightProportionally(newHeight, isOriginal) {
    newHeight = trimToMaxMinHeight(newHeight);
    setHeight(newHeight, isOriginal);
    if (saveProportions) {
        updateWidthProportionally(newHeight, isOriginal);
    }
}

function setWidthProportionally(newWidth, isOriginal) {
    newWidth = trimToMaxMinWidth(newWidth);
    setWidth(newWidth, isOriginal);
    if (saveProportions) {
        updateHeightProportionally(newWidth, isOriginal);
    }
}

function updateWidthProportionally(newHeight, isOriginal) {
    const proportionalWidth = ~~(newHeight * proportionsCoefficient);
    const fixedWidth = trimToMaxMinWidth(proportionalWidth);

    (fixedWidth === proportionalWidth)
        ? setWidth(proportionalWidth, isOriginal)
        : setWidthProportionally(proportionalWidth, isOriginal); // MAY (or not) APPEAR RECURSIVE!!!11 be careful
}

function updateHeightProportionally(newWidth, isOriginal) {
    const proportionalHeight = ~~(newWidth / proportionsCoefficient);
    const fixedHeight = trimToMaxMinHeight(proportionalHeight);

    (fixedHeight === proportionalHeight)
        ? setHeight(proportionalHeight, isOriginal)
        : setHeightProportionally(proportionalHeight, isOriginal); // MAY (or not) APPEAR RECURSIVE!!!11 be careful
}

let $heightControl, $widthControl;
let $heightPreviewControl, $widthPreviewControl;

module.exports = {
    resetToOriginal(imageData) {
        this.enableResetToOriginalFlag();
        this.setHeightStrict(0, original.height, false);
        this.setWidthStrict(0, original.width, false);

        let width, height;

        if (minWidth && minHeight) {
            width = minWidth;
            height = minHeight;
            preview.width = minWidth;
            preview.height = minHeight;

        } else {
            if (this.isAnyRestrictedStyleSize()) {
                width = minWidth ? minWidth : original.width;
                height = minHeight ? minHeight : original.height;
                preview.width = width;
                preview.height = height;
            } else {
                width = original.width;
                height = original.height;
                preview.width = original.width;
                preview.height = original.height;
            }

        }

        this.setCurrentPreviewSize(width, height);
        this.updateSizing(imageData, true, false);
        this.disabledResetToOriginalFlag();
    },

    resetToPreview(imageData) {
        this.setHeightStrict(finalImageStylesPosition.backgroundPositionY, imageData.height, false);
        this.setWidthStrict(finalImageStylesPosition.backgroundPositionX, imageData.width, false);

        let width, height;

        if (minWidth && minHeight) {
            width = minWidth;
            height = minHeight;

        } else {
            width = imageData.width;
            height = imageData.height;
        }

        this.setCurrentPreviewSize(width, height);
        this.setPreview(width, height);
        this.updateSizing(imageData, true, false);
    },
    setCurrentSize(width, height) {
        currentSize.width = width;
        currentSize.height = height;
        proportionsCoefficient = currentSize.width / currentSize.height;
    },
    setCurrentPreviewSize(width, height) {
        currentPrevSize.width = width;
        currentPrevSize.height = height;
        proportionsCoefficient = currentPrevSize.width / currentPrevSize.height;
    },
    setFinalPreviewImageData(image) {
        currentFinalPrevImg = JSON.parse(JSON.stringify(image));
    },
    getFinalPreviewImageData: () => currentFinalPrevImg,
    setFinalPreviewBackGroundPositionX(positionX) {
        finalImageStylesPosition.backgroundPositionX = positionX;
    },
    setFinalPreviewBackGroundPositionY(positionY) {
        finalImageStylesPosition.backgroundPositionY = positionY;
    },
    getOriginal: () => original,
    setOriginal(originalWidth, originalHeight) {
        originImageHeightBlock.setValue(originalHeight);
        originImageWidthBlock.setValue(originalWidth);

        original.width = originalWidth;
        original.height = originalHeight;

        this.setCurrentSize(originalWidth, originalHeight);
    },

    getPreview: () => preview,
    setPreview(previewWidth, previewHeight) {
        preview.width = previewWidth;
        preview.height = previewHeight;

        this.setCurrentPreviewSize(previewWidth, previewHeight);
    },
    setWidthControl($control) {
        $widthControl = $control
    },

    setHeightControl($control) {
        $heightControl = $control
    },

    setPreviewWidthControl($control) {
        $widthPreviewControl = $control
    },

    setPreviewHeightControl($control) {
        $heightPreviewControl = $control
    },

    isProportionsLockedByStyle() {
        return minWidth && minHeight
    },

    isAnyRestrictedStyleSize() {
        return minWidth || minHeight
    },

    isRestrictedWidthStyleSize() {
        return minWidth;
    },

    isRestrictedHeightStyleSize() {
        return minHeight;
    },

    isSaveProportionsEnabled: () => saveProportions,

    getProportionsCoefficient: () => proportionsCoefficient,

    toggleSaveProportions: () => (saveProportions = !saveProportions),

    enableSaveProportions() {
        saveProportions = true;
    },

    enableResetToOriginalFlag() {
        resetToOriginal = true;
    },

    disabledResetToOriginalFlag() {
        resetToOriginal = false;
    },

    setHeight(newValue, isOriginal) {
        setHeight(trimToMaxMinHeight(newValue), isOriginal);
    },

    setWidth(newValue, isOriginal) {
        setWidth(trimToMaxMinWidth(newValue), isOriginal);
    },

    /**
     * Setting without any proportions or min/max checking
     * @param padding for cropped images
     * @param newWidth
     * @param isOriginal
     */

    setWidthStrict(padding, newWidth, isOriginal) {
        if (isOriginal) {
            editableImage.getImage().width(original.width);
            $widthControl.val(newWidth);
        } else {
            previewImage.setBackgroundWidth(original.width);
            previewImage.getPreviewImage().width(newWidth);

            (padding >= 0)
                ? previewImage.setBackgroundPositionX(-padding)
                : previewImage.setBackgroundPositionX(padding);

            $widthPreviewControl.val(newWidth);
        }
    },

    /**
     * Setting without any proportions or min/max checking
     * @param padding for cropped images
     * @param newHeight
     * @param isOriginal
     */
    setHeightStrict(padding, newHeight, isOriginal) {
        if (isOriginal) {
            editableImage.getImage().height(original.height);
            $heightControl.val(newHeight);
        } else {
            previewImage.setBackgroundHeight(original.height);
            previewImage.getPreviewImage().height(newHeight);

            (padding >= 0)
                ? previewImage.setBackgroundPositionY(-padding)
                : previewImage.setBackgroundPositionY(padding);

            $heightPreviewControl.val(newHeight);
        }
    },

    setHeightProportionally: setHeightProportionally,

    setWidthProportionally: setWidthProportionally,

    getWidth: () => editableImage.getImage().width(),

    getHeight: () => editableImage.getImage().height(),

    getPreviewWidth: () => previewImage.getPreviewImage().width(),

    getPreviewHeight: () => previewImage.getPreviewImage().height(),

    setMaxWidth(maxWidthValue) {
        maxWidth = maxWidthValue
    },

    setMaxHeight(maxHeightValue) {
        maxHeight = maxHeightValue
    },

    setMinWidth(minWidthValue) {
        minWidth = minWidthValue
    },

    setMinHeight(minHeightValue) {
        minHeight = minHeightValue
    },

    /**
     * Can be used after setting strict w/h to update all proportions and min/max restrictions
     * @param isOriginal value for original image or original tab data
     * @param imageData
     * @param ignoreCropping
     */
    updateSizing(imageData, ignoreCropping, isOriginal) {
        const originalProportionsK = isOriginal ? original.width / original.height : preview.width / preview.height;

        if (minWidth && minHeight && (originalProportionsK !== (minWidth / minHeight))) {
            const width = minWidth;
            const height = minHeight;
            const restrictedProportionsK = minWidth / minHeight;

            const dX = isOriginal ? width / original.width : width / preview.width;
            const dY = isOriginal ? height / original.height : height / preview.height;

            let newWidth, newHeight, cropHeight, cropWidth;

            const cropRegion = imageData.cropRegion || (imageData.cropRegion = {
                cropX1: 0,
                cropX2: 0,
                cropY1: 0,
                cropY2: 0,
            });

            const widthCurrentObj = isOriginal ? original.width : preview.width;
            const heightCurrentObj = isOriginal ? original.height : preview.height;

            if (dX > dY) {
                const croppedWidth = Math.max(0, cropRegion.cropX2 - cropRegion.cropX1);

                newWidth = Math.max(width, ignoreCropping ? croppedWidth : widthCurrentObj);
                newWidth = Math.min(newWidth, ignoreCropping ? croppedWidth : widthCurrentObj);
                newHeight = ~~(newWidth / proportionsCoefficient);
                cropHeight = ~~(newWidth / restrictedProportionsK);
                cropWidth = newWidth;
            } else {
                const croppedHeight = Math.max(0, cropRegion.cropY2 - cropRegion.cropY1);

                newHeight = Math.max(height, ignoreCropping ? croppedHeight : heightCurrentObj);
                newHeight = Math.min(newHeight, ignoreCropping ? croppedHeight : heightCurrentObj);
                newWidth = ~~(newHeight * proportionsCoefficient);
                cropWidth = ~~(newHeight * restrictedProportionsK);
                cropHeight = newHeight;
            }

            this.setWidthStrict(cropRegion.cropX1, newWidth, isOriginal);
            this.setHeightStrict(cropRegion.cropY1, newHeight, isOriginal);

            if (!ignoreCropping) {
                cropRegion.cropX2 = cropWidth;
                cropRegion.cropY2 = cropHeight;
            }
        }

        const currentWidth = isOriginal ? currentSize.width : currentPrevSize.width;
        const currentHeight = isOriginal ? currentSize.height : currentPrevSize.height;

        setHeightProportionally(currentHeight, isOriginal);
        setWidthProportionally(currentWidth, isOriginal);
    },

    clearData() {
        maxWidth = null;
        maxHeight = null;
        minWidth = null;
        minHeight = null;
        saveProportions = true;
        proportionsCoefficient = 1;
        original.width = null;
        original.height = null;
        preview.width = null;
        preview.height = null;
        currentPrevSize.width = null;
        currentPrevSize.height = null;
        currentFinalPrevImg.width = null;
        currentFinalPrevImg.height = null;
        finalImageStylesPosition.backgroundPositionY = null;
        finalImageStylesPosition.backgroundPositionX = null;
        resetToOriginal = false;
    },
};
