/**
 * Created by Shadowgun on 24.03.2015.
 */
/**
 * Created by Shadowgun on 24.03.2015.
 */
Imcms.Folder = {};
Imcms.Folder.API = function () {

};
Imcms.Folder.API.prototype = {
    create: function (request, response) {
        Imcms.Logger.log("Folder.API::create :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("folders", request.folderBase, request.folderName),
                type: "POST",
                success: response
            }), request);
    },
    read: function (request, response) {
        Imcms.Logger.log("Folder.API::read :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("folders", request.folder, ""),
                type: "GET",
                success: response
            }), request);
    },
    update: function (request, response) {
        Imcms.Logger.log("Folder.API::update :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("folders", request.folderBase, request.folderName),
                type: "PATCH",
                data: request,
                success: response
            }), request);
    },
    delete: function (request, response) {
        Imcms.Logger.log("Folder.API::remove :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("folders", request.folderBase, request.folderName),
                type: "DELETE",
                success: response
            }), request);
    }

};

Imcms.Folder.Loader = function () {

};
Imcms.Folder.Loader.prototype = {
    _api: new Imcms.Folder.API(),
    getAll: function (folder, callback) {
        this._api.read(
            {folder: folder || ""},
            Imcms.Logger.log.bind(this, "Folder::getAll : ", callback)
        )
    },
    get: function (folder, childname, callback) {
        this._api.read(
            {folderBase: folder || "", folderName: childname || "*"},
            Imcms.Logger.log.bind(this, "Folder::get : ", callback)
        )
    },
    move: function (folder, childname, folderTo, callback) {
        this._api.update(
            {folderBase: folder || "", folderName: childname || "*", to: folderTo || ""},
            Imcms.Logger.log.bind(this, "Folder::move : ", callback)
        )
    },
    removeAll: function (folder, callback) {
        this._api.delete(
            {folderBase: folder || "", folderName: "*"},
            Imcms.Logger.log.bind(this, "Folder::removeAll : ", callback)
        )
    },
    remove: function (folder, filename, callback) {
        this._api.delete(
            {folderBase: folder || "", folderName: filename || "*"},
            Imcms.Logger.log.bind(this, "Folder::remove : ", callback)
        )
    }
};