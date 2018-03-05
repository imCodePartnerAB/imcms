/** @namespace tinyMCE.activeEditor.setContent */
/** @namespace tinyMCE.activeEditor.setDirty */
/**
 * Text history window builder in text editor.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.01.18
 */
Imcms.define("imcms-text-history-window-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-i18n-texts",
        "imcms-texts-history-rest-api", "tinyMCE", "imcms-events"
    ],
    function (WindowBuilder, BEM, components, $, texts, textsHistoryRestAPI, tinyMCE, events) {

        var $historyListContainer, $textHistoryView;
        texts = texts.textHistory;

        function onWriteToTextField() {
            var textButton = textHistoryWindowBuilder.$editor.find(".view-text-button")[0];
            viewText.call(textButton);

            tinyMCE.activeEditor.setContent($textHistoryView.html());
            tinyMCE.activeEditor.setDirty(true);
            textHistoryWindowBuilder.closeWindow();
        }

        function onCancel() {
            textHistoryWindowBuilder.closeWindow();
        }

        function buildFooter() {
            return textHistoryWindowBuilder.buildFooter([
                components.buttons.negativeButton({
                    text: texts.cancel,
                    "class": "imcms-text-history-cancel",
                    click: onCancel
                }),
                components.buttons.saveButton({
                    text: texts.writeToText,
                    "style": "display: none;",
                    click: onWriteToTextField
                })
            ]);
        }

        function isClickAllowed(element) {
            var $element = $(element);

            if ($element.hasClass("imcms-button--disabled")) {
                return false;
            }

            textHistoryWindowBuilder.$editor.find(".imcms-button--disabled")
                .removeClass("imcms-button--disabled");

            $element.addClass("imcms-button--disabled");

            return true;
        }

        function viewSource() {
            isClickAllowed(this) && $textHistoryView.text($textHistoryView.html());
        }

        function viewText() {
            isClickAllowed(this) && $textHistoryView.html($textHistoryView.text());
        }

        function buildTextHistoryControlButtons() {
            return components.buttons.buttonsContainer("<div>", [
                components.buttons.negativeButton({
                    "class": "view-source-button",
                    "style": "display: none;",
                    text: texts.viewSource,
                    click: viewSource
                }),
                components.buttons.negativeButton({
                    "class": "view-text-button",
                    "style": "display: none;",
                    text: texts.viewText,
                    click: viewText
                })
            ]);
        }

        function buildHistoryView() {
            return new BEM({
                block: "imcms-right-side",
                elements: {
                    "text-history-view": $textHistoryView = $("<div>"),
                    "buttons": buildTextHistoryControlButtons()
                }
            }).buildBlockStructure("<div>");
        }

        function buildHistoryListContainer() {
            return $("<div>", {"class": "imcms-left-side text-history-list"});
        }

        function buildTextHistory() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": textHistoryWindowBuilder.buildHead(texts.title),
                    "left-side": $historyListContainer = buildHistoryListContainer(),
                    "right-side": buildHistoryView(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "text-history"});
        }

        function buildTextHistoriesForDateContainer(date) {
            var $date = $("<div>", {
                "class": "text-history-date",
                text: date
            });

            var $separator = $("<div>", {"class": "text-history-date-separator"});

            return new BEM({
                block: "text-history-date-container",
                elements: {
                    "date": $date,
                    "separator": $separator
                }
            }).buildBlockStructure("<div>");
        }

        function showTextHistoryUnit(text) {
            $textHistoryView.html(text);
        }

        function onTextHistoryUnitClicked(unit) {
            $(".text-history-unit").removeClass("text-history-date-unit__unit--active");
            $(unit).addClass("text-history-date-unit__unit--active");

            textHistoryWindowBuilder.$editor.find(".imcms-buttons")
                .find(".imcms-button")
                .css("display", "block");

            textHistoryWindowBuilder.$editor.find(".imcms-button--disabled")
                .removeClass("imcms-button--disabled");

            textHistoryWindowBuilder.$editor.find(".view-text-button")
                .addClass("imcms-button--disabled");
        }

        function buildTextHistoriesForDate(textHistoriesForDate) {
            /** @namespace textHistory.modifiedBy.username */
            return textHistoriesForDate.map(function (textHistory) {
                var $textHistoryUnit = $("<div>", {
                    "class": "text-history-unit",
                    text: textHistory.modified.time + " | " + textHistory.modifiedBy.username,
                    click: function () {
                        onTextHistoryUnitClicked(this);
                        showTextHistoryUnit(textHistory.text);
                    }
                });

                return {"unit": $textHistoryUnit};
            });
        }

        function buildTextHistoryUnit(date, textHistoriesForDate) {
            var elements = [{"date": buildTextHistoriesForDateContainer(date)}];

            new BEM({
                block: "text-history-date-unit",
                elements: elements.concat(buildTextHistoriesForDate(textHistoriesForDate))
            }).buildBlockStructure("<div>").appendTo($historyListContainer);
        }

        function loadData(textDTO) {
            textsHistoryRestAPI.read(textDTO).done(function (textsHistory) {
                var dateToTextHistoryUnits = {};

                textsHistory.forEach(function (textHistory) {
                    var date = textHistory.modified.date;
                    dateToTextHistoryUnits[date] = (dateToTextHistoryUnits[date] || []);
                    dateToTextHistoryUnits[date].push(textHistory);
                });

                $.each(dateToTextHistoryUnits, buildTextHistoryUnit);

                $historyListContainer.find(".text-history-date-unit__unit")
                    .first()
                    .click();
            });
        }

        function clearData() {
            events.trigger("enable text editor blur");
            $historyListContainer.empty();
            $textHistoryView.html('');
            textHistoryWindowBuilder.$editor.find(".imcms-buttons")
                .find(".imcms-button")
                .not(".imcms-text-history-cancel")
                .css("display", "none");
        }

        var textHistoryWindowBuilder = new WindowBuilder({
            factory: buildTextHistory,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData
        });

        return {
            buildTextHistory: function (textData) {
                events.trigger("disable text editor blur");
                textHistoryWindowBuilder.buildWindowWithShadow.applyAsync(arguments, textHistoryWindowBuilder);
            }
        };
    }
);
