/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
const originImageHeightBlock = require('imcms-origin-image-height-block');
const originImageWidthBlock = require('imcms-origin-image-width-block');
const editableImage = require('imcms-editable-image');

let saveProportions = true; // by default
const original = {};
const currentSize = {};
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

function setWidth(newWidth) {
    const $image = editableImage.getImage();
    const oldWidth = $image.width();
    const k = newWidth / oldWidth;

    const newImageLeft = k * editableImage.getBackgroundPositionX();
    const newImageBackgroundWidth = k * editableImage.getBackgroundWidth();

    $image.width(newWidth);
    editableImage.setBackgroundWidth(newImageBackgroundWidth);
    editableImage.setBackgroundPositionX(newImageLeft);

    $widthControl.val(newWidth);
}

function setHeight(newHeight) {
    const $image = editableImage.getImage();
    const oldHeight = $image.height();
    const k = newHeight / oldHeight;

    const newImageTop = k * editableImage.getBackgroundPositionY();
    const newImageBackgroundHeight = k * editableImage.getBackgroundHeight();

    $image.height(newHeight);
    editableImage.setBackgroundHeight(newImageBackgroundHeight);
    editableImage.setBackgroundPositionY(newImageTop);

    $heightControl.val(newHeight);
}

function setHeightProportionally(newHeight) {
    newHeight = trimToMaxMinHeight(newHeight);
    setHeight(newHeight);
    updateWidthProportionally(newHeight);
}

function setWidthProportionally(newWidth) {
    newWidth = trimToMaxMinWidth(newWidth);
    setWidth(newWidth);
    updateHeightProportionally(newWidth);
}

function updateWidthProportionally(newHeight) {
    const proportionalWidth = ~~(newHeight * proportionsCoefficient);
    const fixedWidth = trimToMaxMinWidth(proportionalWidth);

    (fixedWidth === proportionalWidth)
        ? setWidth(proportionalWidth)
        : setWidthProportionally(proportionalWidth); // MAY (or not) APPEAR RECURSIVE!!!11 be careful
}

function updateHeightProportionally(newWidth) {
    const proportionalHeight = ~~(newWidth / proportionsCoefficient);
    const fixedHeight = trimToMaxMinHeight(proportionalHeight);

    (fixedHeight === proportionalHeight)
        ? setHeight(proportionalHeight)
        : setHeightProportionally(proportionalHeight); // MAY (or not) APPEAR RECURSIVE!!!11 be careful
}

let $heightControl, $widthControl;

module.exports = {
    resetToOriginal(imageData) {
        this.setHeightStrict(0, original.height);
        this.setWidthStrict(0, original.width);
        this.setCurrentSize(original.width, original.height);
        this.updateSizing(imageData);
    },
    setCurrentSize(width, height) {
        currentSize.width = width;
        currentSize.height = height;
        proportionsCoefficient = currentSize.width / currentSize.height;
    },
    getOriginal: () => original,
    setOriginal(originalWidth, originalHeight) {
        originImageHeightBlock.setValue(originalHeight);
        originImageWidthBlock.setValue(originalWidth);

        original.width = originalWidth;
        original.height = originalHeight;

        this.setCurrentSize(originalWidth, originalHeight);
    },
    setWidthControl($control) {
        $widthControl = $control
    },

    setHeightControl($control) {
        $heightControl = $control
    },

    isProportionsLockedByStyle() {
        return minWidth && minHeight || maxWidth && maxHeight;
    },

    isSaveProportionsEnabled: () => saveProportions,

    toggleSaveProportions: () => (saveProportions = !saveProportions),

    enableSaveProportions() {
        saveProportions = true;
    },

    setHeight(newValue) {
        setHeight(trimToMaxMinHeight(newValue));
    },

    setWidth(newValue) {
        setWidth(trimToMaxMinWidth(newValue));
    },

    /**
     * Setting without any proportions or min/max checking
     * @param padding for cropped images
     * @param newWidth
     */
    setWidthStrict(padding, newWidth) {
        editableImage.setBackgroundWidth(original.width);
        editableImage.getImage().width(newWidth);

        if (padding >= 0) editableImage.setBackgroundPositionX(-padding);

        $widthControl.val(newWidth);
    },

    /**
     * Setting without any proportions or min/max checking
     * @param padding for cropped images
     * @param newHeight
     */
    setHeightStrict(padding, newHeight) {
        editableImage.setBackgroundHeight(original.height);
        editableImage.getImage().height(newHeight);

        if (padding >= 0) editableImage.setBackgroundPositionY(-padding);

        $heightControl.val(newHeight);
    },

    setHeightProportionally: setHeightProportionally,

    setWidthProportionally: setWidthProportionally,

    getWidth: () => editableImage.getImage().width(),

    getHeight: () => editableImage.getImage().height(),

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
     */
    updateSizing(imageData) {
        const originalProportionsK = original.width / original.height;

        let width, height;

        if (minWidth && minHeight && (originalProportionsK !== (minWidth / minHeight))) {
            width = minWidth;
            height = minHeight;

        } else if (maxWidth && maxHeight && (originalProportionsK !== (maxWidth / maxHeight))) {
            width = maxWidth;
            height = maxHeight;
        }

        if (width && height) {
            const dX = width / original.width;
            const dY = height / original.height;

            let newWidth, newHeight;

            if (dX > dY) {
                newWidth = Math.max(width, original.width);
                newWidth = Math.min(newWidth, original.width);
                newHeight = ~~(newWidth / proportionsCoefficient);
            } else {
                newHeight = Math.max(height, original.height);
                newHeight = Math.min(newHeight, original.height);
                newWidth = ~~(newHeight * proportionsCoefficient);
            }

            this.setWidthStrict(0, newWidth);
            this.setHeightStrict(0, newHeight);

            imageData.cropRegion = {
                cropX1: 0,
                cropX2: newWidth,
                cropY1: 0,
                cropY2: newHeight,
            };
        }

        setWidthProportionally(currentSize.width);
        setHeightProportionally(currentSize.height);
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
    },
};
