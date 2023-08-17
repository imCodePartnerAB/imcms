/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 24.07.17.
 */
define("imcms-flags-builder",
    [
        "imcms-bem-builder", "imcms", "jquery", "imcms-i18n-texts", "imcms-overlays-builder",
	    'imcms-selects-builder'
    ],
    function (bemBuilder, imcms, $, texts, overlays,selectsBuilder) {

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

        function onFlagClick(event) {
            const $clickedFlag = $(this);

            if (!$clickedFlag.hasClass(FLAGS_CLASS)) {
                return;
            }

            if ($clickedFlag.hasClass(FLAG_ACTIVE_CLASS)) {
                event.preventDefault();

            } else {
                $clickedFlag.parent().find(`.${FLAG_ACTIVE_CLASS}`).toggleClass(FLAG_ACTIVE_CLASS);
                $clickedFlag.addClass(FLAG_ACTIVE_CLASS);
            }
        }

        function buildFlag(tag, attributes, isActive, language) {
            const modifiers = [language.code];
            if (isActive) {
                modifiers.push("active");
            }

            attributes = attributes || {};

            const $flag = flagsBEM.buildElement("flag", tag, attributes, modifiers).click(onFlagClick);

            overlays.defaultTooltip($flag, language.name + "/" + language.nativeName, {placement: 'bottom'});

            return $flag;
        }

		function buildOption(language) {
			return {
				'data-value': language.code,
				class: `${FLAGS_CLASS} ${FLAGS_CLASS}--${language.code}`,
				text: language.code,
				title: language.name + "/" + language.nativeName
			};
		}

        function mapLanguagesToFlags(languages, flagBuilderDataProducer) {
            return languages.map(language => {
                const flagBuilderData = flagBuilderDataProducer(language),
                    isActive = language.code === imcms.language.code;

                return buildFlag.apply(null, flagBuilderData.concat([isActive, language]));
            });
        }

	    function mapLanguagesToSelectOptions(languages) {
		    return languages.map(language => buildOption(language));
	    }

        function addDisplayMode(displayModeCSS) {
            return function () {
                const $flags = $(this);

                $flags.css(displayModeCSS);
                $flags.next().css(displayModeCSS);
            }
        }

		function buildFlagsSelect($result, languages, flagBuilderDataProducer, currentLanguageCode) {
			const $select = selectsBuilder.imcmsSelect('<div>', {
				class: "imcms-lang-flag--select",
				// do not use currentLanguageCode!!!
				onSelected: (langCode) => {
					// have to simulate language.code behavior in order to execute flagBuilderDataProducer....
					const language = {code: langCode};
					const data = flagBuilderDataProducer(language);

					onFlagSelected.apply($select, data.concat([langCode, $result]))
				}
			}, mapLanguagesToSelectOptions(languages))

			$select.selectValue(currentLanguageCode)
			$select.find(".imcms-drop-down-list__select-item-value")
				   .addClass([FLAGS_CLASS, FLAGS_CLASS + '--' + currentLanguageCode, FLAG_ACTIVE_CLASS])

			return $select;
		}

	    function onFlagSelected(tag, attributes, langCode, $result) {
		    const $chosenValue = this.find(`[data-value=${langCode}]`);
		    const $selectedItemValue = this.find(".imcms-drop-down-list__select-item-value");
		    const classList = $selectedItemValue.attr("class").split(/\s+/);

		   $.each(classList, function (index, className) {
			   if (className.startsWith(FLAGS_CLASS)) {
				   $selectedItemValue
					   .removeClass(className)
					   .addClass([FLAGS_CLASS, FLAGS_CLASS + '--' + langCode, FLAG_ACTIVE_CLASS])
			   }
		   });

		    $result.setActive(langCode)
		    attributes.click.call($chosenValue);
	    }

        return {
            flagsContainer: function (flagBuilderDataProducer) {
	            const currentLanguageCode = imcms.language.code;
                const $result = flagsBEM.buildBlock("<div>", [], {
	                class: "imcms-flags-container"
                });
                $result.setActive = function (langCode) {
                    const $flags = $(this);

                    const activeClass = "imcms-flag--active";
                    const languageFlagClass = "imcms-flag--" + langCode;

                    $flags.find("." + activeClass).removeClass(activeClass);
                    $flags.find("." + languageFlagClass).addClass(activeClass);
                };

                const languages = imcms.availableLanguages;
                if (languages.length > 3) {
                    $result.append(buildFlagsSelect($result, languages, flagBuilderDataProducer, currentLanguageCode));
                } else if (languages.length > 1) {
                    $result.append(mapLanguagesToFlags(languages, flagBuilderDataProducer));
                }
                $result.setActive(currentLanguageCode);

                $result.hideLangFlagsAndCheckbox = addDisplayMode({
                    "display": "none"
                });

                $result.showLangFlagsAndCheckbox = addDisplayMode({
                    "display": "block"
                });

                return $result;
            },
	        onFlagClickReloadWithLangParam: function () {
		        const urlParams = new URLSearchParams(window.location.search);
		        const languageParamName = 'lang';
		        const languageCode = $(this).text();

		        if (urlParams.has(languageParamName)) {
			        urlParams.delete(languageParamName);
		        }
		        urlParams.append(languageParamName, languageCode);

		        location.href = location.origin + location.pathname + '?' + urlParams.toString();
	        }
        };
    }
);
