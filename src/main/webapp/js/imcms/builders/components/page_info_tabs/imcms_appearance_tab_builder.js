Imcms.define("imcms-appearance-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-templates-rest-api", "imcms-document-types",
        "imcms-page-info-tab-form-builder", "imcms-i18n-texts"
    ],
    function (BEM, components, templatesRestApi, docTypes, tabContentBuilder, texts) {

        texts = texts.pageInfo.appearance;

        return {
            name: texts.name,
            data: {},
            tabIndex: null,
            isDocumentTypeSupported: function (docType) {
                return docType === docTypes.TEXT;
            },
            showTab: function () {
                tabContentBuilder.showTab(this.tabIndex);
            },
            hideTab: function () {
                tabContentBuilder.hideTab(this.tabIndex);
            },
            buildTab: function (index) {
                this.tabIndex = index;
                var $templateSelectContainer = components.selects.selectContainer("<div>", {
                        name: "template",
                        text: texts.template
                    }),
                    $templateSelect = $templateSelectContainer.getSelect(),

                    $defaultChildTemplateSelectContainer = components.selects.selectContainer("<div>", {
                        name: "childTemplate",
                        text: texts.defaultChildTemplate
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
                if (!this.isDocumentTypeSupported(documentDTO.type)) {
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
