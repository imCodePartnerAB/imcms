/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.08.18
 */
Imcms.define('imcms-text-editor-toolbar-button-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {
    return {
        buildButton: function (blockName, title, onClick) {
            return new BEM({
                block: blockName,
                elements: {
                    'icon': $('<div>', {
                        'class': 'text-toolbar__icon'
                    })
                }
            }).buildBlockStructure('<div>', {
                class: 'text-toolbar__button',
                title: title,
                click: onClick
            })
        }
    }
});
