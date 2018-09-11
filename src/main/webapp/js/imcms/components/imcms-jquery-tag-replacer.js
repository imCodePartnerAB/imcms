/**
 * source: https://stackoverflow.com/a/36247124/5797023
 */
define('imcms-jquery-tag-replacer', ['jquery'], function ($) {

    $.fn.replaceTagName = function (f) {
        var g = [],
            h = this.length;
        while (h--) {
            var k = document.createElement(f),
                b = this[h],
                d = b.attributes;
            for (var c = d.length - 1; c >= 0; c--) {
                var j = d[c];
                k.setAttribute(j.name, j.value)
            }
            k.innerHTML = b.innerHTML;
            $(b).after(k).remove();
            g[h] = k
        }
        return $(g)
    };

    return {}
});
