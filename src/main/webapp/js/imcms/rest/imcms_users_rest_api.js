Imcms.define("imcms-users-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/users");

    api.getAllAdmins = function () {
        return rest.ajax.call({url: "/users/admins", type: "GET", json: false});
    };

    api.search = function (queryObj) {
        return rest.ajax.call({url: "/users/search", type: "GET", json: false}, queryObj);
    };

    return api;
});
