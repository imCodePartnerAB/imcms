Imcms.define("imcms-appearance-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder",
        "imcms-page-info-tabs-linker", "imcms-choose-image-builder",
        "imcms-languages-rest-api"
    ],
    function (BEM, components, linker, chooseImage, languagesRestApi) {
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

        var tabData = {};

        function mapLanguages(languages) {
            return languages.map(buildDocumentLanguage)
                .reduce(function (l1, l2) {
                    return l1.concat(l2);
                });
        }

        function buildDocumentLanguage(language) {
            var $checkbox = components.checkboxes.imcmsCheckbox("<div>", {
                    name: language.name.toLowerCase(),
                    text: language.name,
                    checked: language.enabled ? "checked" : undefined
                }),
                $checkboxWrapper = components.checkboxes.checkboxContainer("<div>", [
                    $checkbox
                ]),
                $checkboxContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "checkboxes": $checkboxWrapper
                }]),
                $pageTitle = components.texts.textBox("<div>", {
                    name: "title",
                    text: "Title",
                    value: language.title,
                    placeholder: language.title
                }),
                $pageTitleContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"text-box": $pageTitle}
                ]),
                $menuText = components.texts.textArea("<div>", {
                    text: "Menu text",
                    value: language.menuText,
                    name: "menu-text"
                }),
                $menuTextContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"text-area": $menuText}
                ]),
                $linkToImage = chooseImage.container("<div>", {
                    id: "path-to-image",
                    name: "image",
                    placeholder: "Image path",
                    "label-text": "Link to image",
                    "button-text": "choose..."
                }),
                $linkToImageContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{
                    "choose-image": $linkToImage
                }]);

            tabData.languages = tabData.languages || [];

            tabData.languages.push({
                checkbox: $checkbox,
                pageTitle: $pageTitle,
                menuText: $menuText,
                linkToImage: $linkToImage
            });

            return [$checkboxContainer, $pageTitleContainer, $menuTextContainer, $linkToImageContainer]
        }

        return {
            name: "appearance",
            buildTab: function (index, docId) {
                if (!docId) {
                    languagesRestApi.read()
                        .done(function (languages) {
                            if (!tabData.languages) {
                                var documentLanguages = mapLanguages(languages);
                                tabData.$result.prepend(documentLanguages);
                            }
                        });
                }

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
                    $showInContainer,
                    $documentAliasContainer
                ];

                var $result = linker.buildFormBlock(tabElements, index);
                tabData.$result = $result;
                return $result;
            },
            fillTabDataFromDocument: function (document) {
                if (tabData.languages) {
                    tabData.languages.forEach(function (language, index) {
                        var documentLanguage = document.languages[index];
                        language.checkbox.setChecked(documentLanguage.enabled);
                        language.pageTitle.setValue(documentLanguage.title);
                        language.menuText.setValue(documentLanguage.menuText);
                    });
                } else {
                    var documentLanguages = mapLanguages(document.languages);
                    tabData.$result.prepend(documentLanguages);
                }

                tabData.$showIn.selectValue(document.target);
                tabData.$documentAlias.setValue(document.alias);
            },
            clearTabData: function () {
                var emptyString = '';

                tabData.languages.forEach(function (language, index) {
                    language.checkbox.setChecked(index === 0);//check only first
                    language.pageTitle.setValue(emptyString);
                    language.menuText.setValue(emptyString);
                    language.linkToImage.setValue(emptyString);
                });

                tabData.$showIn.selectFirst();
                tabData.$documentAlias.setValue(emptyString);
            }
        };
    }
);
