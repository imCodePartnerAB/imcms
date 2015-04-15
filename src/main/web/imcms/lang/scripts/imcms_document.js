/**
 * Created by Shadowgun on 17.02.2015.
 */
Imcms.Document = {};
Imcms.Document.API = function () {

};
Imcms.Document.API.prototype = {
    path: "/api/document",
    create: function (request, response) {
        $.ajax({
            url: this.path,
            type: "POST",
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
        });
    },
    create2: function (request, response) {
        $.ajax({
            url: this.path,
            type: "PUT",
            data: request,
            success: response
        })
    },
    delete: function (request, response) {
        $.ajax({
            url: this.path + "/" + request,
            type: "DELETE",
            success: response
        })
    }

};

Imcms.Document.Loader = function () {
    this.init();
};
Imcms.Document.Loader.prototype = {
    _api: new Imcms.Document.API(),
    _editor: {},
    init: function () {
        this._editor = new Imcms.Document.Editor(this);
    },
    create: function (name) {
        var that = this;
        this._api.create({name: name}, function (data) {
            if (!data.result) return;
            that.redirect(data.id);
        })
    },
    update: function (data, callback) {
        this._api.create2({data: data}, callback);
    },
    documentsList: function (callback) {
        this._api.read({}, callback);
    },
    getDocument: function (id, callback) {
        this._api.read({id: id}, callback);
    },
    deleteDocument: function (data) {
        this._api.delete(data, function () {
        });
    },
    languagesList: function (callback) {
        Imcms.Editors.Language.read(callback);
    },
    templatesList: function (callback) {
        Imcms.Editors.Template.read(callback);
    },
    redirect: function (id) {
        location.href = "/imcms/docadmin?meta_id=" + id;
    }
};

Imcms.Document.Editor = function (loader) {
    this._loader = loader;
    this.init();
};
Imcms.Document.Editor.prototype = {
    _builder: {},
    _loader: {},
    _documentListAdapter: {},
    init: function () {
        return this.buildView().buildDocumentsList();
    },
    buildView: function () {
        this._builder = new JSFormBuilder("<DIV>")
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
            .end()
            .button()
            .html("Close without saving")
            .class("neutral close-without-saving")
            .end()
            .end()
            .div()
            .class("content")
            .table()
            .reference("documentsList")
            .end()
            .end()
            .div()
            .class("footer")
            .button()
            .class("neutral create-new")
            .html("Create new…")
            .on("click", $.proxy(this.showDocumentViewer, this))
            .end()
            .div()
            .class("clear")
            .end()
            .end()
            .end();
        $(this._builder[0])
            .appendTo("body")
            .addClass("editor-form");
        return this;
    },
    buildDocumentsList: function () {
        this._documentListAdapter =
            new Imcms.Document.ListAdapter(
                this._builder.ref("documentsList"),
                this._loader
            );
    },
    showDocumentViewer: function () {
        new Imcms.Document.Viewer({
            loader: this._loader,
            target: $("body"),
            onApply: $.proxy(this.onApply, this)
        });
    },
    onApply: function (viewer) {
        var data = viewer.serialize();
        this._loader.update(JSON.stringify(data), $.proxy(function () {
            this._documentListAdapter.reload();
        }, this));
    },
    open: function () {
        $(this._builder[0]).fadeIn("fast").find(".content").css({height: $(window).height() - 100});
    }
};

Imcms.Document.Viewer = function (options) {
    this.init(Imcms.Utils.marge(options, this.defaults));
};
Imcms.Document.Viewer.prototype = {
    _loader: {},
    _target: {},
    _builder: {},
    _modal: {},
    _options: {},
    _title: "",
    _activeContent: {},
    _contentCollection: {},
    defaults: {
        data: null,
        loader: {},
        target: {},
        onApply: function () {
        },
        onCancel: function () {
        }
    },
    init: function (options) {
        this._options = options;
        this._loader = options.loader;
        this._target = options.target;
        this._title = options.data ? "DOCUMENT " + options.data.id : "NEW DOCUMENT";
        this.buildView();
        this.createModal();
        this._loader.languagesList($.proxy(this.loadLanguages, this));
        this._loader.templatesList($.proxy(this.loadTemplates, this));
        if (options.data)
            this.deserialize(options.data);
        var $builder = $(this._builder[0]);
        $builder.fadeIn("fast").css({
            left: $(window).width() / 2 - $builder.width() / 2,
            top: $(window).height() / 2 - $builder.height() / 2
        });
        $(this._modal).fadeIn("fast");
    },
    buildView: function () {
        this._builder = JSFormBuilder("<div>")
            .form()
            .div()
            .class("title")
            .html(this._title)
            .hidden()
            .name("id")
            .end()
            .end()
            .div()
            .class("content with-tabs")
            .div()
            .class("tabs")
            .reference("tabs")
            .end()
            .div()
            .class("pages")
            .reference("pages")
            .end()
            .div()
            .class("buttons")
            .button()
            .class("positive")
            .html("OK")
            .on("click", $.proxy(this.apply, this))
            .end()
            .button()
            .class("neutral")
            .html("Cancel")
            .on("click", $.proxy(this.cancel, this))
            .end()
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo($("body")).addClass("document-viewer pop-up-form");
        this.buildLifeCycle();
        this.buildAppearance();
        this.buildAccess();
    },
    buildLifeCycle: function () {
        this._builder.ref("tabs")
            .div()
            .reference("life-cycle-tab")
            .class("life-cycle-tab tab active")
            .html("Life Cycle")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("life-cycle-page")
            .class("life-cycle-page page active")
            .div()
            .class("select")
            .select()
            .name("status")
            .option("In Process", "0")
            .option("Disapproved", "1")
            .option("Approved", "2")
            .option("Published", "3")
            .option("Archived", "4")
            .option("Expired", "5")
            .end()
            .end()
            .end();
        this._contentCollection["life-cycle"] = {
            tab: this._builder.ref("life-cycle-tab"),
            page: this._builder.ref("life-cycle-page")
        };
        this._activeContent = this._contentCollection["life-cycle"];
        this._builder.ref("life-cycle-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["life-cycle"]));
    },
    buildAppearance: function () {
        this._builder.ref("tabs")
            .div()
            .reference("appearance-tab")
            .class("appearance-tab tab")
            .html("Appearance")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("appearance-page")
            .class("appearance-page page")
            .div()
            .class("language")
            .reference("languages")
            .end()
            .div()
            .class("link-action")
            .select()
            .name("target")
            .label("Show in")
            .option("Same frame", "_self")
            .option("New window", "_blank")
            .option("Replace all", "_top")
            .end()
            .end()
            .div()
            .class("alias")
            .div()
            .class("field")
            .text()
            .name('alias')
            .label("/")
            .end()
            .end()
            .div()
            .class("select")
            .select()
            .name("template")
            .label("Template")
            .reference("templates")
            .end()
            .end()
            .end()
            .end();

        this._contentCollection["appearance"] = {
            tab: this._builder.ref("appearance-tab"),
            page: this._builder.ref("appearance-page")
        };
        this._builder.ref("appearance-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["appearance"]));
    },
    buildAccess: function () {
        this._builder.ref("tabs")
            .div()
            .reference("access-tab")
            .class("access-tab tab")
            .html("Access")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("access-page")
            .class("access-page page")
            .table()
            .reference("access")
            .column("Role")
            .column("View")
            .column("Grant")
            .row(
            "User",
            $("<input>")
                .attr("type", "radio")
                .val("view")
                .attr("name", "user-access")[0],
            $("<input>")
                .attr("type", "radio")
                .val("grant")
                .attr("name", "user-access")[0]
        )
            .end()
            .end();
        this._contentCollection["access"] = {
            tab: this._builder.ref("access-tab"),
            page: this._builder.ref("access-page")
        };
        this._builder.ref("access-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["access"]));
    },
    createModal: function () {
        $(this._modal = document.createElement("div")).addClass("modal")
            .click($.proxy(this.cancel, this))
            .appendTo($("body"));
    },
    changeTab: function (collectionItem) {
        $(this._activeContent.tab.getHTMLElement()).removeClass("active");
        $(this._activeContent.page.getHTMLElement()).removeClass("active");

        $(collectionItem.tab.getHTMLElement()).addClass("active");
        $(collectionItem.page.getHTMLElement()).addClass("active");
        this._activeContent = collectionItem;
    },
    loadTemplates: function (data) {
        $.each(data, $.proxy(this.addTemplate, this));
        if (this._options.data)
            this.deserialize(this._options.data);
    },
    addTemplate: function (key, name) {
        this._builder.ref("templates").option(key, name);
    },
    loadLanguages: function (id) {
        $.each(id, $.proxy(this.addLanguage, this));
        if (this._options.data)
            this.deserialize(this._options.data);
    },
    addLanguage: function (language, code) {
        this._builder.ref("languages")
            .div()
            .div()
            .class("checkbox")
            .checkbox()
            .attr("data-node-key", "language")
            .attr("data-node-value", language)
            .label(language)
            .name("enabled")
            .end()
            .end()
            .hidden()
            .attr("data-node-key", "language")
            .attr("data-node-value", language)
            .name("code")
            .value(code)
            .end()
            .div()
            .class("field")
            .text()
            .attr("data-node-key", "language")
            .attr("data-node-value", language)
            .label("Title")
            .name("title")
            .end()
            .end()
            .div()
            .class("field")
            .textarea()
            .attr("data-node-key", "language")
            .attr("data-node-value", language)
            .label("Menu text")
            .name("menu-text")
            .rows(3)
            .end()
            .end()
            .div()
            .class("field")
            .text()
            .attr("data-node-key", "language")
            .attr("data-node-value", language)
            .label("Link to image")
            .name("image")
            .end()
            .end()
            .end();
    },
    apply: function () {
        this._options.onApply(this);
        this.destroy();
    },
    cancel: function () {
        this._options.onCancel(this);
        this.destroy();
    },
    destroy: function () {
        $(this._builder[0]).remove();
        $(this._modal).remove();
    },
    serialize: function () {
        var result = {};
        var $source = $(this._builder[0]);
        $source.find("[name]").filter(function () {
            return !$(this).attr("data-node-key");
        }).each(function () {
            var $this = $(this);
            result[$this.attr("name")] = $this.val();
        });
        result["languages"] = {};
        $source.find("[data-node-key=language]").each(function () {
            var $dataElement = $(this);
            var language = $dataElement.attr("data-node-value");
            if (!Object.prototype.hasOwnProperty.call(result["languages"], language))
                result["languages"][language] = {};
            result["languages"][language][$dataElement.attr("name")] = $dataElement.attr("name") === "enabled" ?
                ($dataElement.is(":checked") ? true : false) : $dataElement.val();
        });
        return result;
    },
    deserialize: function (data) {
        var $source = $(this._builder[0]);
        $source.find("[name]").filter(function () {
            return !$(this).attr("data-node-key");
        }).each(function () {
            var $this = $(this);
            $this.val(data[$this.attr("name")]);
        });
        $source.find("[data-node-key=language]").each(function () {
            var $dataElement = $(this);
            var language = $dataElement.attr("data-node-value");
            if (Object.prototype.hasOwnProperty.call(data.languages, language)) {
                if ($dataElement.attr("name") === "enabled")
                    $dataElement.prop('checked', data.languages[language][$dataElement.attr("name")]);
                else
                    $dataElement.val(data.languages[language][$dataElement.attr("name")]);
            }
        });
        this._builder.ref("access").clear();
        $.each(data.access, function (key, value) {
            this._builder.ref("access")
                .row(
                key,
                $("<input>")
                    .attr("type", "radio")
                    .attr("value", 3)
                    .attr("name", key + "-access")[0],
                $("<input>")
                    .attr("type", "radio")
                    .attr("value", 0)
                    .attr("name", key + "-access")[0]
            );
            $("input[name=" + key + "-access]").filter("[value=" + value + "]").prop("checked", true);
        }.bind(this));
    }
};

Imcms.Document.ListAdapter = function (container, loader) {
    this._container = container;
    this._loader = loader;
    this.init();
};
Imcms.Document.ListAdapter.prototype = {
    _container: {},
    _ul: {},
    _loader: {},
    init: function () {
        this._loader.documentsList($.proxy(this.buildList, this));
    },
    buildList: function (data) {
        $.each(data, $.proxy(this.addDocumentToList, this));
    },
    addDocumentToList: function (position, data) {
        var deleteButton = $("<button>");
        this._container.row(data.id, data.label, data.alias, data.type, $("<span>")
                .append($("<button>")
                    .click($.proxy(this.editDocument, this, data.id))
                    .addClass("positive")
                    .text("Edit…")
                    .attr("type", "button"))
                .append(deleteButton
                    .addClass("negative")
                    .attr("type", "button"))[0]
        );
        var row = this._container.row(position);
        deleteButton
            .click($.proxy(this.deleteDocument, this, data.id, row));
    },
    reload: function () {
        this._container.clear();
        this._loader.documentsList($.proxy(this.buildList, this));
    },
    deleteDocument: function (id, row) {
        this._loader.deleteDocument(id);
        $(row).remove();
    },
    editDocument: function (id) {
        this._loader.getDocument(id, $.proxy(this.showDocumentViewer, this));
    },
    showDocumentViewer: function (data) {
        new Imcms.Document.Viewer({
            data: data,
            loader: this._loader,
            target: $("body")[0],
            onApply: $.proxy(this.saveDocument, this)
        });
    },
    saveDocument: function (viewer) {
        this._loader.update(JSON.stringify(viewer.serialize()), $.proxy(this.reload, this));
    }
};