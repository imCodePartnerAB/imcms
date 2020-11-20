/**
 * Analog to function [Object.fromEntries]{@link https://developer.mozilla.org/ru/docs/Web/JavaScript/Reference/Global_Objects/Object/fromEntries},
 * but it is working for old browsers.
 *
 * @param {[string, Object][]} entries
 * @return {Object}
 */
export function fromEntries(entries) {
    return entries.reduce((acc, [key, value]) => {
        acc[key] = value;
        return acc;
    }, {});
}
