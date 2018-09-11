/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */

const nodePath = require('path');
const jsDirectory = './js';
const separator = '/';
const tail = '.js';

module.exports = {
    resolvePaths: function (structure) {
        function resolveObj(resource, destination, path) {
            Object.keys(resource).forEach(key => {
                const value = resource[key];

                if (value.constructor === Object) resolveObj(value, destination, path + key + separator);
                if (value.constructor === Array) value.forEach(x => destination[x] = nodePath.resolve(__dirname, path + (key ? (key + '/') : '') + x + tail));
                if (value.constructor === String) destination[value] = nodePath.resolve(__dirname, path + key + '/' + value + tail);
            });

            return destination;
        }

        return resolveObj(structure, {}, jsDirectory + separator);
    }
};
