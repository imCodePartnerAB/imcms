/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 09.11.17
 */
define("imcms-documents-search-rest-api", ["imcms-rest-api"], function (rest) {
    const url = '/documents/search';
    const api = new rest.API(url);

    api.searchById = docId => rest.ajax.call({url: `${url}/${docId}`, type: 'GET', json: false});

    return api;
});
