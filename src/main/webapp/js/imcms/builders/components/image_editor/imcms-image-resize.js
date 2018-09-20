/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
const originImageHeightBlock = require('imcms-origin-image-height-block');
const originImageWidthBlock = require('imcms-origin-image-width-block');
const editableImage = require('imcms-editable-image');

let saveProportions = true; // by default
const original = {};

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

function updateWidthProportionally(newHeight) {
    const proportionalWidth = ~~((newHeight * original.width) / original.height);
    setWidth(proportionalWidth);
}

function updateHeightProportionally(newWidth) {
    const proportionalHeight = ~~((newWidth * original.height) / original.width);
    setHeight(proportionalHeight);
}

let $heightControl, $widthControl;

module.exports = {
    resetToOriginal: () => {
        setHeight(original.height);
        setWidth(original.width);
    },
    getOriginal: () => original,
    setOriginal: (originalWidth, originalHeight) => {
        originImageHeightBlock.setValue(originalHeight);
        originImageWidthBlock.setValue(originalWidth);

        original.width = originalWidth;
        original.height = originalHeight;
    },
    setWidthControl: ($control) => $widthControl = $control,

    setHeightControl: ($control) => $heightControl = $control,

    isSaveProportionsEnabled: () => saveProportions,

    toggleSaveProportions: () => (saveProportions = !saveProportions),

    setHeight: setHeight,

    setWidth: setWidth,

    setWidthStrict(padding, newWidth) {
        const originWidth = originImageWidthBlock.getValue();

        editableImage.setBackgroundWidth(originWidth);
        editableImage.getImage().width(newWidth);

        if (padding >= 0) editableImage.setBackgroundPositionX(-padding);

        $widthControl.val(newWidth);
    },

    setHeightStrict(padding, newHeight) {
        const originHeight = originImageHeightBlock.getValue();

        editableImage.setBackgroundHeight(originHeight);
        editableImage.getImage().height(newHeight);

        if (padding >= 0) editableImage.setBackgroundPositionY(-padding);

        $heightControl.val(newHeight);
    },

    setHeightProportionally: (newHeight) => {
        // todo: add checking for (max-)width from page
        setHeight(newHeight);
        saveProportions && updateWidthProportionally(newHeight);
    },
    setWidthProportionally: (newWidth) => {
        // todo: add checking for (max-)height from page
        setWidth(newWidth);
        saveProportions && updateHeightProportionally(newWidth);
    },
    getWidth: () => editableImage.getImage().width(),

    getHeight: () => editableImage.getImage().height(),
};
