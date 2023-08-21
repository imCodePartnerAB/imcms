define("imcms-status-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery", "imcms-document-status", "imcms-page-info-tab"
    ],
    function (BEM, components, texts, $, docStatus, PageInfoTab) {

        texts = texts.pageInfo.status;

        let $currentStatus;
        const tabData = {};

        function buildCurrentStatusInfoRow(){
            $currentStatus = components.texts.infoText('<div>');

            return new BEM({
                block: "imcms-field",
                elements: {
                    "current-status": $currentStatus
                }
            }).buildBlockStructure("<div>");
        }

        function setCurrentStatus(document){
            const status = docStatus.getDocumentStatusTexts(document.documentStatus, document.published.date).tooltip;

            $currentStatus.text(status);
        }

        function buildRowBlock($dateTimeField, $doneByInput) {
            $dateTimeField.modifiers = ["col-3", "float-l"];

            const $doneBy = buildLabelWithInputs(texts.by, [$doneByInput]);
            $doneBy.modifiers = ["col-2-3", "float-l"];

            return new BEM({
                block: "imcms-field",
                elements: {
                    "item": [$dateTimeField, $doneBy]
                }
            }).buildBlockStructure("<div>");
        }

        function buildLabelWithInputs(title, inputFields) {
            const $label = $("<div>", {
                "class": "imcms-label",
                text: title
            });

            const inputs = inputFields.map($input => {
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
            const $dateBlock = components.dateTime.dateBoxReadOnly({id: statusTab.dataTitle + "Date"});
            const $timeBlock = components.dateTime.timeBoxReadOnly({id: statusTab.dataTitle + "Time"});
            const $doneByBlock = components.texts.textBox("<div>", {
                id: statusTab.dataTitle + "By",
                readonly: "readonly"
            });

            saveStatusInfoRowData(statusTab.dataTitle, $dateBlock, $timeBlock, $doneByBlock);

            const $dateTimeField = buildLabelWithInputs(statusTab.title, [$dateBlock, $timeBlock]).addClass("date-time-status-block");
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

        const statusRows = [
            {title: texts.created, dataTitle: "created"},
            {title: texts.modified, dataTitle: "modified"},
            {title: texts.archived, dataTitle: "archived"},
            {title: texts.published, dataTitle: "published"},
            {title: texts.publicationEnd, dataTitle: "publicationEnd"}
        ];

        const StatusTab = function (name) {
            PageInfoTab.call(this, name);
        };

        StatusTab.prototype = Object.create(PageInfoTab.prototype);

        StatusTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };

        StatusTab.prototype.tabElementsFactory = () => {
            const tabElements = [];
            tabElements.push(buildCurrentStatusInfoRow());
            statusRows.forEach(statusRow => tabElements.push(buildStatusInfoRow(statusRow)));

            return tabElements;
        }

        StatusTab.prototype.fillTabDataFromDocument = document => {
            setCurrentStatus(document);
            statusRows.forEach(statusTab => {
                setStatusInfoRowDataFromDocument(statusTab.dataTitle, document);
            });
        };

        StatusTab.prototype.clearTabData = () => {
            const emptyString = '';
            statusRows.forEach(statusTab => {
                setStatusInfoRowData(statusTab.dataTitle, emptyString, emptyString, emptyString);
            });
        };

        StatusTab.prototype.getDocLink = () => texts.documentationLink;

        return new StatusTab(texts.name);
    }
);
