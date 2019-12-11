define("imcms-appearance-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-choose-image-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", "imcms-documents-rest-api", "imcms-modal-window-builder"
    ],
    function (BEM, components, chooseImage, texts, $, PageInfoTab, documentRestApi, modal) {

        texts = texts.pageInfo.title;

        const pageInfoInnerStructureBEM = new BEM({
            block: "imcms-field",
            elements: {
                "checkboxes": "imcms-checkboxes",
                "text-box": "imcms-text-box",
                "text-area": "imcms-text-area",
                "choose-image": "imcms-choose-image",
                "select": "imcms-select",
                "title": "imcms-title",
                "button": "imcms-button",
                "item": ""
            }
        });

        const tabData = {};

        function buildDocumentAliasBlock() {
            const $aliasTitle = components.texts.titleText("<div>", texts.alias);

            const $aliasPrefix = components.texts.titleText("<div>", "/");

            tabData.$documentAlias = components.texts.textBox("<div>", {
                placeholder: texts.aliasPlaceholder
            });
            tabData.$documentAlias.modifiers = ["w-50"];

            const $suggestAliasButton = components.buttons.positiveButton({
                text: texts.makeSuggestion,
                click: () => writeSuggestedAliasToTextInput(tabData.$documentAlias),
            });
            $suggestAliasButton.modifiers = ["ml-auto"];

            const $aliasRow = new BEM({
                block: "imcms-field",
                elements: {
                    "item": [$aliasPrefix, tabData.$documentAlias, $suggestAliasButton],
                }
            }).buildBlockStructure('<div>', {
                class: "imcms-field__item--row imcms-field__item--align-items-baseline"
            });

            return pageInfoInnerStructureBEM.buildBlock("<div>", [
                {"title": $aliasTitle},
                {"item": $aliasRow},
            ]);
        }

        function writeSuggestedAliasToTextInput(textInput) {
            const content = tabData.commonContents.find(element => element.name === "English")
                || tabData.commonContents[0];

            const title = content.pageTitle.getValue()
                .trim()
                .toLowerCase()
                .replace(/[äå]/g, "a")
                .replace(/ö/g, "o")
                .replace(/é/g, "e")
                .replace(/ +/g, "-")
                .replace(/[^\w\-]+/g, "");

            documentRestApi.getUniqueAlias(title).done(uniqueAlias => {
                if (textInput.getValue()) {
                    modal.buildConfirmWindow(
                        texts.confirmOverwritingAlias,
                        () => textInput.setValue(uniqueAlias)
                    );
                } else {
                    textInput.setValue(uniqueAlias);
                }
            });
        }

        function buildHorizontalLine() {
            return $("<hr/>");
        }

        function buildCommonContents(commonContents) {
            tabData.commonContents = [];

            return commonContents.map(buildDocumentCommonContent).reduce((cc1, cc2) => cc1.concat(cc2));
        }

        function buildDocumentCommonContent(commonContent) {
            const $checkbox = components.checkboxes.imcmsCheckbox("<div>", {
                    name: commonContent.language.name.toLowerCase(), // fixme: or native name?
                    text: commonContent.language.name,
                    checked: commonContent.enabled ? "checked" : undefined
                }),
                $checkboxWrapper = components.checkboxes.checkboxContainer("<div>", [$checkbox]),
                $checkboxContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"checkboxes": $checkboxWrapper}]),
                $pageTitle = components.texts.textBox("<div>", {
                    name: "title",
                    text: texts.title,
                    value: commonContent.docId ? commonContent.headline : ''
                }),
                $pageTitleContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"text-box": $pageTitle}]),
                $menuText = components.texts.textArea("<div>", {
                    text: texts.menuText,
                    html: commonContent.menuText,
                    name: "menu-text"
                }),
                $menuTextContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"text-area": $menuText}]);

            tabData.commonContents = tabData.commonContents || [];

            tabData.commonContents.push({
                name: commonContent.language.name,
                checkbox: $checkbox,
                pageTitle: $pageTitle,
                menuText: $menuText
            });

            return [$checkboxContainer, $pageTitleContainer, $menuTextContainer];
        }

        function buildCommonContentsContainer() {
            return tabData.$commonContentsContainer = $('<div>');
        }

        function buildSelectTargetForDocumentLink() {
            tabData.$showIn = components.selects.imcmsSelect("<div>", {
                id: "show-in",
                text: texts.showIn,
                name: "show-in"
            }, [{
                text: texts.sameFrame,
                "data-value": "_self"
            }, {
                text: texts.newWindow,
                "data-value": "_blank"
            }, {
                text: texts.replaceAll,
                "data-value": "_top"
            }]);

            return pageInfoInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$showIn}]);
        }

        function buildBlockForMissingLangSetting() {
            const $languagesTitle = components.texts.titleText("<div>", texts.missingLangRuleTitle);

            tabData.$showDefaultLang = components.radios.imcmsRadio("<div>", {
                text: texts.showInDefault,
                name: "langSetting",
                value: "SHOW_IN_DEFAULT_LANGUAGE",
                checked: "checked" // default value
            });
            tabData.$doNotShow = components.radios.imcmsRadio("<div>", {
                text: texts.doNotShow,
                name: "langSetting",
                value: "DO_NOT_SHOW"
            });

            return pageInfoInnerStructureBEM.buildBlock("<div>", [
                {"title": $languagesTitle},
                {"item": tabData.$showDefaultLang},
                {"item": tabData.$doNotShow}
            ]);
        }

        const AppearanceTab = function (name) {
            PageInfoTab.call(this, name);
        };

        AppearanceTab.prototype = Object.create(PageInfoTab.prototype);

        AppearanceTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };
        AppearanceTab.prototype.tabElementsFactory = () => [
            buildDocumentAliasBlock(),
            buildHorizontalLine(),
            buildCommonContentsContainer(),
            buildHorizontalLine(),
            buildSelectTargetForDocumentLink(),
            buildBlockForMissingLangSetting()
        ];
        AppearanceTab.prototype.fillTabDataFromDocument = document => {
            tabData.$commonContentsContainer.prepend(buildCommonContents(document.commonContents));
            tabData.$showIn.selectValue(document.target);
            tabData.$documentAlias.setValue(document.alias);

            components.radios.group(tabData.$showDefaultLang, tabData.$doNotShow)
                .setCheckedValue(document.disabledLanguageShowMode);
        };
        AppearanceTab.prototype.saveData = documentDTO => {
            documentDTO.commonContents.forEach(docCommonContent => {
                tabData.commonContents.forEach(commonContent => {

                    if (docCommonContent.language.name !== commonContent.name) {
                        // I can't come up with better solution than double forEach
                        return;
                    }

                    docCommonContent.enabled = commonContent.checkbox.isChecked();
                    docCommonContent.headline = commonContent.pageTitle.getValue();
                    docCommonContent.menuText = commonContent.menuText.getValue();
                });
            });

            documentDTO.alias = tabData.$documentAlias.getValue();
            documentDTO.target = tabData.$showIn.getSelectedValue();

            documentDTO.disabledLanguageShowMode = components.radios
                .group(tabData.$showDefaultLang, tabData.$doNotShow)
                .getCheckedValue();

            return documentDTO;
        };
        AppearanceTab.prototype.clearTabData = () => {
            const emptyString = '';

            tabData.commonContents.forEach((commonContent, index) => {
                commonContent.checkbox.setChecked(index === 0);//check only first
                commonContent.pageTitle.setValue(emptyString);
                commonContent.menuText.setValue(emptyString);
            });

            tabData.$commonContentsContainer.empty();

            tabData.$showIn.selectFirst();
            tabData.$documentAlias.setValue(emptyString);
            tabData.$showDefaultLang.setChecked(true); //default value
        };
        AppearanceTab.prototype.isValid = () => tabData.commonContents.reduce((isChecked, commonContent) => isChecked || commonContent.checkbox.isChecked(), false);

        return new AppearanceTab(texts.name);
    }
);
