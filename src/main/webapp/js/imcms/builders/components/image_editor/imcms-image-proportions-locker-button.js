/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 28.09.18
 */
const texts = require('imcms-i18n-texts').editors.image;
const components = require('imcms-components-builder');
const imageResize = require('imcms-image-resize');
const $ = require('jquery');

let $proportionsText;

function getProportionsText() {
    return $proportionsText || ($proportionsText = components.texts.infoText('<div>', 'Proportions locked', {
        style: 'line-height: 35px; vertical-align: top;'
    }))
}

let $proportionsBtn;

function getProportionsButton() {
    return $proportionsBtn || ($proportionsBtn = components.buttons.proportionsButton({
        'data-state': 'active',
        title: texts.proportionsButtonTitle,
        click: function () {
            let saveProportions = imageResize.toggleSaveProportions();
            $(this).attr('data-state', saveProportions ? 'active' : 'passive');

            const $proportionsText = getProportionsText();
            $proportionsText.toggle(saveProportions);
        }
    }))
}

module.exports = {
    getProportionsButton: getProportionsButton,

    getProportionsText: getProportionsText,

    enableProportionsLock() {
        getProportionsButton().attr('data-state', 'active');
        imageResize.enableSaveProportions();
    },
};
