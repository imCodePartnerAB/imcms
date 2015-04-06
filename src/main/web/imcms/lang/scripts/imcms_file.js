/**
 * Created by Shadowgun on 24.03.2015.
 */
Imcms.File = {};
Imcms.File.API = function () {

};
Imcms.File.API.prototype = {
    path: "/api/content/files",
    create: function (request, response) {
        Imcms.Logger.log("File.API::create :",
            $.ajax.bind($, {
                url: this.path + "/" + request.folder + "/" + request.file,
                type: "POST",
                data: request,
                success: response
            }), request);
    },
    read: function (request, response) {
        Imcms.Logger.log("File.API::read :",
            $.ajax.bind($, {
                url: this.path + "/" + request.folder + "/" + request.file,
                type: "GET",
                success: response
            }), request);
    },
    update: function (request, response) {
        Imcms.Logger.log("File.API::update :",
            $.ajax.bind($, {
                url: this.path + "/" + request.folder + "/" + request.file,
                type: "PATCH",
                data: request,
                success: response
            }), request);
    },
    delete: function (request, response) {
        Imcms.Logger.log("File.API::delete :",
            $.ajax.bind($, {
                url: this.path + "/" + request.folder + "/" + request.file,
                type: "DELETE",
                success: response
            }), request);
    }

};

Imcms.File.Loader = function () {

};
Imcms.File.Loader.prototype = {
    _api: new Imcms.File.API(),
    getAll: function (folder, callback) {
        this._api.read(
            {folder: folder || "", file: "*.*"},
            Imcms.Logger.log.bind(this, "File::getAll : ", callback)
        )
    },
    get: function (folder, filename, callback) {
        this._api.read(
            {folder: folder || "", file: filename || "*.*"},
            Imcms.Logger.log.bind(this, "File::get : ", callback)
        )
    },
    move: function (folder, filename, folderTo, callback) {
        this._api.update(
            {folder: folder || "", file: filename || "*.*", to: folderTo || ""},
            Imcms.Logger.log.bind(this, "File::move : ", callback)
        )
    },
    removeAll: function (folder, callback) {
        this._api.delete(
            {folder: folder || "", file: "*.*"},
            Imcms.Logger.log.bind(this, "File::removeAll : ", callback)
        )
    },
    remove: function (folder, filename, callback) {
        this._api.delete(
            {folder: folder || "", file: filename || "*.*"},
            Imcms.Logger.log.bind(this, "File::remove : ", callback)
        )
    }
};