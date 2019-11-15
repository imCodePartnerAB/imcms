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
            this.toShow$.slideDown()
        });
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
};