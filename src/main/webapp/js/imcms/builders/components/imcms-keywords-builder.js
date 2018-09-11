/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 27.07.17.
 */
define("imcms-keywords-builder",
    ["imcms-bem-builder", "imcms-texts-builder", "imcms-buttons-builder", "imcms-primitives-builder", "imcms-uuid-generator", "jquery"],
    function (BEM, texts, buttons, primitives, uuidGenerator, $) {

        var keywordMaxLength = 128;

        function createRemoveKeywordButton() {
            return buttons.closeButton({click: removeKeyword});
        }

        function isUniqueKeyWord(keyWord, $keywordsContainer) {
            return $keywordsContainer.find(".imcms-keyword__keyword")
                .map(function () {
                    return $(this)
                })
                .toArray()
                .reduce(function (isUnique, $keyword) {
                    return isUnique && ($keyword.text() !== keyWord);
                }, true);
        }

        function addKeyword() {
            var $btn = $(this),
                $keywordInput = $btn.parent().find(".imcms-keyword__input"),
                keywordInputVal = $keywordInput.val().trim(),
                $keywordsContainer = $btn.parent().find(".imcms-keyword__keywords")
            ;

            $keywordInput.val("");

            if ((keywordInputVal !== "") && isUniqueKeyWord(keywordInputVal, $keywordsContainer)) {
                $keywordsContainer.css({"display": "block"});

                keywordsBoxBEM.buildBlockElement("keyword", "<div>", {
                    text: keywordInputVal
                }).append(createRemoveKeywordButton()).appendTo($keywordsContainer);
            }
        }

        function removeKeyword() {
            var $btn = $(this),
                $keyword = $btn.parents(".imcms-keyword__keyword"),
                $keywords = $keyword.parent()
            ;

            $keyword.remove();

            if ($keywords.children().length === 0) {
                $keywords.css({"display": "none"})
            }
        }

        function bindAddKeyword($input, $addKeywordButton, $keywordsBlock) {
            return function (keyword) {
                $input.val(keyword);
                $addKeywordButton.click();
                return $keywordsBlock;
            };
        }

        function bindGetKeywords($keywordsBlock) {
            return function () {
                return $keywordsBlock.find(".imcms-keyword__keyword")
                    .map(function () {
                        return $(this).text();
                    })
                    .toArray();
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

                $input.on("input", function () {
                    var $input = $(this);
                    var keywordValue = $input.val().trim();

                    if (keywordValue.length > keywordMaxLength) {
                        $input.val(keywordValue.substring(0, keywordMaxLength));
                    }
                });

                var $keywordsBlock = keywordsContainerBEM.buildBlock("<div>", [{"keywords-box": $keywordsBox}]);

                $keywordsBlock.addKeyword = bindAddKeyword($input, $addKeywordButton, $keywordsBlock);
                $keywordsBlock.getKeywords = bindGetKeywords($keywordsBlock);

                return $keywordsBlock;
            }
        };
    }
);
