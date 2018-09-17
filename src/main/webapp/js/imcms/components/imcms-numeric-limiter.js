/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.03.18
 */

module.exports = class Limit {
    setMin(min) {
        this.min = min;
        return this;
    }

    setMax(max) {
        this.max = max;
        return this;
    }

    forValue(value) {
        return Math.min(Math.max(value, this.min), this.max);
    }
};
