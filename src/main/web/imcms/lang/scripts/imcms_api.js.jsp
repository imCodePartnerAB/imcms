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

        Imcms.MenuEditor.MenuHelper = function (element) {
            this._target = $(element);
            return this.init();
        }
        Imcms.MenuEditor.MenuHelper.prototype = {
            _target: {},
            init: function () {
                this._initItems();
                this._createWrapper();
                this._createHelperComboBox();
                return this;
            },
            _initItems: function () {
                var that = this;
                this._items = this._target.find(".editor-menu-item");
                this._items.each(function (position, item) {
                    that._initItem(new Imcms.MenuEditor.MenuItemHelper(item));
                });
            },
            _initItem: function (menuItem) {
                var that = this;
                menuItem.prev = function () {
                    var position = that._items.index(this._target);
                    if (position > 0)
                        return $(that._items[position - 1]);
                    return null;
                }
                menuItem.next = function () {
                    var position = that._items.index(this._target);
                    if (position < that._items.length - 1)
                        return $(that._items[position + 1]);
                    return null;
                }
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
                    source: $.proxy(Imcms.APIs.Menu, "read"),
                    select: function (data) {
                        that._menuInfo = data;
                    }
                });
                this._wrapper.append($("<button>").text("+").addClass("editor-menu-wrapper-accepter").on("click", function () {
                    var request = Imcms.Utils.margeObjectsProperties(that._target.data().prettify(), that._menuInfo, Imcms.document);
                    Imcms.Editors.Menu.create(request, function () {
                        window.location.reload()
                    });
                    console.info("menu saved");
                }));
            }
        };
        Imcms.MenuEditor.MenuItemHelper = function (element) {
            this._target = $(element);
            return this.init();
        };

        Imcms.MenuEditor.MenuItemHelper.prototype = {
            _target: {},
            init: function () {
                this._createWrapper();
                this._createButtons();
                return this;
            },
            _createWrapper: function () {
                this._wrapper = $("<div>").addClass("editor-menu-item-wrapper").appendTo(this._target);
            },
            _createButtons: function () {
                var that = this;
                this._deleteItemButton = $("<button>")
                        .text("×")
                        .addClass("editor-menu-item-wrapper-button")
                        .on("click", function () {
                            var parentMenu = that._target.parents(".editor-menu");
                            var request = Imcms.Utils.margeObjectsProperties(that._target.data().prettify(),
                                    parentMenu.data().prettify(), Imcms.document);
                            Imcms.Editors.Menu.delete(request, function () {
                                window.location.reload()
                            });
                        }).appendTo(this._wrapper);
                this._moveItemUpButton = $("<button>")
                        .addClass("editor-menu-item-wrapper-button")
                        .text("↑")
                        .on("click", function () {
                            var parentMenu = that._target.parents(".editor-menu");
                            var prev = that.prev();
                            if (prev === null) return;
                            var positionTo = {"menu-item-position-to": prev.data().prettify()["menu-item-position"]};
                            var request = Imcms.Utils.margeObjectsProperties(that._target.data().prettify(),
                                    parentMenu.data().prettify(), Imcms.document, positionTo);
                            Imcms.Editors.Menu.update(request, function () {
                                window.location.reload()
                            });
                        }).appendTo(this._wrapper);
                this._moveItemDownButton = $("<button>")
                        .text("↓")
                        .addClass("editor-menu-item-wrapper-button")
                        .on("click", function () {
                            var parentMenu = that._target.parents(".editor-menu");
                            var next = that.next();
                            if (next === null) return;
                            var positionTo = {"menu-item-position-to": next.data().prettify()["menu-item-position"]};
                            var request = Imcms.Utils.margeObjectsProperties(that._target.data().prettify(),
                                    parentMenu.data().prettify(), Imcms.document, positionTo);
                            Imcms.Editors.Menu.update(request, function () {
                                window.location.reload()
                            });
                        }).appendTo(this._wrapper);
            }
        }

        Imcms.MenuEditor.prototype = {
            init: function () {
                var that = this;
                jQuery(".editor-menu").each(function (pos, element) {
                    new Imcms.MenuEditor.MenuHelper(element);
                });
            },
            create: $.proxy(Imcms.APIs.Menu, "create"),
            read: $.proxy(Imcms.APIs.Menu, "read"),
            update: $.proxy(Imcms.APIs.Menu, "update"),
            delete: $.proxy(Imcms.APIs.Menu, "delete")
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
                            .addClass("custom-combobox")
                            .appendTo(this.element.parent());
                    this._createAutocomplete();
                    this._createShowAllButton();
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
                            .appendTo(this.wrapper)
                            .val("")
                            .attr("title", "")
                            .addClass("custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left")
                            .autocomplete({
                                delay: 0,
                                minLength: 0,
                                source: $.proxy(this, "_source"),
                                select: function (event, ui) {
                                    var select = that.options.select || function () {
                                            }
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
        }