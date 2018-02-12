Imcms.define("imcms-keywords-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-tab-form-builder", "imcms-i18n-texts"
    ],
    function (BEM, components, tabContentBuilder, texts) {

        texts = texts.pageInfo.keywords;

        return {
            name: texts.name,
            data: {},
            tabIndex: null,
            isDocumentTypeSupported: function () {
                return true; // all supported
            },
            showTab: function () {
                tabContentBuilder.showTab(this.tabIndex);
            },
            hideTab: function () {
                tabContentBuilder.hideTab(this.tabIndex);
            },
            buildTab: function (index) {
                this.tabIndex = index;
                this.data.$keywordsBox = components.keywords.keywordsBox("<div>", {
                    "input-id": "keyword",
                    title: texts.title,
                    placeholder: texts.placeholder,
                    "button-text": "ADD+"
                });
                this.data.$searchDisableCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
                    id: "isSearchDisabled",
                    name: "isSearchDisabled",
                    text: texts.disableSearch
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
