/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 04.05.18
 */
define(
    "imcms-top-panel-visibility-initiator",
    ["imcms-events", "imcms-admin-panel-settings-builder", "imcms-admin-panel-state", "imcms-streams", "jquery"],
    function (events, panelSettings, panelState, streams, $) {

        var panelSensitivePixels = 15;
        var panels$ = [];
        var listenersNotSet = true;

        function showPanels() {
            $("#imcmsAdminSpecial").not(".imcms-special-hidden").css("display", "block");

            setTimeout(() => {
                panels$.filter($panel => !$panel.hasClass('imcms-special-hidden'))
                    .forEach(showPanel);

                onPanelsShown();
            }, 100);
        }

        function hidePanels() {
            $("body").css({"top": 0});
            panels$.forEach(hidePanel);

            setTimeout(() => {
                $("#imcmsAdminSpecial").css("display", "none");
            }, 300);
        }

        streams.subscribeFromLast("admin panel visibility", content => {
            if (content.hidePanel) {
                panelState.enableSpecialPanelHiding();
                panelState.disablePanelAppearance();
                hidePanels();

            } else if (content.showPanel) {
                showPanels();

            } else return;

            panelState.refreshSpecialPanelPosition();
        });

        function onPanelsShown() {
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
        }

        function setAdminPanelTop($panel, px) {
            $panel.css({"top": px});
        }

        function setEventListeners() {
            listenersNotSet = false;

            $(document).mousemove(event => {

                var isPanelDisabledOrMouseNotInSensitiveArea = panelState.isPanelAppearanceDisabled
                    || (event.clientY < 0)
                    || (event.clientY > panelSensitivePixels);

                if (isPanelDisabledOrMouseNotInSensitiveArea) return;

                showPanels();
            });

            $(document).click(event => {
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
            refreshBodyTop: onPanelsShown,
            setShowHidePanelRules: $panel => {
                panels$.push($panel);
                listenersNotSet && setEventListeners();
            }
        };
    }
);
