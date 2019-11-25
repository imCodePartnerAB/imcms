/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 18.09.18
 */
const $ = require('jquery');
const BEM = require('imcms-bem-builder');

const angleClass = "imcms-angle";

const angleAttributes = {
    "class": angleClass,
    style: "display: none;" // at first are not shown
};

module.exports = class Angle {
    constructor(modifier, validatorX, validatorY) {
        this.modifier = modifier;
        this.validatorX = validatorX;
        this.validatorY = validatorY;
        this.$angle = null;
    }

    buildAngle() {
        return this.$angle = $("<div>", angleAttributes)
            .addClass(BEM.buildClass("", angleClass, this.modifier))
            .append($('<div>', {'class': 'imcms-angle-inner'}));
    }

    setTopRight(top, right) {
        this.$angle.css({
            top: top,
            right: right
        });
    }

    setTop(newTop) {
        this.$angle.css({top: newTop});
    }

    getTop() {
        return parseInt(this.$angle.css("top"));
    }

    setLeft(newLeft) {
        this.$angle.css({left: newLeft});
    }

    getLeft() {
        return parseInt(this.$angle.css("left"));
    }

    setTopLeft(top, left) {
        this.$angle.css({
            top: top,
            left: left
        });
    }

    setBottomLeft(bottom, left) {
        this.$angle.css({
            bottom: bottom,
            left: left
        });
    }

    setBottomRight(bottom, right) {
        this.$angle.css({
            bottom: bottom,
            right: right
        });
    }

    setBottom(bottom) {
        this.$angle.css({bottom: bottom});
    }

    setRight(right) {
        this.$angle.css({right: right});
    }

    _trimToValidLeftValue(newLeft) {
        return this.validatorX.call(this.$angle, newLeft);
    }

    _trimToValidTopValue(newTop) {
        return this.validatorY.call(this.$angle, newTop);
    }

    moveAngle(deltaX, deltaY) {
        const newLeft = this._trimToValidLeftValue(this.getLeft() - deltaX);
        const newTop = this._trimToValidTopValue(this.getTop() - deltaY);

        this.setTopLeft(newTop, newLeft);
    }

    setNewX(newX) {
        this.setLeft(this._trimToValidLeftValue(newX));
    }

    setNewY(newY) {
        this.setTop(this._trimToValidTopValue(newY));
    }

    animate(attributes, duration) {
        this.$angle.animate(attributes, duration);
    }

    css() {
        this.$angle.css.apply(this.$angle, arguments);
    }
};