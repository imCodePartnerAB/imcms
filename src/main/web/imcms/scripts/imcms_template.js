/**
 * Created by Shadowgun on 26.02.2015.
 *
 * Refactored by Serhii Maksymchuk, 2017
 */
(function (Imcms) {
    var templateUrl = Imcms.Linker.get("template"),
        api = {};

    Imcms.Template = {};
    Imcms.Template.Loader = function () {
        this.init();
    };
    Imcms.Template.Loader.prototype = {
        init: function () {
            api = new Imcms.REST.API(templateUrl);
        },
        read: function (callback) {
            api.get({}, callback);
        }
    };

    return Imcms;
})(Imcms);
