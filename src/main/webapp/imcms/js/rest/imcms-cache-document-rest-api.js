define(
    'imcms-cache-document-rest-api', ["imcms-rest-api"], function (rest) {
        let url = '/document-cache';
        let api = new rest.API(url);

        api.invalidate = dataParam => rest.ajax.call({url: `${url}/invalidate`, type: 'GET', json: false}, dataParam);

        return api;
    }
);