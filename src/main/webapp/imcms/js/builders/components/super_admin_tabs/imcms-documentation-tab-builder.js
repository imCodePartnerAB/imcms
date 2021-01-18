define(
    'imcms-documentation-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms'],
    function (SuperAdminTab, texts, components, imcms) {

        texts = texts.superAdmin.documentationTab;

        function buildDocumentationLink() {
            const linkDocumentation = imcms.documentationLink;

            return components.texts.infoText('<a>', texts.label.toUpperCase(), {
                href: linkDocumentation,
                target: '_blank'
            });
        }

        return new SuperAdminTab(texts.title, [
            buildDocumentationLink()
        ]);
    }
);