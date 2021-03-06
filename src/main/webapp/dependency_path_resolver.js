/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */

const nodePath = require('path');
const separator = '/';

module.exports = {
    resolvePaths(jsDirectory, structure) {
        return (function resolveObj(resource, destination, path) {
            Object.keys(resource).forEach(dir => {
                const value = resource[dir];

                switch (value.constructor) {
                    case Object:
                        resolveObj(value, destination, path + separator + dir);
                        return;

                    case Array:
                        value.forEach(module => destination[module] = nodePath.resolve(
                            __dirname, path + separator + (dir ? (dir + separator) : '') + module
                        ));
                        return;

                    case String :
                        destination[value] = nodePath.resolve(__dirname, [path, dir, value].join(separator));
                }
            });

            return destination

        })(structure, {}, jsDirectory)
    }
};
