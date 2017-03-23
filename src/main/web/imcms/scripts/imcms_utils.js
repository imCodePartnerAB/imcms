/**
 * Imcms Utils
 *
 * Created by Shadowgun on 12.02.2015.
 *
 * Refactored by Serhii Maksymchuk in 2017
 */
(function (Imcms) {
    return Imcms.Utils = {
        /**
         * Marge all passed objects properties to one result {a:1, b:2}+{c:1, b:2} = {a:1, b:2, c:1}
         * @returns {{}}
         */
        mergeObjectsProperties: function () {
            var mergedResult = {};
            for (var objKey in arguments) {
                var obj = arguments[objKey];
                for (var attr in obj)
                    mergedResult[attr] = obj[attr];
            }
            return mergedResult;
        },
        /***
         * @info Function extend properties from source into obj
         * @param {Object} obj main
         * @param {Object} source extend from
         * @returns {Object} or {null} if one of params is incorrect
         */
        merge: function (obj, source) {
            if (!obj || !source || (obj.constructor !== Object) || (source.constructor !== Object)) {
                return obj ? obj : null;
                // stupid checking?

            } else {
                for (var key in source) {
                    if (!Object.prototype.hasOwnProperty.call(obj, key)) {
                        obj[key] = source[key];

                    } else if (obj[key] && obj[key].constructor === Object) {
                        obj[key] = Imcms.Utils.merge(obj[key], source[key]);
                    }
                }

                return obj;
            }
        }
    };
})(Imcms);
