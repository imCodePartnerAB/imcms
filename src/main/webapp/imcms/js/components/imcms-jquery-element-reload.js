/**
 * Reloads jQuery element
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.11.17
 */
define("imcms-jquery-element-reload", ["imcms-jquery-string-selector"], function (stringSelector) {
    return function ($reloadMe, callback) {
        if (!$reloadMe.length) {
            return;
        }

        const elementAsStr = stringSelector($reloadMe),
            reloadStringCommand = location.href + " " + elementAsStr + ">*";

        if ((callback) && (typeof callback === "function")) {
            $reloadMe.on("load",reloadStringCommand, callback);

        } else {
            $reloadMe.on("load",reloadStringCommand);
        }
    }
});
