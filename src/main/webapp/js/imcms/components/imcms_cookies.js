/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 03.05.18
 */
define("imcms-cookies", [], function () {

    var Cookies = function () {
    };

    Cookies.prototype = {
        getCookie: function (name) {
            var matches = document.cookie.match(new RegExp(
                "(?:^|; )" + name.replace(/([.$?*|{}()\[\]\\\/+^])/g, '\\$1') + "=([^;]*)"
            ));
            return matches ? decodeURIComponent(matches[1]) : undefined;
        },
        setCookie: function (name, value, options) {
            options = options || {};

            var expires = options.expires;

            if (expires && (typeof expires === "number")) { // expires in days here
                var currentDate = new Date();
                currentDate.setTime(currentDate.getTime() + expires * 24 * 60 * 60 * 1000);
                expires = options.expires = currentDate;
            }

            if (expires && expires.toUTCString) { // expires as a date here
                options.expires = expires.toUTCString();
            }

            value = encodeURIComponent(value);

            var updatedCookie = name + "=" + value;

            for (var propName in options) {
                updatedCookie += "; " + propName;
                var propValue = options[propName];

                if (propValue !== true) {
                    updatedCookie += "=" + propValue;
                }
            }

            document.cookie = updatedCookie;
        },
        deleteCookie: function (name) {
            this.setCookie(name, "", {
                expires: -1
            });
        }
    };

    return new Cookies();
});
