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
    read: function (request, response) {
        $.ajax({
            url: Imcms.Linker.get("template"),
            type: "GET",
            data: request,
            success: response
        });
    }
};
