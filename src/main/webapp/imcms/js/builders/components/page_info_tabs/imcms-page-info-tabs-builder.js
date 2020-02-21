const WindowTabsBuilder = require('imcms-window-tabs-builder');

module.exports = new WindowTabsBuilder({
    tabBuilders: [
        require('imcms-file-tab-builder'),
        require('imcms-url-tab-builder'),
        require('imcms-appearance-tab-builder'),
        require('imcms-life-cycle-tab-builder'),
        require('imcms-templates-tab-builder'),
        require('imcms-keywords-tab-builder'),
        require('imcms-categories-tab-builder'),
        require('imcms-access-tab-builder'),
        require('imcms-permissions-tab-builder'),
        require('imcms-status-tab-builder'),
        require('imcms-cache-tab-builder'),
    ]
});

