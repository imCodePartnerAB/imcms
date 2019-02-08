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
        'imcms-text-editor-toolbar-button-builder', "imcms-modal-window-builder", "imcms-i18n-texts"
    ],
    function (textValidationAPI, textValidationBuilder, $, toolbarButtonBuilder, modal, texts) {

        texts = texts.textValidation;

        const title = 'Validate Content over W3C'; // todo: localize!

        const getOnTinyMCETextValidationClick = function (editor) {
            return function () {
                const content = editor.getContent();
                const $icon = $(this.$el).find(".mce-ico")
                    .removeAttr("class")
                    .attr("class", "mce-ico mce-i-imcms-w3c-text-validation-processing-icon");

                textValidationAPI.validate({content: content})
                    .done(validationResult => {
                        const iconClass = validationResult.valid
                            ? "mce-i-imcms-w3c-text-validation-valid-icon"
                            : "mce-i-imcms-w3c-text-validation-invalid-icon";

                        $icon.removeAttr("class").attr("class", "mce-ico " + iconClass);

                        if (!validationResult.valid) {
                            textValidationBuilder.buildTextValidationFailWindow(validationResult);
                        }
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.validationFailed));
            };
        };

        const getOnPlainTextValidationClick = activeTextEditor => () => {
            const content = activeTextEditor.getContent();
            const $button = activeTextEditor.$().parent().find('.html-validation-button__icon');

            $button.addClass('imcms-w3c-text-validation-processing-icon');

            textValidationAPI.validate({content: content})
                .done(validationResult => {
                    const iconClass = validationResult.valid
                        ? 'imcms-w3c-text-validation-valid-icon'
                        : 'imcms-w3c-text-validation-invalid-icon';

                    $button.removeClass('imcms-w3c-text-validation-processing-icon').addClass(iconClass);

                    if (!validationResult.valid) {
                        textValidationBuilder.buildTextValidationFailWindow(validationResult);
                    }
                })
                .fail(() => modal.buildErrorWindow(texts.error.validationFailed));
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
