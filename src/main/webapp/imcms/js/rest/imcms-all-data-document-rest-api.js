define('imcms-all-data-document-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/document/all-data';
    const api = new rest.API(url);

    api.getAllDataDocument = docId => rest.ajax.call({url: `${url}/${docId}` , type: 'GET', json: true});

    return api;
});
