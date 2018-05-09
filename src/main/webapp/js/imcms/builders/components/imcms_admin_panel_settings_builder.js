/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
Imcms.define(
    "imcms-admin-panel-settings-builder",
    ["imcms-bem-builder", "imcms-i18n-texts", "jquery"],
    function (BEM, texts, $) {

        texts = texts.panel.settingsList;

        var settingEnabledClass = BEM.buildClass("settings-section", "setting", "enabled");
        var settingEnabledClassSelector = "." + settingEnabledClass;

        var $settings;

        function buildSection(name, settings) {

            var settingsObjects = settings.map(function (setting) {
                setting.click = function () {
                    var $this = $(this);

                    if ($this.hasClass(settingEnabledClass)) return;

                    $this.parent(".settings-section")
                        .find(settingEnabledClassSelector)
                        .removeClass(settingEnabledClass);

                    $this.addClass(settingEnabledClass);

                    setting.onSettingClick.call();
                };

                return {
                    "setting": $("<div>", setting)
                };
            });

            return new BEM({
                block: "settings-section",
                elements: [{
                    "section-name": $("<div>", {text: name})
                }].concat(settingsObjects)
            }).buildBlockStructure("<div>");
        }

        function buildSettings($settingsButton) {
            var bemOptions = {
                block: "admin-panel-settings-list",
                elements: [
                    {
                        "section": buildSection(texts.size.name, [
                            {
                                text: texts.size.small,
                                title: texts.size.smallTitle,
                                onSettingClick: function () {
                                    console.log("Setting 00")
                                }
                            }, {
                                text: texts.size.large,
                                title: texts.size.largeTitle,
                                "class": settingEnabledClass,
                                onSettingClick: function () {
                                    console.log("Setting 01")
                                }
                            }
                        ])
                    }, {
                        "section": buildSection(texts.appearance.name, [
                            {
                                text: texts.appearance.auto,
                                title: texts.appearance.autoTitle,
                                "class": settingEnabledClass,
                                onSettingClick: function () {
                                    console.log("Setting 10")
                                }
                            }, {
                                text: texts.appearance.hidden,
                                title: texts.appearance.hiddenTitle,
                                onSettingClick: function () {
                                    console.log("Setting 11")
                                }
                            }, {
                                text: texts.appearance.visible,
                                title: texts.appearance.visibleTitle,
                                onSettingClick: function () {
                                    console.log("Setting 12")
                                }
                            }
                        ])
                    }
                ]
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
