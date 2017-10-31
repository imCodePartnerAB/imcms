/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 17.08.17.
 */
Imcms.define("imcms-image-files-rest-api", ["imcms-rest-api"], function (rest) {

    var api = new rest.API("/images/folders");

    //mock data
    api.create = function (data) {
        return {
            done: function (callback) {
                console.log("%c Creating new file: ", "color: blue;");
                console.log(data);
                callback({ // mock response object - empty folder
                    name: data.name,
                    path: data.path,
                    files: [],
                    folders: []
                });
            }
        }
    };

    api.update = function (data) {
        return {
            done: function (callback) {
                console.log("%c Updating file: ", "color: blue;");
                console.log(data);
                callback({
                    status: "OK",
                    code: 200
                });
            }
        }
    };

    api.remove = function (path) {
        return {
            done: function (callback) {
                console.log("%c " + path + " was removed (not really)", "color: blue;");
                callback.call();
            }
        }
    };

    return api;
});
