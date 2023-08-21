define("imcms-appearance-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-choose-image-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", "imcms-documents-rest-api", "imcms-modal-window-builder", "imcms"
    ],
    function (BEM, components, chooseImage, texts, $, PageInfoTab, documentRestApi, modal, imcms) {

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

        function buildDocumentAliasBlock(commonContent) {
	        const $aliasTitle = components.texts.titleText("<div>", texts.alias);

	        const $aliasPrefix = components.texts.titleText("<div>", "/", {
		        class: "imcms-flex--mb-0"
	        });
	        $aliasPrefix.css('line-height', '46px');

	        const $documentAlias = components.texts.textBox("<div>", {
		        value: commonContent.docId ? commonContent.alias : '',
		        placeholder: texts.aliasPlaceholder,
		        class: "imcms-flex--w-50"
	        });

	        const $suggestAliasButton = components.buttons.positiveButton({
		        text: texts.makeSuggestion,
		        click: () => writeSuggestedAliasToTextInput($documentAlias, commonContent),
		        class: "imcms-flex--ml-auto"
	        });

	        const $aliasRow = new BEM({
		        block: "imcms-field",
                elements: {
	                "item": [$aliasPrefix, $documentAlias, $suggestAliasButton],
                }
	        }).buildBlockStructure('<div>', {
		        class: "imcms-flex--d-flex imcms-flex--p-0"
	        });

	        const $aliasBlock = pageInfoInnerStructureBEM.buildBlock("<div>", [
		        {"title": $aliasTitle},
		        {"item": $aliasRow},
	        ]);

	        $.extend($aliasBlock, {
		        getValue: () => $documentAlias.getValue(),
		        setValue: (alias) => $documentAlias.setValue(alias)
	        })

	        return $aliasBlock;
        }

	    function writeSuggestedAliasToTextInput($documentAlias, commonContent) {
		    const $commonContent = tabData.commonContents.find(element => element.name === commonContent.language.name);
		    const content = $documentAlias.getValue() ? $documentAlias.getValue() : $commonContent.pageTitle.getValue();

		    const title = content
			    .trim()
			    .toLowerCase()
			    .replace(/[äå]/g, "a")
			    .replace(/ö/g, "o")
			    .replace(/é/g, "e")
			    .replace(/ +/g, "-")
			    .replace(/[^\w\-]+/g, "");

		    if (title) {
			    documentRestApi.getUniqueAlias(title).done(uniqueAlias => {
				    if ($documentAlias.getValue()) {
					    modal.buildConfirmWindow(
						    texts.confirmOverwritingAlias,
						    () => $documentAlias.setValue(uniqueAlias)
					    );
				    } else {
					    $documentAlias.setValue(uniqueAlias);
				    }
			    });
		    }
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
		        $checkboxContainer = pageInfoInnerStructureBEM.buildBlock("<div>",
					[{"checkboxes": $checkboxWrapper}], {
					class: 'common-content-language-checkbox'
				}),

		        $aliasBlock = buildDocumentAliasBlock(commonContent),
		        $pageTitle = components.texts.textBox("<div>", {
			        name: "title",
			        text: texts.title,
			        value: commonContent.docId ? commonContent.headline : ''
		        }),
		        $pageTitleContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"text-box": $pageTitle}]),
		        $menuText = components.texts.textArea("<div>", {
			        text: texts.menuText,
			        html: commonContent.docId ? commonContent.menuText : '',
			        name: "menu-text"
                }),
                $menuTextContainer = pageInfoInnerStructureBEM.buildBlock("<div>", [{"text-area": $menuText}]);

            tabData.commonContents = tabData.commonContents || [];

            tabData.commonContents.push({
	            name: commonContent.language.name,
	            checkbox: $checkbox,
	            alias: $aliasBlock,
	            pageTitle: $pageTitle,
	            menuText: $menuText
            });

			let $docCommonContent = [$checkboxContainer, $aliasBlock, $pageTitleContainer, $menuTextContainer, buildHorizontalLine()]
			if(!(imcms.availableLanguages.length > 1)){
				$docCommonContent.shift();
			}

	        return $docCommonContent;
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

	    function buildUseDefaultLanguageAliasForAllLanguagesBlock() {
		    const $useDefaultLanguageAliasForAllLanguages = components.checkboxes.imcmsCheckbox('<div>', {
			    text: texts.useDefaultLanguageAlias,
		    });

		    tabData.$useDefaultLanguageAliasForAllLanguages = $useDefaultLanguageAliasForAllLanguages;
		    return $('<div>').append($useDefaultLanguageAliasForAllLanguages);
	    }

	    const AppearanceTab = function (name) {
		    PageInfoTab.call(this, name);
	    };

	    AppearanceTab.prototype = Object.create(PageInfoTab.prototype);

	    AppearanceTab.prototype.isDocumentTypeSupported = () => {
		    return true; // all supported
	    };
		AppearanceTab.prototype.tabElementsFactory = () => {
			let $commonContentsContainer = buildCommonContentsContainer(),
				$selectTargetForDocumentLink = buildSelectTargetForDocumentLink(),
				$blockForMissingLangSetting = buildBlockForMissingLangSetting(),
				$horizontalLine = buildHorizontalLine(),
				$defaultLanguageAliasForAllLanguagesBlock = buildUseDefaultLanguageAliasForAllLanguagesBlock()

			if(!(imcms.availableLanguages.length > 1)){
				$blockForMissingLangSetting.hide();
				$horizontalLine.hide();
				$defaultLanguageAliasForAllLanguagesBlock.hide();
			}

			return [
				$commonContentsContainer,
				$selectTargetForDocumentLink,
				$blockForMissingLangSetting,
				$horizontalLine,
				$defaultLanguageAliasForAllLanguagesBlock
			];
		}
        AppearanceTab.prototype.fillTabDataFromDocument = document => {
	        tabData.$commonContentsContainer.prepend(buildCommonContents(document.commonContents));
	        tabData.$showIn.selectValue(document.target);
	        components.radios.group(tabData.$showDefaultLang, tabData.$doNotShow)
		        .setCheckedValue(document.disabledLanguageShowMode);
	        tabData.$useDefaultLanguageAliasForAllLanguages.setChecked(document.defaultLanguageAliasEnabled)
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
	                docCommonContent.alias = commonContent.alias.getValue();
	                docCommonContent.menuText = commonContent.menuText.getValue();
                });
            });

	        documentDTO.target = tabData.$showIn.getSelectedValue();
	        documentDTO.defaultLanguageAliasEnabled = tabData.$useDefaultLanguageAliasForAllLanguages.isChecked();
	        documentDTO.disabledLanguageShowMode = components.radios
		        .group(tabData.$showDefaultLang, tabData.$doNotShow)
		        .getCheckedValue();

            return documentDTO;
        };
        AppearanceTab.prototype.clearTabData = () => {
            const emptyString = '';

            tabData.commonContents.forEach((commonContent, index) => {
                commonContent.checkbox.setChecked(index === 0);//check only first
	            commonContent.alias.setValue(emptyString)
	            commonContent.pageTitle.setValue(emptyString);
                commonContent.menuText.setValue(emptyString);
            });

            tabData.$commonContentsContainer.empty();

            tabData.$showIn.selectFirst();
            tabData.$showDefaultLang.setChecked(true); //default value
        };
        AppearanceTab.prototype.isValid = () => tabData.commonContents.reduce((isChecked, commonContent) => isChecked || commonContent.checkbox.isChecked(), false);

		AppearanceTab.prototype.getDocLink = () => texts.documentationLink;

        return new AppearanceTab(texts.name);
    }
);
