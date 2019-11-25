define("imcms-admin-panel-state", ["jquery"], function ($) {

    const PanelState = function () {
        this.isSpecialPanelHidingPrevented = false;
        this.isPanelAppearanceDisabled = false;
    };

    PanelState.prototype = {
        states: {
            auto: function () {
                this.enablePanelAppearance();
                this.enableSpecialPanelHiding();
            },
            visible: function () {
                this.disableSpecialPanelHiding();
                this.disablePanelAppearance();
            }
        },
        refreshSpecialPanelPosition: function () {
            const $imcmsAdminSpecial = $('#imcmsAdminSpecial');

            if ($imcmsAdminSpecial.length) {
                const adminPanelHeight = this.isSpecialPanelHidingPrevented ? 0 : $('#imcms-admin-panel').outerHeight();
                $imcmsAdminSpecial.css("padding-top", adminPanelHeight);
            }
        },
        disableSpecialPanelHiding: function () {
            this.isSpecialPanelHidingPrevented = true;
        },
        enableSpecialPanelHiding: function () {
            this.isSpecialPanelHidingPrevented = false;
        },
        enablePanelAppearance: function () {
            this.isPanelAppearanceDisabled = false;
        },
        disablePanelAppearance: function () {
            this.isPanelAppearanceDisabled = true;
        },
        setState: function (state) {
            if (!this.states[state] && !this.states[state].call) return;

            this.states[state].call(this);
            this.state = state;
        }
    };

    return new PanelState();
});
