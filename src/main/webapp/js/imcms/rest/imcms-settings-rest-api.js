define('imcms-settings-rest-api', ['imcms-rest-api'], function (rest) {
    var url = '/properties';
    var api = new rest.API(url);

    api.getAllProperties = function () {
        return rest.ajax.call({url: url, type: 'GET', json: true});
    };

    api.findByName = function (name) {
        return rest.ajax.call({url: url + '/' + name, type: 'GET', json: true});
    };

    api.deleteById = function (id) {
        return rest.ajax.call({url: url + '/' + id, type: 'DELETE', json: true});
    };
    api.update = function (property) {
        return rest.ajax.call({url: url, type: 'POST', json: true}, property);
    };
    return api;
});