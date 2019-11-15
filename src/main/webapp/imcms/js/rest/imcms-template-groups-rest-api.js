define("imcms-files-rest-api", ["imcms-rest-api"], function (rest) {

    const url = '/template-group';
    const api = new rest.API(url);

    api.get = name => rest.ajax.call({url: `${url}/${name}`, type: 'GET', json: false});

    api.remove = id => rest.ajax.call({url: `${url}/${id}`, type: 'DELETE', json: false});

    return api;
});