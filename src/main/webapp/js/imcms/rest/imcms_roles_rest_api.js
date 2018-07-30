Imcms.define("imcms-roles-rest-api", ["imcms-rest-api"], function (rest) {
    var url = "/roles";
    var api = new rest.API(url);

    api.remove = function (roleId) {
        return rest.ajax.call({url: url + '/' + roleId, type: 'DELETE', json: false})
    };

    return api;
});
