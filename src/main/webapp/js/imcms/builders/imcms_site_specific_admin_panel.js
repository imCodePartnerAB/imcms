/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 02.05.18
 */
Imcms.define(
    "imcms-site-specific",
    ["imcms-admin-panel-builder", "imcms-i18n-texts", "imcms-cookies", "jquery"],
    function (panelBuilder, texts, cookies, $) {

        texts = texts.panel;

        function initSiteSpecific() {
            var $imcmsAdminSpecial = $('#imcmsAdminSpecial');

            if ($imcmsAdminSpecial.length) {
                $('#imcms-admin').append($imcmsAdminSpecial);
                addLinkToSpecialAdmin($imcmsAdminSpecial);
            }
        }

        function addLinkToSpecialAdmin($imcmsAdminSpecial) {
            var linkText = $imcmsAdminSpecial.data('link-text') || texts.special;
            var $link = $("<li>", {
                title: texts.specialTitle,
                "class": "imcms-panel__item imcms-panel__item--specific",
                text: linkText
            });

            $link.insertAfter('.imcms-admin-panel .imcms-panel__item--page-info:first');
            var $collapsible = $('#imcmsAdminSpecial.imcms-collapsible');

            $link.click(function () {
                if ($collapsible.hasClass('imcms-collapsible-hidden')) {
                    $collapsible.removeClass('imcms-collapsible-hidden');
                    $link.addClass('imcms-panel__item--active');
                    cookies.setCookie("imcms-client-special-area", "opened", {expires: 30});

                } else {
                    $collapsible.addClass('imcms-collapsible-hidden');
                    $link.removeClass('imcms-panel__item--active');
                    cookies.setCookie("imcms-client-special-area", "closed", {expires: 30});
                }

                window.setTimeout(function () {
                    $('body').css('top', $('#imcms-admin').height() + 'px');
                }, 300);
            });

            if (cookies.getCookie("imcms-client-special-area") === "opened") {
                var $imcms = $('#imcms-admin');
                $imcms.css('top', "-10000px"); // dummy value

                // holy shit code! rewrite client-special panel to behave as separated part of regular admin panel,
                // no hiding with height = 0, just set top = -height with next falling down when needs!

                window.setTimeout(function () {
                    $link.addClass('imcms-panel__item--active');
                    $collapsible.removeClass('imcms-collapsible-hidden');

                    window.setTimeout(function () {
                        $imcms.css('top', "-" + $imcms.height() + 'px');
                    }, 100);
                }, 100);
            }
        }

        return {
            init: function (onInitDone) {
                panelBuilder.callOnPanelBuilt(initSiteSpecific);
                panelBuilder.callOnPanelBuilt(onInitDone);
            }
        };
    }
);
