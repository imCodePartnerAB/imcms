Imcms.define("imcms-appearance-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-page-info-tabs-linker", "imcms-choose-image-builder"
    ],
    function (BEM, components, linker, chooseImage) {

        var tabData = {};

        return {
            name: "appearance",
            buildTab: function (index) {
                var pageInfoInnerStructureBEM = new BEM({
                    block: "imcms-field",
                    elements: {
                        "checkboxes": "imcms-checkboxes",
                        "text-box": "imcms-text-box",
                        "text-area": "imcms-text-area",
                        "choose-image": "imcms-choose-image",
                        "select": "imcms-select"
                    }
                });

                tabData.$engCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
                    name: "english",
                    text: "English",
                    checked: "checked"
                });

                var $engCheckboxWrapper = components.checkboxes.checkboxContainer("<div>", [
                    tabData.$engCheckbox
                ]);
                var $engCheckboxContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "checkboxes": $engCheckboxWrapper
                }]);

                tabData.$engPageTitle = components.texts.textBox("<div>", {
                    name: "title",
                    text: "Title",
                    placeholder: "Start page"
                });

                var $pageTitleContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"text-box": tabData.$engPageTitle}
                ]);

                tabData.$engMenuText = components.texts.textArea("<div>", {
                    text: "Menu text",
                    name: "menu-text"
                });

                var $menuTextContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"text-area": tabData.$engMenuText}
                ]);

                tabData.$engLinkToImage = chooseImage.container("<div>", {
                    id: "path-to-image",
                    name: "image",
                    placeholder: "Image path",
                    "label-text": "Link to image",
                    "button-text": "choose..."
                });

                var $linkToImageContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "choose-image": tabData.$engLinkToImage
                }]);

                tabData.$sweCheckbox = components.checkboxes.imcmsCheckbox("<div>", {
                    name: "swedish",
                    text: "Swedish"
                });

                var $sweCheckboxWrapper = components.checkboxes.checkboxContainer("<div>", [
                        tabData.$sweCheckbox
                    ]),
                    $sweCheckboxContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                        "checkboxes": $sweCheckboxWrapper
                    }]);

                tabData.$swePageTitle = components.texts.textBox("<div>", {
                    name: "title",
                    text: "Title",
                    placeholder: "Startsida"
                });

                var $pageTitleSweContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "text-box": tabData.$swePageTitle
                }]);

                tabData.$sweMenuText = components.texts.textArea("<div>", {
                    text: "Menu text",
                    name: "menu-text"
                });

                var $menuTextSweContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "text-area": tabData.$sweMenuText
                }]);

                tabData.$linkToImageSwe = chooseImage.container("<div>", {
                    id: "path-to-image-swe",
                    name: "image",
                    placeholder: "Image path",
                    "label-text": "Link to image",
                    "button-text": "choose..."
                });

                var $linkToImageSweContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "choose-image": tabData.$linkToImageSwe
                }]);

                tabData.$showIn = components.selects.imcmsSelect("<div>", {
                    id: "show-in",
                    text: "Show in",
                    name: "show-in"
                }, [{
                    text: "Same frame",
                    "data-value": "_self"
                }, {
                    text: "New window",
                    "data-value": "_blank"
                }, {
                    text: "Replace all",
                    "data-value": "_top"
                }]);

                var $showInContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"select": tabData.$showIn}
                ]);

                tabData.$documentAlias = components.texts.textBox("<div>", {
                    name: "alias",
                    text: "Document alias",
                    placeholder: "this-doc-alias"
                });

                var $documentAliasContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"text-box": tabData.$documentAlias}
                ]);

                var tabElements = [
                    $engCheckboxContainer,
                    $pageTitleContainer,
                    $menuTextContainer,
                    $linkToImageContainer,
                    $sweCheckboxContainer,
                    $pageTitleSweContainer,
                    $menuTextSweContainer,
                    $linkToImageSweContainer,
                    $showInContainer,
                    $documentAliasContainer
                ];

                return linker.buildFormBlock(tabElements, index);
            },
            fillTabDataFromDocument: function (document) {
                var englishLanguage = document.languages["eng"],
                    swedishLanguage = document.languages["swe"];

                tabData.$engCheckbox.setLabelText(englishLanguage.name).setChecked(englishLanguage.enabled);
                tabData.$engPageTitle.setValue(englishLanguage.title);
                tabData.$engMenuText.setValue(englishLanguage.menu_text);

                tabData.$sweCheckbox.setLabelText(swedishLanguage.name).setChecked(swedishLanguage.enabled);
                tabData.$swePageTitle.setValue(swedishLanguage.title);
                tabData.$sweMenuText.setValue(swedishLanguage.menu_text);

                tabData.$showIn.selectValue(document.show_in);
                tabData.$documentAlias.setValue(document.alias);
            },
            clearTabData: function () {
                var emptyString = '';

                tabData.$engCheckbox.setChecked(true);
                tabData.$engPageTitle.setValue(emptyString);
                tabData.$engMenuText.setValue(emptyString);

                tabData.$sweCheckbox.setChecked(false);
                tabData.$swePageTitle.setValue(emptyString);
                tabData.$sweMenuText.setValue(emptyString);

                tabData.$showIn.selectFirst();
                tabData.$documentAlias.setValue(emptyString);
            }
        };
    }
);
