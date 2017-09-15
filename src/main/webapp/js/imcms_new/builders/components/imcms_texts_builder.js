/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 26.07.17.
 */
Imcms.define("imcms-texts-builder",
    ["imcms-bem-builder", "imcms-primitives-builder", "imcms-buttons-builder", "imcms-uuid-generator", "jquery"],
    function (BEM, primitives, buttons, uuidGenerator, $) {
        function activateNumberBox() {
            var numberBox = $(this).closest(".imcms-number-box"),
                numberBoxInput = numberBox.find(".imcms-number-box__input")
            ;
            numberBox.addClass("imcms-number-box--active");

            if (numberBoxInput.val() === "") {
                numberBoxInput.val(0)
            }
        }

        function validation() {
            var $this = $(this),
                value = $this.val()
            ;

            if (value.match(/[^0-9,-]/g)) {
                $this.val(value.replace(/[^0-9,-]/g, ''));
            }
            if (value.length > 10) {
                $this.val(value.substring(0, 10))
            }
        }

        function incrementNumberBoxValue() {
            changeValue.call(this, 1);
        }

        function decrementNumberBoxValue() {
            changeValue.call(this, -1);
        }

        function changeValue(delta) {
            var numberBoxInput = $(this).closest(".imcms-number-box").find(".imcms-number-box__input"),
                value = (parseInt(numberBoxInput.val()) || 0) + delta
            ;
            return numberBoxInput.val(value);
        }

        function deactivateNumberBox(e) {
            var $target = $(e.target);

            if ($target.hasClass("imcms-number-box__button")
                || $target.parent().children(".imcms-number-box__input").length
            ) {
                return;
            }

            e.stopPropagation();
            $(".imcms-number-box__input").closest(".imcms-number-box")
                .removeClass("imcms-number-box--active");
        }

        function apiSetValue($resultTextBox, $input) {
            return function (value) {
                $input.val(value);
                return $resultTextBox;
            };
        }

        $(document).click(deactivateNumberBox);

        var textBEM = new BEM({
            block: "imcms-text-box",
            elements: {
                "input": "imcms-input"
            }
        });

        var textAreaBEM = new BEM({
            block: "imcms-text-area",
            elements: {}
        });

        var numberBoxBEM = new BEM({
            block: "imcms-number-box",
            elements: {
                "button": "imcms-button"
            }
        });

        var numberBEM = new BEM({
            block: "imcms-number",
            elements: {
                "number-box": "imcms-number-box",
                "error-msg": "imcms-error-msg"
            }
        });

        var pluralInputBEM = new BEM({
            block: "imcms-space-around",
            elements: {
                "input-box": ""
            }
        });

        function generateTextFromBEM(attributes, structureBEM, factory) {
            var id = attributes.id || uuidGenerator.generateUUID(),
                $input = factory({
                    id: id,
                    name: attributes.name,
                    placeholder: attributes.placeholder,
                    value: attributes.value,
                    readonly: attributes.readonly
                }),
                blockElements = [{"input": $input}]
            ;

            if (attributes.text) {
                var $label = primitives.imcmsLabel(id, attributes.text);
                blockElements.unshift({"label": $label});
            }

            var $resultTextBox = structureBEM.buildBlock("<div>", blockElements);

            $resultTextBox.setValue = apiSetValue($resultTextBox, $input);

            return $resultTextBox;
        }

        return {
            textInput: function (attributes) {
                return textBEM.makeBlockElement("input", primitives.imcmsInputText(attributes));
            },
            textBox: function (tag, attributes) {
                return generateTextFromBEM(attributes, textBEM, primitives.imcmsInputText);
            },
            textField: function (tag, attributes) {
                return this.textBox.apply(this, arguments).addClass("imcms-field");
            },
            textArea: function (tag, attributes) {
                return generateTextFromBEM(attributes, textAreaBEM, primitives.imcmsInputTextArea);
            },
            textAreaField: function (tag, attributes) {
                return this.textArea.apply(this, arguments).addClass("imcms-field");
            },
            textNumber: function (tag, attributes) {
                var id = attributes.id || uuidGenerator.generateUUID(),
                    $input = primitives.imcmsInputText({
                        id: id,
                        name: attributes.name,
                        placeholder: attributes.placeholder,
                        click: activateNumberBox
                    }).on('change keyup input click', validation),

                    $buttonIncrement = buttons.incrementButton({click: incrementNumberBoxValue}),
                    $buttonDecrement = buttons.decrementButton({click: decrementNumberBoxValue}),

                    $numberInputBox = numberBoxBEM.buildBlock("<div>", [
                        {"input": $input},
                        {"button": $buttonIncrement},
                        {"button": $buttonDecrement}
                    ]),
                    $label = primitives.imcmsLabel(id, attributes.text),
                    $error = numberBEM.buildElement("error-msg", "<div>", {text: attributes.error})
                ;
                return numberBEM.buildBlock("<div>", [
                    {"label": $label},
                    {"number-box": $numberInputBox},
                    {"error-msg": $error}
                ], (attributes["class"] ? {"class": attributes["class"]} : {}));
            },
            textNumberField: function (tag, attributes) {
                return this.textNumber.apply(this, arguments).addClass("imcms-field");
            },
            pluralInput: function (tag, columns, attributes) {
                var id = columns[0].id || uuidGenerator.generateUUID(),
                    $label = primitives.imcmsLabel(id, attributes.text),
                    inputs = columns.map(function (column) {
                        return pluralInputBEM.buildBlockElement("input", "<input>", {
                            type: "text",
                            id: column.id,
                            placeholder: column.placeholder,
                            name: column.name
                        });
                    }),
                    $inputBox = pluralInputBEM.buildElement("input-box", "<div>").append(inputs)
                ;
                return pluralInputBEM.buildBlock("<div>", [
                    {"label": $label},
                    {"input-box": $inputBox}
                ]);
            },
            errorText: function (tag, text, attributes) {
                return $(tag, (attributes || {})).addClass("imcms-error-msg").text(text || "");
            },
            infoText: function (tag, text, attributes) {
                return $(tag, (attributes || {})).addClass("imcms-info-msg").text(text || "");
            },
            titleText: function (tag, text, attributes) {
                return $(tag, (attributes || {})).addClass("imcms-title").text(text || "");
            }
        }
    }
);
