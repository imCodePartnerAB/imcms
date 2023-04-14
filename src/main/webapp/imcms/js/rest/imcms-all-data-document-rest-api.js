define('imcms-all-data-document-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/document';
    const api = new rest.API(url);

    api.getAllDataDocument = docId => rest.ajax.call({url: `${url}/all-data/${docId}` , type: 'GET', json: true});

    api.getAllVersions = docId => rest.ajax.call({url: `${url}/versions/${docId}` , type: 'GET', json: true});

    return api;
});
