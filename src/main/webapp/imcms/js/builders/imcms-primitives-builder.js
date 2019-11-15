/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 27.07.17.
 */
const BEM = require('imcms-bem-builder');

const IMCMS_LABEL_CLASS = "imcms-label";
const IMCMS_INPUT_CLASS = "imcms-input";

const primitivesBEM = new BEM({
    // no block for now
    elements: {
        "label": IMCMS_LABEL_CLASS,
        "input": IMCMS_INPUT_CLASS
    }
});

module.exports = {
    imcmsLabel: function (idFor, text, attributes) {
        attributes = attributes || {};
        attributes["for"] = idFor;
        attributes.text = text;

        return this.imcmsLabelFromObject(attributes);
    },
    imcmsLabelFromObject: attributes => primitivesBEM.buildElement("label", "<label>", attributes),
    imcmsInput: attributes => primitivesBEM.buildElement("input", "<input>", attributes),
    imcmsInputText: (attributes, modifiersArr) => {
        attributes = attributes || {};
        attributes.type = "text";
        return primitivesBEM.buildElement("input", "<input>", attributes, modifiersArr);
    },
    imcmsInputTextArea: (attributes, modifiersArr) => primitivesBEM.buildElement("input", "<textarea>", attributes, modifiersArr),
    imcmsInputRadio: (attributes, modifiersArr) => {
        attributes = attributes || {};
        attributes.type = "radio";
        return primitivesBEM.buildElement("input", "<input>", attributes, modifiersArr);
    }
};
