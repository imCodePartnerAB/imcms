/**
 * Created by Shadowgun on 15.01.2015.
 */
var JSFormBuilder = function () {
    return this.mix(this.get(arguments[0]), this.fn);
};
JSFormBuilder.Classes = {};
JSFormBuilder.fn = {};
JSFormBuilder.fn.beginForm = function () {

    return this;
}
JSFormBuilder.Classes.Beginner = function () {
    this._parent = arguments[0];

};
JSFormBuilder.Classes.Beginner.prototype = {
    _parent: {},
    _element:{},
    end: function(){
        return this._parent.append(this._element);
    }
};
JSFormBuilder.Classes.ContainerBeginner = function () {
};
JSFormBuilder.Classes.ContainerBeginner.prototype = JSFormBuilder.mix({
    append:function(){
        this.element.appendChild(arguments[0]);
        return this;
    }
}, JSFormBuilder.Classes.Beginner);

JSFormBuilder.get = function (arg) {
    var jQResult = jQuery(arg);
    var result = {};
    for (var i = 0, to = jQResult.length; i < to; i++)
        result[i] = jQResult[i];
    result.length = jQResult.length;
    return result;
}
JSFormBuilder.mix = function (object) {
    var mixins = Array.prototype.slice.call(arguments, 1);
    for (var i = 0; i < mixins.length; ++i) {
        for (var prop in mixins[i]) {
            if (typeof object.prototype[prop] === "undefined") {
                var bindMethod = function (mixin, prop) {
                    return function () {
                        mixin[prop].apply(this, arguments)
                    }
                }

                object.prototype[prop] = bindMethod(mixins[i], prop);
            }
        }
    }
}
var testClass = JSFormBuilder.Classes.ContainerBeginner();
testClass.ololo();