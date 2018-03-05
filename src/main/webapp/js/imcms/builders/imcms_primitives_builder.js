/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 27.07.17.
 */
Imcms.define("imcms-primitives-builder", ["imcms-bem-builder"], function (BEM) {
    var IMCMS_LABEL_CLASS = "imcms-label",
        IMCMS_INPUT_CLASS = "imcms-input"
    ;

    var primitivesBEM = new BEM({
        // no block for now
        elements: {
            "label": IMCMS_LABEL_CLASS,
            "input": IMCMS_INPUT_CLASS
        }
    });

    return {
        imcmsLabel: function (idFor, text, attributes) {
            attributes = attributes || {};
            attributes["for"] = idFor;
            attributes.text = text;

            return this.imcmsLabelFromObject(attributes);
        },
        imcmsLabelFromObject: function (attributes) {
            return primitivesBEM.buildElement("label", "<label>", attributes);
        },
        imcmsInput: function (attributes) {
            return primitivesBEM.buildElement("input", "<input>", attributes);
        },
        imcmsInputText: function (attributes, modifiersArr) {
            attributes = attributes || {};
            attributes.type = "text";
            return primitivesBEM.buildElement("input", "<input>", attributes, modifiersArr);
        },
        imcmsInputTextArea: function (attributes, modifiersArr) {
            return primitivesBEM.buildElement("input", "<textarea>", attributes, modifiersArr);
        },
        imcmsInputRadio: function (attributes, modifiersArr) {
            attributes = attributes || {};
            attributes.type = "radio";
            return primitivesBEM.buildElement("input", "<input>", attributes, modifiersArr);
        }
    }
});
