/**
 * Created by Shadowgun on 15.01.2015.
 */
var JSFormBuilder = function () {
    return JSFormBuilder.mix(JSFormBuilder.get(arguments[0]), JSFormBuilder.fn, JSFormBuilder.prop);
};
JSFormBuilder.Classes = {};
JSFormBuilder.Mixins = {};
JSFormBuilder.fn = {};
JSFormBuilder.prop = {
    scope: {}
};

JSFormBuilder.get = function (arg) {
    var jQResult = jQuery(arg);
    var result = {};
    for (var i = 0, to = jQResult.length; i < to; i++)
        result[i] = jQResult[i];
    result.length = jQResult.length;
    return result;
};
JSFormBuilder.margeObjectsProperties = function () {
    var margedResult = {};
    for (var objKey in arguments) {
        var obj = arguments[objKey];
        for (var attr in obj)
            margedResult[attr] = obj[attr];
    }
    return margedResult;
};
JSFormBuilder.clone = function (obj) {
    if (obj == null || typeof(obj) != 'object')
        return obj;
    if (obj instanceof HTMLElement)
        return obj.cloneNode(true);
    var temp = obj.constructor(); // changed

    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            temp[key] = clone(obj[key]);
        }
    }
    return temp;
};
JSFormBuilder.extend = function (child, parent) {
    var override = child.prototype || {};
    for (var key in parent) {
        if (Object.prototype.hasOwnProperty.call(parent, key)) {
            child[key] = parent[key];
        }
    }
    function surrogateConstructor() {
        this.constructor = child;
    }

    surrogateConstructor.prototype = parent.prototype;
    child.prototype = new surrogateConstructor;
    child.__super__ = parent.prototype;
    child.__super__.constructor = parent;
    for (var prop in override) {
        child.prototype[prop] = override[prop];
    }
    return child;
};

JSFormBuilder.mix = function (object) {
    var modify = object.prototype || object;
    var mixins = Array.prototype.slice.call(arguments, 1);
    for (var i = 0; i < mixins.length; ++i) {
        for (var prop in mixins[i]) {
            if (!modify.hasOwnProperty(prop)) {
                var bindMethod = function (mixin, key) {
                    return function () {
                        return mixin[key].apply(this, arguments)
                    }
                };
                modify[prop] = bindMethod(mixins[i], prop);
            }
        }
    }
    if (object.prototype)
        object.prototype = modify;
    else object = modify;
    return object;
};
JSFormBuilder.capitaliseFirstLetter = function (string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
};

JSFormBuilder.fn.each = function () {
    if (arguments.length === 0) return this;
    for (var i = 0, to = this.length; i < to; i++)
        arguments[0].call(this, i, this[i]);
    return this;
};

JSFormBuilder.fn.form = function () {
    return new JSFormBuilder.Classes.FormAdapter(this);
};

JSFormBuilder.fn.ref = function () {
    if (Object.prototype.hasOwnProperty.apply(this.scope, arguments))
        return this.scope[arguments[0]].get();
    return null;
};

JSFormBuilder.Classes.BaseAdapter = function () {
    if (arguments[0] instanceof JSFormBuilder.Classes.ContainerAdapter || !arguments[0])
        this._parent = arguments[0];
    else throw  "Element '" + arguments[0] + "' should instantiate from 'ContainerAdapter'";

    arguments[1] = arguments[1].toLowerCase();
    if (["form", "input", "textarea", "label", "button", "fieldset", "legend", "select", "div", "table"].indexOf(arguments[1]) > -1) {
        arguments[1] = JSFormBuilder.capitaliseFirstLetter(arguments[1]);
        JSFormBuilder.mix(this, JSFormBuilder.Mixins[arguments[1]], JSFormBuilder.Mixins.AttributeBase, JSFormBuilder.Mixins.FunctionBase);
        JSFormBuilder.Classes[arguments[1]].apply(this);
    }
    //this._element = document.createElement(arguments[1]);
    else throw  "Element '" + arguments[1] + "' is not available for this operation";
};

JSFormBuilder.Classes.BaseAdapter.prototype = {
    _parent: {},
    _element: {},
    _reference: null,
    end: function () {
        if (this._label)
            this._parent._append(this._label);
        if (this._reference) {
            var that = this;
            if (!this._parent._localScope) {
                this._parent._localScope = {};
            }
            this._parent._localScope[this._reference] = {
                get: function () {
                    return that;
                }
            }
        }
        return this._parent._append(this._element);
    }
};

JSFormBuilder.Mixins.AttributeBase = {
    name: function () {
        if (arguments.length === 0) return this._element.name;
        this._element.name = arguments[0];
        return this;
    },
    disabled: function () {
        this._element.disabled = true;
        return this;
    },
    readonly: function () {
        this._element.readonly = true;
        return this;
    },
    required: function () {
        this._element.required = true;
        return this;
    },
    class: function () {
        if (arguments.length === 0) return this._element.className;
        this._element.className = arguments[0];
        return this;
    },
    id: function () {
        if (arguments.length === 0) return this._element.id;
        this._element.id = arguments[0];
        return this;
    },
    reference: function () {
        if (arguments.length = 0) return this._reference;
        this._reference = arguments[0];
        return this;
    },
    attr: function (name) {
        if (arguments.length === 1) return this._element.getAttribute(name);
        this._element.setAttribute(name, arguments[1]);
        return this;
    }
};

JSFormBuilder.Mixins.FunctionBase = {
    getHTMLElement: function () {
        return this._element;
    },
    on: function () {
        if (arguments.length === 0) return this;
        var that = this;
        arguments[0] = arguments[0].toLowerCase();
        var unwrapped = arguments[1];
        var wrapped = function () {
            unwrapped.apply(that, arguments);
        };
        if (this._element.addEventListener) {
            this._element.addEventListener(arguments[0], wrapped, false);
        } else {
            this._element.attachEvent('on' + arguments[0], wrapped);
        }
        return this;
    },
    remove: function () {
        this._element.parentNode.removeChild(this._element);
    }
};

JSFormBuilder.Mixins.Label = {
    label: function () {
        //TODO: How to detect name changing?
        if (!this._label)
            this._label = document.createElement("label");
        this._label.innerHTML = arguments[0] || "";
        this._label.htmlFor = this._element.name;
        return this;
    }
};

JSFormBuilder.Classes.Select = function () {
    this._element = document.createElement("select");
};

JSFormBuilder.Mixins.Select = {
    multiple: function () {
        this._element.setAttribute("multiple", "");
        return this;
    },
    option: function () {
        var opt = document.createElement('option');
        opt.innerHTML = arguments[0];
        opt.value = arguments[1] || arguments[0];
        var appendTo = this._optgroup || this._element;
        appendTo.appendChild(opt);
        return this;
    },
    optgroup: function () {
        //TODO: We lost last optgroup
        this._optgroup = document.createElement('optgroup');
        this._optgroup.label = arguments[0];
        this._element.appendChild(this._optgroup);
        return this;
    }
};
JSFormBuilder.mix(JSFormBuilder.Mixins.Select, JSFormBuilder.Mixins.Label);

JSFormBuilder.Classes.Input = function () {
    this._element = document.createElement("input");
};

JSFormBuilder.Mixins.Input = {

    type: function () {
        if (arguments.length === 0) return this._element.getAttribute("type");
        this._element.setAttribute("type", arguments[0]);
        return this;
    },

    value: function () {
        if (arguments.length === 0) return this._element.value;
        this._element.value = arguments[0];
        return this;
    }
};

JSFormBuilder.Classes.Table = function () {
    this._element = document.createElement("table");
    this._element.setAttribute("cellpadding", "0");
    this._element.setAttribute("cellspacing", "0");
    this._header = document.createElement("thead");
    this._header.tr = document.createElement("tr");
    this._header.appendChild(this._header.tr);
    this._footer = document.createElement("tfoot");
    this._body = document.createElement("tbody");
    this._element.appendChild(this._header);
    this._element.appendChild(this._body);
    this._element.appendChild(this._footer);
};

JSFormBuilder.Mixins.Table = {
    autoheader: function () {
        this._autoheader = true;
        return this;
    },
    column: function () {
        var column = document.createElement("th");
        column.innerHTML = arguments[0];
        this._header.tr.appendChild(column);
        return this;
    },
    row: function () {
        var row = document.createElement("tr");
        if (arguments.length === 1 && typeof arguments[0] === typeof 1)
            return this._body.children[arguments[0]];
        else if (arguments[0] instanceof  Array)
            this._fillFromArray(row, arguments[0]);
        else if (arguments[0] instanceof Object && Object.keys(arguments).length > 0)
            this._fillFromObject(row, arguments[0]);
        else
            this._fillFromArray(row, Array.prototype.slice.call(arguments));
        this._body.appendChild(row);
        return this;
    },
    _fillFromArray: function (row, array) {
        var columnsCount = this._header.tr.children.length || array.length;
        for (var i = 0; i < columnsCount; i++) {
            var td = document.createElement("td");
            this._append(td, array.shift());
            row.appendChild(td);
        }
    },
    _fillFromObject: function (row, object) {
        var objectKeys = Object.keys(object);
        var columnsCount = this._header.tr.children.length || (this._autoheader ? objectKeys.length : 0);
        var needHeader = !this._header.tr.children.length && this._autoheader;
        var children = this._header.tr.children;
        for (var i = 0; i < columnsCount; i++) {
            var td = document.createElement("td");
            var prop = "";
            if (needHeader) {
                prop = objectKeys[i].trim();
                this.column(prop);
            }
            else
                prop = children[i].innerHTML.trim();
            if (Object.prototype.hasOwnProperty.call(object, prop))
                this._append(td, object[prop]);
            row.appendChild(td);
        }
    },
    _append: function (element, data) {
        if (data instanceof  HTMLElement)
            element.appendChild(data);
        else
            element.innerHTML = data;
    },
    clear: function () {
        if (this._autoheader) {
            var tr = this._header.tr;
            while (tr.firstChild)
                tr.removeChild(tr.firstChild);
        }
        while (this._body.firstChild)
            this._body.removeChild(this._body.firstChild);
    }
};

JSFormBuilder.Classes.Button = function () {
    this._element = document.createElement("button");
};

JSFormBuilder.Mixins.Button = {
    html: function () {
        if (arguments.length === 0) return this._element.innerHTML;
        this._element.innerHTML = arguments[0];
        return this;
    }
};
JSFormBuilder.mix(JSFormBuilder.Mixins.Button, JSFormBuilder.Mixins.Input);
JSFormBuilder.mix(JSFormBuilder.Mixins.Input, JSFormBuilder.Mixins.Label);

JSFormBuilder.Classes.Textarea = function () {
    this._element = document.createElement("textarea");
};
JSFormBuilder.Mixins.Textarea = {
    rows: function () {
        if (arguments.length === 0) return this._element.getAttribute("rows");
        this._element.setAttribute("rows", arguments[0]);
        return this;
    },
    cols: function () {
        if (arguments.length === 0) return this._element.getAttribute("cols");
        this._element.setAttribute("cols", arguments[0]);
        return this;
    },
    text: function () {
        if (arguments.length === 0) return this._element.value;
        this._element.value = arguments[0];
        return this;
    }
};
JSFormBuilder.mix(JSFormBuilder.Mixins.Textarea, JSFormBuilder.Mixins.Label);

JSFormBuilder.Classes.Form = function () {
    this._element = document.createElement("form");
};

JSFormBuilder.Mixins.Form = {
    action: function () {
        if (arguments.length === 0) return this._element.action;
        this._element.action = arguments[0];
        return this;
    },
    target: function () {
        if (arguments.length === 0) return this._element.target;
        this._element.target = arguments[0];
        return this;
    },
    novalidate: function () {
        this._element.setAttribute("novalidate", "");
        return this;
    }
};


JSFormBuilder.Mixins.Legend = {
    legend: function () {
        if (!this._legend) {
            this._legend = document.createElement("legend");
            this._element.appendChild(this._legend);
        }
        this._legend.innerHTML = arguments[0] || "";
        return this;
    }
};

JSFormBuilder.Classes.Div = function () {
    this._element = document.createElement("div");
};

JSFormBuilder.Mixins.Div = {
    html: function () {
        if (arguments.length === 0) return this._element.innerHTML;
        this._element.innerHTML = arguments[0];
        return this;
    }
};

JSFormBuilder.Classes.Fieldset = function () {
    this._element = document.createElement("fieldset");
};

JSFormBuilder.Mixins.Fieldset = {};
JSFormBuilder.mix(JSFormBuilder.Mixins.Fieldset, JSFormBuilder.Mixins.Legend);

JSFormBuilder.Classes.ContainerAdapter = function () {
    if (["form", "fieldset", "div"].indexOf(arguments[1]) > -1) {
        JSFormBuilder.Classes.ContainerAdapter.__super__.constructor.apply(this, arguments);
        if (this._parent)
            this._localScope = this._parent._localScope;
    }
    else throw "Element '" + arguments[1] + "' is not compatible with container";
};

JSFormBuilder.Classes.ContainerAdapter.prototype = {
    end: function () {
        return JSFormBuilder.Classes.ContainerAdapter.__super__.end.apply(this, arguments);
    },
    _begin: function () {
        return new JSFormBuilder.Classes.BaseAdapter(this, arguments[0]);
    },
    _wrap: function () {
        return new JSFormBuilder.Classes.ContainerAdapter(this, arguments[0]);
    },
    _append: function () {
        this._element.appendChild(arguments[0]);
        return this;
    }
};
JSFormBuilder.Mixins.ContainerAdapter = {

    //GROUP

    fieldset: function () {
        return this._wrap("fieldset")
    },

    div: function () {
        return this._wrap("div");
    },

    //BUTTON
    button: function () {
        return this._begin("button").type("button");
    },

    submit: function () {
        return this._begin("button");
    },

    //INPUT

    text: function () {
        return this._begin("input").type("text");
    },
    checkbox: function () {
        return this._begin("input").type("checkbox");
    },
    file: function () {
        return this._begin("input").type("file");
    },
    hidden: function () {
        return this._begin("input").type("hidden");
    },
    password: function () {
        return this._begin("input").type("password");
    },
    radio: function () {
        return this._begin("input").type("radio");
    },
    reset: function () {
        return this._begin("input").type("reset");
    },

    //HTML5

    color: function () {
        return this._begin("input").type("submit");
    },
    date: function () {
        return this._begin("input").type("submit");
    },
    datetime: function () {
        return this._begin("input").type("submit");
    },
    email: function () {
        return this._begin("input").type("submit");
    },
    number: function () {
        return this._begin("input").type("submit");
    },
    range: function () {
        return this._begin("input").type("submit");
    },
    tel: function () {
        return this._begin("input").type("submit");
    },
    time: function () {
        return this._begin("input").type("submit");
    },
    url: function () {
        return this._begin("input").type("submit");
    },
    month: function () {
        return this._begin("input").type("submit");
    },
    week: function () {
        return this._begin("input").type("submit");
    },

    //TEXT AREA

    textarea: function () {
        return this._begin("textarea");
    },

    //SELECT

    select: function () {
        return this._begin("select");
    },

    //TABLE

    table: function () {
        return this._begin("table");
    }
};
JSFormBuilder.extend(JSFormBuilder.Classes.ContainerAdapter, JSFormBuilder.Classes.BaseAdapter);
JSFormBuilder.mix(JSFormBuilder.Classes.ContainerAdapter,
    JSFormBuilder.Mixins.ContainerAdapter);


JSFormBuilder.Classes.FormAdapter = function () {
    var args = [null, "form"];
    JSFormBuilder.Classes.FormAdapter.__super__.constructor.apply(this, args);
    this._parent = arguments[0];
    if (!this._localScope) {
        this._localScope = {};
    }
    this._parent.scope = this._localScope;
};

JSFormBuilder.Classes.FormAdapter.prototype = {
    end: function () {
        var that = this;
        this._parent.each(function (position, element) {
            element.appendChild(that._element);
        });
        return this._parent;
    }
};
JSFormBuilder.extend(JSFormBuilder.Classes.FormAdapter, JSFormBuilder.Classes.ContainerAdapter);

