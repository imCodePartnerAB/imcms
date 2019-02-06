/**
 * Function provides possibility to get an object with all
 * element's attributes with values from DOM element
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.11.17
 */
define("imcms-dom-attributes-extractor", ["jquery"], function ($) {
    return function (extractMyAttributes) {
        if (!extractMyAttributes) {
            return null;
        }

        const attributes = {};

        $.each(extractMyAttributes.attributes, function () {
            if (this.specified) {
                attributes[this.name] = this.value;
            }
        });

        return attributes;
    }
});
