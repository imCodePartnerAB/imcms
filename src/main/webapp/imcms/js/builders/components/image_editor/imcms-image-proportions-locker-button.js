/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 28.09.18
 */
const texts = require('imcms-i18n-texts').editors.image;
const components = require('imcms-components-builder');
const imageResize = require('imcms-image-resize');
const imageCropper = require('imcms-image-cropper');
const $ = require('jquery');


let $proportionsBtn;
const classButtonOn = 'imcms-button--switch-on';
const classButtonOff = 'imcms-button--switch-off';

const $proportionsText = components.texts.infoText('<div>', texts.proportion, {
    style: 'line-height: 35px; vertical-align: top;'
});

function getProportionsButton() {
    if ($proportionsBtn) {
        return $proportionsBtn;
    }

        $proportionsBtn =  components.buttons.switchOnButton({
            'data-state': 'active',
        click: function () {
            if (imageResize.isProportionsLockedByStyle()) return;

            let saveProportions = imageResize.toggleSaveProportions();
            if(saveProportions){
                $(this).attr('data-state', 'active');
                $proportionsBtn.removeClass(classButtonOff).addClass(classButtonOn);
            }else{
                $(this).attr('data-state', 'passive');
                $proportionsBtn.removeClass(classButtonOn).addClass(classButtonOff);
            }

            $('.imcms-image-crop-proportions-info').css('display', saveProportions ? 'inline-block' : 'none');

            if (saveProportions) imageCropper.refreshCropping();
        }
    });
    components.overlays.defaultTooltip($proportionsBtn, texts.proportionsButtonTitle);

    return $proportionsBtn;
}

module.exports = {
    getProportionsButton: getProportionsButton,

    getProportionsText: () => $proportionsText,

    enableProportionsLock() {
        getProportionsButton().attr('data-state', 'active');
        imageResize.enableSaveProportions();
    },
};
