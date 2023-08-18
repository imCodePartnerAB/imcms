/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 02.05.18
 */
define(
    "imcms-site-specific-admin-panel",
    [
        "imcms-admin-panel-builder", "imcms-i18n-texts", "imcms-cookies", "imcms-top-panel-visibility-initiator",
        "imcms-admin-panel-state", "imcms-events", "imcms-streams", "jquery"
    ],
    function (panelBuilder, texts, cookies, panelVisibility, panelState, events, streams, $) {

        texts = texts.panel;

        function initSiteSpecific() {
            const $imcmsAdminSpecial = $('#imcmsAdminSpecial');

            if ($imcmsAdminSpecial.length) {
                const $imcms = $('#imcms-admin');

                $imcmsAdminSpecial // there is no real height now
	                .removeClass('imcms-collapsible-hidden') // now it is visible with some real height
	                .addClass('imcms-special-hidden')
	                .appendTo($imcms);

                panelVisibility.setShowHidePanelRules($imcmsAdminSpecial);
                $imcmsAdminSpecial.css('display', 'none');

                const textTab = $imcmsAdminSpecial.data('link-text');
                const textTitle = $imcmsAdminSpecial.data('title-text');
                addLinkToSpecialAdmin(textTab, textTitle);
            }
        }

        function addLinkToSpecialAdmin(textTab, textTitle) {
            const linkText = textTab || texts.special;
            const $imcms = $('#imcms-admin');
            const $link = $("<li>", {
                title: textTitle || texts.specialTitle,
                "class": "imcms-panel__item imcms-panel__item--specific",
                text: linkText
            });

            $link.insertAfter('.imcms-admin-panel .imcms-panel__item--page-info:first');
            const $collapsible = $('#imcmsAdminSpecial.imcms-collapsible');

            $link.click(() => {

                if ($collapsible.hasClass('imcms-special-hidden')) {
                    $collapsible.css('display', 'block');

                    setTimeout(() => {
                        $collapsible.removeClass('imcms-special-hidden').css('top', 0);
                        panelState.isSpecialPanelHidingPrevented || panelVisibility.refreshBodyTop();
                    });

                    $link.addClass('imcms-panel__item--active');
                    cookies.setCookie("imcms-client-special-area", "opened", {expires: 30});

                } else {
                    $collapsible.slideUp(200, () => {
                            panelState.isSpecialPanelHidingPrevented || panelVisibility.refreshBodyTop();
                        })
                        .addClass('imcms-special-hidden')
                        .css('top', "-" + $imcms.css('height'));

                    $link.removeClass('imcms-panel__item--active');
                    cookies.setCookie("imcms-client-special-area", "closed", {expires: 30});
                }
            });

	        if (cookies.getCookie("imcms-client-special-area") === "opened") {
		        $link.addClass('imcms-panel__item--active');
		        $collapsible.removeClass('imcms-special-hidden');

		        if ($imcms.hasClass("imcms-panel-visible")) {
			        $collapsible.css('display', 'block');
		        }
	        }

	        if (cookies.getCookie("panel-appearance") === "visible") {
		        $('#imcmsAdminSpecial').removeClass('imcms-collapsible')
	        }
        }

        return {
            init: onInitDone => {
                panelBuilder.callOnPanelBuilt(initSiteSpecific);
                panelBuilder.callOnPanelBuilt(onInitDone);
            }
        };
    }
);
