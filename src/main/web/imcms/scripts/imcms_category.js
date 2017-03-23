/**
 * Created by Shadowgun on 23.04.2015.
 *
 * Refactored by Serhii Maksymchuk, 2017
 */
(function (Imcms) {
    var categoryUrl = Imcms.Linker.get("category"),
        api = {};

    Imcms.Category = {};
    Imcms.Category.Loader = function () {
        this.init();
    };
    Imcms.Category.Loader.prototype = {
        init: function () {
            api = new Imcms.REST.API(categoryUrl);
        },
        read: function (callback) {
            api.get({}, callback);
        }
    };

    return Imcms;
})(Imcms);
