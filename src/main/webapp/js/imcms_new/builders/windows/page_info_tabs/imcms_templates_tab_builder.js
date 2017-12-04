Imcms.define("imcms-templates-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-templates-rest-api",
        "imcms-page-info-tab-form-builder"
    ],
    function (BEM, components, templatesRestApi, tabContentBuilder) {

        return {
            name: "templates",
            data: {},
            buildTab: function (index) {
                var $templateSelectContainer = components.selects.selectContainer("<div>", {
                        name: "template",
                        text: "Template"
                    }),
                    $templateSelect = $templateSelectContainer.getSelect(),

                    $defaultChildTemplateSelectContainer = components.selects.selectContainer("<div>", {
                        name: "childTemplate",
                        text: "Default child template"
                    }),
                    $defaultChildTemplateSelect = $defaultChildTemplateSelectContainer.getSelect();

                this.data.$templateSelect = $templateSelect;
                this.data.$defaultChildTemplateSelect = $defaultChildTemplateSelect;

                templatesRestApi.read(null)
                    .done(function (templates) {
                        var templatesDataMapped = templates.map(function (template) {
                            return {
                                text: template.name,
                                "data-value": template.name
                            }
                        });

                        components.selects.addOptionsToSelect(templatesDataMapped, $templateSelect);
                        components.selects.addOptionsToSelect(templatesDataMapped, $defaultChildTemplateSelect);
                    });

                var blockElements = [
                    $templateSelectContainer,
                    $defaultChildTemplateSelectContainer
                ];
                return tabContentBuilder.buildFormBlock(blockElements, index);
            },
            fillTabDataFromDocument: function (document) {
                if (document.template) {
                    this.data.$templateSelect.selectValue(document.template.templateName);
                    this.data.$defaultChildTemplateSelect.selectValue(document.template.childrenTemplateName);
                }
            },
            saveData: function (documentDTO) {
                if (!documentDTO.template) {
                    return documentDTO;
                }

                documentDTO.template.templateName = this.data.$templateSelect.getSelectedValue();
                documentDTO.template.childrenTemplateName = this.data.$defaultChildTemplateSelect.getSelectedValue();

                return documentDTO;
            },
            clearTabData: function () {
                this.data.$templateSelect.selectFirst();
                this.data.$defaultChildTemplateSelect.selectFirst();
            }
        };
    }
);
