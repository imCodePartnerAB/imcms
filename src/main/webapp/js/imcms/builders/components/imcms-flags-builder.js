/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-flags-builder",
    ["imcms-bem-builder", "imcms-languages-rest-api", "imcms", "jquery"],
    function (bemBuilder, languagesRestApi, imcms, $) {
        var FLAGS_CLASS = "imcms-flag",
            FLAG_ACTIVE_CLASS = FLAGS_CLASS + "--" + "active"
        ;

        var flagsBEM = new bemBuilder({
            block: "imcms-flags",
            elements: {
                "flag": FLAGS_CLASS
            }
        });

        function changeNeighborFlag($btn) {
            var $neighborFlag = ($btn.next("." + FLAGS_CLASS).length !== 0)
                ? $btn.next("." + FLAGS_CLASS)
                : $btn.prev("." + FLAGS_CLASS);

            $neighborFlag.toggleClass(FLAG_ACTIVE_CLASS);
        }

        function onFlagClick(event) {
            var $clickedFlag = $(this);

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
            var modifiers = [language.code];
            if (isActive) {
                modifiers.push("active");
            }

            attributes = attributes || {};
            attributes.title = language.name + "/" + language.nativeName;

            return flagsBEM.buildElement("flag", tag, attributes, modifiers).click(onFlagClick);
        }

        function mapLanguagesToFlags(languages, flagBuilderDataProducer) {
            return languages.map(language => {
                var flagBuilderData = flagBuilderDataProducer(language),
                    isActive = language.code === imcms.language.code;

                return buildFlag.apply(null, flagBuilderData.concat([isActive, language]));
            });
        }

        function addDisplayMode(displayModeCSS) {
            return function () {
                var $flags = $(this);

                $flags.css(displayModeCSS);
                $flags.next().css(displayModeCSS);
            }
        }

        return {
            flagsContainer: function (flagBuilderDataProducer) {
                var $result = flagsBEM.buildBlock("<div>", [], "flag");

                languagesRestApi.read().done(languages => {
                    var flags = mapLanguagesToFlags(languages, flagBuilderDataProducer);
                    $result.append(flags);
                });

                $result.setActive = function (langCode) {
                    var $flags = $(this);

                    var activeClass = "imcms-flag--active";
                    var languageFlagClass = "imcms-flag--" + langCode;

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
        }
    }
);
