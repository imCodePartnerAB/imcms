define("imcms-templates-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-templates-rest-api", "imcms-document-types",
        "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (BEM, components, templatesRestApi, docTypes, texts, PageInfoTab) {

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
                $defaultChildTemplateSelect = $defaultChildTemplateSelectContainer.getSelect();

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
                $defaultChildTemplateSelectContainer
            ];
        };
        TemplatesTab.prototype.fillTabDataFromDocument = document => {
            if (document.template) {
                tabData.$templateSelect.selectValue(document.template.templateName);
                tabData.$defaultChildTemplateSelect.selectValue(document.template.childrenTemplateName);
            }
        };
        TemplatesTab.prototype.saveData = function (documentDTO) {
            if (!this.isDocumentTypeSupported(documentDTO.type)) {
                return documentDTO;
            }

            documentDTO.template.templateName = tabData.$templateSelect.getSelectedValue();
            documentDTO.template.childrenTemplateName = tabData.$defaultChildTemplateSelect.getSelectedValue();

            return documentDTO;
        };
        TemplatesTab.prototype.clearTabData = () => {
            tabData.$templateSelect.selectFirst();
            tabData.$defaultChildTemplateSelect.selectFirst();
        };

        return new TemplatesTab(texts.name, docTypes.TEXT);
    }
);
