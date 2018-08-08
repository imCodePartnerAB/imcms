/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.03.18
 */
Imcms.define(
    "imcms-image-rotate",
    ["imcms-image-cropping-elements", "imcms-image-resize", "imcms-events"],
    function (cropElements, imageResize, events) {

        var dataContainers, currentAngle;

        var angleNorth = {
            name: "NORTH",
            proportionsInverted: false,
            degrees: 0
        };

        var angleEast = angleNorth.next = {
            name: "EAST",
            proportionsInverted: true,
            degrees: 90,
            prev: angleNorth
        };

        var angleSouth = angleEast.next = {
            name: "SOUTH",
            proportionsInverted: false,
            degrees: 180,
            prev: angleEast
        };

        var angleWest = angleSouth.next = angleNorth.prev = {
            name: "WEST",
            proportionsInverted: true,
            degrees: 270,
            prev: angleSouth,
            next: angleNorth
        };

        var anglesByDirection = {
            "NORTH": angleNorth,
            "EAST": angleEast,
            "SOUTH": angleSouth,
            "WEST": angleWest
        };

        function getRotateCss(angle) {
            var degrees = angle.degrees;
            var transform = "rotate(" + degrees + "deg)";

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
            var sameAngle = !currentAngle || !newAngle || (newAngle === currentAngle);

            currentAngle = newAngle || angleNorth;

            var isImageProportionsInverted = currentAngle.proportionsInverted;
            var style = getRotateCss(currentAngle);

            cropElements.$image.css(style);
            cropElements.$cropImg.css(style);

            var $cropArea = cropElements.$cropArea,
                imageWidth = cropElements.$image.width(),
                imageHeight = cropElements.$image.height(),
                cropAreaHeight = (sameAngle) ? $cropArea.height() : $cropArea.width(),
                cropAreaWidth = (sameAngle) ? $cropArea.width() : $cropArea.height();

            imageResize.resize({
                    image: {
                        width: imageWidth,
                        height: imageHeight
                    },
                    cropArea: {
                        height: cropAreaHeight,
                        width: cropAreaWidth,
                        top: $cropArea.getTop(),
                        left: $cropArea.getLeft()
                    }
                },
                dataContainers,
                isImageProportionsInverted
            );

            events.trigger(isImageProportionsInverted ? "image proportions inverted" : "regular image proportions");

            var heightControlInput = dataContainers.$heightControlInput.getInput();
            var widthControlInput = dataContainers.$widthControlInput.getInput();

            if (isImageProportionsInverted) {
                heightControlInput.val(imageWidth);
                widthControlInput.val(imageHeight);
            } else {
                heightControlInput.val(imageHeight);
                widthControlInput.val(imageWidth);
            }
        }

        return {
            setDataContainers: function (imageDataContainers) {
                dataContainers = imageDataContainers;
            },
            rotateLeft: function () {
                rotate(currentAngle.prev);
            },
            rotateRight: function () {
                rotate(currentAngle.next);
            },
            rotateImage: function (direction) {
                rotate(anglesByDirection[direction]);
            },
            isProportionsInverted: function () {
                return currentAngle.proportionsInverted;
            },
            getCurrentAngle: function () {
                return currentAngle;
            },
            getCurrentRotateCss: function () {
                return getRotateCss(currentAngle || angleNorth);
            },
            destroy: function () {
                currentAngle = null;
            }
        };
    }
);
