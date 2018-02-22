/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.17.
 */
Imcms.define("imcms-radio-buttons-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-uuid-generator"],
    function (bemBuilder, primitives, uuidGenerator) {

        function createGetCheckedValue(radioBlocks$) {
            return function () {
                return Array.prototype.reduce.call(radioBlocks$, function (prevValue, $radioBlock) {
                    var $radio = $radioBlock.find("input");

                    if ($radio.is(":checked")) {
                        return $radio.val();
                    }

                    return prevValue;

                }, null);
            }
        }

        function apiCheckAmongGroup(radioBlocks$) {
            return function (value) {
                Array.prototype.forEach.call(radioBlocks$, function ($radioBlock) {
                    var $radio = $radioBlock.find("input");
                    ($radio.val() === value) && $radio.prop("checked", "checked");
                });
            }
        }

        function apiSetChecked($input) {
            return function (isChecked) {
                isChecked ? $input.prop("checked", "checked") : $input.removeProp("checked");
                return this;
            };
        }

        var radioBEM = new bemBuilder({
                block: "imcms-radio",
                elements: {}
            }),
            containerBEM = new bemBuilder({
                block: "imcms-radios",
                elements: {
                    "radio": "imcms-radio"
                }
            })
        ;

        return {
            imcmsRadio: function (tag, attributes) {
                var id = attributes.id || uuidGenerator.generateUUID(),
                    $input = primitives.imcmsInputRadio({
                        name: attributes.name,
                        id: id,
                        value: attributes.value
                    });

                if (attributes.checked) {
                    $input.prop("checked", "checked");
                }

                var $label = primitives.imcmsLabelFromObject({
                    "for": id,
                    text: attributes.text,
                    click: function () {

                        if (!attributes.click) {
                            return;
                        }

                        var args = arguments;
                        var context = this;

                        setTimeout(function () {
                            if ($input.is(":checked")) {
                                attributes.click.apply(context, args);
                            }
                        });
                    }
                });
                var buildBlock = radioBEM.buildBlock(tag, [
                    {"input": $input},
                    {"label": $label}
                ]);

                buildBlock.setChecked = apiSetChecked($input);

                return buildBlock;
            },
            group: function () {
                this.checkAmongGroup = apiCheckAmongGroup(arguments);
                this.getCheckedValue = createGetCheckedValue(arguments);
                return this;
            },
            radioContainer: function (tag, elements, attributes) {
                return containerBEM.buildBlock(tag, elements, attributes, "radio");
            }
        }
    }
);
