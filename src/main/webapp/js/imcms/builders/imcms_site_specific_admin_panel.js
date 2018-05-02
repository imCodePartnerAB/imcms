/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 02.05.18
 */
Imcms.define(
    "imcms-site-specific",
    ["imcms-admin-panel-builder", "imcms-i18n-texts", "jquery"],
    function (panelBuilder, texts, $) {

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

            $link.click(function () {
                var $collapsible = $('#imcmsAdminSpecial.imcms-collapsible');

                if ($collapsible.hasClass('imcms-collapsible-hidden')) {
                    $collapsible.removeClass('imcms-collapsible-hidden');
                    $link.addClass('imcms-panel__item--active');

                } else {
                    $collapsible.addClass('imcms-collapsible-hidden');
                    $link.removeClass('imcms-panel__item--active');
                }

                window.setTimeout(function () {
                    $('body').css('top', $('#imcms-admin').height() + 'px');
                }, 300);
            });
        }

        return {
            init: function (onInitDone) {
                panelBuilder.callOnPanelBuilt(initSiteSpecific);
                panelBuilder.callOnPanelBuilt(onInitDone);
            }
        };
    }
);
