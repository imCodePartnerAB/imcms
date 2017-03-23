/**
 * Created by Serhii from Ubrainians for ImCode
 * on 23.04.2015.
 *
 * Refactored in 2017
 */
(function (Imcms) {
    var usersUrl = Imcms.Linker.get("users"),
        api = {};

    Imcms.User = {};
    Imcms.User.Loader = function () {
        this.init();
    };
    Imcms.User.Loader.prototype = {
        init: function () {
            api = new Imcms.REST.API(usersUrl);
        },
        read: function (data, callback) {
            api.read(data, callback);
        }
    };

    return Imcms;
})(Imcms);
