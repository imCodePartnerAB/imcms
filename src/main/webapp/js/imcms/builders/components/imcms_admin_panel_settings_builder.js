/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
Imcms.define(
    "imcms-admin-panel-settings-builder",
    ["imcms-bem-builder", "jquery"],
    function (BEM, $) {

        var $settings;

        function onPanelSettingsClicked() {
            $settings.slideToggle();
        }

        function buildSettingsList() {
            return new BEM({
                block: "panel-settings-list",
                elements: [
                    {
                        setting: $("<div>", {
                            text: "Setting 0"
                        })
                    }, {
                        setting: $("<div>", {
                            text: "Setting 1"
                        })
                    }, {
                        setting: $("<div>", {
                            text: "Setting 2"
                        })
                    }, {
                        setting: $("<div>", {
                            text: "Setting 3"
                        })
                    }
                ]
            }).buildBlockStructure("<div>", {
                style: "display: none;"
            });
        }

        return {
            buildButton: function () {
                return new BEM({
                    block: "admin-panel-settings",
                    elements: {
                        button: $("<div>", {
                            title: "Admin panel settings",
                            click: onPanelSettingsClicked
                        }),
                        list: $settings = buildSettingsList()
                    }
                }).buildBlockStructure("<div>");
            }
        }
    }
);
