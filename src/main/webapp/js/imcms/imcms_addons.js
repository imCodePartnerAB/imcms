(function () {

    /**
     * Extension for Array to made convenient remove
     * @param {*} value to remove from array
     */
    Array.prototype.remove = function (value) {
        while (true) {
            var index = this.indexOf(value);
            if (index < 0) return;
            this.splice(index, 1);
        }
    };

    if (!Array.prototype.find) {
        /**
         * Find element from array using predicate
         */
        Array.prototype.find = function (predicate) {
            if (this === null) {
                throw new TypeError('Array.prototype.find called on null or undefined');
            }
            if (typeof predicate !== 'function') {
                throw new TypeError('predicate must be a function');
            }
            var list = Object(this);
            var length = list.length >>> 0;
            var thisArg = arguments[1];
            var value;

            for (var i = 0; i < length; i++) {
                value = list[i];
                if (predicate.call(thisArg, value, i, list)) {
                    return value;
                }
            }
            return undefined;
        };
    }
}());
