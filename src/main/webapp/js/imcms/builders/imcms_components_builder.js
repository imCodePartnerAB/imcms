/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-components-builder",
    [
        "imcms-buttons-builder", "imcms-flags-builder", "imcms-checkboxes-builder", "imcms-radio-buttons-builder",
        "imcms-selects-builder", "imcms-texts-builder", "imcms-keywords-builder", "imcms-date-time-builder",
        "imcms-switch-builder", "imcms-controls-builder"
    ],
    function (buttons, flags, checkboxes, radios, selects, texts, keywords, dateTime, switches, controls) {
        return {
            switches: switches,
            buttons: buttons,
            flags: flags,
            checkboxes: checkboxes,
            radios: radios,
            selects: selects,
            texts: texts,
            keywords: keywords,
            dateTime: dateTime,
            controls: controls
        };
    }
);
