/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-buttons-builder", ["imcms-bem-builder"], function (bemBuilder) {
    var buttonsBEM = new bemBuilder({
        block: "imcms-buttons",
        elements: {
            "button": "imcms-button"
        }
    });

    function buildButtonElement(tag, attributes, modifier) {
        return buttonsBEM.buildElement("button", tag, attributes, [modifier]);
    }

    return {
        imcmsButton: function (attributes, modifiers) {
            return buttonsBEM.buildElement("button", "<button>", attributes, modifiers);
        },
        negative: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "negative");
        },
        positive: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "positive");
        },
        neutral: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "neutral");
        },
        save: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "save");
        },
        close: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "close");
        },
        increment: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "increment");
        },
        decrement: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "decrement");
        },
        prev: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "prev");
        },
        next: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "next");
        },
        dropDown: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "drop-down");
        },
        search: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "search");
        },
        proportions: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "proportions");
        },
        zoomPlus: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "zoom-plus");
        },
        zoomMinus: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "zoom-minus");
        },
        zoomContain: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "zoom-contain");
        },
        rotateLeft: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "rotate-left");
        },
        rotateRight: function (tag, attributes) {
            return buildButtonElement(tag, attributes, "rotate-right");
        },
        negativeButton: function (attributes) {
            return this.negative("<button>", attributes);
        },
        positiveButton: function (attributes) {
            return this.positive("<button>", attributes);
        },
        neutralButton: function (attributes) {
            return this.neutral("<button>", attributes);
        },
        saveButton: function (attributes) {
            return this.save("<button>", attributes);
        },
        closeButton: function (attributes) {
            return this.close("<button>", attributes);
        },
        incrementButton: function (attributes) {
            return this.increment("<button>", attributes);
        },
        decrementButton: function (attributes) {
            return this.decrement("<button>", attributes);
        },
        prevButton: function (attributes) {
            return this.prev("<button>", attributes);
        },
        nextButton: function (attributes) {
            return this.next("<button>", attributes);
        },
        dropDownButton: function (attributes) {
            return this.dropDown("<button>", attributes);
        },
        searchButton: function (attributes) {
            return this.search("<button>", attributes);
        },
        proportionsButton: function (attributes) {
            return this.proportions("<button>", attributes);
        },
        zoomPlusButton: function (attributes) {
            return this.zoomPlus("<button>", attributes);
        },
        zoomMinusButton: function (attributes) {
            return this.zoomMinus("<button>", attributes);
        },
        zoomContainButton: function (attributes) {
            return this.zoomContain("<button>", attributes);
        },
        rotateLeftButton: function (attributes) {
            return this.rotateLeft("<button>", attributes);
        },
        rotateRightButton: function (attributes) {
            return this.rotateRight("<button>", attributes);
        },
        buttonsContainer: function (tag, elements, attributes) {
            return buttonsBEM.buildBlock(tag, elements, attributes, "button");
        }
    }
});
