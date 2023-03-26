define('imcms-document-basket-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/document/basket';
    const api = new rest.API(url);

    api.create = docId => rest.ajax.call({url: `${url}/${docId}`, type: 'POST', json: false});

    api.createByIds = docIds => rest.ajax.call({url: `${url}`, type: 'POST', json: true}, docIds);

    api.restore = docId => rest.ajax.call({url: `${url}/${docId}`, type: 'DELETE', json: false});

    api.restoreByIds = docIds => rest.ajax.call({url: `${url}`, type: 'DELETE', json: true}, docIds);

    return api;
});
