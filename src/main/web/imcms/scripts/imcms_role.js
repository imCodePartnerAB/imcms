/**
 * Created by Shadowgun on 14.04.2015.
 *
 * Refactored by Serhii Maksymchuk, 2017
 */
(function (Imcms) {
    var roleUrl = Imcms.Linker.get("role"),
        api = {};

    Imcms.Role = {};
    Imcms.Role.Loader = function () {
        this.init();
    };
    Imcms.Role.Loader.prototype = {
        init: function () {
            api = new Imcms.REST.API(roleUrl);
        },
        read: function (callback) {
            api.read({}, callback);
        }
    };

    return Imcms;
})(Imcms);
