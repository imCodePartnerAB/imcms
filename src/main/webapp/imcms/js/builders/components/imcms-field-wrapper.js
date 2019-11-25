/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.08.18
 */
define('imcms-field-wrapper', ['jquery'], function ($) {
    return {
        wrap: ($wrapMe, tag) => $((tag || '<div>'), {
            'class': 'imcms-field',
            html: $wrapMe
        })
    }
});
