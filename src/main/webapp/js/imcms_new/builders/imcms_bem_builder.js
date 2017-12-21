/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
Imcms.define("imcms-bem-builder", ["jquery"], function ($) {
    var MODIFIER_SEPARATOR = "--",
        BLOCK_SEPARATOR = "__"
    ;

    function getOriginClass(attributesObj) {
        return attributesObj["class"] ? " " + attributesObj["class"] : "";
    }

    function getElementClassWithModifiers(elementClass, modifiersArr) {
        return elementClass + getModifiersClass(elementClass, modifiersArr);
    }

    function getModifiersClass(baseClass, modifiersArr) {
        var modifiers = "";

        (modifiersArr || []).forEach(function (modifier) {
            modifiers += " " + baseClass + MODIFIER_SEPARATOR + modifier;
        });

        return modifiers;
    }

    function createBlocKElement(element, elementName) {
        var blockElement = {};
        this.elements[elementName] = element["class"] || "";

        if (element.modifiers) {
            blockElement.modifiers = element.modifiers;
        }

        blockElement[elementName] = (element.length) // means jquery, not simple array
            ? element
            : this.buildElement(elementName, element.tag, element.attributes, element.modifiers);

        return blockElement;
    }

    function createBlockElements(elements, elementName) {
        return elements.map(function (element) {
            return createBlocKElement.call(this, element, elementName);
        }.bind(this));
    }

    var BemBuilder = function (options) {
        this.elements = options.elements;
        this.block = options.block;
    };

    BemBuilder.buildClass = function (block, element, modifier) {
        modifier = (element && modifier) ? MODIFIER_SEPARATOR + modifier : "";
        element = (element || "");
        block = (block ? block + BLOCK_SEPARATOR : "");
        return block + element + modifier;
    };

    BemBuilder.buildClassSelector = function (block, element, modifier) {
        return "." + this.buildClass.apply(this, arguments);
    };

    BemBuilder.getBlockSeparator = function () {
        return BLOCK_SEPARATOR;
    };

    BemBuilder.getModifierSeparator = function () {
        return MODIFIER_SEPARATOR;
    };

    BemBuilder.prototype = {
        makeBlockElement: function (elementName, $baseElement, modifiersArr) {
            var blockClass = this.block + BLOCK_SEPARATOR + elementName,
                modifiersClass = getElementClassWithModifiers(blockClass, modifiersArr)
            ;

            return $baseElement.addClass(blockClass).addClass(modifiersClass);
        },
        buildBlockElement: function (elementName, tag, attributes, modifiersArr) {
            return this.buildElement.apply(this, arguments).addClass(this.block + BLOCK_SEPARATOR + elementName);
        },
        buildElement: function (elementName, tag, attributes, modifiersArr) {
            var modifiersClass = getElementClassWithModifiers(this.elements[elementName], modifiersArr);

            attributes = $.extend({}, attributes);
            attributes["class"] = modifiersClass + getOriginClass(attributes);

            return $(tag, attributes);
        },
        buildBlock: function (tag, elements, attributes, blockNameForEach) {
            attributes = attributes || {};
            attributes["class"] = this.block + getOriginClass(attributes);

            elements = (elements || []).map(function (element) {
                var elementName, $element;

                if (blockNameForEach) {
                    elementName = blockNameForEach;
                    $element = element;

                } else {
                    var elementKeys = Object.keys(element);
                    elementName = elementKeys[0];

                    if (elementName === "modifiers") {
                        elementName = elementKeys[1];
                    }

                    $element = element[elementName];
                }

                var blockClass = this.block + BLOCK_SEPARATOR + elementName;

                if (element.modifiers) {
                    var modifiersClass = getModifiersClass(blockClass, element.modifiers);
                    $element.addClass(modifiersClass);
                }

                return $element.addClass(blockClass);

            }.bind(this));

            return $(tag, attributes).append(elements);
        },
        buildBlockStructure: function (tag, attributes) {
            var isArrayElements = (this.elements.constructor === Array);
            var blockElements = [];
            var elements = $.extend({}, this.elements);

            (isArrayElements ? this.elements : Object.keys(elements)).forEach(function (elementOrName) {
                var elementName, $element;

                if (isArrayElements) {
                    var element = elementOrName;
                    var elementKeys = Object.keys(element);

                    elementName = elementKeys[0];

                    if (elementName === "modifiers") {
                        elementName = elementKeys[1];
                    }

                    $element = element[elementName];
                    $element.modifiers = element.modifiers;

                } else {
                    elementName = elementOrName;
                    $element = elements[elementName];
                }

                if ($element.constructor === Array) {
                    blockElements = blockElements.concat(createBlockElements.call(this, $element, elementName));
                    return;
                }

                var blockElement = createBlocKElement.call(this, $element, elementName);
                blockElements.push(blockElement);

            }.bind(this));

            return this.buildBlock(tag, blockElements, attributes);
        }
    };

    return BemBuilder;
});
