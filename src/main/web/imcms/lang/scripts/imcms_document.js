/**
 * Created by Shadowgun on 17.02.2015.
 */
Imcms.Document = {};
Imcms.Document.API = function () {

};
Imcms.Document.API.prototype = {
    path: Imcms.contextPath + "/api/document",
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
            url: this.path + "/" + (request.id || ""),
            type: "GET",
            data: request,
            success: response
        });
    },
    create2: function (request, response, path) {
        $.ajax({
            url: this.path + "/" + (path ? path.join("/") : ""),
            type: "POST",
            contentType: false,
            processData: false,
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
    show: function () {
        this._editor.open();
    },
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
        this._api.create2(data, callback);
    },
    documentsList: function (callback) {
        this._api.read({}, callback);
    },
    filteredDocumentList: function (params, callback) {
        this._api.read({
            filter: params.term || params.filter,
            skip: params.skip,
            take: params.take,
            sort: params.sort,
            order: params.order
        }, callback);
    },
    getDocument: function (id, callback) {
        this._api.read({id: id}, callback);
    },
    copyDocument: function (id, callback) {
        this._api.create2({}, callback, [id, "copy"])
    },
    getPrototype: function (id, callback) {
        this._api.read({id: id, isPrototype: true}, callback);
    },
    deleteDocument: function (data) {
        this._api.delete(data, function () {
        });
    },
    archiveDocument: function (data) {
        this._api.delete(data + "?action=archive", function () {
        });
    },
    unarchiveDocument: function (data) {
        this._api.delete(data + "?action=unarchive", function () {
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
            .class("imcms-header")
            .div()
            .html("Document Editor")
            .class("imcms-title")
            .end()
            .button()
            .reference("closeButton")
            .class("imcms-close-button")
            .on("click", $.proxy(this.close, this))
            .end()
            .end()
            .div()
            .class("imcms-content")
            .table()
            .reference("documentsList")
            .end()
            .end()
            .div()
            .class("imcms-footer")
            .button()
            .class("imcms-neutral create-new")
            .html("Create new…")
            .on("click", $.proxy(this.showDocumentViewer, this))
            .end()
            .end()
            .end();
        $(this._builder[0])
            .appendTo("body")
            .addClass("editor-form editor-document reset");
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
        new Imcms.Document.TypeViewer({
            loader: this._loader,
            onApply: function (data) {
                this._loader.getPrototype(data.parentDocumentId, function (doc) {
                    new Imcms.Document.Viewer({
                        data: doc,
                        type: data.documentType,
                        parentDocumentId: data.parentDocumentId,
                        loader: this._loader,
                        target: $("body"),
                        onApply: $.proxy(this.onApply, this)
                    });
                }.bind(this));
            }.bind(this)
        });
    },
    onApply: function (viewer) {
        var data = viewer.serialize();
        this._loader.update(data, $.proxy(function () {
            this._documentListAdapter.reload();
        }, this));
    },
    open: function () {
        $(this._builder[0]).fadeIn("fast").find(".imcms-content").css({height: $(window).height() - 95});
    },
    close: function () {
        $(this._builder[0]).fadeOut("fast");
    }
};

Imcms.Document.Viewer = function (options) {
    this.init(Imcms.Utils.merge(options, this.defaults));
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
    _rowsCount: 0,
    defaults: {
        data: null,
        type: 2,
        parentDocumentId: 1001,
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
        this.buildValidator();
        this.createModal();
        this._loader.languagesList($.proxy(this.loadLanguages, this));
        this._loader.rolesList($.proxy(this.loadRoles, this));
        this._loader.categoriesList($.proxy(this.loadCategories, this));
        if (options.type === 2) {
            this._loader.templatesList($.proxy(this.loadTemplates, this));
        }

        if (options.data)
            this.deserialize(options.data);
        var $builder = $(this._builder[0]);
        $builder.fadeIn("fast").css({
            left: $(window).width() / 2 - $builder.width() / 2,
            top: $(window).height() / 2 - $builder.height() / 2
        });
        $(this._modal).fadeIn("fast");
    },
    buildValidator: function () {
        $(this._builder[0]).find("form").validate({
            rules: {
                enabled: {
                    required: true
                },
                alias: {
                    remote: {
                        url: this._loader._api.path,
                        type: "GET",
                        success: function (data) {
                            var result = true,
                                validator = $(this._builder[0]).find("form").data("validator"),
                                element = $(this._builder[0]).find("input[name=alias]"),
                                currentAlias = element.val(),
                                previous, errors, message, submitted;

                            element = element[0];
                            previous = validator.previousValue(element);

                            data.forEach(function (it) {
                                result = !result ? false : it.alias != currentAlias || it.id == this._options.data.id;
                            }.bind(this));

                            validator.settings.messages[element.name].remote = previous.originalMessage;

                            if (result) {
                                submitted = validator.formSubmitted;
                                validator.prepareElement(element);
                                validator.formSubmitted = submitted;
                                validator.successList.push(element);
                                delete validator.invalid[element.name];
                                validator.showErrors();
                            } else {
                                errors = {};
                                message = validator.defaultMessage(element, "remote");
                                errors[element.name] = previous.message = $.isFunction(message) ? message(value) : message;
                                validator.invalid[element.name] = true;
                                validator.showErrors(errors);
                            }

                            previous.valid = result;
                            validator.stopRequest(element, result);
                        }.bind(this)
                    }
                }
            },
            messages: {
                alias: {
                    remote: "This alias has already been taken"
                }
            },
            ignore: ""
        });
    },
    buildView: function () {
        this._builder = JSFormBuilder("<div>")
            .form()
            .div()
            .class("imcms-header")
            .div()
            .class("imcms-title")
            .html(this._title)
            .hidden()
            .name("id")
            .end()
            .end()
            .button()
            .reference("closeButton")
            .class("imcms-close-button")
            .on("click", $.proxy(this.cancel, this))
            .end()
            .end()
            .div()
            .class("imcms-content with-tabs")
            .div()
            .class("imcms-tabs")
            .reference("tabs")
            .end()
            .div()
            .class("imcms-pages")
            .reference("pages")
            .end()
            .end()
            .div()
            .class("imcms-footer")
            .div()
            .class("buttons")
            .button()
            .class("imcms-positive")
            .html("OK")
            .on("click", $.proxy(this.apply, this))
            .end()
            .button()
            .class("imcms-neutral")
            .html("Cancel")
            .on("click", $.proxy(this.cancel, this))
            .end()
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo($("body")).addClass("document-viewer pop-up-form reset");
        this.buildAppearance();
        this.buildLifeCycle();
        this.buildKeywords();
        this.buildCategories();
        this.buildAccess();
        switch (this._options.type) {
            case 2:
            {
                this.buildPermissions();
                this.buildTemplates();
            }
                break;
            case 5:
                this.buildLinking();
                break;
            case 8:
                this.buildFile();
                break;
        }
    },
    buildLifeCycle: function () {
        this._builder.ref("tabs")
            .div()
            .reference("life-cycle-tab")
            .class("life-cycle-tab imcms-tab")
            .html("Life Cycle")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("life-cycle-page")
            .class("life-cycle-page imcms-page")
            .div()
            .class("select field")
            .select()
            .name("status")
            .option("In Process", "0")
            .option("Disapproved", "1")
            .option("Approved", "2")
            //.option("Published", "3")
            //.option("Archived", "4")
            //.option("Expired", "5")
            .end()
            .end()
            .end();
        this._contentCollection["life-cycle"] = {
            tab: this._builder.ref("life-cycle-tab"),
            page: this._builder.ref("life-cycle-page")
        };
        this._builder.ref("life-cycle-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["life-cycle"]));
    },
    buildAppearance: function () {
        this._builder.ref("tabs")
            .div()
            .reference("appearance-tab")
            .class("appearance-tab imcms-tab active")
            .html("Appearance")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("appearance-page")
            .class("appearance-page imcms-page active")
            .div()
            .class("language field")
            .reference("languages")
            .end()
            .div()
            .class("link-action field")
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
            .on("focus", function (e) {
                if ($(e.target).val()) {
                    return true;
                }
                var $languagesArea = $(this._builder.ref("languages").getHTMLElement()),
                    lang = $languagesArea.find("input[data-node-key=language]:checked").attr("data-node-value"),
                    title = $languagesArea.find("input[name=title]").filter("[data-node-value=" + lang + "]").val(),
                    $target = $(e.target);

                $target.val(getSlug(title));
            }.bind(this))
            .label("Document Alias")
            .end()
            .end()
            .end()
            .end();

        this._contentCollection["appearance"] = {
            tab: this._builder.ref("appearance-tab"),
            page: this._builder.ref("appearance-page")
        };


        this._activeContent = this._contentCollection["appearance"];
        this._builder.ref("appearance-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["appearance"]));
    },
    buildTemplates: function () {
        this._builder.ref("tabs")
            .div()
            .reference("templates-tab")
            .class("templates-tab imcms-tab")
            .html("Templates")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("templates-page")
            .class("templates-page imcms-page")
            .div()
            .class("select field")
            .select()
            .name("template")
            .label("Template")
            .reference("templates")
            .end()
            .end()
            .div()
            .class("select field")
            .select()
            .name("defaultTemplate")
            .label("Default template for child documents")
            .reference("defaultTemplates")
            .end()
            .end()
            .end();
        this._contentCollection["templates"] = {
            tab: this._builder.ref("templates-tab"),
            page: this._builder.ref("templates-page")
        };
        this._builder.ref("templates-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["templates"]));
    },
    buildPermissions: function () {
        this._builder.ref("tabs")
            .div()
            .reference("permissions-tab")
            .class("permissions-tab imcms-tab")
            .html("Permissions")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("permissions-page")
            .class("permissions-page imcms-page")
            .div()
            .class("imcms-column")
            .div()
            .class("imcms-label")
            .html("Restricted 1")
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 1)
            .name("canEditText")
            .label("Edit Text")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 1)
            .name("canEditMenu")
            .label("Edit Menu")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 1)
            .name("canEditImage")
            .label("Edit Image")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 1)
            .name("canEditLoop")
            .label("Edit Loop")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 1)
            .name("canEditDocumentInformation")
            .label("Edit Doc Info")
            .end()
            .end()
            .end()
            .div()
            .class("imcms-column")
            .div()
            .class("imcms-label")
            .html("Restricted 2")
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 2)
            .name("canEditText")
            .label("Edit Text")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 2)
            .name("canEditMenu")
            .label("Edit Menu")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 2)
            .name("canEditImage")
            .label("Edit Image")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 2)
            .name("canEditLoop")
            .label("Edit Loop")
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .attr("data-node-key", "permissions")
            .attr("data-node-value", 2)
            .name("canEditDocumentInformation")
            .label("Edit Doc Info")
            .end()
            .end()
            .end()
            .end()
            .end();
        this._contentCollection["permissions"] = {
            tab: this._builder.ref("permissions-tab"),
            page: this._builder.ref("permissions-page")
        };
        this._builder.ref("permissions-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["permissions"]));

    },
    buildLinking: function () {
        this._builder.ref("tabs")
            .div()
            .reference("linking-tab")
            .class("linking-tab imcms-tab")
            .html("Linking")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("linking-page")
            .class("linking-page imcms-page")
            .div()
            .class("field")
            .text()
            .name("url")
            .label("Url")
            .reference("linking")
            .end()
            .end()
            .end();
        this._contentCollection["linking"] = {
            tab: this._builder.ref("linking-tab"),
            page: this._builder.ref("linking-page")
        };
        this._builder.ref("linking-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["linking"]));
    },
    buildFile: function () {
        this._builder.ref("tabs")
            .div()
            .reference("file-tab")
            .class("file-tab imcms-tab")
            .html("File")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("file-page")
            .class("file-page imcms-page")
            .div()
            .class("select field")
            .file()
            .name("file")
            .label("File")
            .reference("file")
            .end()
            .end()
            .div()
            .table()
            .reference("files")
            .column("name")
            .column("default")
            .column("")
            .end()
            .end()
            .end();
        this._contentCollection["file"] = {
            tab: this._builder.ref("file-tab"),
            page: this._builder.ref("file-page")
        };
        this._builder.ref("file-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["file"]));
    },
    buildAccess: function () {
        this._builder.ref("tabs")
            .div()
            .reference("access-tab")
            .class("access-tab imcms-tab")
            .html("Access")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("access-page")
            .class("access-page imcms-page")
            .table()
            .reference("access")
            .column("Role")
            .column("View")
            .column("Edit")
            .end()
            .div()
            .class("field")
            .select()
            .reference("rolesList")
            .end()
            .button()
            .class("imcms-positive")
            .html("Add role")
            .on("click", this.addRolePermission.bind(this))
            .end()
            .end()
            .end();

        if (this._options.type === 2) {
            this._builder.ref("access")
                .column("RESTRICTED 1")
                .column("RESTRICTED 2")
        }

        this._builder.ref("access")
            .column("");

        this._contentCollection["access"] = {
            tab: this._builder.ref("access-tab"),
            page: this._builder.ref("access-page")
        };
        this._builder.ref("access-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["access"]));
    },
    buildKeywords: function () {
        this._builder.ref("tabs")
            .div()
            .reference("keywords-tab")
            .class("keywords-tab imcms-tab")
            .html("Keywords")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("keywords-page")
            .class("keywords-page imcms-page")
            .div()
            .class("field")
            .text()
            .label("Keyword text")
            .placeholder("Input keyword name")
            .reference("keywordInput")
            .end()
            .button()
            .class("imcms-positive")
            .html("Add")
            .on("click", this.addKeyword.bind(this))
            .end()
            .end()
            .div()
            .class("field")
            .select()
            .label("Keywords")
            .attr("data-node-key", "keywords")
            .name("keywordsList")
            .reference("keywordsList")
            .multiple()
            .end()
            .button()
            .class("imcms-negative")
            .html("Remove")
            .on("click", this.removeKeyword.bind(this))
            .end()
            .end()
            .div()
            .class("field")
            .checkbox()
            .name("isSearchDisabled")
            .label("Disable Searching")
            .end()
            .end()
            .end();
        this._contentCollection["keywords"] = {
            tab: this._builder.ref("keywords-tab"),
            page: this._builder.ref("keywords-page")
        };
        this._builder.ref("keywords-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["keywords"]));
    },
    buildCategories: function () {
        this._builder.ref("tabs")
            .div()
            .reference("categories-tab")
            .class("categories-tab imcms-tab")
            .html("Categories")
            .end();
        this._builder.ref("pages")
            .div()
            .reference("categories-page")
            .class("categories-page imcms-page")
            .end();
        this._contentCollection["categories"] = {
            tab: this._builder.ref("categories-tab"),
            page: this._builder.ref("categories-page")
        };
        this._builder.ref("categories-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["categories"]));
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
        this._builder.ref("defaultTemplates").option(key, name);
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
            .name("enabled")
            .end()
            .div()
            .class("label")
            .html(language)
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
            .button()
            .class("imcms-neutral")
            .on("click", this.chooseImage.bind(this))
            .html("…")
            .end()
            .end()
            .end();
    },
    loadRoles: function (roles) {
        $.each(roles, this.addRole.bind(this));
        if (this._options.data)
            this.deserialize(this._options.data);
    },
    addRole: function (name, roleId) {
        this._builder.ref("rolesList").option(name, roleId);
    },
    addRolePermission: function (key, value) {
        var divWithHidden, removeButton, hiddenRemoveRole, currentRow;
        if (typeof value === "undefined") {
            value = $(this._builder.ref("rolesList").getHTMLElement()).find("option:selected");
            value = {name: value.text(), roleId: value.val()};
            key = 3;
        }
        else {
            key = value.permission;
            value = value.role;
        }
        hiddenRemoveRole = $("<input>")
            .attr("data-node-key", "access")
            .attr("type", "radio")
            .attr("name", value.name.toLowerCase() + "-access")
            .attr("value", 4).css("display", "none");
        divWithHidden = $("<div>").append(value.name)
            .append($("<input>")
                .attr("type", "hidden")
                .attr("data-node-key", "access")
                .attr("data-role-name", value.name)
                .attr("value", value.roleId)
                .attr("name", value.name.toLowerCase() + "-id")
            ).append(hiddenRemoveRole);
        removeButton = $("<button>").attr("type", "button").addClass("imcms-negative");
        if (this._options.type === 2) {
            this._builder.ref("access")
                .row(
                    divWithHidden[0],
                    $("<input>")
                        .attr("type", "radio")
                        .attr("data-node-key", "access")
                        .attr("value", 3)
                        .attr("name", value.name.toLowerCase() + "-access")[0],
                    $("<input>")
                        .attr("type", "radio")
                        .attr("data-node-key", "access")
                        .attr("value", 0)
                        .attr("name", value.name.toLowerCase() + "-access")[0],
                    $("<input>")
                        .attr("type", "radio")
                        .attr("data-node-key", "access")
                        .attr("value", 1)
                        .attr("name", value.name.toLowerCase() + "-access")[0],
                    $("<input>")
                        .attr("type", "radio")
                        .attr("data-node-key", "access")
                        .attr("value", 2)
                        .attr("name", value.name.toLowerCase() + "-access")[0],
                    removeButton[0]
                );
        }
        else {
            this._builder.ref("access")
                .row(
                    divWithHidden[0],
                    $("<input>")
                        .attr("type", "radio")
                        .attr("data-node-key", "access")
                        .attr("value", 3)
                        .attr("name", value.name.toLowerCase() + "-access")[0],
                    $("<input>")
                        .attr("type", "radio")
                        .attr("data-node-key", "access")
                        .attr("value", 0)
                        .attr("name", value.name.toLowerCase() + "-access")[0],
                    removeButton[0]
                );
            key = key == 3 || key == 0 ? key : 3;
        }
        currentRow = this._builder.ref("access").row(this._rowsCount);
        removeButton.on("click", function () {
            $(currentRow).hide();
            hiddenRemoveRole.prop("checked", true);
        }.bind(this));
        this._rowsCount++;
        $("input[name=" + value.name.toLowerCase() + "-access]").filter("[value=" + key + "]").prop("checked", true);
    },
    loadCategories: function (categories) {
        $.each(categories, this.addCategoryType.bind(this));
        if (this._options.data)
            this.deserialize(this._options.data);
    },
    addCategoryType: function (categoryType, options) {
        this._builder.ref("categories-page")
            .div()
            .class("section field")
            .select()
            .label(categoryType)
            .name(categoryType)
            .reference(categoryType)
            .attr("data-node-key", "categories")
            .end()
            .end();

        if (options.isMultiple) {
            this._builder.ref(categoryType).class("pqSelect").multiple();
        }
        else {
            this._builder.ref(categoryType)
                .option("none", "");
        }

        $.each(options.items, this.addCategory.bind(this, categoryType));
    },
    addCategory: function (categoryType, position, category) {
        $(this._builder.ref(categoryType).getHTMLElement()).append(
            $("<option>")
                .val(category.name).text(category.name).attr("title", category.description)
        );
    },
    addKeyword: function (position, keyword) {
        var exist;

        if (!keyword) {
            keyword = this._builder.ref("keywordInput").value()
        }

        exist = $(this._builder.ref("keywordsList").getHTMLElement()).find("option").filter(function () {
                return $(this).val() === keyword;
            }).length > 0;

        if (!exist) {
            this._builder.ref("keywordsList").option(keyword);
        }
    },
    addFile: function (key, val) {
        var radio = $("<input>").attr("type", "radio").attr("name", "defaultFile").val(val);
        this._builder.ref("files").row(
            val,
            radio[0],
            $("<button>").attr("type", "button").addClass("imcms-negative").click(this.removeFile.bind(this, radio))[0]
        )
    },
    removeFile: function (radio) {
        radio.attr("data-removed", "").parents("tr").hide();
    },
    removeKeyword: function () {
        $(this._builder.ref("keywordsList").getHTMLElement()).find("option:selected").remove();
    },
    chooseImage: function (e) {
        var onFileChosen = function (data) {
            if (data) {
                $(e.target).parent().children("input[name=image]").val(data.urlPathRelativeToContextPath);
            }

            $(this._builder[0]).fadeIn();
        }.bind(this);

        Imcms.Editors.Content.showDialog({
            onApply: $.proxy(onFileChosen, this),
            onCancel: $.proxy(onFileChosen, this)
        });
        $(this._builder[0]).fadeOut();
    },
    apply: function () {
        if (!$(this._builder[0]).find("form").valid()) {
            return false;
        }

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
        var result = {languages: {}, access: {}, keywords: [], categories: {}},
            $source = $(this._builder[0]),
            formData = new FormData();
        $source.find("[name]").filter(function () {
            return !$(this).attr("data-node-key") && "file" !== $(this).attr("type") && !$(this).attr("ignored");
        }).each(function () {
            var $this = $(this);
            if ($this.is("[type=checkbox]")) {
                result[$this.attr("name")] = $this.is(":checked")
            } else {
                result[$this.attr("name")] = $this.val();
            }
        });

        $source.find("[data-node-key=language]").each(function () {
            var $dataElement = $(this);
            var language = $dataElement.attr("data-node-value");
            if (!Object.prototype.hasOwnProperty.call(result["languages"], language))
                result["languages"][language] = {};
            result["languages"][language][$dataElement.attr("name")] = $dataElement.attr("name") === "enabled" ?
                ($dataElement.is(":checked") ? true : false) : $dataElement.val();
        });

        $source.find("[data-role-name]").each(function () {
            var role = {name: $(this).attr("data-role-name"), roleId: $(this).val()},
                permission = $source.find("input[name=" + role.name.toLowerCase() + "-access]").filter(function () {
                    return $(this).prop("checked");
                }).val();

            result["access"][role.roleId] = {permission: permission, role: role};
        });
        $source.find("select[name=keywordsList]").children().each(function () {
            result.keywords.push($(this).val());
        });
        $source.find("select[data-node-key=categories]").each(function () {
            var $this = $(this);
            if ($this.attr("multiple")) {
                result.categories[$this.attr("name")] = $this.val() || [];
            }
            else {
                result.categories[$this.attr("name")] = [$this.val() || ""];
            }
        });

        if (this._options.type === 2) {
            result.permissions = [{}, {}];
            $source.find("input[data-node-key=permissions]").each(function () {
                var $this = $(this);
                var id = +$this.attr("data-node-value") - 1;
                result.permissions[id][$this.attr("name")] = $this.is(":checked");
            });
        }
        if (this._options.type === 8) {
            result["removedFiles"] = $source.find("input[type=radio][data-removed]").map(function (pos, item) {
                return $(item).val();
            }).toArray();
            formData.append("file", $source.find("input[name=file]")[0].files[0]);
        }
        formData.append("data", JSON.stringify(result));
        formData.append("type", this._options.type);
        formData.append("parent", this._options.parentDocumentId);


        return formData;
    },
    deserialize: function (data) {
        var $source = $(this._builder[0]);
        $source.find("[name]").filter(function () {
            return !$(this).attr("data-node-key") && "file" !== $(this).attr("type");
        }).each(function () {
            var $this = $(this);
            if ($this.is("[type=checkbox]")) {
                $this.prop("checked", data[$this.attr("name")]);
            } else {
                $this.val(data[$this.attr("name")]);
            }

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
        this._rowsCount = 0;
        $.each(data.access, this.addRolePermission.bind(this));

        $(this._builder.ref("keywordsList").getHTMLElement()).empty();
        $.each(data.keywords, this.addKeyword.bind(this));

        $.each(data.categories, function (categoryType, selectedCategories) {
            selectedCategories.forEach(function (selectedCategory) {
                $source
                    .find("select[name='" + categoryType + "']")
                    .find("option[value='" + selectedCategory + "']")
                    .attr("selected", "");
            });

            $($source.find("select[name='" + categoryType + "']")).multiselect();
        });
        if (this._options.type === 2 && data.permissions) {
            $.each(data.permissions, function (index, value) {
                var $elements = $source.find("[data-node-key=permissions]").filter("[data-node-value=" + ++index + "]");
                $.each(value, function (key, val) {
                    if (val) {
                        $elements.filter("input[name=" + key + "]").attr("checked", "")
                    }
                });
            });
        }

        if (this._options.type === 8 && data.files) {
            this._builder.ref("files").clear();
            $.each(data.files, this.addFile.bind(this));
            $(this._builder.ref("files").getHTMLElement())
                .find("input[name=defaultFile]")
                .filter('[value="' + data.defaultFile + '"]')
                .prop('checked', true);
        }
    }
};

Imcms.Document.TypeViewer = function (options) {
    this._options = Imcms.Utils.merge(options, this._options);
    this.init();
};
Imcms.Document.TypeViewer.prototype = {
    _builder: undefined,
    _options: {
        loader: undefined,
        onApply: function () {
        },
        onCancel: function () {
        }
    },
    init: function () {
        var $builder;

        this.buildView();
        this.onLoaded();

        $builder = $(this._builder[0]);

        $builder.fadeIn("fast").css({
            left: $(window).width() / 2 - $builder.width() / 2,
            top: $(window).height() / 2 - $builder.height() / 2
        });
    },
    buildView: function () {
        this._builder = JSFormBuilder("<div>")
            .form()
            .div()
            .class("imcms-header")
            .div()
            .class("imcms-title")
            .html("Document Types")
            .hidden()
            .name("id")
            .end()
            .end()
            .button()
            .reference("closeButton")
            .class("imcms-close-button")
            .on("click", $.proxy(this.cancel, this))
            .end()
            .end()
            .div()
            .class("imcms-content")
            .div()
            .class("field select")
            .select()
            .label("Document Type")
            .name("documentTypes")
            .option("Text Document", 2)
            .option("Url Document", 5)
            .option("File Document", 8)
            .end()
            .end()
            .div()
            .class("field select")
            .text()
            .label("Document Parent")
            .reference("parentDocument")
            .name("parentDocument")
            .disabled()
            .end()
            .button()
            .on("click", this.openSearchDocumentDialog.bind(this))
            .class("imcms-neutral browse")
            .html("…")
            .end()
            .end()
            .end()
            .div()
            .class("imcms-footer")
            .div()
            .class("buttons")
            .button()
            .class("imcms-positive")
            .html("OK")
            .on("click", $.proxy(this.apply, this))
            .end()
            .button()
            .class("imcms-neutral")
            .html("Cancel")
            .on("click", $.proxy(this.cancel, this))
            .end()
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo($("body")).addClass("document-type-viewer pop-up-form reset");
    },
    openSearchDocumentDialog: function () {
        var documentSearchDialog = new Imcms.Document.DocumentSearchDialog(function (term, callback) {
            Imcms.Editors.Document.filteredDocumentList(term, callback)
        });

        documentSearchDialog.result(function (data) {
            $(this._builder.ref("parentDocument").getHTMLElement()).val(data.label).attr("data-id", data.id);
            documentSearchDialog.dispose();
        }.bind(this));
        documentSearchDialog._dialog.parent().css("z-index", "99999999");
        documentSearchDialog.open();
    },
    onLoaded: function (data) {
        $(this._builder.ref("parentDocument").getHTMLElement()).val(Imcms.document.label).attr("data-id", Imcms.document.meta);
    },
    apply: function () {
        this._options.onApply({
            documentType: +$(this._builder[0]).find("select[name=documentTypes]").val(),
            parentDocumentId: +$(this._builder[0]).find("input[name=parentDocument]").attr("data-id")
        });
        this.destroy();
    },
    cancel: function () {
        this._options.onCancel();
        this.destroy();
    },
    destroy: function () {
        $(this._builder[0]).remove();
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
    _pagerHandler: undefined,
    init: function () {
        this._loader.documentsList($.proxy(this.buildList, this));
        this.buildPager();
    },
    buildList: function (data) {
        $.each(data, $.proxy(this.addDocumentToList, this));
    },
    addDocumentToList: function (position, data) {
        var deleteButton = $("<button>"),
            row;

        this._container.row(data.id, data.label, data.alias, data.type, $("<span>")
            .append($("<button>")
                .click($.proxy(this.copyDocument, this, data.id))
                .addClass("imcms-positive")
                .text("Copy")
                .attr("type", "button"))
            .append($("<button>")
                .click($.proxy(this.editDocument, this, data.id))
                .addClass("imcms-positive")
                .text("Edit…")
                .attr("type", "button"))
            .append(deleteButton
                .addClass("imcms-negative")
                .attr("type", "button"))[0]
        );

        row = this._container.row(position);
        if (data.isArchived) {
            $(row).addClass("archived");
        }

        $(row).hover(function (e) {
            if (e.shiftKey) {
                deleteButton.attr("data-remove", true).text("");
            }
            else {
                deleteButton.attr("data-remove", false).text(this.isArchived(row) ? "U" : "A");
            }
        }.bind(this));
        deleteButton
            .click($.proxy(this.deleteDocument, this, data.id, row));
    },
    buildPager: function () {
        this._pagerHandler = new Imcms.Document.PagerHandler({
            target: $(this._container.getHTMLElement()).parent()[0],
            handler: this._loader.filteredDocumentList.bind(this._loader),
            itemsPointer: function () {
                var $source = $(this._container.getHTMLElement()).parent(),
                    height = $source.height(),
                    found = false,
                    rows = $source.find("table").find('tr'),
                    pointer = rows.length - 1,
                    index = rows.filter(function (pos, it) {
                        var result = false;

                        if ($(it).position().top > height) {
                            result = !found;
                            found = true;
                        }

                        return result;
                    }).index();

                return index > -1 ? index : pointer;
            }.bind(this),
            resultProcessor: function (startIndex, data) {
                this.buildList(data);
            }.bind(this)
        });
    },
    reload: function () {
        this._container.clear();
        this._loader.documentsList(function (data) {
            this.buildList(data);
            this._pagerHandler.reset();
        }.bind(this));
    },
    deleteDocument: function (id, row) {
        var deleteButton = $(row).find("button.imcms-negative"),
            flag = deleteButton.attr("data-remove");

        if (flag && flag.toString().toLowerCase() == "true") {
            if (!confirm("Are you sure?")) {
                return;
            }

            this._loader.deleteDocument(id);
            $(row).remove();
        } else if (this.isArchived(row)) {
            this.unarchive(id, row);
        }
        else {
            this.archive(id, row);
        }
    },
    isArchived: function (row) {
        return $(row).hasClass("archived");
    },
    archive: function (id, row) {
        this._loader.archiveDocument(id);
        $(row).addClass("archived");
    },
    unarchive: function (id, row) {
        this._loader.unarchiveDocument(id);
        $(row).removeClass("archived");
    },
    editDocument: function (id) {
        this._loader.getDocument(id, $.proxy(this.showDocumentViewer, this));
    },
    copyDocument: function (id) {
        this._loader.copyDocument(id, this.reload.bind(this));
    },
    showDocumentViewer: function (data) {
        new Imcms.Document.Viewer({
            data: data,
            type: (+data.type) || undefined,
            loader: this._loader,
            target: $("body")[0],
            onApply: $.proxy(this.saveDocument, this)
        });
    },
    saveDocument: function (viewer) {
        this._loader.update(viewer.serialize(), $.proxy(this.reload, this));
    }
};

Imcms.Document.DocumentSearchDialog = function (source) {
    this._source = source;
    this.init();
};
Imcms.Document.DocumentSearchDialog.prototype = {
    _source: null,
    _currentTerm: "",
    _builder: {},
    _dialog: {},
    _sort: "",
    _order: "",
    _selectedRow: {},
    _pagerHandler: {},
    _callback: function () {
    },
    init: function () {
        this.buildContent();
        this.buildDialog();
        this.buildPager();

        this.find();
    },
    buildContent: function () {
        var that = this;
        this._builder = JSFormBuilder("<DIV>")
            .form()
            .class("editor-menu-form")
            .div()
            .div()
            .class("field")
            .text()
            .on("input", function () {
                that.find(this.value());
            })
            .reference("searchField")
            .placeholder("Type to find document")
            .end()
            .end()
            .div()
            .class("field")
            .div()
            .class("field-wrapper")
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
            .end()
            .end();
    },
    sort: function (index) {
        //var sortingType = $(this._builder[0]).find("input[name=sorting]:checked").val(),
        //    i = index,
        //    comparator = function (row1, row2) {
        //        return $($(row1).find("td")[i]).text().localeCompare($($(row2).find("td")[i]).text());
        //    },
        //    table = $(this._builder.ref("documentsTable").getHTMLElement());
        //

        //switch (sortingType) {
        //    default:
        //    case "id":
        //        i = 0;
        //        break;
        //    case "headline":
        //        i = 1;
        //        break;
        //    case  "alias":
        //        i = 3;
        //        break;
        //}
        var oldSort = this._sort;
        switch (index) {
            default:
            case 0:
                this._sort = "meta_id";
                break;
            case 1:
                this._sort = "meta_headline";
                break;
            case  2:
                this._sort = "language";
                break;
            case  3:
                this._sort = "alias";
                break;
        }

        if (this._sort === oldSort) {
            this._order = this._order === "asc" ? "desc" : "asc";
        }
        else {
            this._order = "asc";
        }
        //table.find("tr").sort(comparator).detach().appendTo(table);
        this.find(this._currentTerm);
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
        var dialog = $(this._builder[0]).parents(".ui-dialog").removeClass()
            .addClass("pop-up-form menu-viewer reset").css({position: "fixed"}),
            header = dialog.children(".ui-dialog-titlebar").removeClass()
                .addClass("imcms-header").append($("<div>").addClass("imcms-title").text("DOCUMENT SELECTOR")),
            content = dialog.children(".ui-dialog-content").removeClass()
                .addClass("imcms-content"),
            footer = dialog.children(".ui-dialog-buttonpane").removeClass()
                .addClass("imcms-footer"),
            buttons = footer.find(".ui-button").removeClass();

        header.find(".ui-dialog-title").remove();
        header.children("button").empty().removeClass().addClass("imcms-close-button");

        $(buttons[0]).addClass("imcms-positive");
        $(buttons[1]).addClass("imcms-neutral cancel-button");

    },
    buildPager: function () {
        this._pagerHandler = new Imcms.Document.PagerHandler({
            target: $(this._builder[0]).find(".field-wrapper")[0],
            handler: this.pagingHandler.bind(this),
            itemsPointer: function () {
                var $source = $(this._builder[0]).find('.field-wrapper'),
                    height = $source.height(),
                    found = false,
                    rows = $source.find("table").find('tr'),
                    pointer = rows.length - 1,
                    index = rows.filter(function (pos, it) {
                        var result = false;

                        if ($(it).position().top > height) {
                            result = !found;
                            found = true;
                        }

                        return result;
                    }).index();

                return index > -1 ? index : pointer;
            }.bind(this),
            resultProcessor: this.appendDataToTable.bind(this)
        });
    },
    open: function () {
        this._dialog.dialog("open");
    },
    dispose: function () {
        this._dialog.remove();
    },
    find: function (word) {
        this._currentTerm = word;
        this._pagerHandler.reset();
        this._source({term: word || "", sort: this._sort, order: this._order}, $.proxy(this.fillDataToTable, this));
    },
    pagingHandler: function (params, callback) {
        params["term"] = this._currentTerm;
        params["sort"] = this._sort;
        params["order"] = this._order;

        this._source(params, callback);
    },
    fillDataToTable: function (data) {
        this.clearTable();
        this.appendDataToTable(0, data);
    },
    clearTable: function () {
        this._builder.ref("documentsTable").clear();
    },
    appendDataToTable: function (startIndex, data) {
        $(this._builder.ref("documentsTable").getHTMLElement()).find("th").each(function (pos, item) {
            $(item).find("div").remove();
        });

        for (var rowId in data) {
            if (data.hasOwnProperty(rowId) && data[rowId]) {
                this._builder.ref("documentsTable").row(data[rowId]);
            }
        }

        $(this._builder.ref("documentsTable").getHTMLElement()).find("tr")
            .filter(function (pos) {
                return pos >= startIndex;
            }).each(function (pos, item) {
            $(item).on("dragstart", function (event) {
                $(".ui-widget-overlay").css("display", "none");
                event.originalEvent.dataTransfer.setData("data", JSON.stringify(data[pos - 1]));
            }).on("dragend", function (event) {
                $(".ui-widget-overlay").css("display", "block");
            }).attr("draggable", true);

        });


        $(this._builder.ref("documentsTable").getHTMLElement()).find("th").each(function (pos, item) {
            $("<div>").append($(item).html()).click(this.sort.bind(this, pos)).appendTo(item);
        }.bind(this));
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
        var $table = $(e.currentTarget),
            tableOffset = $table.offset();
        element = $table.find("tbody tr").filter(function (index, element) {
            /*  var offset, farCorner;

             element = $(element);

             offset = element.position();
             //offset = {left: offset.left - tableOffset.left, top: offset.top - tableOffset.top};
             farCorner = {right: offset.left + element.width(), bottom: offset.top + element.height()};

             return offset.left <= e.offsetX && offset.top <= e.offsetY && e.offsetX <= farCorner.right && e.offsetY <= farCorner.bottom*/
            return $.contains(element, e.target);
        });
        if (!element.length) {
            return false;
        }
        element = element[0];
        if (this._selectedRow)
            this._selectedRow.className = "";
        this._selectedRow = element;
        this._selectedRow.className = "clicked";
    }
};

Imcms.Document.PagerHandler = function (options) {
    this._target = options.target;
    this._options = Imcms.Utils.merge(options, this._options);

    this.init();
};
Imcms.Document.PagerHandler.prototype = {
    _target: undefined,
    _waiter: undefined,
    _isHandled: false,
    _pageNumber: 1,
    _options: {
        count: 25,
        handler: function () {
        },
        itemsPointer: function () {
            return 0;
        },
        resultProcessor: function (data) {

        },
        waiterContent: ""
    },
    init: function () {
        $(this._target).scroll(this.scrollHandler.bind(this)).bind('beforeShow', this.scrollHandler.bind(this));
        this.scrollHandler();
    },
    handleRequest: function (skip) {
        this._addWaiterToTarget();
        this._isHandled = true;
        this._options.handler({skip: skip, take: this._options.count}, this.requestCompleted.bind(this));
    },
    requestCompleted: function (data) {
        this._options.resultProcessor(this._pageNumber * this._options.count, data);
        this._pageNumber++;
        this._removeWaiterFromTarget();
        this._isHandled = false;
        this.scrollHandler();
    },
    scrollHandler: function (event) {
        if (this._isHandled) {
            return;
        }

        var pointer = this._options.itemsPointer(),
            currentItemsCount = this._pageNumber * this._options.count;

        if (pointer >= currentItemsCount - 3) {
            this.handleRequest(currentItemsCount)
        }
    },
    _addWaiterToTarget: function () {
        this._waiter = $("<div>")
            .addClass("waiter")
            .append(this._options.waiterContent)
            .appendTo(this._target);
    },
    _removeWaiterFromTarget(){
        this._waiter.remove();
    },
    reset: function () {
        this._pageNumber = 1
        this.scrollHandler();
    }
};
