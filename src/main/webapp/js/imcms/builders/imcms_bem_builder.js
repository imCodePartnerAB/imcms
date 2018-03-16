/**
 * Block-Element-Modifier (BEM) builder.
 *
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

    /**
     * Build BEM-styled class with needed separators
     *
     * Usage:
     * <pre><code>
     *     var bemClass = BEM.buildClass("one", "two", "three"); // = "one__two--three"
     * </code></pre>
     *
     * @param block {string?}
     * @param element {string?}
     * @param modifier {string?}
     * @returns {string} BEM-styled class
     */
    BemBuilder.buildClass = function (block, element, modifier) {
        modifier = (modifier) ? MODIFIER_SEPARATOR + modifier : "";
        element = (block && element) ? BLOCK_SEPARATOR + element : (element) ? element : "";
        block = (block || "");
        return block + element + modifier;
    };

    /**
     * Build selector for BEM-styled class
     *
     * Usage:
     * <pre><code>
     *     var bemClassSelector = BEM.buildClassSelector("one", "two", "three"); // = ".one__two--three"
     * </code></pre>
     *
     * @param block {string}
     * @param element {string}
     * @param modifier {string}
     * @returns {string} selector for BEM-styled class
     */
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
        /**
         * Adds BEM-styled class to element
         *
         * Usage:
         * <pre><code>
         *     var exampleBEM = new BEM({
         *          block: "the-block",
         *          elements: {
         *              "some-element": "some-element-class"
         *          }
         *     });
         *     var $element = exampleBEM.makeBlockElement("some-element", $(".some-class"), ["mod-1", "mod-2"]);
         *     // $element will have class "the-block__some-element the-block__some-element--mod-1 the-block__some-element--mod-2"
         * </code></pre>
         *
         * @param elementName {string} name of element in current BEM-builder
         * @param $baseElement {object} jquery-element to be modified
         * @param modifiersArr {array?} array of modifiers
         * @returns {jQuery} modified jquery-element
         */
        makeBlockElement: function (elementName, $baseElement, modifiersArr) {
            var blockClass = this.block + BLOCK_SEPARATOR + elementName,
                modifiersClass = getElementClassWithModifiers(blockClass, modifiersArr)
            ;

            return $baseElement.addClass(blockClass).addClass(modifiersClass);
        },
        /**
         * Builds an BEM-styled jquery-element.
         *
         * Usage:
         * <pre><code>
         *     var exampleBEM = new BEM({
         *          block: "the-block",
         *          elements: {
         *              "some-element": "some-element-class"
         *          }
         *     });
         *     var $element = exampleBEM.buildBlockElement(
         *          "some-element", "&lt;div&gt;", {"name":"some-name"}, ["mod-1", "mod-2"]
         *     );
         *     // $element will be:
         *     &lt;div name="some-name" class="some-element-class some-element-class--mod-1 some-element-class--mod-2 the-block__some-element"&gt;&lt;/div&gt;
         * </code></pre>
         *
         * @param elementName {string}
         * @param tag {string}
         * @param attributes {object?}
         * @param modifiersArr {array?}
         * @returns {jQuery}
         *
         * @see buildElement
         */
        buildBlockElement: function (elementName, tag, attributes, modifiersArr) {
            return this.buildElement.apply(this, arguments).addClass(this.block + BLOCK_SEPARATOR + elementName);
        },
        /**
         * Builds an BEM-styled jquery-element without block__element class.
         *
         * Usage:
         * <pre><code>
         *     var exampleBEM = new BEM({
         *          block: "the-block",
         *          elements: {
         *              "some-element": "some-element-class"
         *          }
         *     });
         *     var $element = exampleBEM.buildElement(
         *          "some-element", "&lt;div&gt;", {"name":"some-name"}, ["mod-1", "mod-2"]
         *     );
         *     // $element will be:
         *     &lt;div name="some-name" class="some-element-class some-element-class--mod-1 some-element-class--mod-2"&gt;&lt;/div&gt;
         * </code></pre>
         *
         * @param elementName {string}
         * @param tag {string}
         * @param attributes {object?}
         * @param modifiersArr {array?}
         * @returns {jQuery}
         *
         * @see buildElement
         */
        buildElement: function (elementName, tag, attributes, modifiersArr) {
            var modifiersClass = getElementClassWithModifiers(this.elements[elementName], modifiersArr);

            attributes = $.extend({}, attributes);
            attributes["class"] = modifiersClass + getOriginClass(attributes);

            return $(tag, attributes);
        },
        /**
         * Builds block element.
         *
         * Usage:
         * <pre><code>
         *     var panelButtonsBEM = new BEM({
         *         block: "imcms-menu"
         *     });
         *
         *     var $block = panelButtonsBEM.buildBlock("&lt;div&gt;", [{"items": $("&lt;ul&gt;")}], {name: "some-name"});
         *
         *     // $block will be:<br/>
         *     &lt;div class="imcms-menu" name="some-name"&gt;
         *     &nbsp;&nbsp;&lt;ul class="imcms-menu__items"&gt;&lt;/ul&gt;
         *     &lt;/div&gt;
         *
         *     // or the same result with this:
         *     var $block = panelButtonsBEM.buildBlock("&lt;div&gt;", [$("&lt;ul&gt;")], {name: "some-name"}, "items");
         *
         * </code></pre>
         *
         * @param tag {string}
         * @param elements {array}
         * @param attributes {object?}
         * @param blockNameForEach {string?}
         * @returns {jQuery} build block with elements inside
         */
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
        /**
         * Builds block using elements from constructor.
         *
         * Usage:
         * <pre><code>
         *
         *     var $block = new BEM({
         *         block: "imcms-list",
         *         elements: {
         *             "imcms-list-item": [$("&lt;li&gt;"), $("&lt;li&gt;")]
         *         }
         *     }).buildBlockStructure("&lt;ul&gt;", {id: "the-id"});
         *
         *     // $block will be:<br/>
         *     &lt;ul id="the-id" class="imcms-list"&gt;
         *     &nbsp;&nbsp;&lt;li class="imcms-list__imcms-list-item"&gt;&lt;/li&gt;
         *     &nbsp;&nbsp;&lt;li class="imcms-list__imcms-list-item"&gt;&lt;/li&gt;
         *     &lt;/ul&gt;
         *
         * </code></pre>
         *
         * @param tag {string}
         * @param attributes {object?}
         * @returns {jQuery}
         */
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
