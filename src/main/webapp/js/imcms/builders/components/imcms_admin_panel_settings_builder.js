/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
Imcms.define(
    "imcms-admin-panel-settings-builder",
    ["imcms-bem-builder", "jquery"],
    function (BEM, $) {

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
                        "section": buildSection("Section 0 name", [
                            {
                                text: "Setting 0",
                                click: function () {
                                    console.log("Setting 00")
                                }
                            }, {
                                text: "Setting 1",
                                click: function () {
                                    console.log("Setting 01")
                                }
                            }
                        ])
                    }, {
                        "section": buildSection("Section 1 name", [
                            {
                                text: "Setting 0",
                                click: function () {
                                    console.log("Setting 10")
                                }
                            }, {
                                text: "Setting 1",
                                click: function () {
                                    console.log("Setting 11")
                                }
                            }, {
                                text: "Setting 2",
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
                $settings.slideUp();
            }
        }
    }
);
