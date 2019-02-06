define('imcms-users-rest-api', ['imcms-rest-api'], function (rest) {
    var url = '/users';
    var api = new rest.API(url);

    api.getAllAdmins = () => rest.ajax.call({url: `${url}/admins`, type: 'GET', json: false});

    api.search = queryObj => rest.ajax.call({url: `${url}/search`, type: 'GET', json: false}, queryObj);

    return api;
});
