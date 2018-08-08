/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.08.18
 */
Imcms.define('imcms-field-wrapper', ['jquery'], function ($) {
    return {
        wrap: function ($wrapMe, tag) {
            return $((tag || '<div>'), {
                'class': 'imcms-field',
                html: $wrapMe
            })
        }
    }
});
