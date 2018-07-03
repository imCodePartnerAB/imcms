Imcms.define("imcms-life-cycle-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-users-rest-api", "imcms", "imcms-i18n-texts",
        "imcms-date-time-validator", "imcms-page-info-tab"
    ],
    function (BEM, components, usersRestApi, imcms, texts, dateTimeValidator, PageInfoTab) {

        texts = texts.pageInfo.lifeCycle;

        var lifeCycleInnerStructureBEM = new BEM({
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
            var lastIndex = items.length - 1;
            var blockElements = [{"title": $title}].concat(items.map(function ($item, index) {
                return {
                    "item": $item,
                    modifiers: (lastIndex === index) ? itemModifiers.concat("margin-l") : itemModifiers
                }
            }));

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
            var $title = components.texts.titleText("<div>", containerData.title),
                $date = components.dateTime.datePickerCalendar({title: containerData.dateTitle}),
                $time = components.dateTime.timePickerClock({title: containerData.timeTitle}),
                $setDateTimeNowBtn = components.buttons.neutralButton({
                    text: texts.now,
                    click: function () {
                        $date.setCurrentDate();
                        $time.setCurrentTime();

                        var date = $date.getDate().split("-");

                        if (!dateTimeValidator.isPublishedDateBeforePublicationEndDate($date, date)) {
                            cleanUpDateAndTime($date, $time);
                        }
                    }
                }),
                $setDateTimeNowContainer = components.buttons.buttonsContainer("<div>", [$setDateTimeNowBtn]),
                $clearDateTimeBtn = components.buttons.neutralButton({
                    text: texts.clear,
                    click: function () {
                        cleanUpDateAndTime($date, $time);
                    }
                }),
                $clearDateTimeContainer = components.buttons.buttonsContainer("<div>", [$clearDateTimeBtn]),
                $dateTime = components.dateTime.dateTimeReadOnly({title: containerData.savedDateTimeTitle})
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

        function buildArchivedDateTimeContainer() {
            return buildDateTimeContainer({
                title: texts.archived.title,
                dateTitle: texts.archived.dateTitle,
                timeTitle: texts.archived.timeTitle,
                savedDateTimeTitle: texts.archived.dateTimeTitle,
                dataTitle: statusRowsNames[1]
            });
        }

        function buildPublishEndDateTimeContainer() {
            return buildDateTimeContainer({
                title: texts.publicationEnd.title,
                dateTitle: texts.publicationEnd.dateTitle,
                timeTitle: texts.publicationEnd.timeTitle,
                savedDateTimeTitle: texts.publicationEnd.dateTimeTitle,
                dataTitle: statusRowsNames[2]
            });
        }

        function buildPublisherSelectRow() {
            tabData.$publisherSelect = components.selects.imcmsSelect("<div>", {
                id: "doc-publisher",
                text: texts.publisher,
                name: "publisher"
            });

            usersRestApi.getAllAdmins().done(function (users) {
                var usersDataMapped = users.map(function (user) {
                    return {
                        text: user.loginName,
                        "data-value": user.id
                    }
                });

                components.selects.addOptionsToSelect(usersDataMapped, tabData.$publisherSelect);
            });// todo receive users with specific role admin

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$publisherSelect}]);
        }

        function buildCurrentVersionRow() {
            var $currentVersionRowTitle = components.texts.titleText("<div>", texts.currentVersion),
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
            var $offlineVersionInfo = components.texts.infoText("<div>", texts.versionHasChanges);
            var $nextVersionIndex = components.texts.infoText("<span>", "", {id: "document-next-version"});

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

        var statusRowsNames = [
            "published",
            "archived",
            "publicationEnd"
        ];

        var LifeCycleTab = function (name) {
            PageInfoTab.call(this, name);
        };

        LifeCycleTab.prototype = Object.create(PageInfoTab.prototype);

        LifeCycleTab.prototype.isDocumentTypeSupported = function () {
            return true; // all supported
        };
        LifeCycleTab.prototype.tabElementsFactory = function () {
            return [
                buildDocStatusSelect(),
                buildPublishedDateTimeContainer(),
                buildArchivedDateTimeContainer(),
                buildPublishEndDateTimeContainer(),
                buildPublisherSelectRow(),
                buildCurrentVersionRow(),
                buildDocVersionsInfoRow()
            ];
        };
        LifeCycleTab.prototype.fillTabDataFromDocument = function (document) {
            var displayRule = ((document.id === imcms.document.id) && imcms.document.hasNewerVersion
                && imcms.isVersioningAllowed) ? "block" : "none";

            tabData.$currentVersionRowBlock.css("display", imcms.isVersioningAllowed ? "block" : "none");
            tabData.$hasNewerVersionInfoBlock.css("display", displayRule);
            tabData.$savingVersionInfo.find("#document-next-version").html(+document.currentVersion.id + 1);
            tabData.$docStatusSelect.selectValue(document.publicationStatus);

            statusRowsNames.forEach(function (rowName) {
                setStatusInfoRowDataFromDocument(rowName, document);
            });

            tabData.$publisherSelect.selectValue(document.published.id);

            tabData.$currentVersionNumber.setValue(document.currentVersion.id);
            tabData.$docVersionSaveDateTime.setDate(document.currentVersion.date)
                .setTime(document.currentVersion.time);
        };
        LifeCycleTab.prototype.saveData = function (documentDTO) {
            documentDTO.publicationStatus = tabData.$docStatusSelect.getSelectedValue();

            statusRowsNames.forEach(function (rowName) {
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
        LifeCycleTab.prototype.clearTabData = function () {
            var emptyString = '';

            tabData.$docStatusSelect.selectFirst();

            statusRowsNames.forEach(function (rowName) {
                setStatusInfoRowData(rowName, emptyString, emptyString);
            });

            tabData.$publisherSelect.selectFirst();

            tabData.$currentVersionNumber.setValue(emptyString);
            tabData.$docVersionSaveDateTime.setDate(emptyString).setTime(emptyString);
            tabData.$hasNewerVersionInfoBlock.css("display", "none");
        };

        return new LifeCycleTab(texts.name);
    }
);
