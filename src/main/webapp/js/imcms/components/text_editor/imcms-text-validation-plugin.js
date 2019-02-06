/**
 * Plugin for text/html validation in Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 */
define(
    "imcms-text-validation-plugin",
    [
        "imcms-texts-validation-rest-api", "imcms-text-validation-result-builder", "jquery",
        'imcms-text-editor-toolbar-button-builder'
    ],
    function (textValidationAPI, textValidationBuilder, $, toolbarButtonBuilder) {

        var title = 'Validate Content over W3C'; // todo: localize!

        var getOnTinyMCETextValidationClick = function (editor) {
            return function () {
                var content = editor.getContent();
                var $icon = $(this.$el).find(".mce-ico")
                    .removeAttr("class")
                    .attr("class", "mce-ico mce-i-imcms-w3c-text-validation-processing-icon");

                textValidationAPI.validate({content: content}).done(validationResult => {
                    var iconClass = validationResult.valid
                        ? "mce-i-imcms-w3c-text-validation-valid-icon"
                        : "mce-i-imcms-w3c-text-validation-invalid-icon";

                    $icon.removeAttr("class").attr("class", "mce-ico " + iconClass);

                    if (!validationResult.valid) {
                        textValidationBuilder.buildTextValidationFailWindow(validationResult);
                    }
                });
            };
        };

        var getOnPlainTextValidationClick = activeTextEditor => () => {
            var content = activeTextEditor.getContent();
            var $button = activeTextEditor.$().parent().find('.html-validation-button__icon');

            $button.addClass('imcms-w3c-text-validation-processing-icon');

            textValidationAPI.validate({content: content}).done(validationResult => {
                var iconClass = validationResult.valid
                    ? 'imcms-w3c-text-validation-valid-icon'
                    : 'imcms-w3c-text-validation-invalid-icon';

                $button.removeClass('imcms-w3c-text-validation-processing-icon').addClass(iconClass);

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
                    tooltip: title,
                    onclick: getOnTinyMCETextValidationClick(editor)
                });
            },
            buildHtmlValidationButton: activeTextEditor => toolbarButtonBuilder.buildButton(
                'html-validation-button', title, getOnPlainTextValidationClick(activeTextEditor)
            )
        };
    }
);
