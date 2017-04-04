(function (Imcms) {
    function isImageType(type) {
        return (type === "image/jpeg"
            || type === "image/jpg"
            || type === "image/png"
            || type === "image/gif"
        );
    }

    var contextPath = Imcms.Linker.getContextPath(),
        MAX_FILE_SIZE = 4194304; // 4MB, not sure why exactly this size

    function isFileTooLarge(fileSize) {
        return (fileSize > MAX_FILE_SIZE);
    }

    var TreeAdapter = function (options) {
        this.init(Imcms.Utils.merge(options, this.defaults));
    };
    TreeAdapter.prototype = {
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
                var node = event.node;
                this._options.onSelectedChanged(node);
            }
        },
        onCreateLi: function (node, $li) {
            var treeElement = $li.find('.jqtree-element').empty();
            $("<span>").css("float", "left").text(node.name).appendTo(treeElement);
        },
        onMoveNode: function (event) {
            var info = event.move_info,
                current = info.moved_node,
                parentFrom = current.parent,
                nodeTo = info.target_node,
                i, count, featurePosition, featureParent;

            this._options.onMoved(current, nodeTo);

            for (i = parentFrom.children.indexOf(current) + 1,
                     count = parentFrom.children.length; i < count; i++)
            {
                parentFrom.children[i].position--;
            }
            switch (info.position) {
                case "inside":
                    featureParent = nodeTo;
                    console.info("Parent node:");
                    console.info(nodeTo);
                    for (i = 0, count = featureParent.children.length; i < count; i++) {
                        featureParent.children[i].position++;
                        console.info(featureParent.children[i]);
                    }
                    current.position = 1;
                    break;
                case "before":
                    featurePosition = nodeTo.position - 1;
                    featureParent = nodeTo.parent;
                    console.info("Parent node:");
                    console.info(featureParent);
                    for (i = featureParent.children.indexOf(nodeTo),
                             count = featureParent.children.length; i < count; i++)
                    {
                        featureParent.children[i].position++;
                        console.info(featureParent.children[i]);
                    }
                    current.position = featurePosition;
                    break;
                case "after":
                    featurePosition = nodeTo.position + 1;
                    featureParent = nodeTo.parent;
                    console.info("Parent node:");
                    console.info(featureParent);
                    for (i = featureParent.children.indexOf(nodeTo) + 1,
                             count = featureParent.children.length; i < count; i++)
                    {
                        featureParent.children[i].position++;
                        console.info(featureParent.children[i]);
                    }
                    current.position = featurePosition;
                    break;
                default :
                    throw "Incorrect movement";
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
        remove: function (node) {
            this._tree.tree('removeNode', node);
        },
        add: function (node) {
            if (this._tree.tree('getNodeById', node.id)) {
                return;
            }

            var data = this._tree.tree("getTree");
            node.position = 0;

            for (var i = 0, count = data.children.length; i < count; i++)
            {
                node.position = node.position < data.children[i].position ?
                    data.children[i].position : node.position;
            }

            node.position++;
            this._tree.tree('appendNode', node);
        },
        reset: function () {
            this._tree.tree('loadData', this._data);
        },
        tree: function (opt) {
            return this._tree.tree(opt);
        }
    };

    var FileView = function (element, data) {
        this._element = element;
        this._data = data;
        this.init();
    };
    FileView.prototype = {
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
            if (this._data) {
                this.buildView(this._data);
            }
        },
        buildView: function (data) {
            $(this._element).empty().resize(this._alignItems.bind(this));
            this._prepareData(data);
            $.each(data, this._buildItem.bind(this));
            this._alignItems();
        },
        _buildItem: function (position, data) {
            var $div = $("<div>")
                .addClass("content-preview")
                .append(this._createImage(contextPath + data.urlPathRelativeToContextPath, data.imageInfo))
                .append(this._createInfo(data.name));

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
                .css({
                    width: currentImageSize,
                    height: currentImageSize
                })
                .find("img")
                .each(function () {
                    var $this = $(this),
                        imageInfo = $this.data("imageInfo"),
                        imageIsTooBig = (imageInfo.width > currentImageSize || imageInfo.height > currentImageSize);

                    $this.css({
                        "object-fit": (imageIsTooBig) ? "cover" : "none"
                    });
                })
        },
        _createImage: function (src, imageInfo) {
            return $("<img>").addClass("content-preview-image")
                .attr("src", src)
                .data("imageInfo", imageInfo);
        },
        _createInfo: function (info) {
            return $("<div>").addClass("content-preview-info").append(info);
        },
        _onSelect: function (element, data) {
            $((this._selectedItem = data)).removeClass("selected");
            $(element).addClass("selected");
            this._selectedElement = element;
        }
    };

    var FileAdapter = function (options) {
        this.init(Imcms.Utils.merge(options, this.defaults));
    };
    FileAdapter.prototype = {
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
            this._fileView = new FileView(this._element, data);
        },
        changeFolder: function (folder) {
            this._loader.getAllFiles(folder, this._fileView.buildView.bind(this._fileView));
        },
        selected: function () {
            return this._fileView._selectedItem;
        }
    };

    var FileUploader = function (options) {
        this._options = Imcms.Utils.merge(options, this._options);
        this._target = this._options.target;
        this.init();
    };
    FileUploader.prototype = {
        _target: {},
        _options: {
            target: undefined,
            onFileUploaded: function () {
            }
        },
        init: function () {
            var dragAndDropArea = $(this._target),
                that = this,
                draggingCount = 0,
                $editorForm = $(".editor-form"),
                preventEvent = function (event) {
                    event.stopPropagation();
                    event.preventDefault();
                },
                testContent = function (event) {
                    return event.originalEvent.dataTransfer.types.some(function (val) {
                        return "files" === val.toLowerCase()
                    });
                },
                testEventContentWithCallbackOnTrue = function (event, callback) {
                    if (testContent(event)) {
                        callback();
                        preventEvent(event);
                    }

                    return false;
                };

            $editorForm.not("input[type=file]").on("dragenter", function (event) {
                return testEventContentWithCallbackOnTrue(event, function () {
                    dragAndDropArea.find(".dropzone").addClass("hover");
                    draggingCount++;
                });
            });

            $editorForm.not("input[type=file]").on("dragover", function (event) {
                return testEventContentWithCallbackOnTrue(event, function () {
                    dragAndDropArea.find(".dropzone").addClass("hover");
                });
            });

            $editorForm.not("input[type=file]").on("dragleave", function (event) {
                return testEventContentWithCallbackOnTrue(event, function () {
                    draggingCount--;
                    if (draggingCount === 0) {
                        dragAndDropArea.find(".dropzone").removeClass("hover");
                    }
                });
            });

            dragAndDropArea[0].ondrop = function (event) {
                event.preventDefault();
                dragAndDropArea.find(".dropzone").removeClass("hover");

                var postedFile = event.dataTransfer.files[0];

                if ((!isImageType(postedFile.type)) || isFileTooLarge(postedFile.size)) {
                    return false;
                }

                var request = new XMLHttpRequest();

                request.open("POST", contextPath + "/api/content/files/" + postedFile.name);
                request.onreadystatechange = that._options.onFileUploaded.bind(this);
                request.setRequestHeader("X-FILE-NAME", postedFile.name);
                request.setRequestHeader("contentType", "multipart/form-data");
                request.send(postedFile);
            };
        },
        uploadFormFile: function (file) {
            Imcms.Editors.File.addFile(file, "", this._options.onFileUploaded.bind(this));
        }
    };

    var Editor = function (loader) {
        this._loader = loader;
        this.init();
    };
    Editor.prototype = {
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
                .setClass("imcms-header")
                .div()
                .html("Content Manager")
                .setClass("imcms-title")
                .end()
                .button()
                .reference("closeButton")
                .setClass("imcms-close-button")
                .on("click", this.cancel.bind(this))
                .end()
                /*
                 .button()
                 .html("Close without saving")
                 .setClass("imcms-neutral close-without-saving")
                 .on("click", this.cancel.bind(this))
                 .end()*/
                .end()
                .div()
                .setClass("imcms-content")
                .div()
                .reference("folders")
                .setClass("folders")
                .end()
                .div()
                .setClass("files-wrapper")
                .reference("files-wrapper")
                .div()
                .setClass("dropzone")
                .end()
                .div()
                .reference("files")
                .setClass("files")
                .end()
                .end()
                .end()
                .div()
                .setClass("imcms-footer")
                .div()
                .setClass("browse-image")
                .file()
                .name("image-upload")
                .on("change", this._onFileChosen.bind(this))
                .end()
                .file()
                .setClass("hidden")
                .on("change", function () {
                    var fileToUpload = $(this._builder[0]).find("input[type=file].hidden")[0].files[0];
                    this._fileUploader.uploadFormFile(fileToUpload);
                }.bind(this))
                .end()
                .end()
                .button()
                .html("Apply")
                .setClass("imcms-positive imcms-save-and-close")
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

            Imcms.BackgroundWorker.createTask({showProcessWindow: true})();
            var folderTree = this._foldersTree.tree('getSelectedNode'),
                folderId = this._escapeMainFolder(folderTree).id,
                folder = unescape(encodeURIComponent(folderId));
            this._loader.addPictureFile(file, folder, this._onFileUploaded.bind(this));

            $("input[type=file][name=image-upload]").val("")
        },
        buildFoldersTree: function () {
            this._foldersTree = new TreeAdapter({
                tree: this._builder.ref("folders").getHTMLElement(),
                loader: this._loader,
                onSelectedChanged: this._onSelectedFolderChanged.bind(this),
                onMoved: this._onFolderMoved.bind(this)
            });
            return this;
        },
        buildFileView: function () {
            this._fileAdapter = new FileAdapter({
                element: this._builder.ref("files").getHTMLElement(),
                loader: this._loader
            });
            this._fileUploader = new FileUploader({
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
            this._loader.moveFolder(
                unescape(encodeURIComponent(moved.fullPath)),
                unescape(encodeURIComponent(moved.name)),
                encodeURIComponent(destinaton.id)
            );
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
            $(this._builder[0]).fadeIn("fast")
                .find(".imcms-content")
                .css({
                    height: $(window).height() - 95
                });
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

    Imcms.Content = {};
    Imcms.Content.Loader = function () {
        this.init();
    };
    Imcms.Content.Loader.prototype = {
        _editor: {},
        init: function () {
            this._editor = new Editor(this);
        },
        addPictureFile: function (file, folder, callback) {
            if (!isImageType(file.type)) {
                throw "Wrong file extension!"; // todo: show validation error message
            }

            if (isFileTooLarge(file.size)) {
                throw "File is too large!"; // todo: show validation error message
            }

            Imcms.Editors.File.addFile(file, folder, callback);
        },
        getAllPictures: function (folder, callback) {
            Imcms.Editors.File.getAllPictures(folder, callback)
        },
        getAllFiles: function (folder, callback) {
            Imcms.Editors.File.getAll(folder, callback)
        },
        getAllFolders: function (folder, callback) {
            Imcms.Editors.Folder.getAll(folder, callback)
        },
        moveFolder: function (folder, childName, folderTo, callback) {
            Imcms.Editors.Folder.move(folder, childName, folderTo, callback)
        },
        showDialog: function (options) {
            this._editor.open(options);
        }
    };

    return Imcms.Content.Loader;
})(Imcms);
