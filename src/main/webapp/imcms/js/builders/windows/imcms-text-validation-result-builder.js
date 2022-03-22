/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * @date 31.01.18
 * @namespace validationResult.data.warnings
 */
define(
    "imcms-text-validation-result-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-events",
        "imcms-i18n-texts"
    ],
    function (WindowBuilder, BEM, components, $, events, texts) {

        let $validationResultContainer;
        texts = texts.textValidation;

        function buildFooter() {
            return WindowBuilder.buildFooter([
                components.buttons.saveButton({
                    text: texts.ok,
                    click: closeWindow
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
                    "head": textValidationFailWindowBuilder.buildHead(texts.title),
                    "body": $validationResultContainer = buildValidationResultContainer(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "w3c-validation-result"});
        }

        function loadData(validationResult) {

            function appendValidationFailRow(item, pos) {
	            const $container = $("<div>").addClass("imcms-w3c-error"),
		            $sourceContainer = $("<div>");

	            const errorMessage = pos + 1 + ". " + item.message;
	            const $errorMessage = $("<div>").text(errorMessage);

	            const $invalidHtml = $("<code>").addClass("language-html")
		            .html(item.lastLine + ": " + item.extract.replace(/[<]/g, "&lt").replace(/[>]/g, "&gt"));

	            $sourceContainer.append($invalidHtml);
	            $container.append($errorMessage).append($sourceContainer);
	            $content.append($container);
            }

	        let $wrapper = $("<div>"),
		        $content = $("<div>").addClass("imcms-w3c-errors"),
		        $errorsTitle = $("<h2>").text(texts.output + validationResult.errors.length + texts.errors);

	        $content.append($errorsTitle);

	        validationResult.errors.forEach(appendValidationFailRow);

	        const $warningsTitle = $("<h2>").text(texts.output + validationResult.warnings.length + texts.warnings);

	        $content.append($warningsTitle);

	        validationResult.warnings.forEach(appendValidationFailRow);

            $wrapper.append($content);
            $validationResultContainer.append($wrapper);
        }

        function clearData() {
            events.trigger("enable text editor blur");
            $validationResultContainer.empty();
        }

        function closeWindow() {
            textValidationFailWindowBuilder.closeWindow();
        }

        var textValidationFailWindowBuilder = new WindowBuilder({
            factory: buildValidationFailWindow,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close",
            onEnterKeyPressed: closeWindow
        });

        return {
            buildTextValidationFailWindow: function (validationResult) {
                events.trigger("disable text editor blur");
                textValidationFailWindowBuilder.buildWindowWithShadow.apply(
                    textValidationFailWindowBuilder, arguments
                );
            }
        }
    }
);
