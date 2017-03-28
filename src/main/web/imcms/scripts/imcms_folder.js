/**
 * Created by Shadowgun on 24.03.2015.
 *
 * Refactored by Serhii Maksymchuk in 2017
 */
(function (Imcms) {
    var api = {
        create: function (request, response) {
            var path = Imcms.Linker.get("folders", request.folderBase, request.folderName);
            Imcms.Logger.log(
                "Folder.API::create :",
                Imcms.REST.post.bindArgs(path, {}, response),
                request
            );
        },
        read: function (request, response) {
            var path = Imcms.Linker.get("folders", request.folder, "");
            Imcms.Logger.log(
                "Folder.API::read :",
                Imcms.REST.get.bindArgs(path, {}, response),
                request
            );
        },
        update: function (request, response) {
            var path = Imcms.Linker.get("folders", request.folderBase, request.folderName);
            Imcms.Logger.log(
                "Folder.API::update :",
                Imcms.REST.patch.bindArgs(path, request, response),
                request
            );
        },
        remove: function (request, response) {
            var path = Imcms.Linker.get("folders", request.folderBase, request.folderName);
            Imcms.Logger.log(
                "Folder.API::remove :",
                Imcms.REST.delete.bindArgs(path, {}, response),
                request
            );
        }
    };

    Imcms.Folder = function () {
    };
    Imcms.Folder.prototype = {
        getAll: function (folder, callback) {
            api.read(
                {folder: folder || ""},
                Imcms.Logger.log.bind(this, "Folder::getAll : ", callback)
            )
        },
        get: function (folder, childname, callback) {
            api.read(
                {folderBase: folder || "", folderName: childname || "*"},
                Imcms.Logger.log.bind(this, "Folder::get : ", callback)
            )
        },
        move: function (folder, childname, folderTo, callback) {
            api.update(
                {folderBase: folder || "", folderName: childname || "*", to: folderTo || ""},
                Imcms.Logger.log.bind(this, "Folder::move : ", callback)
            )
        },
        removeAll: function (folder, callback) {
            api.remove(
                {folderBase: folder || "", folderName: "*"},
                Imcms.Logger.log.bind(this, "Folder::removeAll : ", callback)
            )
        },
        remove: function (folder, filename, callback) {
            api.remove(
                {folderBase: folder || "", folderName: filename || "*"},
                Imcms.Logger.log.bind(this, "Folder::remove : ", callback)
            )
        }
    };

    return Imcms;
})(Imcms);
