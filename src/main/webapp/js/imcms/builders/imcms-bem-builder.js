/**
 * Block-Element-Modifier (BEM) builder.
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */

const $ = require('jquery');

const MODIFIER_SEPARATOR = "--";
const BLOCK_SEPARATOR = "__";

function getOriginClass(attributesObj) {
    return attributesObj["class"] ? " " + attributesObj["class"] : "";
}

function getElementClassWithModifiers(elementClass, modifiersArr) {
    return elementClass + getModifiersClass(elementClass, modifiersArr);
}

function getModifiersClass(baseClass, modifiersArr) {
    let modifiers = "";

    (modifiersArr || []).forEach(function (modifier) {
        modifiers += " " + baseClass + MODIFIER_SEPARATOR + modifier;
    });

    return modifiers;
}

function createBlocKElement(element, elementName) {
    const blockElement = {};
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

module.exports = class BemBuilder {
    constructor(options) {
        this.elements = options.elements;
        this.block = options.block;
    };

    /**
     * Build BEM-styled class with needed separators
     *
     * @example
     *     var bemClass = BEM.buildClass("one", "two", "three"); // = "one__two--three"
     *
     * @param block {null|string}
     * @param element {null|string?}
     * @param modifier {string?}
     * @returns {string} BEM-styled class
     */
    static buildClass(block, element, modifier) {
        modifier = (modifier) ? MODIFIER_SEPARATOR + modifier : "";
        element = (block && element) ? BLOCK_SEPARATOR + element : (element) ? element : "";
        block = (block || "");
        return block + element + modifier;
    }

    /**
     * Build selector for BEM-styled class
     *
     * @example
     *     var bemClassSelector = BEM.buildClassSelector("one", "two", "three"); // = ".one__two--three"
     *
     * @param block {*|string}
     * @param element {null|string?}
     * @param modifier {string?}
     * @returns {string} selector for BEM-styled class
     */
    static buildClassSelector(block, element, modifier) {
        return "." + this.buildClass.apply(this, arguments);
    };

    /**
     * Adds BEM-styled class to element
     *
     * @example
     *     var exampleBEM = new BEM({
     *          block: "the-block",
     *          elements: {
     *              "some-element": "some-element-class"
     *          }
     *     });
     *     var $element = exampleBEM.makeBlockElement("some-element", $(".some-class"), ["mod-1", "mod-2"]);
     *     // $element will have class "the-block__some-element the-block__some-element--mod-1 the-block__some-element--mod-2"
     *
     * @param elementName {string} name of element in current BEM-builder
     * @param $baseElement {object} jquery-element to be modified
     * @param modifiersArr {array?} array of modifiers
     * @returns {*} modified jquery-element
     */
    makeBlockElement(elementName, $baseElement, modifiersArr) {
        const blockClass = this.block + BLOCK_SEPARATOR + elementName,
            modifiersClass = getElementClassWithModifiers(blockClass, modifiersArr)
        ;

        return $baseElement.addClass(blockClass).addClass(modifiersClass);
    }

    /**
     * Builds an BEM-styled jquery-element.
     *
     * @example
     *     var exampleBEM = new BEM({
     *          block: "the-block",
     *          elements: {
     *              "some-element": "some-element-class"
     *          }
     *     });
     *     var $element = exampleBEM.buildBlockElement(
     *          "some-element", "<div>", {"name":"some-name"}, ["mod-1", "mod-2"]
     *     );
     *     // $element will be:
     *     <div name="some-name" class="some-element-class some-element-class--mod-1 some-element-class--mod-2 the-block__some-element"></div>
     *
     * @param elementName {string}
     * @param tag {string}
     * @param attributes {object?}
     * @param modifiersArr {array?}
     * @returns {*}
     *
     * @see buildElement
     */
    buildBlockElement(elementName, tag, attributes, modifiersArr) {
        return this.buildElement.apply(this, arguments).addClass(this.block + BLOCK_SEPARATOR + elementName);
    }

    /**
     * Builds an BEM-styled jquery-element without block__element class.
     *
     * @example
     *     var exampleBEM = new BEM({
     *          block: "the-block",
     *          elements: {
     *              "some-element": "some-element-class"
     *          }
     *     });
     *     var $element = exampleBEM.buildElement(
     *          "some-element", "<div>", {"name":"some-name"}, ["mod-1", "mod-2"]
     *     );
     *     // $element will be:
     *     <div name="some-name" class="some-element-class some-element-class--mod-1 some-element-class--mod-2"></div>
     *
     * @param elementName {string}
     * @param tag {string}
     * @param attributes {object?}
     * @param modifiersArr {array?}
     * @returns {*}
     *
     * @see buildElement
     */
    buildElement(elementName, tag, attributes, modifiersArr) {
        const modifiersClass = getElementClassWithModifiers(this.elements[elementName], modifiersArr);

        attributes = $.extend({}, attributes);
        attributes["class"] = modifiersClass + getOriginClass(attributes);

        return $(tag, attributes);
    }

    /**
     * Builds block element.
     *
     * @example
     *     var panelButtonsBEM = new BEM({
     *         block: "imcms-menu"
     *     });
     *
     *     var $block = panelButtonsBEM.buildBlock("<div>", [{"items": $("<ul>")}], {name: "some-name"});
     *
     *     // $block will be:<br/>
     *     <div class="imcms-menu" name="some-name">
     *       <ul class="imcms-menu__items"></ul>
     *     </div>
     *
     *     // or the same result with this:
     *     var $block = panelButtonsBEM.buildBlock("<div>", [$("<ul>")], {name: "some-name"}, "items");
     *
     *
     * @param tag {string}
     * @param elements {array?}
     * @param attributes {object?}
     * @param blockNameForEach {string?}
     * @returns {*} build block with elements inside
     */
    buildBlock(tag, elements, attributes, blockNameForEach) {
        attributes = attributes || {};
        attributes["class"] = this.block + getOriginClass(attributes);

        elements = (elements || []).map(function (element) {
            let elementName, $element;

            if (blockNameForEach) {
                elementName = blockNameForEach;
                $element = element;

            } else {
                const elementKeys = Object.keys(element);
                elementName = elementKeys[0];

                if (elementName === "modifiers") {
                    elementName = elementKeys[1];
                }

                $element = element[elementName];
            }

            const blockClass = this.block + BLOCK_SEPARATOR + elementName;

            if (element.modifiers) {
                const modifiersClass = getModifiersClass(blockClass, element.modifiers);
                $element.addClass(modifiersClass);
            }

            return $element.addClass(blockClass);

        }.bind(this));

        return $(tag, attributes).append(elements);
    }

    /**
     * Builds block using elements from constructor.
     *
     * @example
     *     var $block = new BEM({
     *         block: "imcms-list",
     *         elements: {
     *             "imcms-list-item": [$("<li>"), $("<li>")]
     *         }
     *     }).buildBlockStructure("<ul>", {id: "the-id"});
     *
     *     // $block will be:<br/>
     *     <ul id="the-id" class="imcms-list">
     *       <li class="imcms-list__imcms-list-item"></li>
     *       <li class="imcms-list__imcms-list-item"></li>
     *     </ul>
     *
     * @param tag {string}
     * @param attributes {object?}
     * @returns {*}
     */
    buildBlockStructure(tag, attributes) {
        const isArrayElements = (this.elements.constructor === Array);
        let blockElements = [];
        const elements = $.extend({}, this.elements);

        (isArrayElements ? this.elements : Object.keys(elements)).forEach(function (elementOrName) {
            let elementName, $element;

            if (isArrayElements) {
                const element = elementOrName;
                const elementKeys = Object.keys(element);

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

            const blockElement = createBlocKElement.call(this, $element, elementName);
            blockElements.push(blockElement);

        }.bind(this));

        return this.buildBlock(tag, blockElements, attributes);
    }
};
