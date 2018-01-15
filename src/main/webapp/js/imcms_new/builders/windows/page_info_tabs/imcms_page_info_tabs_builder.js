Imcms.define("imcms-page-info-tabs-builder",
    [
        "imcms-appearance-tab-builder", "imcms-life-cycle-tab-builder", "imcms-keywords-tab-builder",
        "imcms-categories-tab-builder", "imcms-access-tab-builder", "imcms-permissions-tab-builder",
        "imcms-templates-tab-builder", "imcms-status-tab-builder", "imcms-file-tab-builder"
    ],
    function (appearance, lifeCycle, keywords, categories, access, permissions, templates, status, files) {
        return {
            tabBuilders: [
                appearance,
                lifeCycle,
                templates,
                files,
                keywords,
                categories,
                access,
                permissions,
                status
            ]
        };
    }
);
