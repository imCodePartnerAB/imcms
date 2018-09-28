/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.03.18
 */
const BEM = require('imcms-bem-builder');
const components = require('imcms-components-builder');
const texts = require('imcms-i18n-texts').editors.image;
const $ = require('jquery');
const imageResize = require('imcms-image-resize');

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
    newVal && imageResize.setHeightProportionally(newVal);
}

function onValidWidthChange() {
    const newVal = getNewVal($(this));
    newVal && imageResize.setWidthProportionally(newVal);
}

let $heightControl, $widthControl;
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

    imageResize.setHeightControl($heightControl.getInput());

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

    imageResize.setWidthControl($widthControl.getInput());

    return $widthControl
}

function getWidthControl() {
    return $widthControl ? $widthControl : buildWidthControl()
}

let $proportionsBtn;

function getProportionsButton() {
    return $proportionsBtn || ($proportionsBtn = components.buttons.proportionsButton({
        "data-state": "active",
        title: texts.proportionsButtonTitle,
        click: function () {
            let saveProportions = imageResize.toggleSaveProportions();
            $(this).attr("data-state", saveProportions ? "active" : "passive");
        }
    }))
}

let $title;

function getTitle() {
    return $title || ($title = components.texts.titleText("<div>", texts.displaySize))
}

function buildEditSizeControls() {

    return new BEM({
        block: "imcms-edit-size",
        elements: [
            {"title": getTitle()},
            {"number": getWidthControl()},
            {"button": getProportionsButton()},
            {"number": getHeightControl()}
        ]
    }).buildBlockStructure("<div>");
}

let $sizeControls;

module.exports = {
    getWidthControl: getWidthControl,

    getHeightControl: getHeightControl,

    swapControls: (isInverted) => {
        $widthControl.find('label').text((isInverted) ? heightLabelText : widthLabelText);
        $heightControl.find('label').text((isInverted) ? widthLabelText : heightLabelText);
    },
    setHeight: (newHeight) => getHeightControl().getInput().val(newHeight),

    setWidth: (newWidth) => getWidthControl().getInput().val(newWidth),

    getProportionsButton: getProportionsButton,

    enableProportionsLock() {
        getProportionsButton().attr("data-state", "active");
        imageResize.enableSaveProportions();
    },

    getEditSizeControls: () => $sizeControls || ($sizeControls = buildEditSizeControls()),
};
