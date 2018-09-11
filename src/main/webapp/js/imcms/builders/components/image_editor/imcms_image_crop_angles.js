/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.03.18
 */
define(
    "imcms-image-crop-angles",
    ["jquery", "imcms-bem-builder", "imcms-numeric-limiter", "imcms-image-cropping-elements"],
    function ($, BEM, Limit, cropElements) {

        var angleClass = "imcms-angle";

        var angleAttributes = {
            "class": angleClass,
            style: "display: none;" // at first are not shown
        };


        function getValidTopAngleY(top) {
            return new Limit().setMin(0)
                .setMax(cropElements.$cropArea.getTop() + cropElements.$cropArea.getCurrentHeight() - module.getDoubleHeight() - module.getBorderSize())
                .forValue(top);
        }

        function getValidBottomAngleY(top) {
            return new Limit().setMin(cropElements.$cropArea.getTop() + module.getHeight())
                .setMax(cropElements.$image.getCurrentHeight() - module.getHeight() + module.getBorderSize())
                .forValue(top);
        }

        function getValidLeftAngleX(left) {
            return new Limit().setMin(0)
                .setMax(cropElements.$cropArea.getLeft() + cropElements.$cropArea.getCurrentWidth() - module.getBorderSize() - module.getDoubleWidth())
                .forValue(left);
        }

        function getValidRightAngleX(left) {
            return new Limit().setMin(cropElements.$cropArea.getLeft() + module.getWidth())
                .setMax(cropElements.$image.getCurrentWidth() - module.getWidth() + module.getBorderSize())
                .forValue(left);
        }


        var Angle = function (modifier, validatorX, validatorY) {
            this.modifier = modifier;
            this.validatorX = validatorX;
            this.validatorY = validatorY;
        };

        Angle.prototype = {
            $angle: null,
            buildAngle: function () {
                return this.$angle = $("<div>", angleAttributes)
                    .addClass(BEM.buildClass("", angleClass, this.modifier))
                    .append($('<div>', {'class': 'imcms-angle-inner'}));
            },
            setTop: function (newTop) {
                this.$angle.css({top: newTop});
            },
            getTop: function () {
                return parseInt(this.$angle.css("top"));
            },
            setLeft: function (newLeft) {
                this.$angle.css({left: newLeft});
            },
            getLeft: function () {
                return parseInt(this.$angle.css("left"));
            },
            setTopLeft: function (top, left) {
                this.$angle.css({
                    top: top,
                    left: left
                });
            },
            _trimToValidLeftValue: function (newLeft) {
                return this.validatorX.call(this.$angle, newLeft);
            },
            _trimToValidTopValue: function (newTop) {
                return this.validatorY.call(this.$angle, newTop);
            },
            moveAngle: function (deltaX, deltaY) {
                var newLeft = this._trimToValidLeftValue(this.getLeft() - deltaX);
                var newTop = this._trimToValidTopValue(this.getTop() - deltaY);

                this.setTopLeft(newTop, newLeft);
            },
            setNewX: function (newX) {
                this.setLeft(this._trimToValidLeftValue(newX));
            },
            setNewY: function (newY) {
                this.setTop(this._trimToValidTopValue(newY));
            },
            animate: function (attributes, duration) {
                this.$angle.animate(attributes, duration);
            },
            css: function () {
                this.$angle.css.apply(this.$angle, arguments);
            }
        };

        var module;

        return module = {
            topLeft: new Angle("top-left", getValidLeftAngleX, getValidTopAngleY),
            topRight: new Angle("top-right", getValidRightAngleX, getValidTopAngleY),
            bottomLeft: new Angle("bottom-left", getValidLeftAngleX, getValidBottomAngleY),
            bottomRight: new Angle("bottom-right", getValidRightAngleX, getValidBottomAngleY),
            forEach: function (func) {
                [
                    this.topLeft.$angle,
                    this.topRight.$angle,
                    this.bottomLeft.$angle,
                    this.bottomRight.$angle

                ].forEach(func);
            },
            hideAll: function () {
                this.forEach(function ($angle) {
                    $angle.css("display", "none");
                });
            },
            showAll: function () {
                this.forEach(function ($angle) {
                    $angle.css("display", "block");
                })
            },
            _borderSize: 0,
            _doubleBorderSize: 0,
            _width: 0,
            _height: 0,
            _doubleWidth: 0,
            _doubleHeight: 0,
            getWidth: function () {
                return this._width || (this._width = this.topLeft.$angle.width());
            },
            getHeight: function () {
                return this._height || (this._height = this.topLeft.$angle.height());
            },
            getDoubleWidth: function () {
                return this._doubleWidth || (this._doubleWidth = this.getWidth() * 2);
            },
            getDoubleHeight: function () {
                return this._doubleHeight || (this._doubleHeight = this.getHeight() * 2);
            },
            getBorderSize: function () {
                return this._borderSize
                    || (this._borderSize = parseInt(this.topLeft.$angle.css("border-top-width")) || 0)
            },
            getDoubleBorderSize: function () {
                return this._doubleBorderSize || (this._doubleBorderSize = this.getBorderSize() * 2);
            }
        }
    }
);
