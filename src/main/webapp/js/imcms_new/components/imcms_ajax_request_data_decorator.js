/**
 * Decorates jQuery AJAX request data to support sending deep nested objects/arrays.
 * This helps Spring and Jackson mapper to resolve objects.
 *
 * Original is here: https://stackoverflow.com/questions/5900840/post-nested-object-to-spring-mvc-controller-using-json
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 26.09.17
 */
Imcms.define("imcms-ajax-request-data-decorator", ["jquery"], function ($) {
    var r20 = /%20/g,
        rbracket = /\[\]$/;

    function buildParams(prefix, obj, add) {
        if ($.isArray(obj)) {
            // Serialize array item.
            $.each(obj, function (i, v) {
                rbracket.test(prefix)
                    ? add(prefix, v) // Treat each array item as a scalar.
                    : buildParams(prefix + "[" + ( typeof v === "object" || $.isArray(v) ? i : "" ) + "]", v, add);
            });
        } else if (obj !== null && typeof obj === "object") { // Serialize object item.
            for (var name in obj) {
                buildParams(prefix + "." + name, obj[name], add);
            }
        } else {
            add(prefix, obj); // Serialize scalar item.
        }
    }

    return function (requestData) {
        var params = [],
            add = function (key, value) {
                // If value is a function, invoke it and return its value
                value = $.isFunction(value) ? value() : value;
                params[params.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
            };

        // If an array was passed in, assume that it is an array of form elements.
        if (requestData && ($.isArray(requestData) || (requestData.jQuery && !$.isPlainObject(requestData)))) {
            // Serialize the form elements
            $.each(requestData, function () {
                add(this.name, this.value);
            });

        } else {
            for (var prefix in requestData) {
                buildParams(prefix, requestData[prefix], add);
            }
        }

        // Return the resulting serialization
        return params.join("&").replace(r20, "+");
    }
});
