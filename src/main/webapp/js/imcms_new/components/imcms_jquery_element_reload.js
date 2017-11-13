/**
 * Reloads jQuery element
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.11.17
 */
Imcms.define("imcms-jquery-element-reload", ["imcms-jquery-string-selector"], function (stringSelector) {
    return function ($reloadMe, callback) {
        if (!$reloadMe.length) {
            return;
        }

        var elementAsStr = stringSelector($reloadMe),
            reloadStringCommand = location.href + " " + elementAsStr + ">*";

        if ((callback) && (typeof callback === "function")) {
            $reloadMe.load(reloadStringCommand, callback);

        } else {
            $reloadMe.load(reloadStringCommand);
        }
    }
});
