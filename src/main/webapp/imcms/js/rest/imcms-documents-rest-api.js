define('imcms-documents-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/documents';
    const api = new rest.API(url);

    api.remove = docId => rest.ajax.call({url: `${url}/${docId}`, type: 'DELETE', json: false});

    api.removeByIds = docIds => rest.ajax.call({url: `${url}/deleteAll`, type: 'DELETE', json: false}, docIds);

    api.getUniqueAlias = alias => rest.ajax.call({url: `${url}/alias/unique/${alias}`, type: 'GET', json: false});

    return api;
});
