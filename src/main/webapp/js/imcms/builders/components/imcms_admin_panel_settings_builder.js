/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.05.18
 */
Imcms.define(
    "imcms-admin-panel-settings-builder",
    ["imcms-bem-builder", "jquery"],
    function (BEM, $) {

        function onPanelSettingsClicked() {
            // todo: implement
        }

        return {
            buildButton: function () {
                return new BEM({
                    block: "admin-panel-settings",
                    elements: {
                        button: $("<div>", {
                            title: "Admin panel settings",
                            click: onPanelSettingsClicked
                        })
                    }
                }).buildBlockStructure("<div>");
            }
        }
    }
);
