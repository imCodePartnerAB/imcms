/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 02.05.18
 */
Imcms.define("imcms-site-specific", ["imcms-admin-panel-builder", "jquery"], function (panelBuilder, $) {

    function initSiteSpecific() {
        var $imcmsAdminSpecial = $('#imcmsAdminSpecial');

        if ($imcmsAdminSpecial.length) {
            var $imcmsAdminPanelOuter = $('.imcms-admin:first');
            var $imcmsAdminPanelInner = $imcmsAdminPanelOuter.find('.imcms-admin-panel:first');
            $imcmsAdminPanelInner.attr('id', 'imcmsAdminPanel');
            $imcmsAdminSpecial.appendTo($imcmsAdminPanelOuter);
            addLinkToSpecialAdmin($, $imcmsAdminSpecial);
        }
    }

    function addLinkToSpecialAdmin($, $el) {
        var linkText = $el.data('link-text') || 'Special';
        var $link = $('<li title="Shows client specific administration" class="imcms-panel__item imcms-panel__item--specific">' + linkText + '</li>')
            .insertAfter('.imcms-admin-panel .imcms-panel__item--page-info:first');
        // TODO: Collapsible click event - Re-code so it uses the same slide toggle type as the panel. Add cookie to remember in/out state: --%>
        $link.on('click', function () {
            var $collapsible = $('#imcmsAdminSpecial.imcms-collapsible');
            if ($collapsible.hasClass('imcms-collapsible-hidden')) {
                $collapsible.removeClass('imcms-collapsible-hidden');

                window.setTimeout(function () {
                    $('body').css('top', $('.imcms-admin:first').height() + 'px');
                    // TODO: A better solution to know the height! --%>
                    $link.addClass('imcms-panel__item--active');
                }, 300);
            } else {
                $collapsible.addClass('imcms-collapsible-hidden');
                $link.removeClass('imcms-panel__item--active');

                window.setTimeout(function () {
                    $('body').css('top', '90px');
                    // TODO: A better solution to know the height! --%>
                }, 300);
            }
        });
    }

    return {
        init: function (onInitDone) {
            panelBuilder.callOnPanelBuilt(initSiteSpecific);
            panelBuilder.callOnPanelBuilt(onInitDone);
        }
    };
});

