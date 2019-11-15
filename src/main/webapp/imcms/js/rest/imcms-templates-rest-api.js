define("imcms-templates-rest-api", ["imcms-rest-api"], function (rest) {

    const url = '/templates';
    const api = new rest.API(url);

    api.delete = id => rest.ajax.call({url: `${url}/${id}`, type: 'DELETE', json: false});

    return api;
});
