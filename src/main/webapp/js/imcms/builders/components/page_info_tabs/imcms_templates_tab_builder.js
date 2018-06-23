Imcms.define("imcms-templates-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-templates-rest-api", "imcms-document-types",
        "imcms-i18n-texts", "imcms-window-tab"
    ],
    function (BEM, components, templatesRestApi, docTypes, texts, WindowTab) {

        texts = texts.pageInfo.appearance;

        var tabData = {};

        var TemplatesTab = function (name, docType) {
            WindowTab.apply(this, arguments);
        };

        TemplatesTab.prototype = Object.create(WindowTab.prototype);

        TemplatesTab.prototype.buildTab = function (index) {
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

            tabData.$templateSelect = $templateSelect;
            tabData.$defaultChildTemplateSelect = $defaultChildTemplateSelect;

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
            return this.tabFormBuilder.buildFormBlock(blockElements, index);
        };
        TemplatesTab.prototype.fillTabDataFromDocument = function (document) {
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
        TemplatesTab.prototype.clearTabData = function () {
            tabData.$templateSelect.selectFirst();
            tabData.$defaultChildTemplateSelect.selectFirst();
        };

        return new TemplatesTab(texts.name, docTypes.TEXT);
    }
);
