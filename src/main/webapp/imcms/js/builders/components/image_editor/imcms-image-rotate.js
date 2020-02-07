/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
const previewImage = require('imcms-preview-image-area')
const sizeControls = require('imcms-image-edit-size-controls');

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

function getRotateCss(angle) {
    const degrees = angle.degrees;
    let transform = "rotate(" + degrees + "deg)";

    switch (degrees) {
        case 90:
            transform += " translateY(-100%)";
            break;
        case 180:
            transform += " translate(-100%, -100%)";
            break;
        case 270:
            transform += " translateX(-100%)";
            break;
    }

    return {
        "transform": transform,
        "transform-origin": "top left"
    };
}

function rotate(newAngle) {
    const sameAngle = (newAngle === currentAngle);
    if (sameAngle) return;

    currentAngle = newAngle || angleNorth;

    const style = getRotateCss(currentAngle);
    previewImage.getPreviewImage().css(style);

    sizeControls.swapControls(currentAngle.proportionsInverted);
}

module.exports = {
    rotateLeft: () => {
        rotate(currentAngle.prev);
    },
    rotateRight: () => {
        rotate(currentAngle.next);
    },
    rotateImageByDegrees: (degrees) => rotate(anglesByDegrees[degrees]),
    rotateImage: direction => {
        rotate(anglesByDirection[direction]);
    },
    isProportionsInverted: () => currentAngle.proportionsInverted,
    getCurrentAngle: () => currentAngle,
    getCurrentRotateCss: () => getRotateCss(currentAngle || angleNorth),
    destroy: () => {
        currentAngle = angleNorth;
    }
};
