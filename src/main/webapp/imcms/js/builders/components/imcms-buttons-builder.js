/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */

const BEM = require('imcms-bem-builder');

const buttonsBEM = new BEM({
    block: 'imcms-buttons',
    elements: {
        'button': 'imcms-button'
    }
});

const getButtonBuilder = (modifier) =>
    (tag, attributes) => buttonsBEM.buildElement('button', tag, attributes, [modifier]);

function buildButtonWithIcon({button, icon}, attributes) {
    return new BEM({
        block: 'imcms-icon-button ' + button.attr('class'),
        elements: {
            'icon': icon,
            'button': button,
        }
    }).buildBlockStructure('<div>', attributes);
}

module.exports = {
    imcmsButton: (attributes, modifiers) => buttonsBEM.buildElement('button', '<button>', attributes, modifiers),
    negative: getButtonBuilder('negative'),
    positive: getButtonBuilder('positive'),
    neutral: getButtonBuilder('neutral'),
    warning: getButtonBuilder("warning"),
    error: getButtonBuilder("error"),
    save: getButtonBuilder('save'),
    windowNormal: getButtonBuilder('window-normal'),
    windowAuto: getButtonBuilder('window-auto'),
    windowMaximize: getButtonBuilder('window-maximize'),
    close: getButtonBuilder('close'),
    increment: getButtonBuilder('increment'),
    decrement: getButtonBuilder('decrement'),
    prev: getButtonBuilder('prev'),
    next: getButtonBuilder('next'),
    dropDown: getButtonBuilder('drop-down'),
    search: getButtonBuilder('search'),
    proportions: getButtonBuilder('proportions'),
    zoomPlus: getButtonBuilder('zoom-plus'),
    zoomMinus: getButtonBuilder('zoom-minus'),
    zoomReset: getButtonBuilder('zoom-reset'),
    rotation: getButtonBuilder('rotation'),
    rotateLeft: getButtonBuilder('rotate-left'),
    rotateRight: getButtonBuilder('rotate-right'),
    revert: getButtonBuilder('revert'),
    cropping: getButtonBuilder('crop'),
    openInNewWindow: getButtonBuilder('open-in-new-window'),
    editMetadata: getButtonBuilder('edit-metadata'),
    fit: getButtonBuilder('fit'),
    switch_on: getButtonBuilder('switch-on'),
    switch_off: getButtonBuilder('switch-off'),
    switchOnButton: function (attributes) {
        return this.switch_on('<button>', attributes);
    },
    switchOffButton: function (attributes) {
        return this.switch_off('<button>', attributes);
    },
    negativeButton: function (attributes) {
        return this.negative('<button>', attributes);
    },
    positiveButton: function (attributes) {
        return this.positive('<button>', attributes);
    },
    neutralButton: function (attributes) {
        return this.neutral('<button>', attributes);
    },
    warningButton: function (attributes) {
        return this.warning("<button>", attributes);
    },
    errorButton: function (attributes) {
        return this.error("<button>", attributes);
    },
    saveButton: function (attributes) {
        return this.save('<button>', attributes);
    },
    windowNormalButton: function (attributes) {
        return this.windowNormal('<button>', attributes);
    },
    windowAutoButton: function (attributes) {
        return this.windowAuto('<button>', attributes);
    },
    windowMaximizeButton: function (attributes) {
        return this.windowMaximize('<button>', attributes);
    },
    closeButton: function (attributes) {
        return this.close('<button>', attributes);
    },
    incrementButton: function (attributes) {
        return this.increment('<button>', attributes);
    },
    decrementButton: function (attributes) {
        return this.decrement('<button>', attributes);
    },
    prevButton: function (attributes) {
        return this.prev('<button>', attributes);
    },
    nextButton: function (attributes) {
        return this.next('<button>', attributes);
    },
    dropDownButton: function (attributes) {
        return this.dropDown('<button>', attributes);
    },
    searchButton: function (attributes) {
        return this.search('<button>', attributes);
    },
    proportionsButton: function (attributes) {
        return this.proportions('<button>', attributes);
    },
    zoomPlusButton: function (attributes) {
        return this.zoomPlus('<button>', attributes);
    },
    zoomMinusButton: function (attributes) {
        return this.zoomMinus('<button>', attributes);
    },
    zoomResetButton: function (attributes) {
        return this.zoomReset('<button>', attributes);
    },
    rotationButton: function (attributes) {
        return this.rotation('<button>', attributes);
    },
    rotateLeftButton: function (attributes) {
        return this.rotateLeft('<button>', attributes);
    },
    rotateRightButton: function (attributes) {
        return this.rotateRight('<button>', attributes);
    },
    revertButton: function (attributes) {
        return this.revert('<button>', attributes);
    },
    croppingButton: function (attributes) {
        return this.cropping('<button>', attributes);
    },
    openInNewWindowButton: function (attributes) {
        return this.openInNewWindow('<button>', attributes);
    },
    editMetadataButton: function (attributes) {
        return this.editMetadata('<button>', attributes);
    },
    fitButton: function (attributes) {
        return this.fit('<button>', attributes);
    },
    buttonsContainer: (tag, elements, attributes) => buttonsBEM.buildBlock(tag, elements, attributes, 'button'),
    buttonWithIcon: buildButtonWithIcon,
};
