define('imcms-temporal-data-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/temporal-data';
    const api = new rest.API(url);

    api.rebuildDocumentIndex = () => rest.ajax.call({url: `${url}/document-index`, type: 'DELETE', json: true});
    api.deletePublicDocumentCache = () => rest.ajax.call({url: `${url}/public-document`, type: 'DELETE', json: true});
    api.deleteStaticContentCache = () => rest.ajax.call({url: `${url}/static-content`, type: 'DELETE', json: true});
    api.deleteOtherContentCache = () => rest.ajax.call({url: `${url}/other-content`, type: 'DELETE', json: true});
    return api;
});