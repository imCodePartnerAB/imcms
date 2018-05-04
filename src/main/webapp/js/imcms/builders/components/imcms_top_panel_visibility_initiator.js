/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 04.05.18
 */
Imcms.define("imcms-top-panel-visibility-initiator", ["imcms-events", "jquery"], function (events, $) {

    var panelSensitivePixels = 15;

    var isPanelEnabled = true; // by default

    events.on("enable admin panel", function () {
        isPanelEnabled = true;
    });
    events.on("disable admin panel", function () {
        isPanelEnabled = false;
    });

    function hidePanel($panel) {
        setAdminPanelTop($panel, -$panel.height());
    }

    function showPanel($panel) {
        setAdminPanelTop($panel, 0);
    }

    function setAdminPanelTop($panel, px) {
        $panel.css({"top": "" + px + "px"});
    }

    return {
        setShowPanelRule: function ($panel) {
            var $body = $("body");
            $(document).mousemove(function (event) {

                var isPanelDisabledOrMouseNotInSensitiveArea = !isPanelEnabled
                    || (event.clientY < 0)
                    || (event.clientY > panelSensitivePixels);

                if (isPanelDisabledOrMouseNotInSensitiveArea) return;

                var bodyCss = ($(window).scrollTop() === 0)
                    ? {"top": $panel.height() + "px"}
                    : {"padding-top": "0"};

                $body.css(bodyCss);
                showPanel($panel);
            });
        },
        setHidePanelRule: function ($panel) {
            $(document).click(function (event) {

                if ($(event.target).closest(".imcms-admin").length) return;

                $("body").css({"top": "0px"});
                hidePanel($panel);
            });
        }
    };
});
