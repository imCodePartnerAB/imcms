/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 02.05.18
 */
Imcms.define(
    "imcms-site-specific",
    ["imcms-admin-panel-builder", "imcms-i18n-texts", "imcms-cookies", "imcms-top-panel-visibility-initiator", "jquery"],
    function (panelBuilder, texts, cookies, panelVisibility, $) {

        texts = texts.panel;

        function initSiteSpecific() {
            var $imcmsAdminSpecial = $('#imcmsAdminSpecial');

            if ($imcmsAdminSpecial.length) {
                var $imcms = $('#imcms-admin');

                var adminPanelHeight = $('#imcms-admin-panel').height();
                $imcmsAdminSpecial.css('top', "-" + $imcmsAdminSpecial.css('max-height')) // there is no real height now
                    .removeClass('imcms-collapsible-hidden') // now it is visible with some real height
                    .addClass('imcms-special-hidden')
                    .appendTo($imcms)
                    .css("padding-top", adminPanelHeight)
                    .css("top", "-" + $imcms.css('height'));

                panelVisibility.setShowHidePanelRules($imcmsAdminSpecial);

                addLinkToSpecialAdmin($imcmsAdminSpecial);
            }
        }

        function addLinkToSpecialAdmin($imcmsAdminSpecial) {
            var linkText = $imcmsAdminSpecial.data('link-text') || texts.special;
            var $imcms = $('#imcms-admin');
            var $link = $("<li>", {
                title: texts.specialTitle,
                "class": "imcms-panel__item imcms-panel__item--specific",
                text: linkText
            });

            $link.insertAfter('.imcms-admin-panel .imcms-panel__item--page-info:first');
            var $collapsible = $('#imcmsAdminSpecial.imcms-collapsible');

            $link.click(function () {
                if ($collapsible.hasClass('imcms-special-hidden')) {
                    $collapsible.css('display', 'block');
                    setTimeout(function () {
                        $collapsible.removeClass('imcms-special-hidden').css('top', 0);
                    });
                    $link.addClass('imcms-panel__item--active');
                    cookies.setCookie("imcms-client-special-area", "opened", {expires: 30});

                } else {
                    $collapsible.slideUp(500);
                    $collapsible.addClass('imcms-special-hidden').css('top', "-" + $imcms.css('height'));
                    $link.removeClass('imcms-panel__item--active');
                    cookies.setCookie("imcms-client-special-area", "closed", {expires: 30});
                }
            });

            if (cookies.getCookie("imcms-client-special-area") === "opened") {
                $link.addClass('imcms-panel__item--active');
                $collapsible.removeClass('imcms-special-hidden');
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
