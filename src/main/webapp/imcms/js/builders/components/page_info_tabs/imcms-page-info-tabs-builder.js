const WindowTabsBuilder = require('imcms-window-tabs-builder');

const fileTab =         require('imcms-file-tab-builder');
const urlTab =          require('imcms-url-tab-builder');
const appearanceTab =   require('imcms-appearance-tab-builder');
const lifeCycleTab =    require('imcms-life-cycle-tab-builder');
const templatesTab =    require('imcms-templates-tab-builder');
const keywordsTab =     require('imcms-keywords-tab-builder');
const categoriesTab =   require('imcms-categories-tab-builder');
const accessTab =       require('imcms-access-tab-builder');
const permissionsTab =  require('imcms-permissions-tab-builder');
const statusTab =       require('imcms-status-tab-builder');
const cacheTab =        require('imcms-cache-tab-builder');
const propertiesTab =   require('imcms-properties-tab-builder');
const allDataTab =      require('imcms-all-data-tab-builder');

const textTabBuilders = [fileTab, urlTab, appearanceTab, lifeCycleTab, templatesTab, keywordsTab, categoriesTab, accessTab, permissionsTab,
    statusTab, cacheTab, propertiesTab, allDataTab];

const urlOrFileTabBuilders = [fileTab, urlTab, appearanceTab, lifeCycleTab, templatesTab, keywordsTab, categoriesTab, accessTab, permissionsTab,
    statusTab, cacheTab, propertiesTab];

const limitedTabBuilders = [fileTab, urlTab, appearanceTab, lifeCycleTab, templatesTab, keywordsTab, categoriesTab, accessTab, statusTab, cacheTab];

module.exports = {
    textWindowTabsBuilder: new WindowTabsBuilder({
        tabBuilders: textTabBuilders
    }),

    urlOrFileWindowTabsBuilder: new WindowTabsBuilder({
        tabBuilders: urlOrFileTabBuilders
    }),

    limitedWindowTabsBuilder: new WindowTabsBuilder({
        tabBuilders: limitedTabBuilders
    })
}

