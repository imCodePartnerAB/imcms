/**
 * Created by Shadowgun on 24.03.2015.
 */

Imcms.Content = {};
Imcms.Content.Loader = function () {
    this.init();
};
Imcms.Content.Loader.prototype = {
    _editor: {},
    init: function () {
        this._editor = new Imcms.Content.Editor(this);
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
            .class("header")
            .div()
            .html("Document Editor")
            .class("title")
            .end()
            .button()
            .html("Save and close")
            .class("positive save-and-close")
            .on("click", this.save.bind(this))
            .end()
            .button()
            .html("Close without saving")
            .class("neutral close-without-saving")
            .on("click", this.cancel.bind(this))
            .end()
            .end()
            .div()
            .class("content")
            .div()
            .reference("folders")
            .class("folders")
            .end()
            .div()
            .reference("files")
            .class("files")
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo("body").addClass("editor-form");
        return this;
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
        this._fileUploader = new Imcms.Content.FileUploader(this._builder.ref("files").getHTMLElement());
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
    open: function (option) {
        this._option = option;
        $(this._builder[0]).fadeIn("fast").find(".content").css({height: $(window).height() - 100});
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
    this.init(Imcms.Utils.marge(options, this.defaults));
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
            alert(node.name);
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
    _selectedElement: {},
    _selectedItem: {},
    init: function () {
        if (this._data) this.buildView(this._data);
    },
    buildView: function (data) {
        $(this._element).empty();
        $.each(data, this._buildItem.bind(this));
    },
    _buildItem: function (position, data) {
        var $div = $("<div>")
            .addClass("content-preview")
            .append(this._createImage(data.urlPathRelativeToContextPath, data.imageInfo))
            .append(this._createInfo(data.name));

        $div.appendTo(this._element).click(this._onSelect.bind(this, $div, data));
    },
    _createImage: function (src, imageInfo) {
        return $("<div>").addClass("content-preview-image")
            .css({
                background: "url('" + src + "') 50% 50% no-repeat",
                backgroundSize: (imageInfo.width > 100 || imageInfo.height > 100) ? "contain" : "auto"
            })
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
    this.init(Imcms.Utils.marge(options, this.defaults));
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

Imcms.Content.FileUploader = function (target) {
    this._target = target;
    this.init();
};
Imcms.Content.FileUploader.prototype = {
    _target: {},
    init: function () {
        var dragAndDropArea = $(this._target);

        dragAndDropArea[0].ondragover = function () {
            dragAndDropArea.addClass("hover");
            return false;
        };

        dragAndDropArea[0].ondragleave = function () {
            dragAndDropArea.removeClass("hover");
            return false;
        };

        dragAndDropArea[0].ondrop = function (event) {
            event.preventDefault();
            dragAndDropArea.removeClass("hover");

            var postedFile = event.dataTransfer.files[0];

            if (postedFile.type != "image/jpeg" &&
                postedFile.type != "image/png" &&
                postedFile.type != "image/gif") {
                return false;
            }

            if (postedFile.size > 4194304) {
                return false;
            }

            var request = new XMLHttpRequest();

            //request.onreadystatechange = mt.stateChange;
            request.open("POST", "/api/content/files/" + postedFile.name);
            request.onreadystatechange = function () {
                alert(request.responseText);
            };
            request.setRequestHeader("X-FILE-NAME", postedFile.name);
            request.setRequestHeader("contentType", "multipart/form-data");
            request.send(postedFile);
            // parent.mt.forms.active.setUploadingMode(postedFile.name);
        };
    }
};