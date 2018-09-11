/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-checkboxes-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-uuid-generator"],
    function (BEM, primitives, uuidGenerator) {
        var checkboxBEM = new BEM({
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
            return function (isChecked) {
                isChecked ? $input.prop("checked", "checked") : $input.removeProp("checked");
                return imcmsCheckboxResult;
            };
        }

        function bindSetLabelText(imcmsCheckboxResult, $label) {
            return function (text) {
                $label.text(text);
                return imcmsCheckboxResult;
            };
        }

        function bindIsChecked($input) {
            return function () {
                return $input.is(":checked");
            };
        }

        function bindGetValue($input) {
            return function () {
                return $input.val();
            };
        }

        return {
            imcmsCheckbox: function (tag, attributes) {
                attributes = attributes || {};

                var id = attributes.id || uuidGenerator.generateUUID();
                var options = {
                    type: "checkbox",
                    name: attributes.name,
                    id: id,
                    value: attributes.value
                };

                attributes.disabled && (options.disabled = attributes.disabled);
                attributes.change && (options.change = attributes.change);

                var $input = checkboxBEM.buildElement("checkbox", "<input>", options);

                if (attributes.checked) {
                    $input.prop("checked", "checked");
                }

                var $label = primitives.imcmsLabelFromObject({
                    "for": id,
                    text: attributes.text,
                    click: attributes.click
                });

                var imcmsCheckboxResult = checkboxBEM.buildBlock(tag, [
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
            checkboxContainer: function (tag, elements, attributes) {
                elements = elements.map(function (element) {
                    return {"checkbox": element};
                });

                if (attributes && attributes.title) {
                    var $title = containerBEM.buildElement("title", "<div>", {text: attributes.title});
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
