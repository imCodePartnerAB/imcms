define("imcms-page-info-tabs-builder",
    [
        "imcms-appearance-tab-builder", "imcms-life-cycle-tab-builder", "imcms-keywords-tab-builder",
        "imcms-categories-tab-builder", "imcms-access-tab-builder", "imcms-permissions-tab-builder",
        "imcms-templates-tab-builder", "imcms-status-tab-builder", "imcms-file-tab-builder", "imcms-url-tab-builder",
        "imcms-window-tabs-builder"
    ],
    function (
        appearance, lifeCycle, keywords, categories, access, permissions, templates, status, files, url,
        WindowTabsBuilder
    ) {
        return new WindowTabsBuilder({
            tabBuilders: [
                appearance,
                lifeCycle,
                templates,
                files,
                url,
                keywords,
                categories,
                access,
                permissions,
                status
            ]
        });
    }
);
