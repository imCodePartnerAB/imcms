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
    read: function (request, response) {
        $.ajax({
            url: Imcms.Linker.get("role"),
            type: "GET",
            data: request,
            success: response
        });
    }
};
