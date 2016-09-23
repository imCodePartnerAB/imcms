Imcms.Content = {};
Imcms.Content.Loader = function () {
	this.init();
};
Imcms.Content.Loader.prototype = {
	_editor: {},
	init: function () {
		this._editor = new Imcms.Content.Editor(this);
	},
    addPictureFile: function (file, folder, callback) {
        if (file.type != "image/jpeg" &&
            file.type != "image/jpg" &&
            file.type != "image/png" &&
            file.type != "image/gif") {
            throw "Wrong file extension!"; // todo: show validation error message
        }

        if (file.size > 4194304) { // todo: rewrite this "magic" digits
            throw "File is too large!"; // todo: show validation error message
        }

        Imcms.Editors.File.addPictureFile(file, folder, callback)
    },
	getAllPictures: function (folder, callback) {
		Imcms.Editors.File.getAllPictures(folder, callback)
	},
	getAllFiles: function (folder, callback) {
		Imcms.Editors.File.getAll(folder, callback)
	},
	getFiles: function (folder, filename, callback) {
		Imcms.Editors.File.get(folder, filename, callback)
	},
	deleteAllFiles: function (folder, callback) {
		Imcms.Editors.File.removeAll(folder, callback)
	},
	deleteFiles: function (folder, filename, callback) {
		Imcms.Editors.File.remove(folder, filename, callback)
	},

	getAllFolders: function (folder, callback) {
		Imcms.Editors.Folder.getAll(folder, callback)
	},
	getFolders: function (folder, foldername, callback) {
		Imcms.Editors.Folder.get(folder, foldername, callback)
	},
	deleteAllFolders: function (folder, callback) {
		Imcms.Editors.Folder.removeAll(folder, callback)
	},
	deleteFolders: function (folder, foldername, callback) {
		Imcms.Editors.Folder.remove(folder, foldername, callback)
	},
	moveFolder: function (folder, childname, folderTo, callback) {
		Imcms.Editors.Folder.move(folder, childname, folderTo, callback)
	},
	showDialog: function (options) {
		this._editor.open(options);
	}
};

Imcms.Content.Editor = function (loader) {
	this._loader = loader;
	this.init();
};
Imcms.Content.Editor.prototype = {
	_builder: {},
	_foldersTree: {},
	_fileAdapter: {},
	_loader: {},
	_option: {},
	init: function () {
		this.buildView()
			.buildFoldersTree()
			.buildFileView();
	},
	buildView: function () {
		this._builder = JSFormBuilder("<div>")
			.form()
			.div()
			.class("imcms-header")
			.div()
			.html("Content Manager")
			.class("imcms-title")
			.end()
			.button()
			.reference("closeButton")
			.class("imcms-close-button")
			.on("click", this.cancel.bind(this))
			.end()
			/*
			 .button()
			 .html("Close without saving")
			 .class("imcms-neutral close-without-saving")
			 .on("click", this.cancel.bind(this))
			 .end()*/
			.end()
			.div()
			.class("imcms-content")
			.div()
			.reference("folders")
			.class("folders")
			.end()
			.div()
			.class("files-wrapper")
			.reference("files-wrapper")
			.div()
			.class("dropzone")
			.end()
			.div()
			.reference("files")
			.class("files")
			.end()
			.end()
			.end()
			.div()
			.class("imcms-footer")
			.div()
			.class("browse-image")
			.file()
            .name("image-upload")
            .on("change", this._onFileChosen.bind(this))
			.end()
			.file()
			.class("hidden")
			.on("change", function () {
				this._fileUploader.uploadFormFile($(this._builder[0]).find("input[type=file].hidden")[0].files[0]);
			}.bind(this))
			.end()
			.end()
			.button()
			.html("Apply")
			.class("imcms-positive imcms-save-and-close")
			.on("click", this.save.bind(this))
			.end()
			.end()
			.end();
		$(this._builder[0]).appendTo("body").addClass("editor-form editor-content reset");
		return this;
	},
    _onFileChosen: function () {
        var file = $(this._builder[0])
            .find("input[type=file][name=image-upload]")[0]
            .files[0];

        try {
            Imcms.BackgroundWorker.createTask({showProcessWindow: true})();
            var folder = unescape(encodeURIComponent(this._escapeMainFolder(this._foldersTree.tree('getSelectedNode')).id));
            this._loader.addPictureFile(file, folder, function(){});
        } catch (error) {
            console.log(error);
            // ignore for now because we have an error that do not broke work flow - 406 (Not Acceptable)
            //todo: rewrite to check is this error from controller FileAlreadyExistsException or not
        } finally {
            setTimeout(function () {
                this._onFileUploaded();
            }.bind(this), 2000);
            // Imcms.BackgroundWorker.createTask()();
        }

        $("input[type=file][name=image-upload]").val("")
    },
	buildFoldersTree: function () {
		this._foldersTree = new Imcms.Content.TreeAdapter({
			tree: this._builder.ref("folders").getHTMLElement(),
			loader: this._loader,
			onSelectedChanged: this._onSelectedFolderChanged.bind(this),
			onMoved: this._onFolderMoved.bind(this)
		});
		return this;
	},
	buildFileView: function () {
		this._fileAdapter = new Imcms.Content.FileAdapter({
			element: this._builder.ref("files").getHTMLElement(),
			loader: this._loader
		});
		this._fileUploader = new Imcms.Content.FileUploader({
			target: this._builder.ref("files-wrapper").getHTMLElement(),
			onFileUploaded: this._onFileUploaded.bind(this)
		});
		return this;
	},
	_onSelectedFolderChanged: function (folder) {
		this._fileAdapter.changeFolder(unescape(encodeURIComponent(this._escapeMainFolder(folder).id)));
	},
	_onFolderMoved: function (moved, destinaton) {
		moved = this._escapeMainFolder(moved);
		destinaton = this._escapeMainFolder(destinaton);
		this._loader.moveFolder(unescape(encodeURIComponent(moved.fullPath)),
			unescape(encodeURIComponent(moved.name)),
			encodeURIComponent(destinaton.id));
	},
	_escapeMainFolder: function (folder) {
		var delimiterPosition = folder.id.indexOf("/");
		delimiterPosition = delimiterPosition > -1 ? delimiterPosition : folder.id.length;
		folder.id = folder.id.substring(delimiterPosition);
		folder.fullPath = folder.fullPath.substring(delimiterPosition);
		return folder;
	},
	_onFileUploaded: function () {
		this._onSelectedFolderChanged(this._foldersTree.tree('getSelectedNode'));
	},
	open: function (option) {
		this._option = option;
		$(this._builder[0]).fadeIn("fast").find(".imcms-content").css({height: $(window).height() - 95});
	},
	save: function () {
		this._option.onApply(this._fileAdapter.selected());
		this.close();
	},
	cancel: function () {
		this._option.onCancel();
		this.close();
	},
	close: function () {
		$(this._builder[0]).fadeOut("fast");
	}
};

Imcms.Content.TreeAdapter = function (options) {
	this.init(Imcms.Utils.merge(options, this.defaults));
};
Imcms.Content.TreeAdapter.prototype = {
	_tree: {},
	_loader: {},
	_options: {},
	defaults: {
		tree: null,
		loader: null,
		onSelectedChanged: function () {
		},
		onMoved: function () {

		}
	},
	init: function (options) {
		this._options = options;
		this._loader = options.loader;
		this._tree = $(options.tree);
		this.buildView();
	},
	onSelectLi: function (event) {
		if (event.node) {
			// node was selected
			var node = event.node;
			// alert(node.name);
			this._options.onSelectedChanged(node);
		}
		else {
			// event.node is null
			// a node was deselected
			// e.previous_node contains the deselected node
		}
	},
	onCreateLi: function (node, $li) {
		var treeElement = $li.find('.jqtree-element').empty();
		$("<span>").css("float", "left").text(node.name).appendTo(treeElement);
	},
	onMoveNode: function (event) {
		var info = event.move_info;
		var current = info.moved_node;
		var parentFrom = current.parent;
		var nodeTo = info.target_node;
		this._options.onMoved(current, nodeTo);
		var i, count, featurePosition, featureParent;
		for (i = parentFrom.children.indexOf(current) + 1,
				 count = parentFrom.children.length; i < count; i++)
			parentFrom.children[i].position--;
		switch (info.position) {
			case "inside":
			{
				featureParent = nodeTo;
				console.info("Parent node:");
				console.info(nodeTo);
				for (i = 0, count = featureParent.children.length; i < count; i++) {
					featureParent.children[i].position++;
					console.info(featureParent.children[i]);
				}
				current.position = 1;
			}
				break;
			case "before":
			{
				featurePosition = nodeTo.position - 1;
				featureParent = nodeTo.parent;
				console.info("Parent node:");
				console.info(featureParent);
				for (i = featureParent.children.indexOf(nodeTo),
						 count = featureParent.children.length; i < count; i++) {
					featureParent.children[i].position++;
					console.info(featureParent.children[i]);
				}
				current.position = featurePosition;
			}
				break;
			case "after":
			{
				featurePosition = nodeTo.position + 1;
				featureParent = nodeTo.parent;
				console.info("Parent node:");
				console.info(featureParent);
				for (i = featureParent.children.indexOf(nodeTo) + 1,
						 count = featureParent.children.length; i < count; i++) {
					featureParent.children[i].position++;
					console.info(featureParent.children[i]);
				}
				current.position = featurePosition;
			}
				break;
			default :
			{
				throw "Incorrect movement";
			}
		}
	},
	buildView: function (data) {
		if (!data) {
			this._loader.getAllFolders("", this.buildView.bind(this));
			return this;
		}
		this._data = [this.buildDataTree(data)];
		this._tree.tree({
			selectable: true,
			data: this._data,
			autoOpen: true,
			dragAndDrop: true,
			onCreateLi: $.proxy(this.onCreateLi, this)
		}).bind('tree.move', this.onMoveNode.bind(this));
		this._tree.tree('selectNode', this._tree.tree('getNodeById', this._data[0].id));
		this._tree.bind('tree.select', $.proxy(this.onSelectLi, this));
	},
	buildDataTree: function (data) {
		var tree = [],
			item,
			children = data.subdirectories;
		for (var i = 0, count = children.length; i < count; i++) {
			item = children[i];
			item = this.buildDataTree(item);
			tree.push(item);
		}
		data.children = tree;
		data.id = data.fullPath + data.name;
		return data;
	},
	collect: function (node, prefix, data) {
		var currentNode;
		node = node || this._tree.tree("getTree");
		data = data || [];
		for (var i = 0, count = node.children.length; i < count; i++) {
			currentNode = node.children[i];
			var tree = (prefix ? (prefix + ".") : "") + currentNode.position;
			data.push({
				"referenced-document": currentNode["doc-id"],
				"tree-sort-index": tree
			});
			console.info("----------------");
			console.info(currentNode);
			console.info(tree);
			console.info("----------------");
			if (currentNode.children.length > 0) {
				data = this.collect(currentNode, tree, data);
			}
		}
		return data;
	},
	delete: function (node) {
		this._tree.tree('removeNode', node);
	},
	add: function (node) {
		if (this._tree.tree('getNodeById', node.id)) return;
		var data = this._tree.tree("getTree");
		node.position = 0;
		for (var i = 0, count = data.children.length; i < count; i++)
			node.position = node.position < data.children[i].position ?
				data.children[i].position : node.position;
		node.position++;
		this._tree.tree(
			'appendNode',
			node
		);
	},
	reset: function () {
		this._tree.tree('loadData', this._data);
	},
	tree: function (opt) {
		return this._tree.tree(opt);
	}
};

Imcms.Content.FileView = function (element, data) {
	this._element = element;
	this._data = data;
	this.init();
};
Imcms.Content.FileView.prototype = {
	_element: {},
	_data: {},
	//fixme: should work with no image file
	_fileDefaults: {
		displaySize: {
			height: 0,
			width: 0
		},
		empty: false,
		extension: "",
		imageInfo: {
			format: "",
			height: 0,
			width: 0
		},
		modifiedDatetime: 0,
		name: "",
		nameWithoutExt: "",
		typeId: 0,
		urlPathRelativeToContextPath: ""
	},
	_selectedElement: {},
	_selectedItem: {},
	_minImageCountPerLine: 8,
	_minImageSize: 130,
	init: function () {
		if (this._data) this.buildView(this._data);
	},
	buildView: function (data) {
		$(this._element).empty();
		$(this._element).resize(this._alignItems.bind(this));
		this._prepareData(data);
		$.each(data, this._buildItem.bind(this));
		this._alignItems();
	},
	_buildItem: function (position, data) {
		var $div = $("<div>")
			.addClass("content-preview")
			.append(this._createImage(Imcms.Linker._contextPath + data.urlPathRelativeToContextPath, data.imageInfo))
			.append(this._createInfo(data.name));
		//.css({width: currentImageSize, height: currentImageSize});

		$div.appendTo(this._element).click(this._onSelect.bind(this, $div, data));
	},
	_prepareData: function (data) {
		data.forEach(function (item, index) {
			data[index] = Imcms.Utils.merge(item, this._fileDefaults);
		}.bind(this));
	},
	_alignItems: function () {
		var areaSize = $(this._element).width(),
			currentImageSize = areaSize / this._minImageCountPerLine;
		if (currentImageSize < this._minImageSize) {
			currentImageSize = areaSize / Math.floor(areaSize / this._minImageSize);
		}
		$(this._element).find(".content-preview")
			.css({width: currentImageSize, height: currentImageSize})
			.find("img").each(function (p, i) {
				var imageInfo;
				i = $(i);
				imageInfo = i.data("imageInfo");
				i.css({
					/* background: "url('" + src + "') 50% 50% no-repeat",*/
					/*backgroundSize: (imageInfo.width > 100 || imageInfo.height > 100) ? "contain" : "auto"*/
					"object-fit": (imageInfo.width > currentImageSize || imageInfo.height > currentImageSize) ? "cover" : "none"
				});
			})
	},
	_createImage: function (src, imageInfo) {

		return $("<img>").addClass("content-preview-image").attr("src", src).data("imageInfo", imageInfo);
	},
	_createInfo: function (info) {
		return $("<div>").addClass("content-preview-info").append(info);
	},
	_onSelect: function (element, data) {
		this._selectedItem = data;
		$(this._selectedElement).removeClass("selected");
		$(element).addClass("selected");
		this._selectedElement = element;
	}
};

Imcms.Content.FileAdapter = function (options) {
	this.init(Imcms.Utils.merge(options, this.defaults));
};
Imcms.Content.FileAdapter.prototype = {
	_element: {},
	_loader: {},
	_fileView: {},
	defaults: {
		element: null,
		loader: null
	},
	init: function (options) {
		this._element = options.element;
		this._loader = options.loader;
		this.buildView();
	},
	buildView: function (data) {
		if (!data) {
			this._loader.getAllFiles("", this.buildView.bind(this));
			return this;
		}
		this._fileView = new Imcms.Content.FileView(this._element, data);
	},
	changeFolder: function (folder) {
		this._loader.getAllFiles(folder, this._fileView.buildView.bind(this._fileView));
	},
	selected: function () {
		return this._fileView._selectedItem;
	}
};

Imcms.Content.FileUploader = function (options) {
	this._options = Imcms.Utils.merge(options, this._options);
	this._target = this._options.target;
	this.init();
};
Imcms.Content.FileUploader.prototype = {
	_target: {},
	_options: {
		target: undefined,
		onFileUploaded: function () {

		}
	},
	init: function () {
		var dragAndDropArea = $(this._target),
			that = this,
			draggingcount = 0,
			testContent = function (event) {
				return event.originalEvent.dataTransfer.types.some(function (val) {
					return "files" === val.toLowerCase()
				})
			};

		$(".editor-form").not("input[type=file]").on("dragenter", function (event) {
			if (!testContent(event)) return false;
			dragAndDropArea.find(".dropzone").addClass("hover");
			draggingcount++;
			event.stopPropagation();
			event.preventDefault();
			return false;
		});

		$(".editor-form").not("input[type=file]").on("dragover", function (event) {
			// dragAndDropArea.append($("<div>").addClass("dropzone"));
			if (!testContent(event)) return false;
			dragAndDropArea.find(".dropzone").addClass("hover");
			event.stopPropagation();
			event.preventDefault();
			return false;
		});

		$(".editor-form").not("input[type=file]").on("dragleave", function (event) {
			if (!testContent(event)) return false;
			draggingcount--;
			if (draggingcount === 0) {
				dragAndDropArea.find(".dropzone").removeClass("hover");
			}
			// dragAndDropArea.find(".dropzone").remove();
			event.stopPropagation();
			event.preventDefault();
			return false;
		});

		dragAndDropArea[0].ondrop = function (event) {
			event.preventDefault();
			// if (!testContent(event)) return false;
			dragAndDropArea.find(".dropzone").removeClass("hover");

			var postedFile = event.dataTransfer.files[0];

			if (postedFile.type != "image/jpeg" &&
				postedFile.type != "image/jpg" &&
				postedFile.type != "image/png" &&
				postedFile.type != "image/gif") {
				return false;
			}

			if (postedFile.size > 4194304) {
				return false;
			}

			var request = new XMLHttpRequest();

			//request.onreadystatechange = mt.stateChange;
			request.open("POST", Imcms.Linker.getContextPath() + "/api/content/files/" + postedFile.name);
			request.onreadystatechange = that._options.onFileUploaded;
			request.setRequestHeader("X-FILE-NAME", postedFile.name);
			request.setRequestHeader("contentType", "multipart/form-data");
			request.send(postedFile);
			// parent.mt.forms.active.setUploadingMode(postedFile.name);
		};
	},
	uploadFormFile: function (file) {
		var formData = new FormData();
		formData.append('file', file);
		$.ajax({
			url: Imcms.Linker.getContextPath() + "/api/content/files/" + file.name,
			data: formData,
			// THIS MUST BE DONE FOR FILE UPLOADING
			contentType: false,
			processData: false,
			type: "POST",
			success: this._options.onFileUploaded
		})
	}
};