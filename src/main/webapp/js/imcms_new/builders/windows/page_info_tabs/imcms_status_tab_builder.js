Imcms.define("imcms-status-tab-builder",
    [
        "imcms-date-picker", "imcms-time-picker", "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts",
        "imcms-page-info-tab-form-builder", "jquery"
    ],
    function (DatePicker, TimePicker, BEM, components, texts, tabContentBuilder, $) {

        texts = texts.pageInfo.status;

        var tabData = {};

        function buildRowBlock($dateTimeField, $doneByInput) {
            $dateTimeField.modifiers = ["col-3", "float-l"];

            var $doneBy = buildLabelWithInputs(texts.by, [$doneByInput]);
            $doneBy.modifiers = ["col-2-3", "float-l"];

            return new BEM({
                block: "imcms-field",
                elements: {
                    "item": [$dateTimeField, $doneBy]
                }
            }).buildBlockStructure("<div>");
        }

        function buildLabelWithInputs(title, inputFields) {
            var $label = $("<div>", {
                "class": "imcms-label",
                text: title
            });

            var inputs = inputFields.map(function ($input) {
                $input.modifiers = ["float-l"];
                return $input;
            });

            return new BEM({
                block: "imcms-item",
                elements: {
                    "label": $label,
                    "input": inputs
                }
            }).buildBlockStructure("<div>");
        }

        function saveStatusInfoRowData(rowName, $dateBlock, $timeBlock, $doneByBlock) {
            tabData["$" + rowName + "Date"] = $dateBlock;
            tabData["$" + rowName + "Time"] = $timeBlock;
            tabData["$" + rowName + "By"] = $doneByBlock;
        }

        function buildStatusInfoRow(statusTab) {
            var $dateBlock = components.dateTime.dateBoxReadOnly({id: statusTab.dataTitle + "Date"});
            var $timeBlock = components.dateTime.timeBoxReadOnly({id: statusTab.dataTitle + "Time"});
            var $doneByBlock = components.texts.textBox("<div>", {
                id: statusTab.dataTitle + "By",
                readonly: "readonly"
            });

            saveStatusInfoRowData(statusTab.dataTitle, $dateBlock, $timeBlock, $doneByBlock);

            var $dateTimeField = buildLabelWithInputs(statusTab.title, [$dateBlock, $timeBlock]);
            return buildRowBlock($dateTimeField, $doneByBlock);
        }

        function setStatusInfoRowDataFromDocument(rowName, document) {
            setStatusInfoRowData(rowName, document[rowName].date, document[rowName].time, document[rowName].by);
        }

        function setStatusInfoRowData(rowName, date, time, by) {
            tabData["$" + rowName + "Date"].setDate(date);
            tabData["$" + rowName + "Time"].setTime(time);
            tabData["$" + rowName + "By"].setValue(by);
        }

        var statusTabs = [
            {title: texts.created, dataTitle: "created"},
            {title: texts.modified, dataTitle: "modified"},
            {title: texts.archived, dataTitle: "archived"},
            {title: texts.published, dataTitle: "published"},
            {title: texts.publicationEnd, dataTitle: "publicationEnd"}
        ];

        return {
            name: texts.name,
            tabIndex: null,
            isDocumentTypeSupported: function () {
                return true; // all supported
            },
            showTab: function () {
                tabContentBuilder.showTab(this.tabIndex);
            },
            hideTab: function () {
                tabContentBuilder.hideTab(this.tabIndex);
            },
            buildTab: function (index) {
                this.tabIndex = index;
                return tabContentBuilder.buildFormBlock(statusTabs.map(buildStatusInfoRow), index);
            },
            fillTabDataFromDocument: function (document) {
                statusTabs.forEach(function (statusTab) {
                    setStatusInfoRowDataFromDocument(statusTab.dataTitle, document);
                });
            },
            saveData: function (documentDTO) {
                return documentDTO; // this tab have read-only data, at least yet
            },
            clearTabData: function () {
                var emptyString = '';
                statusTabs.forEach(function (statusTab) {
                    setStatusInfoRowData(statusTab.dataTitle, emptyString, emptyString, emptyString);
                });
            }
        };
    }
);
