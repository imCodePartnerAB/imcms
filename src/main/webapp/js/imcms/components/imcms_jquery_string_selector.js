Imcms.define("imcms-jquery-string-selector", ["imcms-dom-attributes-extractor"], function (getAttributes) {
    /**
     * Transform element into String selector
     * @returns {string|null} transformed DOM object as string like
     * "#id.class1.class2[attr1=value1,attr2=value2]"
     *
     * @author Serhii Maksymchuk from Ubrainians for imCode
     * 13.11.17
     */
    return function ($transformMe) {
        if (!$transformMe.length) {
            return null;
        }

        var attributes = getAttributes($transformMe[0]);

        if (attributes.id) {
            return "#" + attributes.id;

        } else {
            var elementAsString = "";

            if (attributes["class"]) {
                elementAsString += "." + attributes["class"].split(" ").join(".");
                delete attributes["class"];
            }

            delete attributes.style; // this one is quite dynamic to rely on

            var otherAttributes = Object.keys(attributes).map(function (key) {
                return "[" + key + "='" + attributes[key] + "']";
            });

            if (otherAttributes.length) {
                elementAsString += otherAttributes.join("");
            }

            return (elementAsString)
                ? elementAsString
                : $transformMe.selector;
        }
    }
});
