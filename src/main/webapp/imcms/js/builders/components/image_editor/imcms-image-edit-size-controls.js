/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.03.18
 */
const BEM = require('imcms-bem-builder');
const components = require('imcms-components-builder');
const texts = require('imcms-i18n-texts').editors.image;
const $ = require('jquery');
const imageResize = require('imcms-image-resize');
const percentageBuild = require('imcms-image-percentage-proportion-build');

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
    percentageBuild.buildPercentageFromEditControl($wPreviewControl, $hPreviewControl, $('.percentage-image-info'));
}

function onValidWidthChange() {
    const newVal = getNewVal($(this));
    newVal && imageResize.setWidthProportionally(newVal, false);
    percentageBuild.buildPercentageFromEditControl($wPreviewControl, $hPreviewControl, $('.percentage-image-info'));
}

let widthLabelText = "W";
let heightLabelText = "H";

function buildHeightControl() {
    $heightControl = components.texts.textNumber("<div>", {
        name: "height",
        placeholder: texts.height,
        text: heightLabelText,
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
        text: widthLabelText,
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
        text: heightLabelText,
        error: "Error",
        onValidChange: onValidHeightChange
    });

    imageResize.setPreviewHeightControl($hPreviewControl.getInput());

    return $hPreviewControl
}

function getPreviewHeightControl() {
    return $hPreviewControl ? $hPreviewControl : buildPreviewHeightControl()
}

function buildPreviewWidthControl() {
    $wPreviewControl = components.texts.textNumber("<div>", {
        name: "prev-width",
        placeholder: texts.width,
        text: widthLabelText,
        error: "Error",
        onValidChange: onValidWidthChange
    });

    imageResize.setPreviewWidthControl($wPreviewControl.getInput());

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
    return $titlePrev || ($titlePrev = components.texts.titleText("<div>", texts.displaySize))
}

function buildOriginalSizeControls() {

    return new BEM({
        block: "imcms-original-size",
        elements: [
            {"title": getTitle()},
            {"number": getWidthControl()},
            {"number": getHeightControl()}
        ]
    }).buildBlockStructure("<div>", {style: 'display: none;'});
}

function buildPreviewSizeControls() {

    return new BEM({
        block: "imcms-edit-size",
        elements: [
            {"title": getPrevTitle()},
            {"number": getPreviewWidthControl()},
            {"number": getPreviewHeightControl()}
        ]
    }).buildBlockStructure("<div>", {style: 'display: none;'});
}

let $originalSizeControls;
let $prevSizeControls;

module.exports = {
    getWidthControl: getWidthControl,

    getHeightControl: getHeightControl,

    getPreviewHeightControl: getPreviewHeightControl,

    getPreviewWidthControl: getPreviewWidthControl,

    swapControls: (isInverted) => {
        $widthControl.find('label').text((isInverted) ? heightLabelText : widthLabelText);
        $heightControl.find('label').text((isInverted) ? widthLabelText : heightLabelText);
    },
    setHeight: (newHeight) => getHeightControl().getInput().val(newHeight),

    setWidth: (newWidth) => getWidthControl().getInput().val(newWidth),

    setPrevHeight: (newHeight) => getPreviewHeightControl().getInput().val(newHeight),

    setPrevWidth: (newWidth) => getPreviewWidthControl().getInput().val(newWidth),

    getOriginSizeControls: () => $originalSizeControls || ($originalSizeControls = buildOriginalSizeControls()),

    getEditSizeControls: () => $prevSizeControls || ($prevSizeControls = buildPreviewSizeControls()),
};
