/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 27.07.17.
 */
Imcms.define("imcms-keywords-builder",
    ["imcms-bem-builder", "imcms-texts-builder", "imcms-buttons-builder", "imcms-primitives-builder", "imcms-uuid-generator", "jquery"],
    function (BEM, texts, buttons, primitives, uuidGenerator, $) {
        function createRemoveKeywordButton() {
            return buttons.closeButton({click: removeKeyword});
        }

        function addKeyword() {
            var $btn = $(this),
                keywordInput = $btn.parent().find(".imcms-keyword__input"),
                keywordInputVal = keywordInput.val().trim(),
                keywords = $btn.parent().find(".imcms-keyword__keywords")
            ;

            keywordInput.val("");

            if (keywordInputVal !== "") {
                keywords.css({"display": "block"});

                keywordsBoxBEM.buildBlockElement("keyword", "<div>", {
                    text: keywordInputVal
                }).append(createRemoveKeywordButton()).appendTo(keywords);
            }
        }

        function removeKeyword() {
            var $btn = $(this),
                keyword = $btn.parents(".imcms-keyword__keyword"),
                keywords = keyword.parent()
            ;

            keyword.remove();
            if (keywords.children().length === 0) {
                keywords.css({"display": "none"})
            }
        }

        function apiAddKeyword($input, $addKeywordButton, $keywordResult) {
            return function (keyword) {
                $input.val(keyword);
                $addKeywordButton.click();
                return $keywordResult;
            };
        }

        var keywordsContainerBEM = new BEM({
                block: "imcms-field",
                elements: {
                    "keywords-box": "imcms-keyword"
                }
            }),
            keywordsBoxBEM = new BEM({
                block: "imcms-keyword",
                elements: {
                    "keywords": "",
                    "keyword": ""
                }
            })
        ;

        return {
            keywordsBox: function (tag, attributes) {
                var inputId = attributes["input-id"] || uuidGenerator.generateUUID(),
                    $label = primitives.imcmsLabel(inputId, attributes.title),
                    $input = primitives.imcmsInputText({
                        id: inputId,
                        placeholder: attributes.placeholder
                    }, ["wide"]),
                    $addKeywordButton = buttons.neutralButton({
                        text: attributes["button-text"],
                        click: addKeyword
                    }),
                    $keywordsContainer = keywordsBoxBEM.buildElement("keywords", "<div>"),
                    $keywordsBox = keywordsBoxBEM.buildBlock("<div>", [
                        {"label": $label},
                        {"input": $input},
                        {"button": $addKeywordButton},
                        {"keywords": $keywordsContainer}
                    ])
                ;
                var $keywordResult = keywordsContainerBEM.buildBlock("<div>", [{"keywords-box": $keywordsBox}]);

                $keywordResult.addKeyword = apiAddKeyword($input, $addKeywordButton, $keywordResult);

                return $keywordResult;
            }
        };
    }
);
