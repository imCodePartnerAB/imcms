define("imcms-template-groups-rest-api", ["imcms-rest-api"], function (rest) {

    const url = '/template-group';
    const api = new rest.API(url);

    api.get = name => rest.ajax.call({url: `${url}/${name}`, type: 'GET', json: false});

    api.remove = id => rest.ajax.call({url: `${url}/${id}`, type: 'DELETE', json: false})

    api.addTemplate = pathParam => rest.ajax.call({url: `${url}/add-template`, type: 'PUT', json: true}, pathParam);

    api.deleteTemplate = pathParam => rest.ajax.call({url: `${url}/delete-template`, type: 'PATCH', json: true}, pathParam);

    return api;
});