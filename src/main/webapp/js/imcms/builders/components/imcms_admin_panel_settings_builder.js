/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
Imcms.define(
    "imcms-admin-panel-settings-builder",
    ["imcms-bem-builder", "imcms-i18n-texts", "jquery"],
    function (BEM, texts, $) {

        texts = texts.panel.settingsList;
        var $settings;

        function buildSection(name, settings) {
            var settingsObjects = settings.map(function (setting) {
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
                                click: function () {
                                    console.log("Setting 00")
                                }
                            }, {
                                text: texts.size.large,
                                title: texts.size.largeTitle,
                                "class": "settings-section__setting--enabled",
                                click: function () {
                                    console.log("Setting 01")
                                }
                            }
                        ])
                    }, {
                        "section": buildSection(texts.appearance.name, [
                            {
                                text: texts.appearance.auto,
                                title: texts.appearance.autoTitle,
                                "class": "settings-section__setting--enabled",
                                click: function () {
                                    console.log("Setting 10")
                                }
                            }, {
                                text: texts.appearance.hidden,
                                title: texts.appearance.hiddenTitle,
                                click: function () {
                                    console.log("Setting 11")
                                }
                            }, {
                                text: texts.appearance.visible,
                                title: texts.appearance.visibleTitle,
                                click: function () {
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
