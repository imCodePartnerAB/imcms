/**
 * Created by Serhii from Ubrainians for ImCode
 * on 23.04.2015.
 */
Imcms.User = {};
Imcms.User.Loader = function () {
    this.init();
};
Imcms.User.Loader.prototype = {
    _api: {},
    init: function () {
        this._api = new Imcms.User.API();
    },
    read: function (callback) {
        this._api.read({}, callback);
    }
};

Imcms.User.API = function () {

};
Imcms.User.API.prototype = {
    path: Imcms.Linker.get("users"),
    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        });
    }
};
