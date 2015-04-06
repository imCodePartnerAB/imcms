/*
 Menu Editer
 */
Imcms.Menu = {};

Imcms.Menu.TreeAdapter = function (tree, data) {
    this.init(tree, data);
};
Imcms.Menu.TreeAdapter.prototype = {
    _tree: {},
    _data: {},
    init: function (tree, data) {
        this._tree = $(tree);
        this._data = this.buildDataTree(data);
        this._tree.tree({
            selectable: false,
            data: this._data,
            autoOpen: true,
            dragAndDrop: true,
            onCreateLi: $.proxy(this.onCreateLi, this)
        }).bind('tree.move', this.onMoveNode);
    },
    onCreateLi: function (node, $li) {
        var treeElement = $li.find('.jqtree-element').empty();
        $("<span>").text(node["doc-id"]).appendTo(treeElement);
        $("<span>").text(node.name).appendTo(treeElement);
        $("<span>").appendTo(treeElement).append(
            $("<button>")
                .addClass("negative")
                .attr("type", "button")
                .click($.proxy(this.delete, this, node))
        );
    },
    onMoveNode: function (event) {
        var info = event.move_info;
        var current = info.moved_node;
        var parentFrom = current.parent;
        var nodeTo = info.target_node;
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
    buildDataTree: function (floatData) {
        var itemLevel,
            tree = [],
            currentLevel = 1,
            current = null,
            item;
        while (item = floatData[0]) {
            itemLevel = (item["tree-index"].match(/\./g) || []).length + 1;
            if (!current || itemLevel == currentLevel) {
                current = item;
                current["children"] = [];
                currentLevel = itemLevel;
                tree.push(current);
                floatData.shift();
            } else if (itemLevel > currentLevel && itemLevel - currentLevel == 1) {
                current["children"] = this.buildDataTree(floatData);
            } else {
                break;
            }
        }
        return tree;
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

Imcms.Menu.DialogAdapter = function (source) {
    this._source = source;
    this.init();
};
Imcms.Menu.DialogAdapter.prototype = {
    _source: null,
    _builder: {},
    _dialog: {},
    _selectedRow: {},
    _callback: function () {
    },
    init: function () {
        this.buildContent();
        this.buildDialog();
    },
    buildContent: function () {
        var that = this;
        this._builder = JSFormBuilder("<DIV>")
            .form()
            .class("editor-menu-form")
            .fieldset()
            .div()
            .text()
            .on("input", function () {
                that.find(this.value());
            })
            .reference("searchField")
            .label("Document name")
            .end()
            .end()
            .div()
            .table()
            .on("click", $.proxy(this._onSelectElement, this))
            .column("id")
            .column("label")
            .column("language")
            .column("alias")
            .reference("documentsTable")
            .end()
            .end()
            .end()
            .end();
        this.find();
    },
    buildDialog: function () {
        this._dialog = $(this._builder[0]).dialog({
            autoOpen: false,
            height: 500,
            width: 700,
            modal: true,
            buttons: {
                "Add selected": $.proxy(this._onApply, this),
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });
    },
    open: function () {
        this._dialog.dialog("open");
    },
    find: function (word) {
        this._source({term: word || ""}, $.proxy(this.fillDataToTable, this));
    },
    fillDataToTable: function (data) {
        this._builder.ref("documentsTable").clear();
        for (var rowId in data)
            this._builder.ref("documentsTable").row(data[rowId]);
    },
    result: function (callback) {
        this._callback = callback;
        return this;
    },
    _onApply: function () {
        var resultData = {id: this._selectedRow.children[0].innerHTML, label: this._selectedRow.children[1].innerHTML};
        this._callback(resultData);
        this._dialog.dialog("close");
    },
    _onSelectElement: function (e) {
        var element = document.elementFromPoint(e.pageX, e.pageY);
        if (this._selectedRow)
            this._selectedRow.className = "";
        this._selectedRow = element.parentElement;
        this._selectedRow.className = "clicked";
    }
};

Imcms.Menu.AutocompleteAdapter = function (element, source) {
    this._element = element;
    this._source = source;
    this.init();
};
Imcms.Menu.AutocompleteAdapter.prototype = {
    _source: {},
    _selected: {},
    _element: {},
    cache: {},
    init: function () {
        $(this._element)
            .val("")
            .attr("title", "")
            .attr("placeholder", "Type to find document")
            .autocomplete({
                delay: 0,
                minLength: 0,
                source: $.proxy(this._find, this),
                select: $.proxy(this._onSelect, this)
            })
            .tooltip({
                tooltipClass: "ui-state-highlight"
            });
    },
    data: function () {
        return this._selected;
    },
    _find: function (request, response) {
        var that = this;
        var term = request.term;
        if (term in this.cache) {
            response(this.cache[term]);
            return;
        }
        this._source(request, function (data) {
            that.cache[term] = data;
            response(data);
        });
    },
    find: function (word, callback) {
        this._find({term: word}, callback);
    },
    _onSelect: function (event, ui) {
        this._selected = ui.item;
    }
};

Imcms.Menu.Editor = function (element, loader) {
    this._target = $(element);
    this._loader = loader;
    return this.init();
};
Imcms.Menu.Editor.prototype = {
    _loader: {},
    _target: {},
    _frame: {},
    _builder: {},
    _treeAdapter: {},
    _dialogAdapter: {},
    _autocompleteAdapter: {},
    init: function () {
        this._dialogAdapter = new Imcms.Menu.DialogAdapter($.proxy(this._loader.read, this._loader))
            .result($.proxy(this._addItem, this));
        return this.buildEditor()
            .buildMenu()
            .buildExtra();
    },
    buildEditor: function () {
        this._builder = new JSFormBuilder("<div>")
            .form()
            .div()
            .class("header")
            .div()
            .html("Menu Editor")
            .class("title")
            .end()
            .button()
            .on("click", $.proxy(this.saveAndClose, this))
            .html("Save and close")
            .class("positive save-and-close")
            .reference("saveButton")
            .end()
            .button()
            .on("click", $.proxy(this.close, this))
            .html("Close without saving")
            .class("neutral close-without-saving")
            .reference("closeButton")
            .end()
            .end()
            .div()
            .class("content")
            .div()
            .reference("menuContent")
            .end()
            .end()
            .div()
            .class("footer")
            .reference("footer")
            .text()
            .reference("findDocument")
            .end()
            .button()
            .html("…")
            .class("neutral browse")
            .on("click", $.proxy(this._dialogAdapter.open, this._dialogAdapter))
            .end()
            .button()
            .html("Add")
            .class("positive add")
            .on("click", $.proxy(function () {
                this._addItem();
            }, this))
            .end()
            .button()
            .html("Create new…")
            .class("neutral create-new")
            .on("click", $.proxy(this._openDocumentViewer, this))
            .end()
            .div()
            .class("clear")
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo("body").addClass("editor-form");
        return this;
    },
    buildMenu: function () {
        var data = [];
        this._target.find(".editor-menu-item").each(function (position, item) {
                item = $(item);
                var menuItemData = item.data().prettify();
                var treePosition = menuItemData["menu-item-tree-position"].match(/[0-9]|\./) ?
                    menuItemData["menu-item-tree-position"] : "" + (position + 1);
                data.push({
                    id: menuItemData["menu-item-name"],
                    position: parseInt(treePosition
                        .substr(treePosition.lastIndexOf(".") + 1)),
                    "doc-id": menuItemData["menu-item-id"],
                    label: menuItemData["menu-item-name"],
                    name: menuItemData["menu-item-name"],
                    "tree-index": treePosition
                });
            }
        );
        this._treeAdapter = new Imcms.Menu.TreeAdapter(this._builder.ref("menuContent").getHTMLElement(), data);
        return this;
    },
    buildExtra: function () {
        this._autocompleteAdapter = new Imcms.Menu
            .AutocompleteAdapter(this._builder.ref("findDocument").getHTMLElement(),
            $.proxy(this._loader.read, this._loader));
        this._frame = new Imcms.FrameBuilder()
            .title("Menu Editor")
            .click($.proxy(this.open, this))
            .build()
            .appendTo(this._target);
        return this;
    },
    open: function () {
        $(this._builder[0]).fadeIn("fast").find(".content").css({height: $(window).height() - 100});
    },
    _addItem: function (data) {
        data = data || this._autocompleteAdapter.data();
        this._treeAdapter.add({id: data.label, "doc-id": data.id, label: data.label, name: data.label});
    },
    _openDocumentViewer: function () {
        new Imcms.Document.Viewer({
            loader: this._loader,
            onApply: $.proxy(this._addMenuItemFromDocumentViewer, this),
            onCancel: function () {
            }
        })
    },
    _addMenuItemFromDocumentViewer: function (viewer) {
        var data = viewer.serialize();
        this._loader.update(JSON.stringify(data), $.proxy(function (answer) {
            if (!answer.result) return;
            this._addItem({id: answer.data.id, label: data.languages[Imcms.language.name].title});
        }, this));
    },
    saveAndClose: function () {
        $(this._builder[0]).hide();
        var response = Imcms.Utils.margeObjectsProperties(
            {items: this._treeAdapter.collect()},
            Imcms.document,
            this._target.data().prettify());
        this._loader.updateMenu({data: JSON.stringify(response)}, function () {
            location.reload();
        });
    }
    ,
    close: function () {
        $(this._builder[0]).hide();
        this._treeAdapter.reset();
    }
}
;

Imcms.Menu.Loader = function () {
    this.init();
};
Imcms.Menu.Loader.prototype = {
    _menuHelpers: [],
    _api: {},
    init: function () {
        var that = this;
        this._api = new Imcms.Menu.API();
        jQuery(".editor-menu").each(function (pos, element) {
            element = $(element);
            that._menuHelpers[pos] = new Imcms.Menu.Editor(element, that);
        });
    },
    languagesList: function (callback) {
        Imcms.Editors.Language.read(callback);
    },
    templatesList: function (callback) {
        Imcms.Editors.Template.read(callback);
    },
    create: function () {
        Imcms.Editors.Document.create("document" + Math.random());
    },
    read: function () {
        this._api.read.apply(this._api, arguments);
    },
    update: function (data, callback) {
        Imcms.Editors.Document.update(data, callback);
    },
    updateMenu: function () {
        this._api.update.apply(this._api, arguments);
    },
    delete: function () {
        this._api.delete.apply(this._api, arguments);
    }
};

Imcms.Menu.API = function () {
};

Imcms.Menu.API.prototype = {
    path: "/editmenu",

    delete: function (request, response) {
        $.ajax({
            url: this.path,
            type: "DELETE",
            data: request,
            success: response
        })
    },

    update: function (request, response) {
        $.ajax({
            url: this.path,
            type: "PUT",
            data: request,
            success: response
        })
    },

    read: function (request, response) {
        $.ajax({
            url: this.path,
            type: "GET",
            data: request,
            success: response
        })
    },

    create: function (request, response) {
        $.ajax({
            url: this.path,
            type: "POST",
            data: request,
            success: response
        })
    }

};
