Imcms.define("imcms-page-info-tabs-linker", ["imcms-bem-builder"], function (BEM) {

    var formsBEM = new BEM({
        block: "imcms-form",
        elements: {"field": "imcms-field"}
    });

    return {
        buildFormBlock: function (elements, index) {
            return formsBEM.buildBlock("<div>", elements, {"data-window-id": index}, "field");
        }
    };
});
