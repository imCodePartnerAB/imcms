/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */

const WindowTabsBuilder = require('imcms-window-tabs-builder');

module.exports = new WindowTabsBuilder({
    tabBuilders: [
        require('imcms-users-tab-builder'),
        require('imcms-roles-tab-builder'),
        require('imcms-ip-access-tab-builder'),
        require('imcms-delete-docs-tab-builder'),
        require('imcms-files-tab-builder'),
        require('imcms-link-validator-tab-builder'),
        require('imcms-images-tab-builder'),
        require('imcms-categories-admin-tab-builder'),
        require('imcms-profiles-tab-builder'),
        require('imcms-system-properties-tab-builder'),
        require('imcms-temporal-data-tab-builder'),
        require('imcms-data-version-admin-tab-builder'),
        require('imcms-documentation-tab-builder')
    ]
});
