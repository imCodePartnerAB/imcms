/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 04.05.18
 */
Imcms.define(
    "imcms-top-panel-visibility-initiator",
    ["imcms-events", "imcms-admin-panel-settings-builder", "imcms-admin-panel-state", "imcms-streams", "jquery"],
    function (events, panelSettings, panelState, streams, $) {

        var panelSensitivePixels = 15;
        var panels$ = [];

        var listenersNotSet = true;
        var $body = $("body");

        function showPanels() {
            panels$
                .filter(function ($panel) {
                    return !$panel.hasClass('imcms-special-hidden');
                })
                .forEach(showPanel);
        }

        function hidePanels() {
            $body.css({"top": 0});
            panels$.forEach(hidePanel);
        }

        streams.subscribeFromLast("admin panel visibility", function (content) {
            if (content.hidePanel) {
                panelState.enableSpecialPanelHiding();
                panelState.disablePanelAppearance();
                hidePanels();

            } else if (content.showPanel) {
                showPanels();

            } else return;

            panelState.refreshSpecialPanelPosition();
        });

        function onPanelShown() {
            var bodyCss = ($(window).scrollTop() === 0)
                ? {"top": $("#imcms-admin").height() || $("#imcms-admin-panel").height()}
                : {"padding-top": "0"};

            $("body").css(bodyCss);
        }

        function hidePanel($panel) {
            setAdminPanelTop($panel, "-" + $panel.css('height'));
        }

        function showPanel($panel) {
            setAdminPanelTop($panel, 0);
            onPanelShown();
        }

        function setAdminPanelTop($panel, px) {
            $panel.css({"top": px});
        }

        function setEventListeners() {
            listenersNotSet = false;

            $(document).mousemove(function (event) {

                var isPanelDisabledOrMouseNotInSensitiveArea = panelState.isPanelAppearanceDisabled
                    || (event.clientY < 0)
                    || (event.clientY > panelSensitivePixels);

                if (isPanelDisabledOrMouseNotInSensitiveArea) return;

                showPanels();
            });

            $(document).click(function (event) {
                if (!$(event.target).closest(".admin-panel-settings-list").length
                    && !$(event.target).closest(".imcms-panel__item--settings").length)
                {
                    panelSettings.hideSettings();
                }

                if (panelState.isPanelAppearanceDisabled || $(event.target).closest(".imcms-admin").length) return;

                hidePanels();
            });
        }

        return {
            refreshBodyTop: onPanelShown,
            setShowHidePanelRules: function ($panel) {
                panels$.push($panel);
                listenersNotSet && setEventListeners();
            }
        };
    }
);
