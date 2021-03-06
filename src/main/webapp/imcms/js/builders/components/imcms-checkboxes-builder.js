/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-checkboxes-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-uuid-generator"],
    function (BEM, primitives, uuidGenerator) {
        const checkboxBEM = new BEM({
                block: "imcms-checkbox",
                elements: {
                    "checkbox": ""
                }
            }),
            containerBEM = new BEM({
                block: "imcms-checkboxes",
                elements: {
                    "checkbox": "imcms-checkbox",
                    "title": "imcms-title"
                }
            })
        ;

        function bindSetChecked(imcmsCheckboxResult, $input) {
            return isChecked => {
                isChecked ? $input.prop("checked", "checked") : $input.removeProp("checked");
                return imcmsCheckboxResult;
            };
        }

        function bindSetLabelText(imcmsCheckboxResult, $label) {
            return text => {
                $label.text(text);
                return imcmsCheckboxResult;
            };
        }

        function bindIsChecked($input) {
            return () => $input.is(":checked");
        }

        function bindGetValue($input) {
            return () => $input.val();
        }

        return {
            imcmsCheckbox: (tag, attributes) => {
                attributes = attributes || {};

                const id = attributes.id || uuidGenerator.generateUUID();
                const options = {
                    type: "checkbox",
                    name: attributes.name,
                    id: id,
                    value: attributes.value
                };

                attributes.disabled && (options.disabled = attributes.disabled);
                attributes.change && (options.change = attributes.change);

                const $input = checkboxBEM.buildElement("checkbox", "<input>", options);

                if (attributes.checked) {
                    $input.prop("checked", "checked");
                }

                const $label = primitives.imcmsLabelFromObject({
                    "for": id,
                    text: attributes.text,
                    click: attributes.click
                });

                const imcmsCheckboxResult = checkboxBEM.buildBlock(tag, [
                    {"checkbox": $input},
                    {"label": $label}
                ]);

                imcmsCheckboxResult.$input = $input;
                imcmsCheckboxResult.setLabelText = bindSetLabelText(imcmsCheckboxResult, $label);
                imcmsCheckboxResult.setChecked = bindSetChecked(imcmsCheckboxResult, $input);
                imcmsCheckboxResult.isChecked = bindIsChecked($input);
                imcmsCheckboxResult.getValue = bindGetValue($input);

                return imcmsCheckboxResult;
            },
            checkboxContainer: (tag, elements, attributes) => {
                elements = elements.map(element => ({"checkbox": element}));

                if (attributes && attributes.title) {
                    const $title = containerBEM.buildElement("title", "<div>", {text: attributes.title});
                    delete attributes.title;
                    elements.unshift({"title": $title});
                }

                return containerBEM.buildBlock(tag, elements, attributes);
            },
            checkboxContainerField: function (tag, elements, attributes) {
                return new BEM({
                    block: "imcms-field",
                    elements: {
                        "checkboxes": this.checkboxContainer("<div>", elements, attributes)
                    }
                }).buildBlockStructure(tag);
            }
        }
    }
);
