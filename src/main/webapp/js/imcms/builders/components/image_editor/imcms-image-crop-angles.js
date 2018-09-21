/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.03.18
 */

const Limit = require('imcms-numeric-limiter');
const cropArea = require('imcms-cropping-area');
const Angle = require('imcms-cropping-angle');

const editableAreaBorderWidth = cropArea.getEditableAreaBorderWidth();

function getValidTopAngleY(top) {
    return new Limit().setMin(editableAreaBorderWidth - _this.getBorderSize())
        .setMax(
            cropArea.getCroppingArea().getTop()
            + cropArea.getCroppingArea().getCurrentHeight()
            - _this.getDoubleHeight()
            - _this.getBorderSize()
        )
        .forValue(top);
}

function getValidBottomAngleY(top) {
    return new Limit().setMin(cropArea.getCroppingArea().getTop() + _this.getHeight())
        .setMax(cropArea.getImage().getCurrentHeight() - _this.getHeight() + editableAreaBorderWidth)
        .forValue(top);
}

function getValidLeftAngleX(left) {
    return new Limit().setMin(editableAreaBorderWidth - _this.getBorderSize())
        .setMax(
            cropArea.getCroppingArea().getLeft()
            + cropArea.getCroppingArea().getCurrentWidth()
            - _this.getBorderSize()
            - _this.getDoubleWidth()
        )
        .forValue(left);
}

function getValidRightAngleX(left) {
    return new Limit().setMin(cropArea.getCroppingArea().getLeft() + _this.getWidth())
        .setMax(cropArea.getImage().getCurrentWidth() - _this.getWidth() + editableAreaBorderWidth)
        .forValue(left);
}

let _this;

module.exports = _this = {
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
};
