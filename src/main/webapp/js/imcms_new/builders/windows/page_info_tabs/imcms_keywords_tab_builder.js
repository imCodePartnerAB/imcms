Imcms.define("imcms-keywords-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder",
        "imcms-page-info-tab-form-builder"
    ],
    function (BEM, components, tabContentBuilder) {

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

                return tabContentBuilder.buildFormBlock([this.data.$keywordsBox, $checkboxField], index);
            },
            fillTabDataFromDocument: function (document) {
                document.keywords.forEach(this.data.$keywordsBox.addKeyword);
                this.data.$searchDisableCheckbox.setChecked(document.searchDisabled);
            },
            saveData: function (documentDTO) {
                documentDTO.keywords = this.data.$keywordsBox.getKeywords();
                documentDTO.searchDisabled = this.data.$searchDisableCheckbox.isChecked();
                return documentDTO;
            },
            clearTabData: function () {
                this.data.$keywordsBox.find('.imcms-keyword__keywords')
                    .find('.imcms-button--close')
                    .click();

                this.data.$searchDisableCheckbox.setChecked(false);
            }
        };
    }
);
