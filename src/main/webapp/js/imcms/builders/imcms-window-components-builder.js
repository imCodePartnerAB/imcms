/**
 * Window common used components builder module.
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
define("imcms-window-components-builder",
    ["imcms-bem-builder", "imcms-components-builder"],
    function (BEM, components) {
        return {
            /**
             * Builds head with specified title and close button
             * @param {string} title
             * @param {function} onCloseClick
             * @returns {*} head as jQuery element
             */
            buildHead: function (title, onCloseClick) {
                return new BEM({
                    block: "imcms-head",
                    elements: {
                        "title": components.texts.titleText("<div>", title),
                        "button": components.buttons.closeButton({click: onCloseClick})
                    }
                }).buildBlockStructure("<div>");
            },
            /**
             * Builds head with specified title
             * @param {string} title
             * @returns {*} head as jQuery element
             */
            buildNonClosableHead: function (title) {
                return new BEM({
                    block: "imcms-head",
                    elements: {
                        "title": components.texts.titleText("<div>", title)
                    }
                }).buildBlockStructure("<div>");
            },
            /**
             * Builds footer with specified buttons as it's block elements
             * @param {[]} buttons - array of jQuery elements
             * @returns {*} footer as jQuery element
             */
            buildFooter: function (buttons) {
                var elements = {};

                if (buttons) {
                    elements.buttons = components.buttons.buttonsContainer("<div>", buttons);
                }

                return new BEM(
                    {
                        block: "imcms-footer",
                        elements: elements
                    }
                ).buildBlockStructure("<div>");
            }
        };
    }
);
