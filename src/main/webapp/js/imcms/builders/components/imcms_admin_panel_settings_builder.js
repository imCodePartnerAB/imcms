/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
Imcms.define(
    "imcms-admin-panel-settings-builder",
    ["imcms-bem-builder", "imcms-cookies", "imcms-i18n-texts", "jquery"],
    function (BEM, cookies, texts, $) {

        texts = texts.panel.settingsList;

        var settingEnabledClass = BEM.buildClass("settings-section", "setting", "enabled");
        var settingEnabledClassSelector = "." + settingEnabledClass;

        var $settings;

        var sections = {
            size: {
                id: "panel-size",
                text: texts.size.name,
                settings: [{
                    id: "panel-size-small",
                    text: texts.size.small,
                    title: texts.size.smallTitle,
                    onSettingClick: function () {
                        console.log("panel small")
                    }
                }, {
                    id: "panel-size-large",
                    isDefault: true,
                    text: texts.size.large,
                    title: texts.size.largeTitle,
                    onSettingClick: function () {
                        console.log("panel large")
                    }
                }]
            },
            appearance: {
                id: "panel-appearance",
                text: texts.appearance.name,
                settings: [{
                    id: "panel-appearance-auto",
                    isDefault: true,
                    text: texts.appearance.auto,
                    title: texts.appearance.autoTitle,
                    onSettingClick: function () {
                        console.log("panel appearance auto")
                    }
                }, {
                    id: "panel-appearance-hidden",
                    text: texts.appearance.hidden,
                    title: texts.appearance.hiddenTitle,
                    onSettingClick: function () {
                        console.log("panel appearance hidden")
                    }
                }, {
                    id: "panel-appearance-visible",
                    text: texts.appearance.visible,
                    title: texts.appearance.visibleTitle,
                    onSettingClick: function () {
                        console.log("panel appearance visible")
                    }
                }]
            }
        };

        var allSections = [
            sections.size,
            sections.appearance
        ];

        function buildSection(section) {
            var savedSetting = cookies.getCookie(section.id);

            var settingsElements = section.settings.map(function (setting) {
                var attributes = {
                    id: setting.id,
                    text: setting.text,
                    title: setting.title
                };

                attributes.click = function () {
                    var $setting = $(this);

                    if ($setting.hasClass(settingEnabledClass)) return;

                    $setting.parent(".settings-section")
                        .find(settingEnabledClassSelector)
                        .removeClass(settingEnabledClass);

                    $setting.addClass(settingEnabledClass);

                    setting.onSettingClick.call();
                    cookies.setCookie(section.id, setting.id);
                };

                if ((savedSetting && (savedSetting === setting.id))
                    || (!savedSetting && setting.isDefault))
                {
                    attributes["class"] = settingEnabledClass;
                }

                return {
                    "setting": $("<div>", attributes)
                };
            });

            return new BEM({
                block: "settings-section",
                elements: [{
                    "section-name": $("<div>", {text: section.text})
                }].concat(settingsElements)
            }).buildBlockStructure("<div>");
        }

        function buildSettings($settingsButton) {
            var sections$ = allSections.map(buildSection);

            var bemOptions = {
                block: "admin-panel-settings-list",
                elements: {
                    "section": sections$
                }
            };

            return new BEM(bemOptions)
                .buildBlockStructure("<div>", {style: "display: none;"})
                .insertAfter($settingsButton);
        }

        return {
            onSettingsClicked: function () {
                ($settings || ($settings = buildSettings($(this)))).slideToggle();
            },
            hideSettings: function () {
                $settings.slideUp(300);
            }
        }
    }
);
