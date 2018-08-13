/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.08.18
 */
Imcms.define('imcms-text-editor-toolbar-button-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {
    return {
        buildButton: function (blockName, title, onClick, isDisabled) {
            var $btn;
            var classDisabled = 'text-toolbar__button--disabled';
            return $btn = new BEM({
                block: blockName,
                elements: {
                    'icon': $('<div>', {
                        'class': 'text-toolbar__icon'
                    })
                }
            }).buildBlockStructure('<div>', {
                class: 'text-toolbar__button' + (isDisabled ? ' ' + classDisabled : ''),
                title: title,
                click: function () {
                    if ($btn.hasClass(classDisabled)) return;
                    onClick.call();
                }
            })
        }
    }
});
