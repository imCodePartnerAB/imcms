/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.09.18
 */
const events = require('imcms-events');
const cropArea = require('imcms-cropping-area');
const components = require('imcms-components-builder');
const imageCropper = require('imcms-image-cropper');
const $ = require('jquery');

const editableAreaBorderWidth = cropArea.getEditableAreaBorderWidth();

let $cropCoordX;
let $cropCoordY;
let $cropCoordX1;
let $cropCoordY1;

events.on('crop area position changed', function () {
    const $croppingArea = cropArea.getCroppingArea();
    const x = $croppingArea.getLeft() - editableAreaBorderWidth;
    const y = $croppingArea.getTop() - editableAreaBorderWidth;
    const x1 = $croppingArea.width() + x;
    const y1 = $croppingArea.height() + y;

    getCropCoordX().setValue(x);
    getCropCoordY().setValue(y);
    getCropCoordX1().setValue(x1);
    getCropCoordY1().setValue(y1);

    // todo: move this on "apply" in cropping mode
    // imageData.cropRegion = {
    //     cropX1: x,
    //     cropX2: x1,
    //     cropY1: y,
    //     cropY2: y1
    // }
});

function setValidation(onValid) {
    return function () {
        const inputField = $(this),
            stringFieldValue = inputField.val(),
            intFieldValue = +stringFieldValue,
            minFieldValue = 0;

        if (isNaN(intFieldValue)) {
            const val = parseInt(stringFieldValue);
            inputField.val(isNaN(val) ? 0 : val);
            return;
        }

        if (intFieldValue < minFieldValue) {
            inputField.val(minFieldValue);
            return;
        }

        onValid.call(this, intFieldValue);
    }
}

function getCropCoordX() {
    return $cropCoordX || ($cropCoordX = components.texts.textNumber("<div>", {
        name: "cropX0",
        placeholder: "X",
        text: "X",
        error: "Error",
        onValidChange: setValidation(imageCropper.setCropX)
    }));
}

function getCropCoordX1() {
    return $cropCoordX1 || ($cropCoordX1 = components.texts.textNumber("<div>", {
        name: "cropX1",
        placeholder: "X1",
        text: "X1",
        error: "Error",
        onValidChange: setValidation(imageCropper.setCropX1)
    }));
}

function getCropCoordY() {
    return $cropCoordY || ($cropCoordY = components.texts.textNumber("<div>", {
        name: "cropY0",
        placeholder: "Y",
        text: "Y",
        error: "Error",
        onValidChange: setValidation(imageCropper.setCropY)
    }));
}

function getCropCoordY1() {
    return $cropCoordY1 || ($cropCoordY1 = components.texts.textNumber("<div>", {
        name: "cropY1",
        placeholder: "Y1",
        text: "Y1",
        error: "Error",
        onValidChange: setValidation(imageCropper.setCropY1)
    }));
}

module.exports = {
    getCropCoordX: getCropCoordX,
    getCropCoordY: getCropCoordY,
    getCropCoordX1: getCropCoordX1,
    getCropCoordY1: getCropCoordY1,
};