/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
import previewImage from 'imcms-preview-image-area';
import sizeControls from 'imcms-image-edit-size-controls';
import {removeCssFunctionsFromString} from 'css-utils';

const angleNorth = {
    name: "NORTH",
    proportionsInverted: false,
    degrees: 0
};

const angleEast = angleNorth.next = {
    name: "EAST",
    proportionsInverted: true,
    degrees: 90,
    prev: angleNorth
};

const angleSouth = angleEast.next = {
    name: "SOUTH",
    proportionsInverted: false,
    degrees: 180,
    prev: angleEast
};

const angleWest = angleSouth.next = angleNorth.prev = {
    name: "WEST",
    proportionsInverted: true,
    degrees: 270,
    prev: angleSouth,
    next: angleNorth
};

const anglesByDirection = {
    "NORTH": angleNorth,
    "EAST": angleEast,
    "SOUTH": angleSouth,
    "WEST": angleWest
};

let currentAngle = angleNorth;

const anglesByDegrees = {
    0: angleNorth,
    90: angleEast,
    180: angleSouth,
    270: angleWest,
};

function getRotateCssTransformString(angle) {
    const degrees = angle.degrees;
    let transform = "rotate(" + degrees + "deg)";

    switch (degrees) {
        case 90:
            transform += " translate(0%, -100%)";
            break;
        case 180:
            transform += " translate(-100%, -100%)";
            break;
        case 270:
            transform += " translate(-100%, 0%)";
            break;
    }

    return transform;
}

function getUpdatedTransformString(angle, $image) {
    const rotateCss = getRotateCssTransformString(currentAngle);
    const transformString = $image[0].style.transform;
    const cleanTransformString = removeCssFunctionsFromString(transformString, ['rotate', 'translate']);

    return `${cleanTransformString} ${rotateCss}`;
}

function rotate(newAngle) {
    const sameAngle = (newAngle === currentAngle);
    if (sameAngle) return;

    currentAngle = newAngle || angleNorth;

    const $image = previewImage.getPreviewImage();

    $image.css('transform', getUpdatedTransformString(currentAngle, $image));

    sizeControls.swapControls(currentAngle.proportionsInverted);
}

export const rotateLeft = () => rotate(currentAngle.prev);
export const rotateRight = () => rotate(currentAngle.next);
export const rotateImageByDegrees = (degrees) => rotate(anglesByDegrees[degrees]);
export const rotateImage = direction => rotate(anglesByDirection[direction]);
export const isProportionsInverted = () => currentAngle.proportionsInverted;
export const getCurrentAngle = () => currentAngle;
export const destroy = () => {
    currentAngle = angleNorth;
}
