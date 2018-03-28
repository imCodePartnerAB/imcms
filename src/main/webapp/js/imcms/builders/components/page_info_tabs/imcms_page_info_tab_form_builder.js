Imcms.define("imcms-page-info-tab-form-builder", ["imcms-bem-builder", "jquery"], function (BEM, $) {

    var formsBEM = new BEM({
        block: "imcms-form",
        elements: {"field": "imcms-field"}
    });

    return {
        showTab: function (tabIndex) {
            $(".imcms-tabs__tab[data-window-id=" + tabIndex + "]").css("display", "block");
        },
        hideTab: function (tabIndex) {
            $(".imcms-tabs__tab[data-window-id=" + tabIndex + "]").css("display", "none");
        },
        buildFormBlock: function (elements, index) {
            return formsBEM.buildBlock("<div>", elements, {"data-window-id": index}, "field");
        }
    };
});
