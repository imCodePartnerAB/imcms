/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.03.18
 */
define("imcms-numeric-limiter", [], function () {

    var Limit = function () {
    };

    Limit.prototype = {
        setMin: function (min) {
            this.min = min;
            return this;
        },
        setMax: function (max) {
            this.max = max;
            return this;
        },
        forValue: function (value) {
            return Math.min(Math.max(value, this.min), this.max);
        }
    };

    return Limit;
});
