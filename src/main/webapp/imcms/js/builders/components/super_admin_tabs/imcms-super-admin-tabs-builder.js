/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */

const WindowTabsBuilder = require('imcms-window-tabs-builder');
const imcms = require('imcms');

let opts;
if (imcms.isSuperAdmin) {
    let tabBuilders = [
        require('imcms-users-tab-builder'),
        require('imcms-delete-docs-tab-builder'),
        require('imcms-link-validator-tab-builder'),
        require('imcms-images-tab-builder'),
        require('imcms-profiles-tab-builder'),
        require('imcms-documentation-tab-builder'),
        require('imcms-data-version-admin-tab-builder')
    ]
    let advancedTabBuilders = [
        require('imcms-roles-tab-builder'),
        require('imcms-ip-access-tab-builder'),
        require('imcms-templates-css-tab-builder'),
        require('imcms-doc-versions-tab-builder'),
        require('imcms-categories-admin-tab-builder'),
        require('imcms-system-properties-tab-builder'),
        require('imcms-temporal-data-tab-builder'),
        require('imcms-import-documents-tab-builder')
    ]

    if (imcms.hasFileAdminAccess) {
        tabBuilders.splice(3, 0, require('imcms-files-tab-builder'));
    }

    opts = {
        tabBuilders: tabBuilders,
        advancedTabBuilders: advancedTabBuilders
    }
}else{
    let tabBuilders = [
        require('imcms-users-tab-builder'),
        require('imcms-delete-docs-tab-builder'),
        require('imcms-link-validator-tab-builder'),
        require('imcms-images-tab-builder'),
        require('imcms-data-version-admin-tab-builder')
    ];

    let advancedTabBuilders = [
        require('imcms-roles-tab-builder'),
        require('imcms-templates-css-tab-builder'),
        require('imcms-categories-admin-tab-builder'),
        require('imcms-temporal-data-tab-builder')
    ]

    opts = {
        tabBuilders: tabBuilders,
        advancedTabBuilders: advancedTabBuilders
    }
}

module.exports = new WindowTabsBuilder(opts)
