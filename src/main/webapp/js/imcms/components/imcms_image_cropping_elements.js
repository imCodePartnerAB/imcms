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

        events.on("rotate img", function () {
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
                return setFunctionality(this.$cropArea);
            },
            buildImage: function () {
                this.$image = $("<img>", {"class": "imcms-editable-img"});
                return setFunctionality(this.$image);
            }
        };
    }
);
