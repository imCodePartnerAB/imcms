/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 25.07.17.
 */
define("imcms-radio-buttons-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-uuid-generator"],
    function (bemBuilder, primitives, uuidGenerator) {

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

                        setTimeout(() => {
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
                var radioBlocks$ = Array.prototype.slice.call(arguments);

                return {
                    setCheckedValue: value => {
                        radioBlocks$.forEach($radioBlock => {
                            var $radio = $radioBlock.find("input");
                            ($radio.val() === value) && $radio.prop("checked", "checked");
                        });
                    },
                    getCheckedValue: () => radioBlocks$.reduce(function (prevValue, $radioBlock) {
                        var $radio = $radioBlock.find("input");

                        if ($radio.is(":checked")) {
                            return $radio.val();
                        }

                        return prevValue;

                    }, null)
                };
            },
            radioContainer: (tag, elements, attributes) => containerBEM.buildBlock(tag, elements, attributes, "radio")
        }
    }
);
