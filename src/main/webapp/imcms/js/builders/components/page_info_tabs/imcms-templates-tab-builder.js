define("imcms-templates-tab-builder",
    [
        "jquery","imcms-bem-builder", "imcms-components-builder", "imcms-templates-rest-api", "imcms-document-types",
        "imcms-i18n-texts", "imcms-page-info-tab", "imcms-templates-css-rest-api", "imcms-templates-css-versions"
    ],
	function ($, BEM, components, templatesRestApi, docTypes, texts, PageInfoTab, templatesCSSRestApi, templatesCSSVersions) {

        texts = texts.pageInfo.appearance;

        const tabData = {};

        const TemplatesTab = function (name, docType) {
            PageInfoTab.apply(this, arguments);
        };

        TemplatesTab.prototype = Object.create(PageInfoTab.prototype);

        TemplatesTab.prototype.tabElementsFactory = () => {
            const $templateSelectContainer = components.selects.selectContainer("<div>", {
                    name: "template",
                    text: texts.template
                }),
                $templateSelect = $templateSelectContainer.getSelect(),

                $defaultChildTemplateSelectContainer = components.selects.selectContainer("<div>", {
                    name: "childTemplate",
                    text: texts.defaultChildTemplate
                }),
                $defaultChildTemplateSelect = $defaultChildTemplateSelectContainer.getSelect(),

	            $previewTemplateCSSBtn = components.buttons.neutralButton( {
		            text: texts.previewTemplateCSSBtnText,
		            click: onPreviewTemplateCSSBtnClick
	            }),
	            $descriptionIcon = $('<div class="imcms-control imcms-control--edit imcms-control--info">'),
	            $previewTemplateCSSContainer = new BEM({
		            block: 'preview-template-css-container',
		            elements: {
			            'preview-btn': $previewTemplateCSSBtn,
			            'description-icon': $descriptionIcon
		            }
	            }).buildBlockStructure('<div>', {});

			components.overlays.defaultTooltip($descriptionIcon, texts.previewTemplateCSSBtnInfo)

            tabData.$templateSelect = $templateSelect;
            tabData.$defaultChildTemplateSelect = $defaultChildTemplateSelect;

            templatesRestApi.read(null)
                .done(templates => {
                    const templatesDataMapped = templates.map(template => ({
                        text: template.name,
                        "data-value": template.name
                    }));

                    components.selects.addOptionsToSelect(templatesDataMapped, $templateSelect);
                    components.selects.addOptionsToSelect(templatesDataMapped, $defaultChildTemplateSelect);
                });

            return [
                $templateSelectContainer,
                $defaultChildTemplateSelectContainer,
	            $previewTemplateCSSContainer
            ];
        };

		function onPreviewTemplateCSSBtnClick() {
			const id = "templateCSS";
			let templateCSS = document.getElementById(id);

			if (!templateCSS) {
				templateCSS = document.createElement('style');
				templateCSS.id = id;
			}

			templatesCSSRestApi.get(tabData.$templateSelect.getSelectedValue(), templatesCSSVersions.WORKING)
				.done((data) => {
					templateCSS.innerHTML = data;
					document.head.appendChild(templateCSS);
				})
		}

        TemplatesTab.prototype.fillTabDataFromDocument = document => {
            if (document.template) {
                //prevent value selection while building tab
                const interval = setInterval(() => {
                    if (tabData.$templateSelect.hasOptions()) {
                        tabData.$templateSelect.selectValue(document.template.templateName);
                        tabData.$defaultChildTemplateSelect.selectValue(document.template.childrenTemplateName);
                        clearInterval(interval);
                    }
                }, 10);
            }
        };

        TemplatesTab.prototype.saveData = function (documentDTO) {
            if (!this.isDocumentTypeSupported(documentDTO.type)) {
                return documentDTO;
            }

            documentDTO.template = {
                templateName: tabData.$templateSelect.getSelectedValue(),
                childrenTemplateName: tabData.$defaultChildTemplateSelect.getSelectedValue()
            };

            return documentDTO;
        };
        TemplatesTab.prototype.clearTabData = () => {
            tabData.$templateSelect.selectFirst();
            tabData.$defaultChildTemplateSelect.selectFirst();
        };

        TemplatesTab.prototype.getDocLink = () => texts.documentationLink;

        return new TemplatesTab(texts.name, docTypes.TEXT);
    }
);
