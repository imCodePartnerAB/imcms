define('imcms-temporal-data-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/temporal-data';
    const api = new rest.API(url);

    api.rebuildDocumentIndex = () => rest.ajax.call({url: `${url}/document-index`, type: 'DELETE', json: true});
    api.deletePublicDocumentCache = () => rest.ajax.call({url: `${url}/public-document`, type: 'DELETE', json: true});
    api.deleteStaticContentCache = () => rest.ajax.call({url: `${url}/static-content`, type: 'DELETE', json: true});
    api.deleteOtherContentCache = () => rest.ajax.call({url: `${url}/other-content`, type: 'DELETE', json: true});

    api.getAmountOfIndexedDocuments = () => rest.ajax.call({url: `${url}/indexed-documents-amount`, type: 'GET', json: false});

    api.getDateDocumentIndex = () => rest.ajax.call({url: `${url}/date-reindex`, type: 'GET', json: false});
    api.getDateRemoveDocumentCache = () => rest.ajax.call({url: `${url}/date-public-document`, type: 'GET', json: false});
    api.getDateRemoveStaticContentCache = () => rest.ajax.call({url: `${url}/date-static-content`, type: 'GET', json: false});
    api.getDateRemoveOtherContentCache = () => rest.ajax.call({url: `${url}/date-other-content`, type: 'GET', json: false});

    return api;
});