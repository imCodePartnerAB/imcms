/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 05.09.17
 */
define("imcms-menus-rest-api", ["imcms-rest-api"], function (rest) {
    let url = '/menus';
    let api = new rest.API(url);

    api.getSortTypes = nested => rest.ajax.call({url: `${url}/sort-types`, type: 'GET', json: false}, nested);
    api.getSortedItems = menuData => rest.ajax.call({url: `${url}/sorting`, type: 'PUT', json: true}, menuData);

    return api;
});
