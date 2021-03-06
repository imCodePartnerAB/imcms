/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
define(
    "imcms-admin-panel-settings-builder",
    [
        "imcms-bem-builder", "imcms-cookies", "imcms-i18n-texts", "imcms-admin-panel-state", "jquery", "imcms-events",
        "imcms-streams"
    ],
    function (BEM, cookies, texts, panelState, $, events, streams) {

        texts = texts.panel.settingsList;

        const adminPanelVisibilityPublisher = streams.createPublisherOnTopic("admin panel visibility");
        const settingEnabledClass = BEM.buildClass("settings-section", "setting", "enabled");
        const settingEnabledClassSelector = "." + settingEnabledClass;

        let $settings;

        function wrapWithPositionRefresh(refreshAfterMe) {
            return () => {
                refreshAfterMe.call();
                setTimeout(refreshSettingsPosition, 200); // check: it may appear setTimeout is not required
            };
        }

        const $switchPanelVisibilityButton = $("<button>", {
            id: "imcms-switch-panel-visibility-button",
            "class": "imcms-button imcms-button--positive imcms-button--locked",
            title: texts.unlockPanel
        });

        function prependToAdminPanel($prependMe) {
            $prependMe.appendTo($("#imcms-admin-panel"));
        }

        const settings = {
            small: {
                id: "small",
                text: texts.size.small,
                title: texts.size.smallTitle,
                onSettingClick: wrapWithPositionRefresh(() => {
                    $("#imcms-admin-panel").addClass("imcms-admin-panel--small");
                })
            },
            large: {
                id: "large",
                isDefault: true,
                text: texts.size.large,
                title: texts.size.largeTitle,
                onSettingClick: wrapWithPositionRefresh(() => {
                    $("#imcms-admin-panel").removeClass("imcms-admin-panel--small");
                })
            },
            auto: {
                id: "auto",
                isDefault: true,
                text: texts.appearance.auto,
                title: texts.appearance.autoTitle,
                onSettingClick: () => {
                    panelState.setState("auto");
                    $("#imcms-admin").removeClass("imcms-panel-visible");
                    panelState.refreshSpecialPanelPosition();

                    $switchPanelVisibilityButton.css("display", "none");
                }
            },
            hidden: {
                id: "hidden",
                text: texts.appearance.hidden,
                title: texts.appearance.hiddenTitle,
                onSettingClick: () => {
                    hideSettings();

                    $("#imcms-admin").removeClass("imcms-panel-visible");

                    adminPanelVisibilityPublisher.publish({
                        hidePanel: true
                    });

                    prependToAdminPanel($switchPanelVisibilityButton);
                    switchPanelVisibility = showPanel;

                    $switchPanelVisibilityButton.removeClass("imcms-button--up")
                        .addClass("imcms-button--locked imcms-button--positive")
                        .attr("title", texts.unlockPanel)
                        .fadeIn();
                }
            },
            visible: {
                id: "visible",
                text: texts.appearance.visible,
                title: texts.appearance.visibleTitle,
                onSettingClick: () => {
                    $("body").css("top", 0);

                    panelState.setState("visible");

                    $("#imcms-admin").addClass("imcms-panel-visible");
                    $("#imcms-admin-panel,#imcmsAdminSpecial").css("top", 0);

                    panelState.refreshSpecialPanelPosition();
                    $switchPanelVisibilityButton.css("display", "none");
                }
            }
        };

        function switchShowHideButton(title) {
            $switchPanelVisibilityButton.fadeToggle(70, function () {
                $(this).toggleClass("imcms-button--up imcms-button--locked imcms-button--positive")
                    .attr("title", title)
                    .fadeToggle(70);
            });
        }

        function showPanel() {
            adminPanelVisibilityPublisher.publish({
                showPanel: true
            });

            switchShowHideButton(texts.hidePanel);
            switchPanelVisibility = hidePanel;
        }

        function hidePanel() {
            adminPanelVisibilityPublisher.publish({
                hidePanel: true
            });

            switchShowHideButton(texts.unlockPanel);
            switchPanelVisibility = showPanel;
        }

        var switchPanelVisibility = showPanel;

        $switchPanelVisibilityButton.click(() => {
            switchPanelVisibility();
        });

        const sections = {
            size: {
                id: "panel-size",
                text: texts.size.name,
                settings: [settings.small, settings.large]
            },
            appearance: {
                id: "panel-appearance",
                text: texts.appearance.name,
                settings: [settings.auto, settings.hidden, settings.visible]
            }
        };

        const allSections = [];

        for (var key in sections) {
            allSections.push(sections[key]);
        }

        function buildSection(section) {
            const savedSetting = cookies.getCookie(section.id);

            const settingsElements = section.settings.map(function (setting) {
                const attributes = {
                    id: "imcms-" + section.id + "-" + setting.id,
                    text: setting.text,
                    title: setting.title
                };

                attributes.click = function () {
                    const $setting = $(this);

                    if ($setting.hasClass(settingEnabledClass)) return;

                    $setting.parent(".settings-section")
                        .find(settingEnabledClassSelector)
                        .removeClass(settingEnabledClass);

                    $setting.addClass(settingEnabledClass);

                    setting.onSettingClick.call();
                    cookies.setCookie(section.id, setting.id, {path: "/"}, {expires: 30});
                };

                if ((setting.id && (savedSetting === setting.id))
                    || (!savedSetting && setting.isDefault)) {
                    attributes["class"] = settingEnabledClass;
                }

                return {
                    "setting": $("<div>", attributes)
                };
            });

            const sectionName = {"section-name": $("<div>", {text: section.text})};
            const bemOptions = {
                block: "settings-section",
                elements: [sectionName].concat(settingsElements)
            };

            return new BEM(bemOptions).buildBlockStructure("<div>");
        }

        function buildSettings(settingsButton) {
            const sections$ = allSections.map(buildSection);

            const bemOptions = {
                block: "admin-panel-settings-list",
                elements: {
                    "section": sections$
                }
            };

            return new BEM(bemOptions)
                .buildBlockStructure("<div>", {style: "display: none;"})
                .insertAfter(settingsButton);
        }

        function refreshSettingsPosition() {
            $settings && $settings.css("top", $("#imcms-admin-panel").outerHeight());
            panelState.refreshSpecialPanelPosition();
        }

        function hideSettings() {
            $settings && $settings.slideUp(300);
        }

        return {
            applyCurrentSettings: () => {
                allSections
                    .map(section => settings[cookies.getCookie(section.id)])
                    .filter(setting => setting && !setting.isDefault)
                    .forEach(setting => {
                        setting.onSettingClick.call();
                    });
            },
            onSettingsClicked: function () {
                $settings = ($settings || (buildSettings(this)));

                refreshSettingsPosition();
                $settings.slideToggle();
            },
            hideSettings: hideSettings
        };
    }
);
