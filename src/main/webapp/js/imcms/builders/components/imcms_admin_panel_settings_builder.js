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
            small: {
                id: "small",
                text: texts.size.small,
                title: texts.size.smallTitle,
                onSettingClick: function () {
                    $("#imcms-admin-panel").addClass("imcms-admin-panel--small")
                }
            },
            large: {
                id: "large",
                isDefault: true,
                text: texts.size.large,
                title: texts.size.largeTitle,
                onSettingClick: function () {
                    $("#imcms-admin-panel").removeClass("imcms-admin-panel--small")
                }
            },
            auto: {
                id: "auto",
                isDefault: true,
                text: texts.appearance.auto,
                title: texts.appearance.autoTitle,
                onSettingClick: function () {
                    console.log("panel appearance auto")
                }
            },
            hidden: {
                id: "hidden",
                text: texts.appearance.hidden,
                title: texts.appearance.hiddenTitle,
                onSettingClick: function () {
                    console.log("panel appearance hidden")
                }
            },
            visible: {
                id: "visible",
                text: texts.appearance.visible,
                title: texts.appearance.visibleTitle,
                onSettingClick: function () {
                    console.log("panel appearance visible")
                }
            }
        };

        var sections = {
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

        var allSections = [];

        for (var key in sections) {
            allSections.push(sections[key]);
        }

        function buildSection(section) {
            var savedSetting = cookies.getCookie(section.id);

            var settingsElements = section.settings.map(function (setting) {
                var attributes = {
                    id: "imcms-" + section.id + "-" + setting.id,
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

                if ((setting.id && (savedSetting === setting.id))
                    || (!savedSetting && setting.isDefault))
                {
                    attributes["class"] = settingEnabledClass;
                }

                return {
                    "setting": $("<div>", attributes)
                };
            });

            var sectionName = {"section-name": $("<div>", {text: section.text})};
            var bemOptions = {
                block: "settings-section",
                elements: [sectionName].concat(settingsElements)
            };

            return new BEM(bemOptions).buildBlockStructure("<div>");
        }

        function buildSettings(settingsButton) {
            var sections$ = allSections.map(buildSection);

            var bemOptions = {
                block: "admin-panel-settings-list",
                elements: {
                    "section": sections$
                }
            };

            return new BEM(bemOptions)
                .buildBlockStructure("<div>", {style: "display: none;"})
                .insertAfter(settingsButton);
        }

        return {
            applyCurrentSettings: function () {
                allSections
                    .map(function (section) {
                        return settings[cookies.getCookie(section.id)];
                    })
                    .filter(function (setting) {
                        return setting && !setting.isDefault;
                    })
                    .forEach(function (setting) {
                        setting.onSettingClick.call();
                    });
            },
            onSettingsClicked: function () {
                $settings = ($settings || (buildSettings(this)));

                $settings.css("top", $("#imcms-admin-panel").height())
                    .slideToggle();
            },
            hideSettings: function () {
                $settings && $settings.slideUp(300);
            }
        }
    }
);
