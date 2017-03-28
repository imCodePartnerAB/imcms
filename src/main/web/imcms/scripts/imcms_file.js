(function (Imcms) {
    function restApiCall(restFunc, isEmptyData) {
        return function (request, response) {
            Imcms.Logger.log(
                "File.API::" + restFunc.name + " :",
                restFunc.bindArgs(getApiPath(request), (isEmptyData ? {} : request), response),
                request
            );
        };
    }

    function getApiPath(request) {
        return Imcms.Linker.get("files", request.folder, request.file);
    }

    function createRequestData(folder, file, additionalOptions) {
        return Imcms.Utils.mergeObjectsProperties(additionalOptions, {
            folder: (folder || ""),
            file: (file || "*.*")
        });
    }

    var api = {
        create: function (request, response) {
            var path = getApiPath(request);
            Imcms.Logger.log("File.API::create :",
                $.ajax.bind($, {
                    url: path,
                    contentType: false,
                    processData: false,
                    type: "POST",
                    data: request.data,
                    success: response
                }), request);
        },
        read: restApiCall(Imcms.REST.get, false),
        update: restApiCall(Imcms.REST.patch, true),
        remove: restApiCall(Imcms.REST.delete, false)
    };

    Imcms.File = function () {
    };
    Imcms.File.prototype = {
        getAllPictures: function (folder, callback) {
            [
                "*.jpg",
                "*.jpeg",
                "*.png",
                "*.gif"

            ].forEach(function (file) {
                api.read(createRequestData(folder, file), callback);
            });
        },
        getAll: function (folder, callback) {
            api.read(createRequestData(folder), callback);
        },
        get: function (folder, filename, callback) {
            api.read(createRequestData(folder, filename), callback);
        },
        move: function (folder, filename, folderTo, callback) {
            api.update(createRequestData(folder, filename, {to: (folderTo || "")}), callback);
        },
        removeAll: function (folder, callback) {
            api.remove(createRequestData(folder), callback);
        },
        remove: function (folder, filename, callback) {
            api.remove(createRequestData(folder), callback);
        },
        addPictureFile: function (file, folder, callback) {
            var data = new FormData();
            data.append("file", file);
            api.create(createRequestData(folder, file.name, {data: data}), callback);
        }
    };

    return Imcms;
})(Imcms);
