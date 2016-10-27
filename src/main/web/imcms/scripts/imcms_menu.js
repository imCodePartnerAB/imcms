/*
 Menu Editer
 */
Imcms.Menu = {};

Imcms.Menu.TreeAdapter = function (options) {
	options = Imcms.Utils.merge(options, this._defaults);
	this._loader = options.loader;

	this.init(options.tree, options.data);
};
Imcms.Menu.TreeAdapter.prototype = {
	_tree: {},
	_data: {},
	_loader: {},
	_defaults: {
		tree: {},
		data: {},
		loader: {}
	},
	_sorting: 0,
	init: function (tree, data) {
		this._tree = $(tree);
		this._data = this.buildDataTree(data);
		this._tree.tree({
			selectable: false,
			data: this._data,
			autoOpen: true,
			dragAndDrop: true,
			onCreateLi: $.proxy(this.onCreateLi, this)
		}).bind('tree.move', this.onMoveNode.bind(this));
	},
	onCreateLi: function (node, $li) {
		var treeElement = $li.find('.jqtree-element').empty();
        var docId = node["doc-id"];
        $("<span>").text(docId).appendTo(treeElement);
		$("<span>").attr("data-name", "").text(node.name).appendTo(treeElement);
		$("<span>").text(node.status).appendTo(treeElement);

        if (node.status == "A") { // means archived document
            treeElement.addClass("archived");
        }

        $("<span>").addClass("column-right")
            .appendTo(treeElement)
            .append($('<input>')
                .click($.proxy(this.showPluralArchiveAndCopyButtons, this))
                .addClass("field menu-doc-checkbox")
                .attr("type", "checkbox")
                .attr("menu-doc-id", docId));

		$("<span>").addClass("column-right buttons").appendTo(treeElement).append(
			$("<button>")
				.addClass("imcms-neutral")
                .html("<a href ='" + Imcms.Linker.get("document", docId) + "' target='_blank'>open<a/> ")
				.attr("type", "button")
		).append(
			$("<button>")
				.addClass("imcms-neutral")
				.text("edit")
				.attr("type", "button")
				.click($.proxy(this.editDocument, this, node, $li))
		).append(
			$("<button>")
				.addClass("imcms-negative")
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

		setTimeout(this.sort.bind(this), 2);
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
	setSoring: function (sorting) {
		var needSorting = sorting && this._sorting !== sorting;

		this._sorting = sorting;

		if (needSorting) {
			this.sort();
		}
	},
	sort: function () {
		if (!this._sorting) {
			return;
		}

		var data = this._tree.tree("getTree"),
			result = [],
			comparator = function (a, b) {
				var result = 0;

				if (typeof (a[this._sorting] || b[this._sorting]) === typeof 0) {
					result = a[this._sorting] > b[this._sorting] ? 1 : a[this._sorting] === b[this._sorting] ? 0 : -1;
				}
				else {
					result = a[this._sorting].localeCompare(b[this._sorting]);
				}

				return result
			}.bind(this),
			postProcessor = function (it, index) {
				var children = [];

				it.position = index + 1;
				it.children.sort(comparator);
				it.children.forEach(postProcessor.bind(children));

				this.push({
					position: it.position,
					name: it.name,
					label: it.name,
					status: it.status,
					"doc-id": it["doc-id"],
					id: it.id,
					children: children
				});
			};

		data.children.sort(comparator);
		data.children.forEach(postProcessor.bind(result));

		this._tree.tree('loadData', result);
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
	editDocument: function (node, $li) {
		this._loader.getDocument(node["doc-id"], $.proxy(this.showDocumentViewer, this, node, $li));
	},
	showDocumentViewer: function (node, $li, data) {
		new Imcms.Document.Viewer({
			data: data,
			type: (+data.type) || undefined,
			loader: this._loader,
			target: $("body")[0],
			onApply: $.proxy(this.saveDocument, this, node, $li)
		});
	},
	saveDocument: function (node, $li, viewer) {
		var data = viewer.serialize();
		this._loader.updateDocument(data, $.proxy(this.reload, this, node, $li, data));
	},
	reload: function (node, $li, data) {
		//node.label = data.languages[Imcms.language.name].title;
		//$li.find("[data-name]").text(node.label);
	},
    showPluralArchiveAndCopyButtons: function () {
        var checked = $('input.menu-doc-checkbox')
            .filter(function (i, element) {
                return $(element).is(":checked");
            }).length;

        if (checked) {
            $('.pluralCopyArchMenuButton').show();
        } else {
            $('.pluralCopyArchMenuButton').hide();
        }
    }
};
Imcms.Menu.TreeAdapter.Sorting = {
	NONE: false,
	NAME: "name",
	ID: "doc-id"
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
		return this.buildEditor()
			.buildMenu()
			.buildExtra();
	},
	buildEditor: function () {
		this._builder = new JSFormBuilder("<div>")
			.form()
			.on("drop", this._onDrop.bind(this))
			.div()
			.on("drop", this._onDrop.bind(this))
			.class("imcms-header")
			.div()
			.on("drop", this._onDrop.bind(this))
			.html("Menu Editor " + $(this._target).data().meta + "-" + $(this._target).data().no)
			.class("imcms-title")
			.end()
			/*.button()
			 .on("click", $.proxy(this.close, this))
			 .html("Close without saving")
			 .class("imcms-neutral close-without-saving")
			 .reference("closeButton")
			 .end()*/
			.button()
			.reference("closeButton")
			.class("imcms-close-button")
			.on("click", $.proxy(this.close, this))
			.end()
			.end()
			.div()
			.class("imcms-content")
			.on("drop", this._onDrop.bind(this))
			.div()
			.on("drop", this._onDrop.bind(this))
			.reference("menuContent")
			.end()
			.end()
			.div()
			.class("imcms-footer")
			.reference("footer")
			.button()
			.html($.i18n.prop('menu.search'))
			.class("imcms-positive add")
			.on("click", $.proxy(this._openDocumentEditor, this))
			.end()
			.button()
			.html($.i18n.prop('menu.new'))
			.class("imcms-neutral create-new")
			.on("click", $.proxy(this._openDocumentViewer, this))
			.end()
			.div()
			.class("imcms-menu-sort-cases")
			.radio()
			.name("menu-sort-case")
			.value(Imcms.Menu.TreeAdapter.Sorting.NAME)
			.end()
			.button()
			.html($.i18n.prop('menu.sortAlphabet'))
			.class("imcms-neutral create-new")
			.on("click", $.proxy(this._sortItems, this))
			.end()
			.radio()
			.name("menu-sort-case")
			.value(Imcms.Menu.TreeAdapter.Sorting.ID)
			.end()
			.button()
			.html($.i18n.prop('menu.sortId'))
			.class("imcms-neutral create-new")
			.on("click", $.proxy(this._sortItems, this))
			.end()
			.end()
            .button()
            .class("imcms-positive hidden pluralCopyArchMenuButton")
            .html("Copy")
            .on("click", this.copyChecked.bind(this))
            .end()
            .button()
            .class("imcms-positive hidden pluralCopyArchMenuButton")
            .html("Archive")
            .on("click", this.archiveChecked.bind(this))
            .end()
			.button()
			.on("click", $.proxy(this.saveAndClose, this))
			.html("Save and close")
			.class("imcms-positive imcms-save-and-close")
			.reference("saveButton")
			.end()
			.div()
			.class("clear")
			.end()
			.end()
			.end();

		$(this._builder[0]).appendTo("body").addClass("editor-form reset");

		return this;
	},
	buildMenu: function () {
		this._loader.read($(this._target).data(), function (data) {
			var result = [];
			data.forEach(function (it) {
				var treePosition = it["treeSortIndex"].match(/[0-9]|\./) ?
					it["treeSortIndex"] : "" + (it.position + 1);

				result.push({
					id: it["name"],
					position: parseInt(treePosition
						.substr(treePosition.lastIndexOf(".") + 1)),
					"doc-id": it["documentId"],
					label: it["name"],
					name: it["name"],
					status: it["status"],
					"tree-index": treePosition
				});
			});
			this._treeAdapter = new Imcms.Menu.TreeAdapter({
				tree: this._builder.ref("menuContent").getHTMLElement(),
				data: result,
				loader: this._loader
			});
		}.bind(this));

		return this;
	},
	buildExtra: function () {
		this._frame = new Imcms.FrameBuilder()
			.title("Menu Editor")
			.tooltip($(this._target).data().no)
			.click($.proxy(this.open, this))
			.build()
			.prependTo(this._target);
		return this;
	},
	open: function () {
		$(this._builder[0]).fadeIn("fast").find(".imcms-content").css({height: $(window).height() - 95});
	},
	_sortItems: function (e) {
		var relatedRadio = $(e.target).parent().children("input[type=radio]").filter(function () {
				return $(this).next()[0] === e.target
			}),
			isChecked = relatedRadio.prop("checked");

		relatedRadio.prop("checked", !isChecked);
		this._treeAdapter.setSoring((!isChecked) ? relatedRadio.val() : false);
	},
	_addItem: function (data) {
		data = data || this._autocompleteAdapter.data();
		this._treeAdapter.add({
			id: data.label,
			"doc-id": data.id,
			label: data.label,
			name: data.label,
			status: data.status
		});
	},
	_openDocumentViewer: function () {
		new Imcms.Document.TypeViewer({
			loader: this._loader,
			onApply: function (data) {
				this._loader.getPrototype(data.parentDocumentId, function (doc) {
					new Imcms.Document.Viewer({
						data: doc,
						type: data.documentType,
						parentDocumentId: data.parentDocumentId,
						loader: this._loader,
						onApply: $.proxy(this._addMenuItemFromDocumentViewer, this),
						onCancel: function () {
						}
					});
				}.bind(this));
			}.bind(this)
		});
	},

	_openDocumentEditor: function () {
		Imcms.Editors.Document.show(true);
	},

	_onDrop: function (event) {
		event.preventDefault();
		var data = JSON.parse(event.dataTransfer.getData("data"));
		this._addItem(data);
	},
	_addMenuItemFromDocumentViewer: function (viewer) {
		var formData = viewer.serialize();

		this._loader.update(formData, $.proxy(function (answer) {
			if (!answer.result) return;
			this._addItem({id: answer.data.id, label: answer.data.languages[Imcms.language.name].title});
		}, this));
	},
    saveAndClose: function () {
        Imcms.Events.fire("imcmsEditorClose");
        $(this._builder[0]).hide();

        var $target = $(this._target);
        var response = Imcms.Utils.margeObjectsProperties(
            {data: JSON.stringify(this._treeAdapter.collect())},
            $target.data());

        this._loader.updateMenu(
            response,
            Imcms.BackgroundWorker.createTask({
                showProcessWindow: true,
                reloadContent: {
                    element: $target,
                    callback: Imcms.Editors.rebuildEditorsIn.bind(this, $target)
                }
            })
        );
    },
    close: function () {
        Imcms.Events.fire("imcmsEditorClose");
        $(this._builder[0]).hide();
        this._treeAdapter.reset();
    },
    copyChecked: function () {
        this.doWithAllCheckedDocs(function (id) {
            Imcms.Editors.Document.copyDocument(id, this.addItem.bind(this));
        }.bind(this));

        $('input.menu-doc-checkbox').removeProp("checked");
    },
    addItem: function (response) {
        if (!response.result) {
            return;
        }
        this._addItem(response.data);
    },
    archiveChecked: function () {
        this.doWithAllCheckedDocs(function (id) {
            Imcms.Editors.Document.archiveDocument(id);
            $($("input[menu-doc-id=" + id + "]").parents("div")[0]).addClass("archived")
        }.bind(this));

        $('input.menu-doc-checkbox').removeProp("checked");
    },
    doWithAllCheckedDocs: function (apply) {
        return $('input.menu-doc-checkbox')
            .filter(function (i, element) {
                return $(element).is(":checked");
            })
            .map(function (i, element) {
                apply($(element).attr("menu-doc-id"));
            })
    }
};

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
    datesList: function (id, callback) {
        $.ajax({
            url: Imcms.Linker.get("dateTimes.fill", id),
            type: "GET",
            success: callback
        });
    },
	languagesList: function (callback) {
		Imcms.Editors.Language.read(callback);
	},
	templatesList: function (callback) {
		Imcms.Editors.Template.read(callback);
	},
	rolesList: function (callback) {
		Imcms.Editors.Role.read(callback);
	},
	categoriesList: function (callback) {
		Imcms.Editors.Category.read(callback);
	},
	create: function () {
		Imcms.Editors.Document.create("document" + Math.random());
	},
	getPrototype: function (id, callback) {
		Imcms.Editors.Document.getPrototype(id, callback);
	},
	documentsList: function (callback) {
		Imcms.Editors.Document.documentsList(callback);
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
	/* delete: function () {
	 this._api.delete.apply(this._api, arguments);
	 },*/
	getDocument: function (id, callback) {
		Imcms.Editors.Document.getDocument(id, callback);
	},
	updateDocument: function (data, callback) {
		Imcms.Editors.Document.update(data, callback);
	},
    usersList: function (callback) {
        Imcms.Editors.User.read(callback);
    }
};

Imcms.Menu.API = function () {
};

Imcms.Menu.API.prototype = {
	delete: function (request, response) {
		$.ajax({
			url: Imcms.Linker.get("menu", request.meta, request.no),
			type: "DELETE",
			data: request,
			success: response
		})
	},

	update: function (request, response) {
		$.ajax({
			url: Imcms.Linker.get("menu", request.meta, request.no),
			type: "PUT",
			data: request,
			success: response
		})
	},

	read: function (request, response) {
		$.ajax({
			url: Imcms.Linker.get("menu", request.meta, request.no),
			type: "GET",
			data: request,
			success: response
		})
	},

	create: function (request, response) {
		$.ajax({
			url: Imcms.Linker.get("menu", request.meta, request.no),
			type: "POST",
			data: request,
			success: response
		})
	}
};
