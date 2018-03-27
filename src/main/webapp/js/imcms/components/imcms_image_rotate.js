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

        function rotate(newAngle) {
            var sameAngle = !currentAngle || (newAngle === currentAngle);

            currentAngle = newAngle;

            var style = {};
            var degrees = newAngle.degrees;
            var isImageProportionsInverted = currentAngle.proportionsInverted;

            switch (degrees) {
                case 90:
                    style = {
                        "transform": "rotate(" + degrees + "deg) translateY(-100%)",
                        "transform-origin": "top left"
                    };
                    break;
                case 180:
                    style = {
                        "transform": "rotate(" + degrees + "deg) translate(-100%, -100%)",
                        "transform-origin": "top left"
                    };
                    break;
                case 270:
                    style = {
                        "transform": "rotate(" + degrees + "deg) translateX(-100%)",
                        "transform-origin": "top left"
                    };
                    break;
                default:
                    style = {
                        "transform": "rotate(" + degrees + "deg)",
                        "transform-origin": "top left"
                    };
            }
            cropElements.$image.css(style);
            cropElements.$cropImg.css(style);

            var imageWidth = cropElements.$image.width(),
                imageHeight = cropElements.$image.height(),
                cropAreaHeight = (sameAngle) ? cropElements.$cropArea.height() : cropElements.$cropArea.width(),
                cropAreaWidth = (sameAngle) ? cropElements.$cropArea.width() : cropElements.$cropArea.height();

            // TODO: fix crop area position when it is out of img
            imageResize.resize({
                    image: {
                        width: imageWidth,
                        height: imageHeight
                    },
                    cropArea: {
                        height: cropAreaHeight,
                        width: cropAreaWidth,
                        top: 2,
                        left: 2
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
            }
        };
    }
);
