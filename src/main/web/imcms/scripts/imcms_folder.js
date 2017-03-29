/**
 * Created by Shadowgun on 24.03.2015.
 *
 * Refactored by Serhii Maksymchuk in 2017
 */
(function (Imcms) {
    function createRequestData(folderBase, folderName, additionalOptions) {
        return Imcms.Utils.mergeObjectsProperties(additionalOptions, {
            folderBase: (folderBase || ""),
            folderName: (folderName === undefined) ? "*" : folderName
        });
    }

    function restApiCall(restFunc, isEmptyData) {
        return function (request, response) {
            var path = Imcms.Linker.get("folders", request.folderBase, request.folderName);

            Imcms.Logger.log(
                "Folder.API::" + restFunc.name + " :",
                restFunc.bindArgs(path, (isEmptyData ? {} : request), response),
                request
            );
        };
    }

    var api = {
        create: restApiCall(Imcms.REST.post, true),
        read: restApiCall(Imcms.REST.get, true),
        update: restApiCall(Imcms.REST.patch, false),
        remove: restApiCall(Imcms.REST.delete, true)
    };

    Imcms.Folder = function () {
    };
    Imcms.Folder.prototype = {
        getAll: function (folder, callback) {
            api.read(createRequestData(folder, ""), callback);
        },
        get: function (folder, childName, callback) {
            api.read(createRequestData(folder, childName), callback);
        },
        move: function (folder, childName, folderTo, callback) {
            api.update(createRequestData(folder, childName, {to: (folderTo || "")}), callback);
        },
        removeAll: function (folder, callback) {
            api.remove(createRequestData(folder), callback);
        },
        remove: function (folder, filename, callback) {
            api.remove(createRequestData(folder, filename), callback);
        }
    };

    return Imcms;
})(Imcms);
