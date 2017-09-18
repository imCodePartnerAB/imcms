/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 05.09.17
 */
Imcms.define("imcms-menu-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/api/menu");

    api.remove = function (data) {
        return {
            done: function (callback) {
                console.log("%c Removing (not really) menu item:", "color: blue");
                console.log(data);

                callback({
                    code: 200,
                    status: "OK"
                });
            }
        }
    };

    api.create = function (data) {
        return {
            done: function (callback) {
                console.log("%c Creating (not really) new menu item:", "color: blue");
                console.log(data);

                callback({
                    code: 200,
                    status: "OK"
                });
            }
        }
    };

    return api;
});
