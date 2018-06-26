Imcms.define("imcms-users-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/users");

    api.getAllAdmins = function () {
        return api.call({url: "/users/admins", type: "GET", json: false});
    };

    return api;
});
