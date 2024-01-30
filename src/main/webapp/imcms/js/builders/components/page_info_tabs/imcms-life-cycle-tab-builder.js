define("imcms-life-cycle-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-users-rest-api", "imcms", "imcms-i18n-texts",
        "imcms-date-time-validator", "imcms-document-types", "imcms-page-info-tab", "imcms-modal-window-builder"
    ],
    function (BEM, components, usersRestApi, imcms, texts, dateTimeValidator, docTypes, PageInfoTab, modal) {

        texts = texts.pageInfo.lifeCycle;
        const WORKING_VERSION = 0;

        const lifeCycleInnerStructureBEM = new BEM({
                block: "imcms-field",
                elements: {
                    "select": "imcms-select",
                    "title": "imcms-title",
                    "item": ""
                }
            }),
            itemModifiers = ["float-l"],
            tabData = {}
        ;

        function buildDocStatusSelect() {
            tabData.$docStatusSelect = components.selects.imcmsSelect("<div>", {
                id: "doc-status",
                text: texts.status.title,
                name: "status"
            }, [{
                text: texts.status.inProcess,
                "data-value": "NEW"
            }, {
                text: texts.status.disapproved,
                "data-value": "DISAPPROVED"
            }, {
                text: texts.status.approved,
                "data-value": "APPROVED"
            }]);

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$docStatusSelect}]);
        }

        function buildDateTimeContainerBlock($title, items) {
            const lastIndex = items.length - 1;
            const blockElements = [{"title": $title}].concat(items.map(($item, index) => ({
                "item": $item,
                modifiers: (lastIndex === index) ? itemModifiers.concat("margin-l") : itemModifiers
            })));

            return lifeCycleInnerStructureBEM.buildBlock("<div>", blockElements);
        }

        function saveDateTimeContainerData(dataTitle, $time, $date, $dateTime) {
            tabData["$" + dataTitle + "Time"] = $time;
            tabData["$" + dataTitle + "Date"] = $date;
            tabData["$" + dataTitle + "DateTime"] = $dateTime;
        }

        function cleanUpDateAndTime($date, $time) {
            $date.setDate('');
            $time.setTime('');
        }

        function buildDateTimeContainer(containerData) {
            const $title = components.texts.titleText("<div>", containerData.title),
                $date = components.dateTime.datePickerCalendar({title: containerData.dateTitle}),
                $time = components.dateTime.timePickerClock({title: containerData.timeTitle}),
                $setDateTimeNowBtn = components.buttons.neutralButton({
                    text: texts.now,
                    click: () => {
                        $date.setCurrentDate();
                        $time.setCurrentTime();
                    }
                }),
                $setDateTimeNowContainer = components.buttons.buttonsContainer("<div>", [$setDateTimeNowBtn]),
                $clearDateTimeBtn = components.buttons.neutralButton({
                    text: texts.clear,
                    click: () => {
                        cleanUpDateAndTime($date, $time);
                    }
                }),
                $clearDateTimeContainer = components.buttons.buttonsContainer("<div>", [$clearDateTimeBtn]),
                $dateTime = components.dateTime.dateTimeReadOnly({title: containerData.savedDateTimeTitle}).addClass("saved-date-life-cycle");
            ;

            saveDateTimeContainerData(containerData.dataTitle, $time, $date, $dateTime);

            return buildDateTimeContainerBlock($title,
                [$date, $time, $setDateTimeNowContainer, $clearDateTimeContainer, $dateTime]
            );
        }

        function buildPublishedDateTimeContainer() {
            return buildDateTimeContainer({
                title: texts.published.title,
                dateTitle: texts.published.dateTitle,
                timeTitle: texts.published.timeTitle,
                savedDateTimeTitle: texts.published.dateTimeTitle,
                dataTitle: statusRowsNames[0]
            });
        }

        function buildModifiedDateTimeContainer() {
            return buildDateTimeContainer({
                title: texts.modified.title,
                dateTitle: texts.modified.dateTitle,
                timeTitle: texts.modified.timeTitle,
                savedDateTimeTitle: texts.modified.dateTimeTitle,
                dataTitle: statusRowsNames[1]
            });
        }

        function buildArchivedDateTimeContainer() {
            return buildDateTimeContainer({
                title: texts.archived.title,
                dateTitle: texts.archived.dateTitle,
                timeTitle: texts.archived.timeTitle,
                savedDateTimeTitle: texts.archived.dateTimeTitle,
                dataTitle: statusRowsNames[2]
            });
        }

        function buildPublishEndDateTimeContainer() {
            return buildDateTimeContainer({
                title: texts.publicationEnd.title,
                dateTitle: texts.publicationEnd.dateTitle,
                timeTitle: texts.publicationEnd.timeTitle,
                savedDateTimeTitle: texts.publicationEnd.dateTimeTitle,
                dataTitle: statusRowsNames[3]
            });
        }

        function buildPublisherSelectRow() {
            tabData.$publisherSelect = components.selects.imcmsSelect("<div>", {
                id: "doc-publisher",
                text: texts.publisher,
                name: "publisher"
            });

            usersRestApi.getAllAdmins()
                .done(users => {
                    const usersDataMapped = users.map(user => ({
                        text: user.login,
                        "data-value": user.id
                    }));
                    components.selects.addOptionsToSelect(usersDataMapped, tabData.$publisherSelect);
                })
                .fail(() => modal.buildErrorWindow(texts.error.userLoadFailed)); // todo receive users with specific role admin

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$publisherSelect}]);
        }

        function buildCurrentVersionRow() {
            const $currentVersionRowTitle = components.texts.titleText("<div>", texts.currentVersion),
                $docVersionSaveDateTime = components.dateTime.dateTimeReadOnly();

            tabData.$currentVersionNumber = components.texts.textBox("<div>", {
                readonly: "readonly",
                value: "0"
            });
            tabData.$docVersionSaveDateTime = $docVersionSaveDateTime;

            return tabData.$currentVersionRowBlock = lifeCycleInnerStructureBEM.buildBlock("<div>", [
                {"title": $currentVersionRowTitle},
                {
                    "item": tabData.$currentVersionNumber,
                    modifiers: itemModifiers.concat("short")
                }, {
                    "item": $docVersionSaveDateTime,
                    modifiers: itemModifiers
                }
            ]);
        }

        function buildDocVersionsInfoRow() {
            const $offlineVersionInfo = components.texts.infoText("<div>", texts.versionHasChanges);
            const $nextVersionIndex = components.texts.infoText("<span>", "", {id: "document-next-version"});

            tabData.$offlineVersionInfo = $offlineVersionInfo;
            tabData.$savingVersionInfo = components.texts.infoText("<div>", texts.publishMessage)
                .append($nextVersionIndex);

            return tabData.$hasNewerVersionInfoBlock = lifeCycleInnerStructureBEM.buildBlock("<div>", [
                {"item": $offlineVersionInfo},
                {"item": tabData.$savingVersionInfo}
            ]);
        }

        function setStatusInfoRowDataFromDocument(rowName, document) {
            setStatusInfoRowData(rowName, document[rowName].date, document[rowName].time);
        }

        function setStatusInfoRowData(rowName, date, time) {
            tabData["$" + rowName + "Date"].setDate(date);
            tabData["$" + rowName + "Time"].setTime(time);
            tabData["$" + rowName + "DateTime"].setDate(date).setTime(time);
        }

        function setStatusEditorRowData(rowName, date, time){
            tabData["$" + rowName + "Date"].setDate(date);
            tabData["$" + rowName + "Time"].setTime(time);
        }

        var statusRowsNames = [
            "published",
            "modified",
            "archived",
            "publicationEnd"
        ];

        const LifeCycleTab = function (name) {
            PageInfoTab.call(this, name);
        };

        LifeCycleTab.prototype = Object.create(PageInfoTab.prototype);

        LifeCycleTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };
        LifeCycleTab.prototype.tabElementsFactory = () => [
            buildDocStatusSelect(),
            buildPublishedDateTimeContainer(),
            buildModifiedDateTimeContainer(),
            buildArchivedDateTimeContainer(),
            buildPublishEndDateTimeContainer(),
            buildPublisherSelectRow(),
            buildCurrentVersionRow(),
            buildDocVersionsInfoRow()
        ];
        LifeCycleTab.prototype.fillTabDataFromDocument = document => {
            const changesDisplayRule = imcms.isVersioningAllowed && document.currentVersion.id === WORKING_VERSION;
            const saveAndPublishDisplayRule = changesDisplayRule &&
                    ((document.id === imcms.document.id && document.type === docTypes.TEXT) || document.type !== docTypes.TEXT);

            tabData.$savingVersionInfo.find("#document-next-version").html(+document.latestVersion.id + 1);
            tabData.$hasNewerVersionInfoBlock.css("display", saveAndPublishDisplayRule || changesDisplayRule ? "block" : "none");
            tabData.$savingVersionInfo.css("display", saveAndPublishDisplayRule ? "block" : "none");
            tabData.$offlineVersionInfo.css("display", changesDisplayRule ? "block" : "none");

            tabData.$currentVersionRowBlock.css("display", imcms.isVersioningAllowed ? "block" : "none");
            tabData.$docStatusSelect.selectValue(document.publicationStatus);

            statusRowsNames.forEach(rowName => {
                setStatusInfoRowDataFromDocument(rowName, document);
            });
            // modified date -- remove the date from editor fields, but show the current value (to notice changes in fields)
            setStatusEditorRowData(statusRowsNames[1], '', '');

            tabData.$publisherSelect.selectValue(document.published.id);

            tabData.$currentVersionNumber.setValue(document.latestVersion.id);
            tabData.$docVersionSaveDateTime.setDate(document.latestVersion.date)
                .setTime(document.latestVersion.time);
        };
        LifeCycleTab.prototype.saveData = documentDTO => {
            documentDTO.publicationStatus = tabData.$docStatusSelect.getSelectedValue();

            statusRowsNames.forEach(rowName => {
                documentDTO[rowName].by = null;
                documentDTO[rowName].date = tabData["$" + rowName + "Date"].getDate() || null;
                documentDTO[rowName].time = tabData["$" + rowName + "Time"].getTime() || null;

                if (!documentDTO[rowName].date || !documentDTO[rowName].time) {
                    documentDTO[rowName].id = null;
                }
            });

            documentDTO.published.id = tabData.$publisherSelect.getSelectedValue();

            return documentDTO;
        };
        LifeCycleTab.prototype.clearTabData = () => {
            const emptyString = '';

            tabData.$docStatusSelect.selectFirst();

            statusRowsNames.forEach(rowName => {
                setStatusInfoRowData(rowName, emptyString, emptyString);
            });

            tabData.$publisherSelect.selectFirst();

            tabData.$currentVersionNumber.setValue(emptyString);
            tabData.$docVersionSaveDateTime.setDate(emptyString).setTime(emptyString);
            tabData.$hasNewerVersionInfoBlock.css("display", "none");
        };

        LifeCycleTab.prototype.getDocLink = () => texts.documentationLink;

        return new LifeCycleTab(texts.name);
    }
);
