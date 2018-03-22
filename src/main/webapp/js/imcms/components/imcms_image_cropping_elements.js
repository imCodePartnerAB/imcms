/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.03.18
 */
Imcms.define(
    "imcms-image-cropping-elements",
    ["jquery", "imcms-bem-builder", "imcms-events"],
    function ($, BEM, events) {

        var cropAreaClass = "imcms-crop-area";
        var isImgRotate = false;

        function getCurrentWidth($element) {
            return (isImgRotate) ? $element.height() : $element.width();
        }

        function getCurrentHeight($element) {
            return (isImgRotate) ? $element.width() : $element.height();
        }

        events.on("image rotated", function () {
            isImgRotate = !isImgRotate;
        });

        function setFunctionality($element) {
            $element.getCurrentWidth = function () {
                return getCurrentWidth($element);
            };

            $element.getCurrentHeight = function () {
                return getCurrentHeight($element);
            };

            $element.getTop = function () {
                return $element.position().top;
            };

            $element.getLeft = function () {
                return $element.position().left;
            };

            return $element;
        }

        function setPositionListeners($element, eventName) {
            var oldCss = $element.css;
            $element.css = function () {
                var retVal = oldCss.apply($element, arguments);
                events.trigger(eventName);
                return retVal;
            };

            var oldHeight = $element.height;
            $element.height = function () {
                var retVal = oldHeight.apply($element, arguments);

                if (arguments.length >= 1) {
                    events.trigger(eventName);
                }

                return retVal;
            };

            var oldWidth = $element.width;
            $element.width = function () {
                var retVal = oldWidth.apply($element, arguments);

                if (arguments.length >= 1) {
                    events.trigger(eventName);
                }

                return retVal;
            };

            var oldAnimate = $element.animate;
            $element.animate = function (params, duration, callback) {
                return oldAnimate.call($element, params, duration, function () {
                    callback && callback.call();
                    events.trigger(eventName);
                });
            };

            return $element;
        }

        return {
            $cropImg: null,
            $cropArea: null,
            $image: null,
            buildCropImage: function () {
                this.$cropImg = $("<img>", {"class": BEM.buildClass(cropAreaClass, "crop-img")});
                return setFunctionality(this.$cropImg);
            },
            buildCropArea: function () {
                this.$cropArea = $("<div>", {"class": cropAreaClass}).append(this.buildCropImage());
                return setPositionListeners(setFunctionality(this.$cropArea), "crop area position changed");
            },
            buildImage: function () {
                this.$image = $("<img>", {"class": "imcms-editable-img"});
                return setFunctionality(this.$image);
            }
        };
    }
);
