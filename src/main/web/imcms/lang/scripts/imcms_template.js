/**
 * Created by Shadowgun on 26.02.2015.
 */
Imcms.Template = {};
Imcms.Template.Loader = function () {
    this.init();
};
Imcms.Template.Loader.prototype = {
    _api: {},
    init: function () {
        this._api = new Imcms.Template.API();
    },
    read: function (callback) {
        this._api.read({}, callback);
    }
};

Imcms.Template.API = function () {

};
Imcms.Template.API.prototype = {
    path: "/api/template",
    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        });
    }
};
