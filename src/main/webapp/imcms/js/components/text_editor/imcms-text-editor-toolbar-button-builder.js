/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.08.18
 */
define('imcms-text-editor-toolbar-button-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {
    return {
        buildButton: function (blockName, title, onClick, isDisabled, isActive) {
            let $btn;
            const classDisabled = 'text-toolbar__button--disabled';
            const classActive = 'text-toolbar__button--active';

            return $btn = new BEM({
                block: blockName,
                elements: {
                    'icon': $('<button>', {
                        'class': 'text-toolbar__icon'
                    })
                }
            }).buildBlockStructure('<div>', {
                class: 'mce-widget mce-btn text-toolbar__button'
                    + (isDisabled ? (' ' + classDisabled) : '')
                    + (isActive ? (' ' + classActive) : ''),
                title: title,
                click: function () {
                    if ($btn.hasClass(classDisabled)) return;
                    onClick.apply(this, arguments);
                }
            });
        }
    };
});
