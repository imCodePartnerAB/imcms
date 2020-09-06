/**
 * Returns string with removed css functions (e.g. 'rotate(90deg)') from the given string
 *
 * @example
 * const result = removeCssFunctionsFromString('rotate(90deg) translate(0, 100%)', ['translate']);
 * console.log(result); // 'rotate(90deg)'
 *
 * @param {string} str
 * @param {Array<string>} functionNames
 * @returns {string}
 */
export function removeCssFunctionsFromString(str, functionNames) {
    let resultStr = str;
    functionNames.forEach((name) => resultStr = resultStr.replace(getCssFunctionRegExp(name), ''));
    return resultStr;
}

/**
 * @param {string} functionName
 * @returns {RegExp}
 */
function getCssFunctionRegExp(functionName) {
    return new RegExp(functionName + '\\([^(]+\\)');
}
