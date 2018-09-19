/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
const originImageHeightBlock = require('imcms-origin-image-height-block');
const originImageWidthBlock = require('imcms-origin-image-width-block');
const editableImage = require('imcms-editable-image');
const editArea = require('imcms-editable-area');

let saveProportions = true; // by default
const original = {};

function setWidth(newWidth) {
    const $image = editableImage.getImage();
    const $imageWrapper = editArea.getEditableImageWrapper();
    const oldWidth = $imageWrapper.width();
    const k = newWidth / oldWidth;

    const newImageLeft = k * $image.position().left;
    const newImageWidth = k * $image.width();

    if (newImageLeft) $image.css('left', newImageLeft);
    if (newImageWidth) $image.width(newImageWidth);

    $imageWrapper.width(newWidth);
    $widthControl.val(newWidth);
}

function setHeight(newHeight) {
    const $image = editableImage.getImage();
    const $imageWrapper = editArea.getEditableImageWrapper();
    const oldHeight = $imageWrapper.height();
    const k = newHeight / oldHeight;

    const newImageTop = k * $image.position().top;
    const newImageHeight = k * $image.height();

    if (newImageTop) $image.css('top', newImageTop);
    if (newImageHeight) $image.height(newImageHeight);

    $imageWrapper.height(newHeight);
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
    getWidth: () => editArea.getEditableImageWrapper().width(),

    getHeight: () => editArea.getEditableImageWrapper().height(),
};
