Imcms.Document = {};
Imcms.Document.API = function () {

};
Imcms.Document.API.prototype = {
    create: function (request, response) {
        $.ajax({
            url: Imcms.Linker.get("document.create"),
            type: "POST",
            data: request,
            success: response
        })
    },
    read: function (request, response) {
        $.ajax({
            url: Imcms.Linker.get("document.read", (request.id || "")),
            type: "GET",
            traditional: true,
            data: request,
            success: response
        });
    },
    create2: function (request, response, path) {
        $.ajax({
            url: Imcms.Linker.get("document.create2", (path ? path.join("/") : null)),
            type: "POST",
            contentType: false,
            processData: false,
            data: request,
            success: response
        })
    },
    delete: function (request, response) {
        $.ajax({
            url: Imcms.Linker.get("document.delete", request),
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
    show: function (windowMode) {
        if(windowMode){
            $(this._editor._builder[0]).addClass("window-mode pop-up-form");
            // $(this._editor._builder[0]).addClass("pop-up-form menu-viewer reset");
            // $(this._editor._builder[0]).addClass("ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-draggable ui-resizable");



            // $(this._editor._builder[0]).addClass("window-mode pop-up-form");
            //
            //     $(this._editor._builder[0]).parents(".ui-dialog").removeClass()
            // .addClass("pop-up-form menu-viewer reset")
                $(this._editor._builder[0]).dialog({
                    // height: 500,
                    //         width: 700,
                            modal: true,
                            buttons: {
                            //     "Add selected": $.proxy(this._onApply, this),
                                Cancel: function () {
                                    $(this).dialog("close");
                                }
                            }

                });





            var dialog = $(this._editor._builder[0]).parents(".ui-dialog"),
                // .replaceWith($(".window-mode"));





                    footer = dialog.children(".ui-dialog-buttonpane"),
            //             // .removeClass()
            //  // ,           // .addClass("imcms-footer"),
            // //
                    buttons = footer.find(".ui-button");
            // //
            //     header.find(".ui-dialog-title").remove();
            //     header.children("button").remove();


            // var
            // var events = $(buttons[0]).data('events');
            // var $other_link = $(".editor-document.window-mode.imcms-close-button");
            // if ( events ) {
            //     for ( var eventType in events ) {
            //         for ( var idx in events[eventType] ) {
            //             this will essentially do $other_link.click( fn ) for each bound event
                        // $other_link[ eventType ]( events[eventType][idx].handler );
                    // }
                // }
            // }

            buttons.remove();
            dialog.replaceWith($(".window-mode"));

            // $(buttons[0]).clone
            //
            //     // $(buttons[0]).addClass("imcms-positive");
            //     $(buttons[0]).addClass("imcms-neutral cancel-button");







            // //             .removeClass()
            // //             .addClass("pop-up-form reset")
            // //             .css({position: "fixed"});
            // //
            //         header = dialog.children(".ui-dialog-titlebar")
            //             .removeClass();
            // //     ,         // .addClass("imcms-header")
            // //
            //         content = dialog.children(".ui-dialog-content").append($(this._editor._builder[0]))
            //             // .removeClass()
            // //             .addClass("imcms-content");


        //
        //     $(this._editor._builder[0]).parents(".ui-dialog").removeClass()
        // .addClass("pop-up-form window-mode reset")
        // // .addClass("window-mode")
        //     $(this._editor._builder[0]).dialog({
        //         height: 500,
        //         // autoOpen: false,
        //         width: 700,
        //         modal: true,
        //         buttons: {
        //             Cancel: function () {
        //                 $(this).dialog("destroy");
        //             }
        //         }
        //     });

            //
            // var dialog = $(this._editor._builder[0]).parents(".ui-dialog");
            // //             .removeClass()
            // //             .addClass("pop-up-form reset")
            // //             .css({position: "fixed"});
            // //
            //         header = dialog.children(".ui-dialog-titlebar")
            //             .removeClass();
            // //     ,         // .addClass("imcms-header")
            // //
            //         content = dialog.children(".ui-dialog-content").append($(this._editor._builder[0]))
            //             // .removeClass()
            // //             .addClass("imcms-content");
            // //
            //         footer = dialog.children(".ui-dialog-buttonpane")
            //             // .removeClass()
            //  // ,           // .addClass("imcms-footer"),
            // //
            //         buttons = footer.find(".ui-button").removeClass();
            // //
            //     header.find(".ui-dialog-title").remove();
            //     header.children("button").remove();


            //
            //     // $(buttons[0]).addClass("imcms-positive");
            //     $(buttons[0]).addClass("imcms-neutral cancel-button");





            // buildDialog: function () {
            //     this._dialog = $(this._builder[0]).dialog({
            //         autoOpen: false,
            //         height: 500,
            //         width: 700,
            //         modal: true,
            //         buttons: {
            //             "Add selected": $.proxy(this._onApply, this),
            //             Cancel: function () {
            //                 $(this).dialog("close");
            //             }
            //         }
            //     });
            //     var dialog = $(this._builder[0]).parents(".ui-dialog")
            //             .removeClass()
            //             .addClass("pop-up-form menu-viewer reset")
            //             .css({position: "fixed"}),
            //
            //         header = dialog.children(".ui-dialog-titlebar")
            //             .removeClass()
            //             .addClass("imcms-header")
            //             .append($("<div>").addClass("imcms-title").text("DOCUMENT SELECTOR"))
            //             .on('mousedown', function (e) {
            //                 // jquery-ui (or smth else) pins onmousedown event listener on this header and produces
            //                 // errors while clicking on it
            //                 e.preventDefault();
            //             }),
            //
            //         content = dialog.children(".ui-dialog-content")
            //             .removeClass()
            //             .addClass("imcms-content"),
            //
            //         footer = dialog.children(".ui-dialog-buttonpane")
            //             .removeClass()
            //             .addClass("imcms-footer"),
            //
            //         buttons = footer.find(".ui-button").removeClass();
            //
            //     header.find(".ui-dialog-title").remove();
            //     header.children("button").empty().removeClass().addClass("imcms-close-button");
            //
            //     $(buttons[0]).addClass("imcms-positive");
            //     $(buttons[1]).addClass("imcms-neutral cancel-button");







        }
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
        this._api.read({
            sort: "modified_datetime",
            order: "desc"
        }, callback);
    },
    filteredDocumentList: function (params, callback) {
        this._api.read({
            filter: params.term || params.filter,
            skip: params.skip,
            take: params.take,
            sort: params.sort,
            order: params.order,
            userId: params.userId,
            categoriesId: params.categoriesId
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
    usersList: function (callback) {
        Imcms.Editors.User.read({}, callback);
    },
    currentUser: function (callback) {
        Imcms.Editors.User.read({current: true}, callback);
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
    _currentUser: {
        id: "",
        loginName: ""
    },
    _builder: {},
    _loader: {},
    _documentListAdapter: {},
    init: function () {
        return this.buildView().buildDocumentsList();
    },
    buildView: function () {
        var that = this;
        this._builder = new JSFormBuilder("<DIV>")
            .form()
            .div()
            .class("imcms-header")

            .div()
            .html("Document Editor")
            .class("imcms-title")
            .end()

            .text()
            .id("freeTextSearchInput")
            .class("imcms-text-field imcms-element")
            .on("input", function () {
                that.find(this.value());
            })
            .on('keydown', function (e) {
                // pressing 'enter' in this field causes error, with this fix 'enter' (it's code is 13) ignored
                if (e.which == 13) {
                    e.preventDefault();
                }
            })
            .reference("searchFieldDoc")
            .placeholder("Type to find document")
            .end()

            .reference("document-editor-header")

            .div()
            .id("categoryFilterList")
            .class("category")
            .ul()
            .li()
            .html("Categories")
            .reference("document-editor-category")
            .end()
            .end()
            .end()

            .button()
            .reference("findByCategoriesButton")
            .class("imcms-neutral imcms-element")
            .html("Search by categories")
            .on("click", $.proxy(this.findByCategories, this))
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
            .on("click", $.proxy(this.onSelectElement, this))
            .column("id", "document-sort", {doc_sorting: "meta_id"})
            .column("title", "document-sort", {doc_sorting: "meta_headline"})
            .column("alias", "document-sort", {doc_sorting: "alias"})
            .column("last modified", "document-sort", {doc_sorting: "modified_datetime"})
            .column("type", "document-sort", {doc_sorting: "doc_type_id"})
            .column("")
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

            .button()
            .class("imcms-positive create-new")
            // .hidden()
            .html("Add selected…")
            .on("click", $.proxy(this.addSelected, this))
            .end()

            .button()
            .class("imcms-positive hidden pluralCopyArchButton")
            .html("Copy")
            .on("click", this.copyChecked.bind(this))
            .end()
            .button()
            .class("imcms-positive hidden pluralCopyArchButton")
            .html("Archive")
            .on("click", this.archiveChecked.bind(this))
            .end()
            .end()
            .end();

        $(this._builder[0]).appendTo("body").addClass("editor-form editor-document reset");
        this._loader.currentUser($.proxy(this.setCurrentUser, this));
        this._loader.usersList($.proxy(this.loadUsers, this));
        this._loader.categoriesList($.proxy(this.loadCategories, this));
        return this;
    },
    buildDocumentsList: function () {
        var that = this;
        this._documentListAdapter =
            new Imcms.Document.ListAdapter(
                this._builder.ref("documentsList"),
                this._loader
            );
        //Adding click event for sort
        $(".document-sort").on("click", function () {
            that.sort($(".imcms-text-field").val(), $(this).attr('doc_sorting'));
        });
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
    loadUsers: function (users) {
        this._builder.ref("document-editor-header")
            .div()
            .class("select field")
            .select()
            .id("user-filter-select")
            .name("user")
            .reference("user-filter-list")
            .on("change", this.filterByUser.bind(this))
            .end()
            .end();

        //Adding option to get all documents
        this.addUserToList(0, {id: -1, loginName: "All Documents"});
        $.each(users, this.addUserToList.bind(this));
    },
    setCurrentUser: function (user) {
        this._currentUser = user[0];
    },
    addUserToList: function (count, user) {
        $(this._builder.ref("user-filter-list").getHTMLElement()).append(
            user.id === this._currentUser.id ?
                $("<option>").val(user.id).text("My Documents").attr('selected', 'selected') : $("<option>").val(user.id).text(user.loginName)
        );
    },
    filterByUser: function () {
        var userId = $("#user-filter-select").find("option:selected").val();
        this._documentListAdapter.reloadWithData("", "", userId);
    },

    loadCategories: function (categories) {
        $.each(categories, this.addCategoryType.bind(this));
        $('#categoryFilterList').jstree({
            "checkbox": {
                "keep_selected_style": false
            },
            "plugins": ["checkbox"]
        });
    },
    addCategoryType: function (categoryType, options) {
        this._builder.ref("document-editor-category")
            .ul()
            .li()
            .html(categoryType)
            .ul()
            .reference(categoryType)
            .end()
            .end()
            .end();
        if (options.isMultiple) {
            this._builder.ref(categoryType);
        }

        $.each(options.items, this.addCategory.bind(this, categoryType));
    },
    addCategory: function (categoryType, position, category) {
        $(this._builder.ref(categoryType).getHTMLElement()).append(
            $("<li>").attr('id', category.id).text(category.name)
        );
    },
    onSelectElement: function (e) {
        var $table = $(e.currentTarget),
//            tableOffset = $table.offset();
            element = $table.find("tbody tr").filter(function (index, element) {
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
    },
    addSelected: function () {
        var data = {
            id: this._selectedRow.children[0].innerHTML,
            label: this._selectedRow.children[1].children[0] != null ? this._selectedRow.children[1].children[0].innerHTML : ""
        };
        Imcms.Editors.Menu._menuHelpers[0]._addItem(data);
        this.close();
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
    },
    copyChecked: function () {
        var loader = this._loader;
        var documentListAdapter = this._documentListAdapter;

        this.doWithAllCheckedDocs(function (id) {
            loader.copyDocument(id, {});
        });

        setTimeout(function () {
            documentListAdapter.reload();
        }, 2000);
    },
    archiveChecked: function () {
        this.doWithAllCheckedDocs(function (id) {
            this._loader.archiveDocument(id);
            $("input[doc-id=" + id + "]").parents("tr").first().addClass("archived")
        }.bind(this));
    },
    doWithAllCheckedDocs: function (apply) {
        return $('input.doc-checkbox')
            .filter(function (i, element) {
                return $(element).is(":checked");
            })
            .map(function (i, element) {
                apply($(element).attr("doc-id"));
            })
    },
    find: function (word) {
        this._documentListAdapter.reloadWithData(word);
    },
    sort: function (word, sort) {
        this._documentListAdapter.reloadWithData(word, sort);
    },
    findByCategories: function () {
        var checkedCategoriesId = [];
        var checkedCategories = $("#categoryFilterList").jstree('get_selected', true);

        $.each(checkedCategories, function (index, value) {
            if (value.id.indexOf('_') < 0) {
                checkedCategoriesId.push(value.id);
            }
        });
        $("#freeTextSearchInput").val("");
        this._documentListAdapter.reloadWithData("", "", "", checkedCategoriesId);
    }
};

Imcms.Document.MissingLangProperties = {
    name: "missing-lang-prop",
    defaultLang: {
        name: "SHOW_IN_DEFAULT_LANGUAGE",
        checked: true
    },
    doNotShow: {
        name: "DO_NOT_SHOW",
        checked: false
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
        this._title = options.data.id ? "DOCUMENT " + options.data.id : "NEW DOCUMENT";
        this.buildView();
        //this.buildValidator();
        this.createModal();

        this._loader.languagesList(this.loadLanguages.bind(this));
        this._loader.rolesList(this.loadRoles.bind(this));
        this._loader.categoriesList(this.loadCategories.bind(this));
        this._loader.usersList(this.loadUsers.bind(this));

        if (options.type === 2) {
            this._loader.templatesList(this.loadTemplates.bind(this));
        }

        if (options.data) {
            this.deserialize(options.data);
        }

        if (this._options.data.id) {
            this._loader.datesList(this._options.data.id, this.loadDates.bind(this));
        }

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
        switch (this._options.type) {
            case 2:
                this.buildStandard();
                this.buildPermissions();
                this.buildTemplates();
                break;
            case 5:
                this.buildLinking();
                this.buildStandard();
                break;
            case 8:
                this.buildFile();
                this.buildStandard();
                break;
        }

        if (this._options.data.id) {
            this.buildDates();
        }
    },
    buildStandard: function () {
        this.buildLifeCycle();
        this.buildKeywords();
        this.buildCategories();
        this.buildAccess();
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

            .span()
            .class("date-times-label")
            .html("Published:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time-short")
            .name("published-date")
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time-short")
            .name("published-time")
            .placeholder("Empty")
            .end()
            .button()
            .class("imcms-positive")
            .html("Now")
            .on("click", this.setDateTimeNow.bind(this, "published"))
            .end()
            .text()
            .class("date-time")
            .name("published-date-time-saved")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .span()
            .class("date-times-label")
            .html("Archived:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time-short")
            .name("archived-date")
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time-short")
            .name("archived-time")
            .placeholder("Empty")
            .end()
            .button()
            .class("imcms-positive")
            .html("Now")
            .on("click", this.setDateTimeNow.bind(this, "archived"))
            .end()
            .text()
            .class("date-time")
            .name("archived-date-time-saved")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .span()
            .class("date-times-label")
            .html("Publication end:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time-short")
            .name("publication-end-date")
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time-short")
            .name("publication-end-time")
            .placeholder("Empty")
            .end()
            .button()
            .class("imcms-positive")
            .html("Now")
            .on("click", this.setDateTimeNow.bind(this, "publication-end"))
            .end()
            .text()
            .class("date-time")
            .name("publication-end-date-time-saved")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .button()
            .class("imcms-positive")
            .html("Clear")
            .on("click", this.setDateTimeEmpty.bind(this, "publication-end"))
            .end()
            .end()

            .div()
            .class("field")
            .html("Publisher: ")
            .text()
            .name("published-by")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .button()
            .class("imcms-positive")
            .html("Change publisher")
            .on("click", this.openUsersList)
            .end()
            .end()
            .div()
            .reference("publisher-users-list")
            .id("users-select")
            .class("section field hidden")
            .end()

            .div()
            .class("field")
            .html("If requested language is missing")
            .end()

            .div()
            .radio()
            .name(Imcms.Document.MissingLangProperties.name)
            .value(Imcms.Document.MissingLangProperties.defaultLang.name)
            .label("Show in default language")
            .end()
            .end()
            .div()
            .radio()
            .name(Imcms.Document.MissingLangProperties.name)
            .value(Imcms.Document.MissingLangProperties.doNotShow.name)
            .label("Don't show at all")
            .end()
            .end()

            .end();
        this._contentCollection["life-cycle"] = {
            tab: this._builder.ref("life-cycle-tab"),
            page: this._builder.ref("life-cycle-page")
        };
        this._builder.ref("life-cycle-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["life-cycle"]));
    },
    setDateTimeNow: function (kindOfDate) {
        var currentDate = new Date(),
            dateTime = {
                date: currentDate.toISOString().slice(0, 10), // smth like "2016-09-06"
                time: currentDate.getHours() + ":" + currentDate.getMinutes()
            };

        this.setDateTime(kindOfDate, dateTime);
    },
    setDateTimeEmpty: function (kindOfDate) {
        var dateTime = {
            date: "--",
            time: "--"
        };

        this.setDateTime(kindOfDate, dateTime);
    },
    setDateTime: function (kindOfDate, dateTimeObj) {
        $("input.date-time-short[name=" + kindOfDate + "-date]").val(dateTimeObj.date);
        $("input.date-time-short[name=" + kindOfDate + "-time]").val(dateTimeObj.time);
    },
    openUsersList: function () {
        $('#users-select').removeClass("hidden");
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
            .name("file1")
            .reference("file1")
            .end()

            .button()
            .class("imcms-positive")
            .html("Upload file")
            .on("click", $.proxy(this.uploadDocumentFile, this))
            .end()

            .end()
            .div()

            .div()
            .id("uploadedFilesContainer")
            .class("hidden")
            .html("Upload query")
            .table()
            .id("uploadedFiles")
            .reference("uploadedFiles")
            .column("name")
            .column("")
            .column("")
            .end()
            .end()

            .div()
            .id("existingFilesContainer")
            .html("Existing files")
            .table()
            .reference("files")
            .column("id")
            .column("name")
            .column("default")
            .column("")
            .end()
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
    buildDates: function () {
        this._builder.ref("tabs")
            .div()
            .reference("dates-tab")
            .class("dates-tab imcms-tab")
            .html("Status")
            .end();
        this._builder.ref("pages")
            .div().reference("dates-page").class("dates-page imcms-page")

            .span()
            .class("date-times-label")
            .html("Created:")
            .end()
            .span()
            .html("By:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time")
            .name("created-date")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("created-time")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("created-by")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .span()
            .class("date-times-label")
            .html("Modified:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time")
            .name("modified-date")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("modified-time")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("modified-by")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .span()
            .class("date-times-label")
            .html("Archived:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time")
            .name("archived-date")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("archived-time")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("archived-by")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .span()
            .class("date-times-label")
            .html("Published:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time")
            .name("published-date")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("published-time")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("published-by")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .span()
            .class("date-times-label")
            .html("Publication end:")
            .end()

            .div()
            .class("field")
            .text()
            .class("date-time")
            .name("publication-end-date")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("publication-end-time")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .text()
            .class("date-time")
            .name("publication-end-by")
            .attr("readonly", true)
            .attr("ignored", true)
            .placeholder("Empty")
            .end()
            .end()

            .end();

        this._contentCollection["dates"] = {
            tab: this._builder.ref("dates-tab"),
            page: this._builder.ref("dates-page")
        };
        this._builder.ref("dates-tab").on("click", $.proxy(this.changeTab, this, this._contentCollection["dates"]));
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
        var shouldBeChecked = Imcms.language.name == language ? "checked" : "should-not";
        this._builder.ref("languages")
            .div()
            .div()
            .class("checkbox")
            .checkbox()
            .attr("data-node-key", "language")
            .attr("data-node-value", language)
            .attr(shouldBeChecked, shouldBeChecked)
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
    loadUsers: function (users) {
        this._builder.ref("publisher-users-list")
            .select()
            .name("publisher")
            .reference("users-list")
            .on("change", this.selectNewPublisher)
            .end();

        $.each(users, this.addUserToList.bind(this));
    },
    addUserToList: function (count, user) {
        $(this._builder.ref("users-list").getHTMLElement()).append(
            $("<option>").val(user.id).text(user.loginName)
        );
    },
    selectNewPublisher: function () {
        var $select = $('#users-select');
        var $selected = $select.find('option:selected'),
            newPublisher = {
                loginName: $selected.text(),
                id: $selected.val()
            };

        $('input[name=published-by]').val(newPublisher.loginName);
        $select.addClass("hidden");
    },
    loadDates: function (dateTimeMadeByForDateType) {
        setTimeout(function () {
            $.each(dateTimeMadeByForDateType, this.addDateTimeMadeBy.bind(this));
        }.bind(this), 100);
    },
    addDateTimeMadeBy: function (dateType, dateTimeMadeBy) {
        $.each(dateTimeMadeBy, function (name, value) {
            $("input[name=" + dateType + "-" + name + "]").val(value);
        });

        $("input[name^=" + dateType + "]")
            .filter("[name$=saved]")
            .val(dateTimeMadeBy.date + " " + dateTimeMadeBy.time);
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
            $("<option>").val(category.name).text(category.name).attr("title", category.description)
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
        var radio = $("<input>").attr("type", "radio").attr("name", "defaultFile").val(key);
        var idInput = $("<input>").attr("type", "text").attr("name", "file_").attr("oldId", key).val(key);
        this._builder.ref("files").row(
            idInput,
            val,
            radio[0],
            $("<button>").attr("type", "button").addClass("imcms-negative").click(this.removeFile.bind(this, radio))[0]
        )
    },
    removeFile: function (radio) {
        radio.attr("data-removed", "").parents("tr").hide();
    },

    addUploadedFile: function (fileInput) {
        if ($("#uploadedFiles tr").length == 1) {
            $("#uploadedFilesContainer").removeClass("hidden");
        }
        var removeButton = $("<button>").attr("type", "button").addClass("imcms-negative");
        this._builder.ref("uploadedFiles").row(
            fileInput.prop("files")[0].name,
            fileInput,
            removeButton.click(this.removeUploadedFile.bind(this, removeButton))
        )
    },

    removeUploadedFile: function (removeButton) {
        removeButton.parents("tr").remove();
        if ($("#uploadedFiles tr").length == 1) {
            $("#uploadedFilesContainer").addClass("hidden");
        }
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

    uploadDocumentFile: function () {
        if ($('input[type=file][name="file1"]').prop("files").length > 0) {
            var $item = $('input[type=file][name="file1"]');
            var $clone = $item.clone();
            $item.attr("name", "tmp_file").addClass("hidden");
            $item.after($clone);
            $item.addClass("hidden");
            this.addUploadedFile($item);
        }
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

            } else if ($this.is("[type=radio]")) {
                // need to be inside upper 'if' to not fall into next 'else' block if not checked
                if ($this.is(":checked")) {
                    result[$this.attr("name")] = $this.val();
                }
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

            // editedFiles
            var tmp = {};
            $source.find("input[name^=file_]").each(function () {
                if ($(this).attr("oldId") != $(this).val()) {
                    var obj = {};
                    tmp[$(this).attr("oldId")] = $(this).val();
                    return obj;
                }
            });
            result["editedFiles"] = tmp;

            $source.find("input[name$=_file]").each(function () {
                var fileInput = $(this).prop("files")[0];
                formData.append("file", fileInput);
            });
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
            } else if ($this.is("[type=radio][name=" + Imcms.Document.MissingLangProperties.name + "]")) {
                $this.prop("checked", $this.val() == data[Imcms.Document.MissingLangProperties.name]);
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
    _pageNumber: -1,
    init: function () {
        this._loader.documentsList($.proxy(this.buildList, this));
        this.buildPager();
    },
    buildList: function (data) {
        this._pageNumber++;
        $.each(data, $.proxy(this.addDocumentToList, this));

        var count = this._pagerHandler._options.count;
        var pageNumber = this._pageNumber;

        $(this._container.getHTMLElement()).find("tr")
            .filter(function (pos) {
                return pos >= pageNumber * count;
            }).each(function (pos, item) {
            $(item).on("dragstart", function (event) {
                var realPos = pos - 1;

                $(".ui-widget-overlay").css("display", "none");
                //Required to get correct label from table
                var tmpData = {};
                tmpData['name'] = data[realPos].name;
                tmpData['alias'] = data[realPos].alias;
                tmpData['language'] = data[realPos].language;
                tmpData['id'] = data[realPos].id;
                tmpData['label'] = [data[realPos].label[0] != null ? data[realPos].label[0].innerText : ""];
                tmpData['lastModified'] = data[realPos].lastModified;
                tmpData['type'] = data[realPos].type;
                tmpData['status'] = data[realPos].status;
                event.originalEvent.dataTransfer.setData("data", JSON.stringify(tmpData));
            }).on("dragend", function () {
                $(".ui-widget-overlay").css("display", "block");
            }).attr("draggable", true);
        });

    },
    addDocumentToList: function (position, data) {
        var deleteButton = $("<button>"),
            row;

        // linked doc title
        if (data.label) {
            var linkURL = Imcms.Linker.getContextPath() + "/" + (data.alias ? data.alias : data.id);
            data.label = $("<a>").attr("href", linkURL).html(data.label);
        }

        this._container.row(data.id, data.label, data.alias, data.lastModified, data.type, $("<span>")
            .append($('<input>')
                .click(this.showPluralArchiveAndCopyButtons)
                .addClass("field doc-checkbox")
                .attr("type", "checkbox")
                .attr("doc-id", data.id))
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
    showPluralArchiveAndCopyButtons: function () {
        var checked = $('input.doc-checkbox')
            .filter(function (i, element) {
                return $(element).is(":checked");
            }).length;

        if (checked) {
            $('.pluralCopyArchButton').show();
        } else {
            $('.pluralCopyArchButton').hide();
        }
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
            this._pageNumber = -1;
            this._pagerHandler.reset();
        }.bind(this));
    },
    reloadWithData: function (word, sort, userId, categoriesId) {
        this._container.clear();
        this._pagerHandler.reset(word, sort, userId, categoriesId);
        this._pageNumber = -1;
        this._loader.filteredDocumentList({
            term: word || "",
            sort: sort || "",
            order: this._pagerHandler._order,
            userId: this._pagerHandler._userId,
            categoriesId: this._pagerHandler._categoriesId
        }, $.proxy(this.buildList, this));
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
            .on('keydown', function (e) {
                // pressing 'enter' in this field causes error, with this fix 'enter' (it's code is 13) ignored
                if (e.which == 13) {
                    e.preventDefault();
                }
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
        var dialog = $(this._builder[0]).parents(".ui-dialog")
            .removeClass()
            .addClass("pop-up-form menu-viewer reset")
            .css({position: "fixed"}),

            header = dialog.children(".ui-dialog-titlebar")
                .removeClass()
                .addClass("imcms-header")
                .append($("<div>").addClass("imcms-title").text("DOCUMENT SELECTOR"))
                .on('mousedown', function (e) {
                    // jquery-ui (or smth else) pins onmousedown event listener on this header and produces
                    // errors while clicking on it
                    e.preventDefault();
                }),

            content = dialog.children(".ui-dialog-content")
                .removeClass()
                .addClass("imcms-content"),

            footer = dialog.children(".ui-dialog-buttonpane")
                .removeClass()
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
                return pos > startIndex;
            }).each(function (pos, item) {
            $(item).on("dragstart", function (event) {
                $(".ui-widget-overlay").css("display", "none");
                event.originalEvent.dataTransfer.setData("data", JSON.stringify(data[pos]));
            }).on("dragend", function () {
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
//            tableOffset = $table.offset();
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

    //Filter options
    _term: "",
    _sort: "",
    _order: "",
    _userId: "",
    _categoriesId: [],

    _options: {
        count: 50,
        handler: function () {
        },
        itemsPointer: function () {
            return 0;
        },
        resultProcessor: function (data) {

        },
        waiterContent: ""
    },
    init: function (term, sort, order) {
        this._term = term;
        this._sort = sort === undefined ? "modified_datetime" : sort;
        this._order = order === undefined ? (this._sort === "modified_datetime" ? "desc" : "asc") : order;
        $(this._target).scroll(this.scrollHandler.bind(this)).bind('beforeShow', this.scrollHandler.bind(this));
        this.scrollHandler();
    },
    handleRequest: function (skip) {
        this._addWaiterToTarget();
        this._isHandled = true;
        this._options.handler({
            skip: skip,
            take: this._options.count,
            term: this._term,
            sort: this._sort,
            order: this._order,
            userId: this._userId,
            categoriesId: this._categoriesId
        }, this.requestCompleted.bind(this));
    },
    requestCompleted: function (data) {
        this._options.resultProcessor(this._pageNumber * this._options.count, data);
        this._pageNumber++;
        this._removeWaiterFromTarget();
        this._isHandled = false;
        this.scrollHandler();
    },
    scrollHandler: function () {
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
    _removeWaiterFromTarget: function () {
        this._waiter.remove();
    },
    reset: function (term, sort, userId, categoriesId) {
        var oldSort = this._sort;
        var oldUserId = this._userId;
        var oldCategoriesId = this._categoriesId;
        this._pageNumber = 1;
        this._term = term;
        this._userId = userId == undefined && oldUserId != undefined ? oldUserId : userId;
        this._sort = sort;
        if (this._sort === oldSort || oldSort == undefined) {
            this._order = this._order === "asc" ? "desc" : "asc";
        }
        else {
            this._order = "asc";
        }
        this._categoriesId = (categoriesId == undefined || categoriesId == []) && oldCategoriesId != [] ? oldCategoriesId : categoriesId;

        this.scrollHandler();
    }
};
