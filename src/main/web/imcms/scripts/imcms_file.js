Imcms.File = {};
Imcms.File.API = function () {

};
Imcms.File.API.prototype = {
	create: function (request, response) {
		Imcms.Logger.log("File.API::create :",
			$.ajax({
				url: Imcms.Linker.get("files", request.folder, request.filename),
                contentType: false,
                processData: false,
				type: "POST",
				data: request.data,
				success: response
			}), request);
	},
	read: function (request, response) {
		Imcms.Logger.log("File.API::read :",
			$.ajax.bind($, {
				url: Imcms.Linker.get("files", request.folder, request.file),
				type: "GET",
				success: response
			}), request);
	},
	update: function (request, response) {
		Imcms.Logger.log("File.API::update :",
			$.ajax.bind($, {
				url: Imcms.Linker.get("files", request.folder, request.file),
				type: "PATCH",
				data: request,
				success: response
			}), request);
	},
	delete: function (request, response) {
		Imcms.Logger.log("File.API::delete :",
			$.ajax.bind($, {
				url: Imcms.Linker.get("files", request.folder, request.file),
				type: "DELETE",
				success: response
			}), request);
	}
};

Imcms.File.Loader = function () {

};
Imcms.File.Loader.prototype = {
	_api: new Imcms.File.API(),
	getAllPictures: function (folder, callback) {
		this._api.read(
			{folder: folder || "", file: "*.jpg"},
			Imcms.Logger.log.bind(this, "File::getAllPictures : ", callback)
		);
		this._api.read(
			{folder: folder || "", file: "*.jpeg"},
			Imcms.Logger.log.bind(this, "File::getAllPictures : ", callback)
		);
		this._api.read(
			{folder: folder || "", file: "*.png"},
			Imcms.Logger.log.bind(this, "File::getAllPictures : ", callback)
		);
		this._api.read(
			{folder: folder || "", file: "*.gif"},
			Imcms.Logger.log.bind(this, "File::getAllPictures : ", callback)
		)
	},
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
	},
    addPictureFile: function (file, folder, callback) {
        var data = new FormData();
        data.append("file", file);

        this._api.create(
            {folder: folder || "", filename: file.name, data: data},
            Imcms.Logger.log.bind(this, "File::addPictureFile : ", callback)
        )
    }
};