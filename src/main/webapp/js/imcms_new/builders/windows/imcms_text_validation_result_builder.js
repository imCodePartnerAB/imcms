/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * @date 31.01.18
 * @namespace validationResult.data.warnings
 */
Imcms.define(
    "imcms-text-validation-result-builder",
    ["imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-events"],
    function (WindowBuilder, BEM, components, $, events) {

        var $validationResultContainer;

        function buildFooter() {
            return textValidationFailWindowBuilder.buildFooter([
                components.buttons.saveButton({
                    text: "OK",
                    click: textValidationFailWindowBuilder.closeWindow.bind(textValidationFailWindowBuilder)
                })
            ]);
        }

        function buildValidationResultContainer() {
            return $("<div>");
        }

        function buildValidationFailWindow() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": textValidationFailWindowBuilder.buildHead("Validation Result Dialog"),
                    "body": $validationResultContainer = buildValidationResultContainer(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "w3c-validation-result"});
        }

        function loadData(validationResult) {

            function appendValidationFailRow(item, pos) {
                var $container = $("<div>").addClass("imcms-w3c-error"),
                    $sourceContainer = $("<div>");

                var errorMessage = pos + 1 + ". " + item.message.charAt(0).toUpperCase() + item.message.slice(1);
                var $errorMessage = $("<div>").text(errorMessage);

                var $invalidHtml = $("<code>").addClass("language-html")
                    .html(item.line + ": " + item.source.replace(/(<([^>]+)>)/ig, ""));

                $sourceContainer.append($invalidHtml);
                $container.append($errorMessage).append($sourceContainer);
                $content.append($container);
            }

            var $wrapper = $("<div>"),
                $content = $("<div>").addClass("imcms-w3c-errors"),
                $errorsTitle = $("<h2>").text("Validation Output: " + validationResult.data.errors.length + " Errors");

            $content.append($errorsTitle);

            validationResult.data.errors.forEach(appendValidationFailRow);

            var $warningsTitle = $("<h2>").text("Validation Output: " + validationResult.data.warnings.length + " Warnings");

            $content.append($warningsTitle);

            validationResult.data.warnings.forEach(appendValidationFailRow);

            $wrapper.append($content);
            $validationResultContainer.append($wrapper);
        }

        function clearData() {
            events.trigger("enable text editor blur");
            $validationResultContainer.empty();
        }

        var textValidationFailWindowBuilder = new WindowBuilder({
            factory: buildValidationFailWindow,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData
        });

        return {
            buildTextValidationFailWindow: function (validationResult) {
                events.trigger("disable text editor blur");
                textValidationFailWindowBuilder.buildWindowWithShadow.applyAsync(
                    arguments, textValidationFailWindowBuilder
                );
            }
        }
    }
);
