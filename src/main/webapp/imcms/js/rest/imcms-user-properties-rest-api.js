define(
    'imcms-user-properties-rest-api', ["imcms-rest-api"], function (rest) {
        let url = '/user/properties';
        let api = new rest.API(url);

        api.create = properties => rest.ajax.call({url: `${url}`, type: 'POST', json: true}, properties);

        return api;
    }
);