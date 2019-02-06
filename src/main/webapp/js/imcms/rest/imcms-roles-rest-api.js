define('imcms-roles-rest-api', ['imcms-rest-api'], function (rest) {
    var url = '/roles';
    var api = new rest.API(url);

    api.remove = role => rest.ajax.call({url: `${url}/${role.id}`, type: 'DELETE', json: false});

    return api;
});
