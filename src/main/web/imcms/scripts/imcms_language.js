/**
 * Created by Shadowgun on 24.02.2015.
 *
 * Refactored by Serhii Maksymchuk, 2017
 */
(function (Imcms) {
    var languageUrl = Imcms.Linker.get("language"),
        api = {};

    Imcms.Language = {};
    Imcms.Language.Loader = function () {
        this.init();
    };
    Imcms.Language.Loader.prototype = {
        init: function () {
            api = new Imcms.REST.API(languageUrl);
        },
        read: function (callback) {
            api.get({}, callback);
        }
    };

    return Imcms;
})(Imcms);
