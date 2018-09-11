/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.17.
 */
define("imcms-switch-builder", ["imcms-bem-builder", "imcms-primitives-builder"], function (BEM, primitives) {

    var switchBlockBEM = new BEM({
        block: "imcms-switches",
        elements: {
            "switch": "imcms-switch"
        }
    });

    var switchBEM = new BEM({
        block: "imcms-switch",
        elements: {
            "input": "imcms-input",
            "label": "imcms-label"
        }
    });

    return {
        imcmsSwitch: function (tag, attributes) {
            var $label = primitives.imcmsLabel(attributes.id);
            attributes.type = "checkbox";
            var $input = primitives.imcmsInput(attributes);

            return switchBEM.buildBlock("<div>", [
                {"input": $input},
                {"label": $label}
            ]);
        },
        switchBlock: function (tag, elements, attributes) {
            return switchBlockBEM.buildBlock(tag, elements, attributes, "switch");
        }
    }
});
