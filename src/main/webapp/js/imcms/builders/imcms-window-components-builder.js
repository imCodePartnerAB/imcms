/**
 * Window common used components builder module.
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */

const BEM = require('imcms-bem-builder');
const components = require('imcms-components-builder');

module.exports = {
    /**
     * Builds head with specified title and close button
     * @param {string} title
     * @param {function} onCloseClick
     * @returns {*} head as jQuery element
     */
    buildHead: (title, onCloseClick) => new BEM({
        block: "imcms-head",
        elements: {
            "title": components.texts.titleText("<div>", title),
            "button": components.buttons.closeButton({click: onCloseClick})
        }
    }).buildBlockStructure("<div>"),
    /**
     * Builds head with specified title
     * @param {string} title
     * @returns {*} head as jQuery element
     */
    buildNonClosableHead: title => new BEM({
        block: "imcms-head",
        elements: {
            "title": components.texts.titleText("<div>", title)
        }
    }).buildBlockStructure("<div>"),
    /**
     * Builds footer with specified buttons as it's block elements
     * @param {[]} buttons - array of jQuery elements
     * @returns {*} footer as jQuery element
     */
    buildFooter: buttons => {
        const elements = {};

        if (buttons) {
            elements.buttons = components.buttons.buttonsContainer("<div>", buttons);
        }

        return new BEM({
                block: "imcms-footer",
                elements: elements
            }
        ).buildBlockStructure("<div>");
    }
};
