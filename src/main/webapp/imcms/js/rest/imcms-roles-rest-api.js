define('imcms-roles-rest-api', ['imcms-rest-api'], function (rest) {
    const url = '/roles';
    const api = new rest.API(url);

    api.currentUserRoleIds = () => rest.ajax.call({url: `${url}/current-user`, type: 'GET', json: false});

    api.remove = role => rest.ajax.call({url: `${url}/${role.id}`, type: 'DELETE', json: false});

    return api;
});
