/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 17.09.18
 */
const $ = require('jquery');

let currentToolbarView;

module.exports = class ToolbarViewBuilder {

    constructor() {
        this.toHide$ = $();
        this.toShow$ = $();
        this.toPrevSizeHide$ = $();
        this.toOriginSizeShow$ = $();
    }

    static getCurrentToolbarView() {
        return currentToolbarView
    }

    _onClose() {
        this.toShow$.slideUp('fast', () => {
            this.toHide$.slideDown()
        });
    }

    _onShow() {
        this.toHide$.slideUp('fast', () => {
            this.toShow$.slideDown();
            this.toOriginSizeShow$.show();
            this.toPrevSizeHide$.hide();
        });
    }

    showAll() {
        this.toShow$.slideDown('fast');
        this.toHide$.slideDown('fast');
        this.toOriginSizeShow$.hide();
        this.toPrevSizeHide$.show();
    }

    hide(...hideUs$) {
        hideUs$.forEach($hideMe => {
            this.toHide$ = this.toHide$.add($hideMe)
        });
        return this;
    }

    show(...showUs$) {
        showUs$.forEach($showMe => {
            this.toShow$ = this.toShow$.add($showMe)
        });
        return this;
    }

    prevControlSizeHide(previewSize$) {
        this.toPrevSizeHide$ = this.toPrevSizeHide$.add(previewSize$);
        return this;
    }

    originControlSizeShow(originSize$) {
        this.toOriginSizeShow$ = this.toOriginSizeShow$.add(originSize$);
        return this;
    }


    onCancel(callOnCancel) {
        this.callOnCancel = callOnCancel;
        return this;
    }

    onApply(callOnApply) {
        this.callOnApply = callOnApply;
        return this;
    }

    cancelChanges() {
        this.callOnCancel && this.callOnCancel.call();
        this._onClose();
    }

    applyChanges() {
        this.callOnApply && this.callOnApply.call();
        this._onClose();
        currentToolbarView = null;
    }

    build() {
        currentToolbarView = this;
        this._onShow();
    }

    buildEditorElement() {
        currentToolbarView = this;
        this.showAll();
    }

};