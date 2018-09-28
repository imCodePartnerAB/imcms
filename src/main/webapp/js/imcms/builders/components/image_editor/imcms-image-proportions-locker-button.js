/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 28.09.18
 */
const texts = require('imcms-i18n-texts').editors.image;
const components = require('imcms-components-builder');
const imageResize = require('imcms-image-resize');
const $ = require('jquery');

let $proportionsBtn;

function getProportionsButton() {
    return $proportionsBtn || ($proportionsBtn = components.buttons.proportionsButton({
        'data-state': 'active',
        title: texts.proportionsButtonTitle,
        click: function () {
            let saveProportions = imageResize.toggleSaveProportions();
            $(this).attr('data-state', saveProportions ? 'active' : 'passive');
        }
    }))
}

module.exports = {
    getProportionsButton: getProportionsButton,

    enableProportionsLock() {
        getProportionsButton().attr('data-state', 'active');
        imageResize.enableSaveProportions();
    },
};
