Imcms.define("imcms-page-info-tab-form-builder", ["imcms-bem-builder", "jquery"], function (BEM, $) {

    var formsBEM = new BEM({
        block: "imcms-form",
        elements: {"field": "imcms-field"}
    });

    function setDisplay(tabIndex, displayValue) {
        $(".imcms-tabs__tab[data-window-id=" + tabIndex + "]").css("display", displayValue);
    }

    return {
        showTab: function (tabIndex) {
            setDisplay(tabIndex, "block");
        },
        hideTab: function (tabIndex) {
            setDisplay(tabIndex, "none");
        },
        buildFormBlock: function (elements, index) {
            return formsBEM.buildBlock("<div>", elements, {"data-window-id": index}, "field");
        }
    };
});
