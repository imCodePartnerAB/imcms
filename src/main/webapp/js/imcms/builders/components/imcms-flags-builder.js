/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-flags-builder",
    [
        "imcms-bem-builder", "imcms-languages-rest-api", "imcms", "jquery", "imcms-i18n-texts"
    ],
    function (bemBuilder, languagesRestApi, imcms, $, texts) {

        texts = texts.languageFlags;

        const FLAGS_CLASS = "imcms-flag",
            FLAG_ACTIVE_CLASS = FLAGS_CLASS + "--" + "active"
        ;

        const flagsBEM = new bemBuilder({
            block: "imcms-flags",
            elements: {
                "flag": FLAGS_CLASS
            }
        });

        function changeNeighborFlag($btn) {
            const $neighborFlag = ($btn.next("." + FLAGS_CLASS).length !== 0)
                ? $btn.next("." + FLAGS_CLASS)
                : $btn.prev("." + FLAGS_CLASS);

            $neighborFlag.toggleClass(FLAG_ACTIVE_CLASS);
        }

        function onFlagClick(event) {
            const $clickedFlag = $(this);

            if (!$clickedFlag.hasClass(FLAGS_CLASS)) {
                return;
            }

            if ($clickedFlag.hasClass(FLAG_ACTIVE_CLASS)) {
                event.preventDefault();

            } else {
                $clickedFlag.addClass(FLAG_ACTIVE_CLASS);
                changeNeighborFlag($clickedFlag);
            }
        }

        function buildFlag(tag, attributes, isActive, language) {
            const modifiers = [language.code];
            if (isActive) {
                modifiers.push("active");
            }

            attributes = attributes || {};
            attributes.title = language.name + "/" + language.nativeName;

            return flagsBEM.buildElement("flag", tag, attributes, modifiers).click(onFlagClick);
        }

        function mapLanguagesToFlags(languages, flagBuilderDataProducer) {
            return languages.map(language => {
                const flagBuilderData = flagBuilderDataProducer(language),
                    isActive = language.code === imcms.language.code;

                return buildFlag.apply(null, flagBuilderData.concat([isActive, language]));
            });
        }

        function addDisplayMode(displayModeCSS) {
            return function () {
                const $flags = $(this);

                $flags.css(displayModeCSS);
                $flags.next().css(displayModeCSS);
            }
        }

        return {
            flagsContainer: function (flagBuilderDataProducer) {
                const $result = flagsBEM.buildBlock("<div>", [], "flag");

                languagesRestApi.read()
                    .done(languages => {
                        const flags = mapLanguagesToFlags(languages, flagBuilderDataProducer);
                        $result.append(flags);
                    })
                    .fail(() => console.log(texts.error.loadFailed));

                $result.setActive = function (langCode) {
                    const $flags = $(this);

                    const activeClass = "imcms-flag--active";
                    const languageFlagClass = "imcms-flag--" + langCode;

                    $flags.find("." + activeClass).removeClass(activeClass);
                    $flags.find("." + languageFlagClass).addClass(activeClass);
                };

                $result.hideLangFlagsAndCheckbox = addDisplayMode({
                    "display": "none"
                });

                $result.showLangFlagsAndCheckbox = addDisplayMode({
                    "display": "block"
                });

                return $result;
            }
        };
    }
);
