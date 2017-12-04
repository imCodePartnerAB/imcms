Imcms.define("imcms-appearance-tab-builder",
    ["imcms-bem-builder", "imcms-components-builder", "imcms-page-info-tab-form-builder", "imcms-choose-image-builder"],
    function (BEM, components, tabFormBuilder, chooseImage) {
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

        function buildCommonContents(commonContents) {
            tabData.commonContents = [];

            return commonContents.map(buildDocumentCommonContent).reduce(function (cc1, cc2) {
                return cc1.concat(cc2);
            });
        }

        function buildDocumentCommonContent(commonContent) {
            var $checkbox = components.checkboxes.imcmsCheckbox("<div>", {
                    name: commonContent.language.name.toLowerCase(), // fixme: or native name?
                    text: commonContent.language.name,
                    checked: commonContent.enabled ? "checked" : undefined
                }),
                $checkboxWrapper = components.checkboxes.checkboxContainer("<div>", [$checkbox]),
                $checkboxContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"checkboxes": $checkboxWrapper}]),
                $pageTitle = components.texts.textBox("<div>", {
                    name: "title",
                    text: "Title",
                    value: commonContent.headline
                }),
                $pageTitleContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"text-box": $pageTitle}]),
                $menuText = components.texts.textArea("<div>", {
                    text: "Menu text",
                    html: commonContent.menuText,
                    name: "menu-text"
                }),
                $menuTextContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"text-area": $menuText}]),
                $linkToImage = chooseImage.container("<div>", {
                    id: "path-to-image",
                    name: "image",
                    value: commonContent.menuImageURL,
                    placeholder: "Image path",
                    "label-text": "Link to image",
                    "button-text": "choose..."
                }),
                $linkToImageContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"choose-image": $linkToImage}]);

            tabData.commonContents = tabData.commonContents || [];

            tabData.commonContents.push({
                name: commonContent.language.name,
                checkbox: $checkbox,
                pageTitle: $pageTitle,
                menuText: $menuText,
                linkToImage: $linkToImage
            });

            return [$checkboxContainer, $pageTitleContainer, $menuTextContainer, $linkToImageContainer];
        }

        return {

            name: "appearance",

            buildTab: function (index) {
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

                var $showInContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$showIn}]);

                tabData.$documentAlias = components.texts.textBox("<div>", {
                    name: "alias",
                    text: "Document alias",
                    placeholder: "this-doc-alias"
                });

                var $documentAliasContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [
                    {"text-box": tabData.$documentAlias}
                ]);

                var tabElements = [$showInContainer, $documentAliasContainer];

                var $result = tabFormBuilder.buildFormBlock(tabElements, index);
                tabData.$result = $result;

                return $result;
            },

            fillTabDataFromDocument: function (document) {
                if (tabData.commonContentElements) {
                    tabData.commonContentElements.forEach(function ($element) {
                        $element.detach();
                    });
                }

                tabData.commonContentElements = buildCommonContents(document.commonContents);
                tabData.$result.prepend(tabData.commonContentElements);
                tabData.$showIn.selectValue(document.target);
                tabData.$documentAlias.setValue(document.alias);
            },

            saveData: function (documentDTO) {
                documentDTO.commonContents.forEach(function (docCommonContent) {
                    tabData.commonContents.forEach(function (commonContent) {

                        if (docCommonContent.language.name !== commonContent.name) {
                            // I can't come up with better solution than double forEach
                            return;
                        }

                        docCommonContent.enabled = commonContent.checkbox.isChecked();
                        docCommonContent.headline = commonContent.pageTitle.getValue();
                        docCommonContent.menuText = commonContent.menuText.getValue();
                        docCommonContent.menuImageURL = commonContent.linkToImage.getValue();
                    });
                });

                documentDTO.alias = tabData.$documentAlias.getValue();
                documentDTO.target = tabData.$showIn.getSelectedValue();

                return documentDTO;
            },

            clearTabData: function () {
                var emptyString = '';

                tabData.commonContents.forEach(function (commonContent, index) {
                    commonContent.checkbox.setChecked(index === 0);//check only first
                    commonContent.pageTitle.setValue(emptyString);
                    commonContent.menuText.setValue(emptyString);
                    commonContent.linkToImage.setValue(emptyString);
                });

                tabData.$showIn.selectFirst();
                tabData.$documentAlias.setValue(emptyString);
            }
        };
    }
);
