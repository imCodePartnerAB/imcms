/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 26.07.17.
 */
const BEM = require('imcms-bem-builder');
const primitives = require('imcms-primitives-builder');
const buttons = require('imcms-buttons-builder');
const uuidGenerator = require('imcms-uuid-generator');
const $ = require('jquery');

function activateNumberBox() {
    const numberBox = $(this).closest(".imcms-number-box");
    const numberBoxInput = numberBox.find(".imcms-number-box__input");
    numberBox.addClass("imcms-number-box--active");

    if (numberBoxInput.val() === "") {
        numberBoxInput.val(0)
    }
}

function validation(onValidChange) {
    const $this = $(this);
    let value = $this.val();

    if (value.length > 10) {
        value = value.substring(0, 10);
        $this.val(value);
    }

    if (value.match(/[^0-9,-]/g)) {
        $this.val(value.replace(/[^0-9,-]/g, ''));
    }

    onValidChange && onValidChange.call(this);
}

function incrementNumberBoxValue() {
    changeValue.call(this, 1);
}

function decrementNumberBoxValue() {
    changeValue.call(this, -1);
}

function changeValue(delta) {
    const numberBoxInput = $(this).closest(".imcms-number-box").find(".imcms-number-box__input");
    const value = (parseInt(numberBoxInput.val()) || 0) + delta;
    numberBoxInput.val(value);
}

function deactivateNumberBox(e) {
    const $target = $(e.target);

    if ($target.hasClass("imcms-number-box__button")
        || $target.parent().children(".imcms-number-box__input").length)
    {
        return;
    }

    e.stopPropagation();

    $(".imcms-number-box__input").closest(".imcms-number-box").removeClass("imcms-number-box--active");
}

function createSetValue($resultTextBox, $input) {
    return value => {
        $input.val(value);
        return $resultTextBox;
    };
}

function createGetValue($input) {
    return function () {
        return $input.val();
    };
}

$(document).click(deactivateNumberBox);

const textBEM = new BEM({
    block: "imcms-text-box",
    elements: {
        "input": "imcms-input"
    }
});

const textAreaBEM = new BEM({
    block: "imcms-text-area",
    elements: {}
});

const numberBoxBEM = new BEM({
    block: "imcms-number-box",
    elements: {
        "button": "imcms-button"
    }
});

const numberBEM = new BEM({
    block: "imcms-number",
    elements: {
        "number-box": "imcms-number-box",
        "error-msg": "imcms-error-msg"
    }
});

const pluralInputBEM = new BEM({
    block: "imcms-space-around",
    elements: {
        "input-box": ""
    }
});

function generateTextFromBEM(attributes, structureBEM, factory) {
    const id = attributes.id || uuidGenerator.generateUUID();
    const $input = factory({
        id: id,
        name: attributes.name,
        placeholder: attributes.placeholder,
        value: attributes.value,
        html: attributes.html,
        readonly: attributes.readonly
    });
    const blockElements = [{"input": $input}];

    if (attributes.text) {
        const $label = primitives.imcmsLabel(id, attributes.text);
        blockElements.unshift({"label": $label});
    }

    const $resultTextBox = structureBEM.buildBlock("<div>", blockElements);

    $resultTextBox.setValue = createSetValue($resultTextBox, $input);
    $resultTextBox.getValue = createGetValue($input);
    $resultTextBox.$input = $input;

    return $resultTextBox;
}

module.exports = {
    textInput: attributes => textBEM.makeBlockElement("input", primitives.imcmsInputText(attributes)),
    textBox: (tag, attributes) => generateTextFromBEM(attributes, textBEM, primitives.imcmsInputText),
    textField: function (tag, attributes) {
        return this.textBox.apply(this, arguments).addClass("imcms-field");
    },
    textArea: (tag, attributes) => generateTextFromBEM(attributes, textAreaBEM, primitives.imcmsInputTextArea),
    textAreaField: function (tag, attributes) {
        return this.textArea.apply(this, arguments).addClass("imcms-field");
    },
    textNumber: function (tag, attributes) {
        attributes = attributes || {};
        const id = attributes.id || uuidGenerator.generateUUID();
        const $input = primitives.imcmsInputText({
                id: id,
                name: attributes.name,
                placeholder: attributes.placeholder,
            click: activateNumberBox,
            disabled: attributes.disabled
            })
            .on('change keyup input', function (e) { // click
                if (e.key === "ArrowDown") {
                    decrementNumberBoxValue.call(this);

                } else if (e.key === "ArrowUp") {
                    incrementNumberBoxValue.call(this);
                }

                validation.call(this, attributes.onValidChange);
            });

        const $buttonIncrement = buttons.incrementButton({
            click: function () {
                incrementNumberBoxValue.call(this);
                $input.change();
            }
        });
        const $buttonDecrement = buttons.decrementButton({
            click: function () {
                decrementNumberBoxValue.call(this);
                $input.change();
            }
        });

        const $numberInputBox = numberBoxBEM.buildBlock("<div>", [
            {"input": $input},
            {"button": $buttonIncrement},
            {"button": $buttonDecrement}
        ]);
        const $label = primitives.imcmsLabel(id, attributes.text);
        const $error = numberBEM.buildElement("error-msg", "<div>", {text: attributes.error});

        const block = numberBEM.buildBlock("<div>", [
            {"label": $label},
            {"number-box": $numberInputBox},
            {"error-msg": $error}
        ], (attributes["class"] ? {"class": attributes["class"]} : {}));

        block.getInput = () => $input;
        block.setValue = (val) => $input.val(val);

        return block;
    },
    textNumberField: function (tag, attributes) {
        return this.textNumber.apply(this, arguments).addClass("imcms-field");
    },
    pluralInput: (tag, columns, attributes) => {
        const id = columns[0].id || uuidGenerator.generateUUID();
        const $label = primitives.imcmsLabel(id, attributes.text);
        const inputs = columns.map(column => pluralInputBEM.buildBlockElement("input", "<input>", {
            type: "text",
            id: column.id,
            placeholder: column.placeholder,
            name: column.name,
            blur: column.blur
        }));
        const $inputBox = pluralInputBEM.buildElement("input-box", "<div>").append(inputs);

        return pluralInputBEM.buildBlock("<div>", [
            {"label": $label},
            {"input-box": $inputBox}
        ]);
    },
    errorText: (tag, text, attributes) => $(tag, (attributes || {})).addClass("imcms-error-msg").text(text || ""),
    infoText: (tag, text, attributes) => $(tag, (attributes || {})).addClass("imcms-info-msg").text(text || ""),
    infoHtml: (tag, text, attributes) => $(tag, (attributes || {})).addClass("imcms-info-msg").html(text || ""),
    titleText: (tag, text, attributes) => $(tag, (attributes || {})).addClass("imcms-title").text(text || ""),
    secondaryText: (tag, text, attributes) => $(tag, (attributes || {})).addClass("imcms-secondary-msg").text(text || ""),
};
