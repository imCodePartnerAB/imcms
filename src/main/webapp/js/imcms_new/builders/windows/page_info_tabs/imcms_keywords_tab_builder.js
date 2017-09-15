Imcms.define("imcms-keywords-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder",
        "imcms-page-info-tabs-linker"
    ],
    function (BEM, components, linker) {

        return {
            name: "keywords",
            data: {},
            buildTab: function (index) {
                this.data.$keywordsBox = components.keywords.keywordsBox("<div>", {
                    "input-id": "keyword",
                    title: "Keywords",
                    placeholder: "keyword",
                    "button-text": "ADD+"
                });
                this.data.$searchDisableCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
                    id: "isSearchDisabled",
                    name: "isSearchDisabled",
                    text: "Disable search"
                });
                var $checkboxField = components.checkboxes.checkboxContainerField("<div>", [
                    this.data.$searchDisableCheckbox
                ]);

                return linker.buildFormBlock([this.data.$keywordsBox, $checkboxField], index);
            },
            fillTabDataFromDocument: function (document) {
                var keywordsTab = this.data;

                document.keywords.forEach(keywordsTab.$keywordsBox.addKeyword);

                keywordsTab.$searchDisableCheckbox.setChecked(document.disable_search);

            },
            clearTabData: function () {
                var keywordsTab = this.data;

                keywordsTab.$keywordsBox
                    .find('.imcms-keyword__keywords')
                    .find('.imcms-button--close')
                    .click();

                keywordsTab.$searchDisableCheckbox.setChecked(false);
            }
        };
    }
);
