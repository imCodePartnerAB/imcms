/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.03.18
 */

const Limit = require('imcms-numeric-limiter');
const cropArea = require('imcms-cropping-area');
const Angle = require('imcms-cropping-angle');

module.exports = {
    getValidTopAngleY(top) {
        return new Limit().setMin(-this.getBorderSize())
            .setMax(
                cropArea.getCroppingArea().getTop()
                + cropArea.getCroppingArea().getCurrentHeight()
                - this.getDoubleHeight()
                - this.getBorderSize()
            )
            .forValue(top);
    },
    getValidBottomAngleY(top) {
        return new Limit().setMin(cropArea.getCroppingArea().getTop() + this.getHeight())
            .setMax(cropArea.getImage().getCurrentHeight() - this.getHeight())
            .forValue(top);
    },
    getValidLeftAngleX(left) {
        return new Limit().setMin(-this.getBorderSize())
            .setMax(
                cropArea.getCroppingArea().getLeft()
                + cropArea.getCroppingArea().getCurrentWidth()
                - this.getBorderSize()
                - this.getDoubleWidth()
            )
            .forValue(left);
    },
    getValidRightAngleX(left) {
        return new Limit().setMin(cropArea.getCroppingArea().getLeft() + this.getWidth())
            .setMax(cropArea.getImage().getCurrentWidth() - this.getWidth())
            .forValue(left);
    },
    topLeft: new Angle("top-left", this.getValidLeftAngleX, this.getValidTopAngleY),
    topRight: new Angle("top-right", this.getValidRightAngleX, this.getValidTopAngleY),
    bottomLeft: new Angle("bottom-left", this.getValidLeftAngleX, this.getValidBottomAngleY),
    bottomRight: new Angle("bottom-right", this.getValidRightAngleX, this.getValidBottomAngleY),
    forEach: function (func) {
        [
            this.topLeft.$angle,
            this.topRight.$angle,
            this.bottomLeft.$angle,
            this.bottomRight.$angle

        ].forEach(func);
    },
    hideAll: function () {
        this.forEach($angle => {
            $angle.css("display", "none");
        });
    },
    showAll: function () {
        this.forEach($angle => {
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
};
