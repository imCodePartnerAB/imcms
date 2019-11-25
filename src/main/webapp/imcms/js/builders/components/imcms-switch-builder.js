/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.17.
 */
define("imcms-switch-builder", ["imcms-bem-builder", "imcms-primitives-builder"], function (BEM, primitives) {

    const switchBlockBEM = new BEM({
        block: "imcms-switches",
        elements: {
            "switch": "imcms-switch"
        }
    });

    const switchBEM = new BEM({
        block: "imcms-switch",
        elements: {
            "input": "imcms-input",
            "label": "imcms-label"
        }
    });

    return {
        imcmsSwitch: (tag, attributes) => {
            const $label = primitives.imcmsLabel(attributes.id);
            attributes.type = "checkbox";
            const $input = primitives.imcmsInput(attributes);

            return switchBEM.buildBlock("<div>", [
                {"input": $input},
                {"label": $label}
            ]);
        },
        switchBlock: (tag, elements, attributes) => switchBlockBEM.buildBlock(tag, elements, attributes, "switch")
    }
});
