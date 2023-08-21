define("imcms-keywords-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (BEM, components, texts, PageInfoTab) {

        texts = texts.pageInfo.keywords;

        const tabData = {};

        const KeywordsTab = function (name) {
            PageInfoTab.call(this, name);
        };

        KeywordsTab.prototype = Object.create(PageInfoTab.prototype);

        KeywordsTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };
        KeywordsTab.prototype.tabElementsFactory = () => {
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
            const $checkboxField = components.checkboxes.checkboxContainerField("<div>", [
                tabData.$searchDisableCheckbox
            ]);

            return [tabData.$keywordsBox, $checkboxField];
        };
        KeywordsTab.prototype.fillTabDataFromDocument = document => {
            document.keywords.forEach(tabData.$keywordsBox.addKeyword);
            tabData.$searchDisableCheckbox.setChecked(document.searchDisabled);
        };
        KeywordsTab.prototype.saveData = documentDTO => {
            documentDTO.keywords = tabData.$keywordsBox.getKeywords();
            documentDTO.searchDisabled = tabData.$searchDisableCheckbox.isChecked();
            return documentDTO;
        };
        KeywordsTab.prototype.clearTabData = () => {
            tabData.$keywordsBox.find('.imcms-keyword__keywords')
                .find('.imcms-button--close')
                .click();

            tabData.$searchDisableCheckbox.setChecked(false);
        };

        KeywordsTab.prototype.getDocLink = () => texts.documentationLink;

        return new KeywordsTab(texts.name);
    }
);
