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

let compress = true;
let $compressBtn;

const classButtonOn = 'imcms-button--switch-on';
const classButtonOff = 'imcms-button--switch-off';

const $proportionsText = getExplainText(texts.proportion);

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
                imageCropper.refreshCropping();
            }else{
                $(this).attr('data-state', 'passive');
                $proportionsBtn.removeClass(classButtonOn).addClass(classButtonOff);
            }

            $('.imcms-image-crop-proportions-info').css('display', saveProportions ? 'inline-block' : 'none');
        }
    });
    components.overlays.defaultTooltip($proportionsBtn, texts.proportionsButtonTitle);

    return $proportionsBtn;
}

const $compressText = getExplainText(texts.compression);

function getCompressButton() {

    compress = true;

    $compressBtn =  components.buttons.switchOnButton({
        'data-state': 'active',
        click: function () {

            compress = !compress;

            if(compress){
                $(this).attr('data-state', 'active');
                $compressBtn.removeClass(classButtonOff).addClass(classButtonOn);
            }else{
                $(this).attr('data-state', 'passive');
                $compressBtn.removeClass(classButtonOn).addClass(classButtonOff);
            }

        }
    });
    components.overlays.defaultTooltip($compressBtn, texts.compressionButtonTitle);

    return $compressBtn;
}

function getExplainText(text){
    return components.texts.infoText('<div>', text);
}

module.exports = {
    getProportionsButton: getProportionsButton,

    getProportionsText: () => $proportionsText,

    enableProportionsLock() {
        getProportionsButton().attr('data-state', 'active');
        $proportionsBtn.removeClass(classButtonOff).addClass(classButtonOn);
        imageResize.enableSaveProportions();
    },

    getCompressButton: getCompressButton,

    getCompressText: () => $compressText,

    getCompress: () => compress
};
