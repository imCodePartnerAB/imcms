/**
 * Plugin for text/html validation in Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 */
Imcms.define(
    "imcms-text-validation-plugin",
    ["imcms-texts-validation-rest-api", "imcms-text-validation-result-builder", "jquery", 'imcms-text-editor'],
    function (textValidationAPI, textValidationBuilder, $, textEditor) {

        var onTextValidationClick = function () {
            var content = textEditor.getActiveTextEditor().getContent();
            var $icon = $(this.$el).find(".mce-ico")
                .removeAttr("class")
                .attr("class", "mce-ico mce-i-imcms-w3c-text-validation-processing-icon");

            textValidationAPI.validate({content: content}).done(function (validationResult) {
                var iconClass = validationResult.valid
                    ? "mce-i-imcms-w3c-text-validation-valid-icon"
                    : "mce-i-imcms-w3c-text-validation-invalid-icon";

                $icon.removeAttr("class").attr("class", "mce-ico " + iconClass);

                if (!validationResult.valid) {
                    textValidationBuilder.buildTextValidationFailWindow(validationResult);
                }
            });
        };

        return {
            pluginName: 'w3c_validation',
            initTextValidation: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'imcms-w3c-text-validation-icon',
                    tooltip: 'Validate Content over W3C',
                    onclick: onTextValidationClick
                });
            }
        };
    }
);
