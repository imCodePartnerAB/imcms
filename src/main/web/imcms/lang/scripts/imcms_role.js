/**
 * Created by Shadowgun on 14.04.2015.
 */

Imcms.Role = {};
Imcms.Role.Loader = function () {
    this.init();
};
Imcms.Role.Loader.prototype = {
    _api: {},
    init: function () {
        this._api = new Imcms.Role.API();
    },
    read: function (callback) {
        this._api.read({}, callback);
    }
};

Imcms.Role.API = function () {

};
Imcms.Role.API.prototype = {
    path: Imcms.contextPath + "/api/role",
    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        });
    }
};
