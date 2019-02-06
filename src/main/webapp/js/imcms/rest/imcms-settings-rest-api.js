define('imcms-settings-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/properties';
    const api = new rest.API(url);

    api.getAllProperties = () => rest.ajax.call({url: url, type: 'GET', json: true});
    api.findByName = name => rest.ajax.call({url: `${url}/${name}`, type: 'GET', json: true});
    api.deleteById = id => rest.ajax.call({url: `${url}/${id}`, type: 'DELETE', json: true});
    api.update = property => rest.ajax.call({url: url, type: 'POST', json: true}, property);
    return api;
});