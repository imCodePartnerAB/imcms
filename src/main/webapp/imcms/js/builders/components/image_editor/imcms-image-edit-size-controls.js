/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.03.18
 */
const BEM = require('imcms-bem-builder');
const components = require('imcms-components-builder');
const texts = require('imcms-i18n-texts').editors.image;
const $ = require('jquery');
const imageResize = require('imcms-image-resize');

let $heightControl, $widthControl, $hPreviewControl, $wPreviewControl;

function getNewVal($input) {
    const newVal = +$input.val();

    if (isNaN(newVal) || newVal < 0) {
        $input.val($input.val().replace(/[^0-9]/g, ''));
        return;
    }

    return newVal
}

function onValidHeightChange() {
    const newVal = getNewVal($(this));
    newVal && imageResize.setHeightProportionally(newVal, false);
    $wantedHeightControl.getInput().val(newVal);
}

function onValidWidthChange() {
    const newVal = getNewVal($(this));
    newVal && imageResize.setWidthProportionally(newVal, false);
    $wantedWidthControl.getInput().val(newVal);
}

function buildHeightControl() {
    $heightControl = components.texts.textNumber("<div>", {
        name: "height",
        placeholder: texts.height,
        error: "Error",
        onValidChange: onValidHeightChange
    });

    imageResize.setHeightControl($heightControl.getInput().attr('disabled', 'disabled'));

    return $heightControl
}

function getHeightControl() {
    return $heightControl ? $heightControl : buildHeightControl()
}

function buildWidthControl() {
    $widthControl = components.texts.textNumber("<div>", {
        name: "width",
        placeholder: texts.width,
        error: "Error",
        onValidChange: onValidWidthChange
    });

    imageResize.setWidthControl($widthControl.getInput().attr('disabled', 'disabled'));

    return $widthControl
}

function getWidthControl() {
    return $widthControl ? $widthControl : buildWidthControl()
}


function buildPreviewHeightControl() {
    $hPreviewControl = components.texts.textNumber("<div>", {
        name: "prev-height",
        placeholder: texts.height,
        error: "Error",
        onValidChange: onValidHeightChange
    });

    imageResize.setPreviewHeightControl($hPreviewControl.getInput().attr('disabled', 'disabled'));

    return $hPreviewControl
}

function getPreviewHeightControl() {
    return $hPreviewControl ? $hPreviewControl : buildPreviewHeightControl()
}

function buildPreviewWidthControl() {
    $wPreviewControl = components.texts.textNumber("<div>", {
        name: "prev-width",
        placeholder: texts.width,
        error: "Error",
        onValidChange: onValidWidthChange
    });

    imageResize.setPreviewWidthControl($wPreviewControl.getInput().attr('disabled', 'disabled'));

    return $wPreviewControl
}

function getPreviewWidthControl() {
    return $wPreviewControl ? $wPreviewControl : buildPreviewWidthControl()
}

let $title, $titlePrev;

function getTitle() {
    return $title || ($title = components.texts.titleText("<div>", texts.originSize))
}

function getPrevTitle() {
    return $titlePrev || ($titlePrev = components.texts.titleText("<div>", texts.wantedSize))
}

function buildOriginalSizeControls() {

    return new BEM({
        block: "imcms-original-size",
        elements: [
            {"title": getTitle()},
            {"number": getWidthControl()},
            {'block-x': $("<div>", {text: 'X'})},
            {"number": getHeightControl()}
        ]
    }).buildBlockStructure("<div>", {style: 'display: none;'});
}

let $wantedWidthControl;
let $wantedHeightControl;

function getWantedWidthControl() {
    return $wantedWidthControl ? $wantedWidthControl : buildWantedWidthControl();
}

function getWantedHeightControl() {
    return $wantedHeightControl ? $wantedHeightControl : buildWantedHeightControl();
}

function buildWantedSizeBlock() {

    return new BEM({
        block: "imcms-edit-size",
        elements: [
            {"title": getPrevTitle()},
            {"number": buildWantedWidthControl()},
            {'block-x': $("<div>", {text: 'X'})},
            {"number": buildWantedHeightControl()}
        ]
    }).buildBlockStructure("<div>");
}

function buildWantedWidthControl() {
    $wantedWidthControl = components.texts.textNumber("<div>", {
        name: "wanted-width",
        placeholder: texts.width,
        error: "Error",
        onValidChange: onValidWidthChange
    });

    imageResize.setWantedWidthControl($wantedWidthControl.getInput());

    return $wantedWidthControl;
}

function buildWantedHeightControl() {
    $wantedHeightControl = components.texts.textNumber("<div>", {
        name: "wanted-height",
        placeholder: texts.width,
        error: "Error",
        onValidChange: onValidHeightChange
    });

    imageResize.setWantedHeightControl($wantedHeightControl.getInput());

    return $wantedHeightControl;
}

let $titleDisplay;

function getDisplayTitle() {
    return $titleDisplay || ($titleDisplay = components.texts.titleText('<div>', texts.displaySize));
}

function buildDisplaySizeBlock() {
    return new BEM({
        block: 'imcms-display-size',
        elements: [
            {'title': getDisplayTitle()},
            {'number': buildPreviewWidthControl()},
            {'block-x': $("<div>", {text: 'X'})},
            {'number': buildPreviewHeightControl()}
        ]
    }).buildBlockStructure('<div>');
}

function buildImageSizeControlBlock() {
    return new BEM({
        block: 'imcms-image-size-control',
        elements: {
            'wanted-size': buildWantedSizeBlock(),
            'display-size': buildDisplaySizeBlock()
        }
    }).buildBlockStructure('<div>', {style: 'display: none;'});

}

let $originalSizeControls;
let $prevSizeControls;
let $imageSizeBlock;

module.exports = {
    getWidthControl: getWidthControl,

    getHeightControl: getHeightControl,

    getPreviewHeightControl: getPreviewHeightControl,

    getPreviewWidthControl: getPreviewWidthControl,

    swapControls: (isInverted) => {
        // $widthControl.find('label').text((isInverted) ? heightLabelText : widthLabelText);
        // $heightControl.find('label').text((isInverted) ? widthLabelText : heightLabelText);
    },
    setHeight: (newHeight) => getHeightControl().getInput().val(newHeight),

    setWidth: (newWidth) => getWidthControl().getInput().val(newWidth),

    setPrevHeight: (newHeight) => getPreviewHeightControl().getInput().val(newHeight),

    setPrevWidth: (newWidth) => getPreviewWidthControl().getInput().val(newWidth),

    getOriginSizeControls: () => $originalSizeControls || ($originalSizeControls = buildOriginalSizeControls()),

    getEditSizeControls: () => $prevSizeControls || ($prevSizeControls = buildWantedSizeBlock()),

    getImageSizeControlBlock: () => $imageSizeBlock || ($imageSizeBlock = buildImageSizeControlBlock()),

    getWantedHeightControl,
    getWantedWidthControl,
};
