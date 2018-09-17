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
    editableImage.getImage().width(newWidth);
    $widthControl.val(newWidth);
}

function setHeight(newHeight) {
    editableImage.getImage().height(newHeight);
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
    setOriginal: (originalWidth, originalHeight) => {
        originImageHeightBlock.setOriginalHeight(originalHeight);
        originImageWidthBlock.setOriginalWidth(originalWidth);

        original.width = originalWidth;
        original.height = originalHeight;
    },
    setWidthControl: ($control) => $widthControl = $control,

    setHeightControl: ($control) => $heightControl = $control,

    toggleSaveProportions: () => (saveProportions = !saveProportions),

    setHeight: setHeight,

    setWidth: setWidth,

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
