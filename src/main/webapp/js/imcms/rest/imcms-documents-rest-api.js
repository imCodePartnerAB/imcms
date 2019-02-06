define('imcms-documents-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/documents';
    const api = new rest.API(url);

    api.remove = docId => rest.ajax.call({url: `${url}/${docId}`, type: 'DELETE', json: false});

    return api;
});
