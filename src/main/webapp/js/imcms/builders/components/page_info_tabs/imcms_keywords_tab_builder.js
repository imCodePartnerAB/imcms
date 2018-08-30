define("imcms-keywords-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (BEM, components, texts, PageInfoTab) {

        texts = texts.pageInfo.keywords;

        var tabData = {};

        var KeywordsTab = function (name) {
            PageInfoTab.call(this, name);
        };

        KeywordsTab.prototype = Object.create(PageInfoTab.prototype);

        KeywordsTab.prototype.isDocumentTypeSupported = function () {
            return true; // all supported
        };
        KeywordsTab.prototype.tabElementsFactory = function () {
            tabData.$keywordsBox = components.keywords.keywordsBox("<div>", {
                "input-id": "keyword",
                title: texts.title,
                placeholder: texts.placeholder,
                "button-text": texts.add
            });
            tabData.$searchDisableCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
                id: "isSearchDisabled",
                name: "isSearchDisabled",
                text: texts.disableSearch
            });
            var $checkboxField = components.checkboxes.checkboxContainerField("<div>", [
                tabData.$searchDisableCheckbox
            ]);

            return [tabData.$keywordsBox, $checkboxField];
        };
        KeywordsTab.prototype.fillTabDataFromDocument = function (document) {
            document.keywords.forEach(tabData.$keywordsBox.addKeyword);
            tabData.$searchDisableCheckbox.setChecked(document.searchDisabled);
        };
        KeywordsTab.prototype.saveData = function (documentDTO) {
            documentDTO.keywords = tabData.$keywordsBox.getKeywords();
            documentDTO.searchDisabled = tabData.$searchDisableCheckbox.isChecked();
            return documentDTO;
        };
        KeywordsTab.prototype.clearTabData = function () {
            tabData.$keywordsBox.find('.imcms-keyword__keywords')
                .find('.imcms-button--close')
                .click();

            tabData.$searchDisableCheckbox.setChecked(false);
        };

        return new KeywordsTab(texts.name);
    }
);
