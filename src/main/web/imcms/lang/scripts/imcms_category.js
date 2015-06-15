/**
 * Created by Shadowgun on 23.04.2015.
 */
Imcms.Category = {};
Imcms.Category.Loader = function () {
    this.init();
};
Imcms.Category.Loader.prototype = {
    _api: {},
    init: function () {
        this._api = new Imcms.Category.API();
    },
    read: function (callback) {
        this._api.read({}, callback);
    }
};

Imcms.Category.API = function () {

};
Imcms.Category.API.prototype = {
    path: "/api/category",
    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        });
    }
};
