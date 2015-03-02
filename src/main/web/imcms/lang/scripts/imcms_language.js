/**
 * Created by Shadowgun on 24.02.2015.
 */
Imcms.Language = {};
Imcms.Language.Loader = function () {
    this.init();
};
Imcms.Language.Loader.prototype = {
    _api: {},
    init: function () {
        this._api = new Imcms.Language.API();
    },
    read: function (callback) {
        this._api.read({}, callback);
    }
};

Imcms.Language.API = function () {

};
Imcms.Language.API.prototype = {
    path: "/api/language",
    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        });
    }
};
