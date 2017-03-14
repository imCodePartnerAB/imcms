(function (Imcms) {
    var path = Imcms.Linker.get("loop") + window.location.search,
        simpleAPI = new Imcms.REST.API(path),
        API = {
            create: simpleAPI.post,
            read: simpleAPI.get,
            create2: simpleAPI.put,
            update: simpleAPI.post
        };

    var LoopListAdapter = function (container, data, loopId) {
        this._container = container.getHTMLElement();
        this._data = data;
        this._loopId = loopId;
        this.init();
    };
    LoopListAdapter.prototype = {
        _container: {},
        _ul: {},
        _data: {},
        init: function () {
            this.buildList(this._data);
            this.enableSorting();
        },
        buildList: function (data) {
            data.forEach(this.addLoopToList, this);
        },
        addLoopToList: function (data) {
            var deleteRowBtn = $("<button>")
                    .attr("type", "button")
                    .addClass("loop-editor-content__button")
                    .addClass("loop-editor-content__button_negative"),
                isEnabledCheckbox = $("<input>")
                    .attr("type", "checkbox")
                    .attr("name", "isEnabled")
                    .prop("checked", data.isEnabled)
                    .addClass("loop-editor-content__checkbox"),
                entryIndex = $("<div>")
                    .addClass("loop-editor-content__number")
                    .html(data.no),
                entryContent = $("<div>")
                    .addClass("loop-editor-content__content")
                    .html(data.text),
                entryActions = $("<div>")
                    .addClass("loop-editor-content__actions")
                    .append(isEnabledCheckbox)
                    .append(deleteRowBtn),
                entryRow = $("<li>")
                    .attr("data-entry-no", data.no)
                    .addClass("loop-editor-content__loop-entry")
                    .addClass("loop-editor-content__loop-entry_sortable")
                    .append(entryIndex)
                    .append(entryContent)
                    .append(entryActions)
                    .appendTo(this._container);

            deleteRowBtn.click(this.deleteLoop.bind(this, data, entryRow));
        },
        deleteLoop: function (data, $row) {
            $row.remove();
            this._data.remove(data);
        },
        enableSorting: function () {
            var sortOptions = {
                axis: "y",
                placeholder: "loop-editor-content__loop-entry loop-editor-content__loop-entry_empty",
                update: this.updateData.bind(this)
            };
            $(".loop-editor-content__list")
                .sortable(sortOptions)
                .disableSelection();
        },
        updateData: function () {
            this._data = this.collectOrderedEntryIndexes().map(this.findEntryByNo, this)
        },
        findEntryByNo: function (entryNo) {
            return this._data.find(function (entry) {
                return (entry.no === entryNo)
            });
        },
        addLoop: function () {
            var maxEntryNo = Math.max.apply(null, this.collectOrderedEntryIndexes()),
                nextEntryNo = (maxEntryNo && isFinite(maxEntryNo))
                    ? maxEntryNo + 1
                    : 1,
                newEntry = {
                    no: nextEntryNo,
                    isEnabled: true,
                    text: ""
                };
            this._data.push(newEntry);
            this.addLoopToList(newEntry);
        },
        collectOrderedEntryIndexes: function () {
            return $("[data-loop-id=" + this._loopId + "]")
                .sortable("toArray", {
                    attribute: 'data-entry-no'
                })
                .map(function (entryNoStr) {
                    return parseInt(entryNoStr, 10);
                });
        },
        collect: function () {
            return this._data.map(this.checkIsEnabled, this);
        },
        checkIsEnabled: function (entry) {
            entry.isEnabled = $(this._container)
                .find("li[data-entry-no=" + entry.no + "]")
                .find(".loop-editor-content__checkbox")
                .is(":checked");

            return entry;
        }
    };

    var Editor = function (element, loader) {
        this._loader = loader;
        this._target = element;
        this._loopId = $(element).data().no;
        this.init();
    };
    Editor.prototype = {
        _builder: {},
        _target: {},
        _loader: {},
        _frame: {},
        _loopListAdapter: {},
        init: function () {
            return this.buildView().buildExtra();
        },
        buildView: function () {
            this._builder = new JSFormBuilder("<DIV>")
                .form()
                .setClass("loop-editor")
                .div()
                .setClass("imcms-header")
                .div()
                .html("Loop Editor")
                .setClass("imcms-title")
                .end()
                .button()
                .reference("closeButton")
                .setClass("imcms-close-button")
                .on("click", this.close.bind(this))
                .end()
                .end()
                .div()
                .setClass("loop-editor-content")
                .ul()
                .attr("data-loop-id", this._loopId)
                .setClass("loop-editor-content__list")
                .reference("entriesList")
                .end()
                .end()
                .div()
                .setClass("loop-editor-footer")
                .button()
                .on("click", this.createNew.bind(this))
                .setClass("loop-editor-footer__button loop-editor-footer__button_neutral")
                .html("Create new")
                .end()
                .button()
                .html("Save and close")
                .setClass("loop-editor-footer__button loop-editor-footer__button_positive")
                .on("click", this.save.bind(this))
                .end()
                .div()
                .setClass("clear")
                .end()
                .end()
                .end();
            $(this._builder[0])
                .appendTo("body")
                .addClass("editor-form loop-viewer reset");
            return this;
        },
        buildLoopsList: function (data) {
            if (!data) {
                this._loader.entriesList({loopId: this._loopId}, this.buildLoopsList.bind(this));
                return this;
            }
            this._loopListAdapter = new LoopListAdapter(this._builder.ref("entriesList"), data, this._loopId);
            return this;
        },
        buildExtra: function () {
            this._frame = new Imcms.FrameBuilder()
                .title("Loop Editor")
                .click(this.open.bind(this))
                .build()
                .prependTo(this._target);
            return this;
        },
        save: function () {
            var $element = $(this._target),
                loopId = $element.data().no,
                orderedEntries = this._loopListAdapter.collect(),
                orderedIndexes = this._getOrderedIndexes(orderedEntries),
                orderedIsEnabledFlags = this._getOrderedIsEnabledFlags(orderedEntries);

            this._loader.update(
                orderedIndexes,
                orderedIsEnabledFlags,
                loopId,
                Imcms.BackgroundWorker.createTask({
                    showProcessWindow: true,
                    reloadElement: $element,
                    callback: Imcms.Editors.rebuildEditorsIn.bind(this, $element)
                })
            );
            this.close();
        },
        _getOrderedIndexes: function (orderedEntries) {
            return orderedEntries.map(function (entry) {
                return entry.no
            })
        },
        _getOrderedIsEnabledFlags: function (orderedEntries) {
            return orderedEntries.map(function (entry) {
                return entry.isEnabled
            })
        },
        open: function () {
            this.buildLoopsList().showEditorWindow();
        },
        showEditorWindow: function () {
            $(this._builder[0]).fadeIn("fast")
                .find(".imcms-content")
                .css({
                    height: $(window).height() - 95
                });
        },
        close: function () {
            $(this._builder[0]).fadeOut("fast");
            $(".loop-editor-content__loop-entry").remove();
        },
        createNew: function () {
            this._loopListAdapter.addLoop()
        }
    };

    Imcms.Loop = function () {
        this.init();
    };
    Imcms.Loop.prototype = {
        _editorList: [],
        init: function () {
            $(".editor-loop").each(function (pos, element) {
                this._editorList[pos] = new Editor(element, this);
            }.bind(this));
        },
        update: function (indexes, isEnabledFlags, loopId, callback) {
            API.update({
                loopId: loopId,
                meta: Imcms.document.meta,
                indexes: indexes,
                isEnabledFlags: isEnabledFlags
            }, callback);
        },
        entriesList: function (data, callback) {
            API.read(Imcms.Utils.margeObjectsProperties(Imcms.document, data), function (response) {
                callback((response && response.result) ? response.data : {});
            });
        }
    };
})(Imcms);
