/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
Imcms.define(
    'imcms-super-admin-tabs-builder',
    [
        'imcms-window-tabs-builder', 'imcms-users-tab-builder', 'imcms-roles-tab-builder', 'imcms-ip-access-tab-builder',
        'imcms-ip-white-list-tab-builder', 'imcms-delete-docs-tab-builder', 'imcms-templates-admin-tab-builder',
        'imcms-files-tab-builder', 'imcms-search-tab-builder', 'imcms-link-validator-tab-builder',
        'imcms-categories-admin-tab-builder', 'imcms-profiles-tab-builder', 'imcms-system-properties-tab-builder'
    ],
    function (WindowTabsBuilder, users, roles, ipAccess, ipWhiteList, deleteDocs, templates, files, search, link,
              categories, profiles, systemProps) {

        return new WindowTabsBuilder({
            tabBuilders: [
                users,
                roles,
                ipAccess,
                ipWhiteList,
                deleteDocs,
                templates,
                files,
                search,
                link,
                categories,
                profiles,
                systemProps
            ]
        });
    }
);
