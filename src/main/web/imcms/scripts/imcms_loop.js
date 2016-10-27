Imcms.Loop = {};
Imcms.Loop.API = function () {
};
Imcms.Loop.API.prototype = {
    path: Imcms.Linker.get("loop"),
    create: function (request, response) {
        $.ajax({
            url: this.path + window.location.search,
            type: "POST",
            data: request,
            success: response
        })
    },
    read: function (request, response) {
        $.ajax({
            url: this.path + window.location.search,
            type: "GET",
            data: request,
            success: response
        });
    },
    create2: function (request, response) {
        $.ajax({
            url: this.path + window.location.search,
            type: "PUT",
            data: request,
            success: response
        })
    },
    update: function (request, response) {
        $.ajax({
            url: this.path + window.location.search,
            type: "POST",
            data: request,
            success: response
        })
    }
};

Imcms.Loop.Loader = function () {
    this.init();
};
Imcms.Loop.Loader.prototype = {
    _api: new Imcms.Loop.API(),
    _editorList: [],
    init: function () {
        $(".editor-loop").each(function (pos, element) {
            this._editorList[pos] = new Imcms.Loop.Editor(element, this);
        }.bind(this));
    },
    create: function (name) {
        var that = this;
        this._api.create({name: name}, function (data) {
            if (!data.result) return;
            that.redirect(data.id);
        })
    },
    update: function (loops, loopId, callback) {
        this._api.update({
            loopId: loopId,
            meta: Imcms.document.meta,
            noArr: JSON.stringify(this.generateNo(loops))
        }, callback);
    },
    entriesList: function (data, callback) {
        this._api.read(Imcms.Utils.margeObjectsProperties(Imcms.document, data), function (response) {
            if (response && response.result) {
                callback(response.data)
            }
            else {
                callback({});
            }
        });
    },
    generateNo: function (loops) {
        var res = [];
        loops.forEach(function (loop) {
            res.push(loop['no']);
        });
        return res;
    }
};

Imcms.Loop.Editor = function (element, loader) {
    this._loader = loader;
    this._target = element;
    this.init();
};
Imcms.Loop.Editor.prototype = {
    _builder: {},
    _target: {},
    _loader: {},
    _frame: {},
    _loopListAdapter: {},
    init: function () {
        return this.buildView().buildLoopsList().buildExtra();
    },
    buildView: function () {
        this._builder = new JSFormBuilder("<DIV>")
            .form()
            .div()
            .class("imcms-header")
            .div()
            .html("Loop Editor")
            .class("imcms-title")
            .end()
            .button()
            .reference("closeButton")
            .class("imcms-close-button")
            .on("click", $.proxy(this.close, this))
            .end()
            /*
             .button()
             .html("Close without saving")
             .class("imcms-neutral close-without-saving")
             .on("click", $.proxy(this.close, this))
             .end()*/
            .end()
            .div()
            .class("imcms-content")
            .table()
            .reference("entriesList")
            .end()
            .end()
            .div()
            .class("imcms-footer")
            .button()
            .reference("createNew")
            .class("imcms-neutral create-new")
            .html("Create new")
            .end()
            .button()
            .html("Save and close")
            .class("imcms-positive imcms-save-and-close")
            .on("click", $.proxy(this.save, this))
            .end()
            .div()
            .class("clear")
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
            this._loader.entriesList({loopId: $(this._target).data().no}, $.proxy(this.buildLoopsList, this));
            return this;
        }
        this._loopListAdapter =
            new Imcms.Loop.ListAdapter(
                this._builder.ref("entriesList"),
                data
            );
        this._builder.ref("createNew").on("click", $.proxy(this._loopListAdapter.addLoop, this._loopListAdapter));
        return this;
    },
    buildExtra: function () {
        this._frame = new Imcms.FrameBuilder()
            .title("Loop Editor")
            .click($.proxy(this.open, this))
            .build()
            .prependTo(this._target);
        return this;
    },
    save: function () {
        var $element = $(this._target);

        this._loader.update(
            this._loopListAdapter.collect(),
            $element.data().no,
            Imcms.BackgroundWorker.createTask({
                showProcessWindow: true,
                reloadContent: {
                    element: $element,
                    callback: function () {
                        this.buildExtra();

                        $element.find($(".editor-image")).each(
                            Imcms.Editors.Image.initEditor.bind(Imcms.Editors.Image)
                        );

                        $element.find("[contenteditable='true']").each(
                            Imcms.Editors.Text.addEditor.bind(Imcms.Editors.Text)
                        );
                    }.bind(this)
                }
            })
        );
        this.close();
    },
    open: function () {
        $(this._builder[0]).fadeIn("fast").find(".imcms-content").css({height: $(window).height() - 95});
    },
    close: function () {
        $(this._builder[0]).fadeOut("fast");
    }
};

Imcms.Loop.ListAdapter = function (container, data) {
    this._container = container;
    this._data = data;
    this.init();
};
Imcms.Loop.ListAdapter.prototype = {
    _container: {},
    _ul: {},
    _data: {},
    init: function () {
        this.buildList(this._data);
    },
    buildList: function (data) {
        $.each(data, $.proxy(this.addLoopToList, this));
    },
    addLoopToList: function (position, data) {
        var deleteButton = $("<button>");
        this._container.row(data.no, data.text, deleteButton
            .addClass("imcms-negative")
            .attr("type", "button")[0]
        );
        var row = this._container.row(position);
        deleteButton
            .click($.proxy(this.deleteLoop, this, data, row));
    },
    deleteLoop: function (data, row) {
        $(row).remove();
        this._data.remove(data);
    },
    addLoop: function () {
        var length = this._data.length;
        this._data.push({no: length ? this._data[length - 1].no + 1 : 1, text: ""});
        this.addLoopToList(length, this._data[length]);
    },
    collect: function () {
        return this._data;
    }
};
