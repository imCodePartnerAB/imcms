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

        var settings = {
            size: {
                small: {
                    id: "panel-size-small",
                    text: texts.size.small,
                    title: texts.size.smallTitle,
                    onSettingClick: function () {
                        console.log("panel small")
                    }
                },
                large: {
                    id: "panel-size-large",
                    isDefault: true,
                    text: texts.size.large,
                    title: texts.size.largeTitle,
                    onSettingClick: function () {
                        console.log("panel large")
                    }
                }
            },
            appearance: {
                auto: {
                    id: "panel-appearance-auto",
                    isDefault: true,
                    text: texts.appearance.auto,
                    title: texts.appearance.autoTitle,
                    onSettingClick: function () {
                        console.log("panel appearance auto")
                    }
                },
                hidden: {
                    id: "panel-appearance-hidden",
                    text: texts.appearance.hidden,
                    title: texts.appearance.hiddenTitle,
                    onSettingClick: function () {
                        console.log("panel appearance hidden")
                    }
                },
                visible: {
                    id: "panel-appearance-visible",
                    text: texts.appearance.visible,
                    title: texts.appearance.visibleTitle,
                    onSettingClick: function () {
                        console.log("panel appearance visible")
                    }
                }
            }
        };

        function activateSetting(element) {
            var $setting = $(element);

            if ($setting.hasClass(settingEnabledClass)) return false;

            $setting.parent(".settings-section")
                .find(settingEnabledClassSelector)
                .removeClass(settingEnabledClass);

            $setting.addClass(settingEnabledClass);

            return true;
        }

        function buildSection(id, name, settings) {
            var cookieName = "panel" + id;
            var savedSetting = cookies.getCookie(cookieName);

            var settingsElements = settings.map(function (setting) {
                var attributes = {
                    id: setting.id,
                    text: setting.text,
                    title: setting.title
                };

                attributes.click = function () {
                    activateSetting(this) && setting.onSettingClick.call();
                    cookies.setCookie(cookieName, setting.id);
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
                    "section-name": $("<div>", {text: name})
                }].concat(settingsElements)
            }).buildBlockStructure("<div>");
        }

        function buildSettings($settingsButton) {
            var $sizeSection = buildSection("size", texts.size.name, [
                settings.size.small, settings.size.large
            ]);

            var $appearanceSection = buildSection("appearance", texts.appearance.name, [
                settings.appearance.auto, settings.appearance.hidden, settings.appearance.visible
            ]);

            var bemOptions = {
                block: "admin-panel-settings-list",
                elements: {
                    "section": [$sizeSection, $appearanceSection]
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
