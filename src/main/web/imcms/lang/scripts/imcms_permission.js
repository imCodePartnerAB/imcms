/**
 * Created by Shadowgun on 14.04.2015.
 */

Imcms.Permission = {};
Imcms.Permission.Loader = function () {
    this.init();
};
Imcms.Permission.Loader.prototype = {
    _api: {},
    init: function () {
        this._api = new Imcms.Permission.API();
    },
    read: function (callback) {
        this._api.read({}, callback);
    }
};

Imcms.Permission.API = function () {

};
Imcms.Permission.API.prototype = {
    path: Imcms.contextPath + "/api/permission",
    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        });
    }
};
