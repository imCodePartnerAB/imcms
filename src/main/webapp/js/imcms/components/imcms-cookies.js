/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 03.05.18
 */
module.exports = {
    getCookie(name) {
        const matches = document.cookie.match(new RegExp(
            "(?:^|; )" + name.replace(/([.$?*|{}()\[\]\\\/+^])/g, '\\$1') + "=([^;]*)"
        ));
        return matches ? decodeURIComponent(matches[1]) : undefined;
    },
    setCookie(name, value, options) {
        options = options || {};

        let expires = options.expires;

        if (expires && (typeof expires === "number")) { // expires in days here
            const currentDate = new Date();
            currentDate.setTime(currentDate.getTime() + expires * 24 * 60 * 60 * 1000);
            expires = options.expires = currentDate;
        }

        if (expires && expires.toUTCString) { // expires as a date here
            options.expires = expires.toUTCString();
        }

        value = encodeURIComponent(value);

        let updatedCookie = name + "=" + value;

        for (let propName in options) {
            updatedCookie += "; " + propName;
            const propValue = options[propName];

            if (propValue !== true) {
                updatedCookie += "=" + propValue;
            }
        }

        document.cookie = updatedCookie;
    },
    deleteCookie(name) {
        this.setCookie(name, "", {
            expires: -1
        });
    }
};
