/**
 * Created by Shadowgun on 14.04.2015.
 *
 * Refactored by Serhii Maksymchuk, 2017
 */
(function (Imcms) {
    var permissionUrl = Imcms.Linker.get("permission"),
        api = {};

    Imcms.Permission = {};
    Imcms.Permission.Loader = function () {
        this.init();
    };
    Imcms.Permission.Loader.prototype = {
        init: function () {
            api = new Imcms.REST.API(permissionUrl);
        },
        read: function (callback) {
            api.get({}, callback);
        }
    };

    return Imcms;
})(Imcms);
