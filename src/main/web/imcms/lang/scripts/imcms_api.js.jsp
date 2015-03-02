<%@ page
                 contentType="text/javascript"
                 pageEncoding="UTF-8"

%>
        var Imcms = {};
        Imcms.APIs = {};
        Imcms.APIs.Text = {};
        Imcms.APIs.Menu = {}
        Imcms.Editors = {};
        Imcms.Editors.Text = {};
        Imcms.Editors.Menu = {};
        Imcms.Utils = {};
        Imcms.document = {"meta": <%=request.getParameter("meta_id")%>};

        $(document).ready(function () {
            new Imcms.Bootstrapper().bootstrap();
        });


    /*
     Imcms bootstraper
     */

        Imcms.Bootstrapper = function () {
        }
        Imcms.Bootstrapper.prototype = {
            bootstrap: function () {
                Imcms.APIs.Text = new Imcms.TextAPI();
                Imcms.Editors.Text = new Imcms.TextEditor();
                Imcms.APIs.Menu = new Imcms.MenuAPI();
                Imcms.Editors.Menu = new Imcms.MenuEditor();
            }
        };
    /*
     Text Editer
     */

        Imcms.TextEditor = function () {
            this.init();
        };

        Imcms.TextEditor.prototype = {

            init: function () {
                var that = this;
                var textFrame = new Imcms.FrameBuilder().title("Text Editor");
                $("[contenteditable='true']").each(function (position, element) {
                    element = $(element);
                    var parent = element.parent().css({position: "relative"});
                    var currentFrame = textFrame.click(function (e) {
                        currentFrame.hide();
                        element.focus();
                        element.trigger(e);
                        element.blur(function () {
                            currentFrame.show();
                        });
                    }).build().appendTo(parent);
                });
                CKEDITOR.on('instanceCreated', $.proxy(this, "_onCreated"));
                CKEDITOR.on("confirmChanges", $.proxy(this, "_onConfirm"));
            },
            _onConfirm: function (event) {
                Imcms.APIs.Text.update(event.editor.element.$, null);
            },
            _onCreated: function (event) {
                var editor = event.editor,
                        element = editor.element;

                // Customize editors.
                // These editors don't need features like smileys, templates, iframes etc.\
                // Customize the editor configurations on "configLoaded" event,
                // which is fired after the configuration file loading and
                // execution. This makes it possible to change the
                // configurations before the editor initialization takes place.
                editor.on('configLoaded', $.proxy(this, "_onEditorLoaded"));

            },
            _onEditorLoaded: function (event) {
                var editor = event.editor,
                        element = editor.element;


                // Remove unnecessary plugins to make the editor simpler.
                editor.config.removePlugins = 'colorbutton,find,flash,font,' +
                'forms,iframe,image,newpage,removeformat,' +
                'smiley,specialchar,stylescombo,templates';
                editor.config.extraPlugins = editor.config.extraPlugins + ",documentSaver";

                editor.config.toolbar = 'MyToolbar';
                editor.config.toolbar_MyToolbar =
                        [
                            ['Bold', 'Italic', 'Underline', 'Strike'],
                            ['NumberedList', 'BulletedList', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Outdent', 'Indent'],
                            ['Link', 'Unlink'],
                            ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Scayt'],
                            ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'],
                            ['confirm', 'cancel']
                        ];
            }
        };

        Imcms.TextAPI = function () {
        };

        Imcms.TextAPI.prototype = {
            path: "/edit",

            update: function (sender, callback) {
                var data = jQuery(sender).data().prettify();
                data.meta = Imcms.document.meta;
                data.content = jQuery(sender).html();
                console.info(data);
                $.ajax({
                    url: this.path,
                    type: "POST",
                    data: data,
                    success: callback
                })

            }
        };

    /*
     Menu Editer
     */

        Imcms.MenuEditor = function () {
            this.init();
        };

        Imcms.MenuEditor.MenuHelper = function (element, menu) {
            this._menu = menu;
            this._target = $(element);
            return this.init();
        };
        Imcms.MenuEditor.MenuHelper.prototype = {
            _menu: {},
            _target: {},
            _builder: {},
            init: function () {
                this._createWrapper();
                this._createHelperComboBox();
                return this;
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
                        .html("Save and close")
                        .class("positive save-and-close")
                        .reference("saveButton")
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
                        .end()
                        .end();
            },
            buildTree:function(){
                var data = [];
                element.find(".editor-menu-item").each(function (position, item) {
                    item = $(item);
                    var menuItemData = item.data().prettify();
                    data.push({
                        id: menuItemData["menu-item-tree-position"],
                        position: parseInt(menuItemData["menu-item-tree-position"]
                                .substr(menuItemData["menu-item-tree-position"].lastIndexOf(".") + 1)),
                        "doc-id": menuItemData["menu-item-id"],
                        label: item.find("a").text(),
                        name: item.find("a").text(),
                        "tree-index": menuItemData["menu-item-tree-position"]
                    });
                });
            },
            _createWrapper: function () {
                this._wrapper = $("<div>").addClass("editor-menu-wrapper");
                this._target.append(this._wrapper);
            },
            _createHelperComboBox: function () {
                var that = this;
                this._helper = $("<input>");
                this._wrapper.append(this._helper);
                this._helper.combobox({
                    source: function () {
                        Imcms.Editors.Menu.read.apply(Imcms.Editors.Menu, arguments);
                    },
                    select: function (data) {
                        that._menuInfo = data;
                    }
                });
                this._wrapper.append($("<button>").text("...").attr("type", "button").addClass("positive browse").on("click", function () {
                    Imcms.Editors.Menu._popup(that);
                }));
                this._wrapper.append($("<button>").text("Add").attr("type", "button").addClass("positive add").on("click", function () {
                    var request = Imcms.Utils.margeObjectsProperties(that._menu.data().prettify(), that._menuInfo, Imcms.document);
                    Imcms.Editors.Menu.create(request, function () {
                        window.location.reload();
                    });
                    console.info("menu saved");
                }));
                this._wrapper.append($("<div>").css("clear", "both"));
            }
        };

        Imcms.MenuEditor.prototype = {
            _menuHelpers: [],
            init: function () {
                var that = this;
                var menuFrame = new Imcms.FrameBuilder().title("Menu Editor");
                jQuery(".editor-menu").each(function (pos, element) {
                    element = $(element);
                    var builder = new JSFormBuilder("<div>")
                            .form()
                            .div()
                            .class("header")
                            .div()
                            .html("Menu Editor")
                            .class("title")
                            .end()
                            .button()
                            .html("Save and close")
                            .class("positive save-and-close")
                            .reference("saveButton")
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
                            .end()
                            .end();
                    var data = [];
                    element.find(".editor-menu-item").each(function (position, item) {
                        item = $(item);
                        var menuItemData = item.data().prettify();
                        data.push({
                            id: menuItemData["menu-item-tree-position"],
                            position: parseInt(menuItemData["menu-item-tree-position"]
                                    .substr(menuItemData["menu-item-tree-position"].lastIndexOf(".") + 1)),
                            "doc-id": menuItemData["menu-item-id"],
                            label: item.find("a").text(),
                            name: item.find("a").text(),
                            "tree-index": menuItemData["menu-item-tree-position"]
                        });
                    });

                    function buildTree(items) {
                        var tree = [];
                        var currentLevel = 1;
                        var current = null, item;
                        while ((item = items[0])) {
                            var itemLevel = (item["tree-index"].match(/\./g) || []).length + 1;
                            if (!current || itemLevel == currentLevel) {
                                current = item;
                                current["children"] = [];
                                currentLevel = itemLevel;
                                tree.push(current);
                                items.shift();
                            } else if (itemLevel > currentLevel && itemLevel - currentLevel == 1) {
                                current["children"] = buildTree(items);
                            } else {
                                break;
                            }
                        }
                        return tree;
                    }

                    data = buildTree(data);
                    var menuContent = $(builder.ref("menuContent").getHTMLElement()).tree({
                        selectable: false,
                        data: data,
                        autoOpen: true,
                        dragAndDrop: true,
                        onCreateLi: function (node, $li) {
                            // Append a link to the jqtree-element div.
                            // The link has an url '#node-[id]' and a data property 'node-id'.
                            var treeElement = $li.find('.jqtree-element').css({display: "inline-block"}).empty();
                            $("<span>").text(node["doc-id"]).appendTo(treeElement);
                            $("<span>").text(node.name).appendTo(treeElement);
                            $("<span>").appendTo(treeElement).append(
                                    $("<button>").addClass("negative").attr("type", "button").click(function () {
                                        /*var response = Imcms.Utils.margeObjectsProperties(
                                         Imcms.document,
                                         element.data().prettify(),
                                         {"referenced-document": node["doc-id"]}
                                         );
                                         // that.delete(response);*/
                                        menuContent.tree('removeNode', node);
                                    }));
                        }
                    });
                    menuContent.bind(
                            'tree.move',
                            function (event) {
                                var info = event.move_info;
                                var current = info.moved_node;
                                var parentFrom = current.parent;
                                var nodeTo = info.target_node;

                                for (var i = parentFrom.children.indexOf(current) + 1,
                                             count = parentFrom.children.length; i < count; i++)
                                    parentFrom.children[i].position--;
                                switch (info.position) {
                                    case "inside":
                                    {
                                        var featureParent = nodeTo;
                                        console.info("Parent node:");
                                        console.info(nodeTo);
                                        for (var i = 0, count = featureParent.children.length; i < count; i++) {
                                            featureParent.children[i].position++;
                                            console.info(featureParent.children[i]);
                                        }
                                        current.position = 1;
                                    }
                                        break;
                                    case "before":
                                    {
                                        var featurePosition = nodeTo.position - 1;
                                        var featureParent = nodeTo.parent;
                                        console.info("Parent node:");
                                        console.info(featureParent);
                                        for (var i = featureParent.children.indexOf(nodeTo),
                                                     count = featureParent.children.length; i < count; i++) {
                                            featureParent.children[i].position++;
                                            console.info(featureParent.children[i]);
                                        }
                                        current.position = featurePosition;
                                    }
                                        break;
                                    case "after":
                                    {
                                        var featurePosition = nodeTo.position + 1;
                                        var featureParent = nodeTo.parent;
                                        console.info("Parent node:");
                                        console.info(featureParent);
                                        for (var i = featureParent.children.indexOf(nodeTo) + 1,
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
                            }
                    );
                    builder.ref("saveButton").on("click", function () {
                                $(builder[0]).hide();
                                function collect(node, prefix, data) {
                                    var result = data || [];
                                    for (var i = 0, count = node.children.length; i < count; i++) {
                                        var currentNode = node.children[i];
                                        var tree = (prefix ? (prefix + ".") : "") + currentNode.position;
                                        result.push({
                                            "referenced-document": currentNode["doc-id"],
                                            "tree-sort-index": tree
                                        });
                                        console.info("----------------");
                                        console.info(node);
                                        console.info(response);
                                        console.info("----------------");
                                        if (currentNode.children.length > 0) {
                                            result = collect(currentNode, tree, result);
                                        }
                                    }
                                    return result;
                                }

                                var response = Imcms.Utils.margeObjectsProperties({items: collect(menuContent.tree("getTree"))},
                                        Imcms.document,
                                        element.data().prettify());
                                that.update({data: JSON.stringify(response)}, function () {
                                    location.reload();
                                });
                            }
                    );
                    that._menuHelpers[pos] = new Imcms.MenuEditor.MenuHelper(builder.ref("footer").getHTMLElement(), element);
                    $(builder[0]).appendTo("body").addClass("editor-form");
                    menuFrame.click(function () {
                        $(builder[0]).fadeIn("fast").find(".content").css({height: $(window).height() - 100});
                    }).build().appendTo(element);
                });
            },
            create: function () {
                Imcms.APIs.Menu.create.apply(Imcms.APIs.Menu, arguments);
            }

            ,
            read: function () {
                Imcms.APIs.Menu.read.apply(Imcms.APIs.Menu, arguments);
            }
            ,
            update: function () {
                Imcms.APIs.Menu.update.apply(Imcms.APIs.Menu, arguments);
            }
            ,
            delete: function () {
                Imcms.APIs.Menu.delete.apply(Imcms.APIs.Menu, arguments);
            }

            ,

            _popup: function () {
                var menuHelper = arguments[0] || this._menuHelpers[0];
                var that = this;
                var selectedRow = null;
                var formBuilder = JSFormBuilder("<DIV>")
                        .form()
                        .class("editor-menu-form")
                        .fieldset()
                        .div()
                        .text()
                        .reference("searchField")
                        .label("Document name")
                        .end()
                        .end()
                        .div()
                        .table()
                        .on("click", function (e) {
                            var element = document.elementFromPoint(e.pageX, e.pageY);
                            if (selectedRow)
                                selectedRow.className = "";
                            selectedRow = element.parentElement;
                            selectedRow.className = "clicked";
                        })
                        .column("id")
                        .column("label")
                        .column("language")
                        .column("alias")
                        .reference("documentsTable")
                        .end()
                        .end()
                        .end()
                        .end();
                $(formBuilder[0]).dialog({
                    autoOpen: true,
                    height: 500,
                    width: 700,
                    modal: true,
                    buttons: {
                        "Add selected": function () {
                            var dialog = $(this);
                            dialog.next(".ui-dialog-buttonpane button").filter(function () {
                                return $(this).text() == "Add selected";
                            }).attr("disabled", true).addClass("ui-state-disabled");
                            var request = Imcms.Utils.margeObjectsProperties(Imcms.document, menuHelper._menu.data().prettify(), {id: selectedRow.children[0].innerHTML});
                            that.create(request, function () {
                                dialog.dialog("close");
                                window.location.reload();
                            });
                        },
                        Cancel: function () {
                            $(this).dialog("close");
                        }
                    }
                });
                var response = function (data) {
                    formBuilder.ref("documentsTable").clear();
                    for (var rowId in data)
                        formBuilder.ref("documentsTable").row(data[rowId]);
                };
                formBuilder.ref("searchField").on("input", function () {
                    that.read({term: this.value()}, response);
                });
                this.read({term: ""}, response);
            }
        };


        Imcms.MenuAPI = function () {
        };

        Imcms.MenuAPI.prototype = {
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


    /*
     JQuery Addons Data
     return all attr in tag that started with `data-`
     */
        (function ($) {
            $.widget("custom.combobox", {
                _create: function () {
                    this.wrapper = $("<span>")
                            .appendTo(this.element.parent());
                    this._createAutocomplete();
                    //this._createShowAllButton();
                    this.cache = {};
                },
                _source: function (request, response) {
                    var that = this;
                    var term = request.term;
                    if (term in this.cache) {
                        response(this.cache[term]);
                        return;
                    }

                    var source = this.options.source || function (request, response) {
                                response({});
                            };
                    source(request, function (data) {
                        that.cache[term] = data;
                        response(data);
                    });
                },

                _createAutocomplete: function () {
                    var that = this;
                    this.element
                            .val("")
                            .attr("title", "")
                            .attr("placeholder", "Type to find document")
                            .autocomplete({
                                delay: 0,
                                minLength: 0,
                                source: $.proxy(this, "_source"),
                                select: function (event, ui) {
                                    var select = that.options.select || function () {
                                            };
                                    select(ui.item);
                                }
                            })
                            .tooltip({
                                tooltipClass: "ui-state-highlight"
                            });
                },

                _createShowAllButton: function () {
                    var input = this.element,
                            wasOpen = false;

                    $("<a>")
                            .attr("tabIndex", -1)
                            .attr("title", "Show All Items")
                            .tooltip()
                            .appendTo(this.wrapper)
                            .button({
                                icons: {
                                    primary: "ui-icon-triangle-1-s"
                                },
                                text: false
                            })
                            .removeClass("ui-corner-all")
                            .addClass("custom-combobox-toggle ui-corner-right")
                            .mousedown(function () {
                                wasOpen = input.autocomplete("widget").is(":visible");
                            })
                            .click(function () {
                                input.focus();

                                // Close if already visible
                                if (wasOpen) {
                                    return;
                                }

                                // Pass empty string as value to search for, displaying all results
                                input.autocomplete("search", "");
                            });
                },

                _destroy: function () {
                    this.wrapper.remove();
                    this.element.show();
                }
            });
        })(jQuery);

        Imcms.FrameBuilder = function () {

        };
        Imcms.FrameBuilder.prototype = {
            _click: function () {
            },
            _title: "",
            title: function () {
                this._title = arguments[0];
                return this;
            },
            click: function () {
                this._click = arguments[0];
                return this;
            },
            build: function () {
                var frame = $("<div>").addClass("editor-frame");

                this._createHeader().appendTo(frame);
                frame.click(this._click);
                return frame;
            },

            _createHeader: function () {
                var headerPh = $("<div>").addClass("header-ph");
                var header = $("<div>").addClass("header").appendTo(headerPh);

                this._createTitle().appendTo(header);
                return headerPh;
            },

            _createTitle: function () {
                return $("<div>").addClass("title").html(this._title);
            }
        };

    /*$(function () {
     $("#combobox").combobox();
     $("#toggle").click(function () {
     $("#combobox").toggle();
     });
     });*/

        (function ($) {
            var $super = $.fn.data;
            $.fn.data = function () {
                if (arguments.length === 0) {
                    if (this.length === 0) {
                        console.error("No element");
                        return null;
                    }

                    var uglyResult = {
                        prettify: function () {
                            var prettyResult = {};
                            for (var property in this) {
                                if (this.hasOwnProperty(property)) {
                                    prettyResult[property.replace("data-", "")] = this[property];
                                }
                            }

                            return prettyResult;
                        }
                    };
                    $.each(this[0].attributes, function () {
                        if (this.specified && this.name.indexOf("data-") > -1) {
                            uglyResult[this.name] = this.value;
                        }
                    });

                    return uglyResult;
                }

                return $super.apply(this, arguments);
            };
        })(jQuery);
    /*
     CKEditor Addons
     */
        CKEDITOR.define("confirmChanges", {});
        CKEDITOR.plugins.add("documentSaver", {
            init: function (editor) {
                var pluginName = "documentSaver";
                editor.documentSnapshot = editor.getData();
                editor.blurHandler = function (e) {
                    if (e.editor.checkDirty()) {
                        e.editor.openDialog(pluginName);
                    }
                };
                editor.focusHandler = function (e) {
                    e.removeListener();
                    e.editor.on('blur', e.editor.blurHandler);
                }
                var confirmCommandFunction = function (e) {
                    CKEDITOR.fire("confirmChanges", {}, e);
                    editor.documentSnapshot = e.getData();
                    e.resetDirty();
                    e.removeListener('blur', e.blurHandler);
                    e.focusManager.unlock();
                    e.element.$.blur();
                    e.focusManager.blur();
                    e.on("focus", e.focusHandler);
                };
                var confirmCommandDefinition =
                {
                    // This command works in both editing modes.
                    modes: {wysiwyg: 1, source: 1},

                    // This command will not auto focus editor before execution.
                    editorFocus: false,

                    // This command requires no undo snapshot.
                    canUndo: false,

                    exec: confirmCommandFunction
                };
                editor.addCommand("confirmChanges", confirmCommandDefinition);
                var cancelCommandFunction = function (e) {
                    e.setData(e.documentSnapshot);
                    e.removeListener('blur', e.blurHandler);
                    e.focusManager.unlock();
                    e.element.$.blur();
                    e.focusManager.blur();
                    e.on("focus", e.focusHandler);
                };
                var cancelCommandDefinition =
                {
                    // This command works in both editing modes.
                    modes: {wysiwyg: 1, source: 1},

                    // This command will not auto focus editor before execution.
                    editorFocus: false,

                    // This command requires no undo snapshot.
                    canUndo: false,

                    exec: cancelCommandFunction
                };
                editor.on('blur', editor.blurHandler);


                editor.addCommand("cancelChanges", cancelCommandDefinition);
                editor.ui.addButton('confirm',
                        {
                            label: 'Save all changes',
                            command: "confirmChanges",
                            icon: "images/ic_apply.png"
                        });
                editor.ui.addButton('cancel',
                        {
                            label: 'Cancel all changes and restore document to previous state',
                            command: "cancelChanges",
                            icon: "images/ic_cancel.png"
                        });
            }
        });
        CKEDITOR.dialog.add("documentSaver", function (e) {
            return {
                title: 'Save Dialog',
                width: 200,
                height: 25,
                onOk: function () {
                    e.execCommand("confirmChanges");
                },
                onCancel: function () {
                    e.execCommand("cancelChanges");
                },
                onHide: function () {
                    setTimeout(function () {
                        e.focusManager.unlock();
                        e.element.$.blur();
                        e.focusManager.blur();
                        $(e.focusManager.currentActive).blur();
                    }, 500);
                },
                contents: [
                    {
                        id: 'general',
                        label: 'Settings',
                        elements: [
                            {
                                type: 'html',
                                html: '<div style="text-align:center; line-height:25px">Save document?</div>'
                            }
                        ]
                    }
                ]
            };
        });


    /*
     Imcms Utils
     */

    //Marge all passed objects properties to one result {a:1, b:2}+{c:1, b:2} = {a:1, b:2, c:1}
        Imcms.Utils.margeObjectsProperties = function () {
            var margedResult = {};
            for (var objKey in arguments) {
                var obj = arguments[objKey];
                for (var attr in obj)
                    margedResult[attr] = obj[attr];
            }
            return margedResult;
        };