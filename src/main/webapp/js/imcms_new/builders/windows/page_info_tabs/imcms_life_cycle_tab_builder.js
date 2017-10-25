Imcms.define("imcms-life-cycle-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-users-rest-api",
        "imcms-page-info-tabs-linker"
    ],
    function (BEM, components, usersRestApi, linker) {

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

        function onTimeNowButtonClick() {
            console.log("%c Not implemented feature: set time.", "color: red;")
        }

        function onTimeClearButtonClick() {
            console.log("%c Not implemented feature: clear time.", "color: red;")
        }

        function buildDocStatusSelect() {
            tabData.$docStatusSelect = components.selects.imcmsSelect("<div>", {
                id: "doc-status",
                text: "Status",
                name: "status"
            }, [{
                text: "In Process",
                "data-value": "0"
            }, {
                text: "Disapproved",
                "data-value": "1"
            }, {
                text: "Approved",
                "data-value": "2"
            }]);

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$docStatusSelect}]);
        }

        function buildDateTimeContainerBlock($title, items) {
            var blockElements = [{"title": $title}].concat(items.map(function ($item) {
                return {
                    "item": $item,
                    modifiers: itemModifiers
                }
            }));

            return lifeCycleInnerStructureBEM.buildBlock("<div>", blockElements);
        }

        function saveDateTimeContainerData(dataTitle, $time, $date, $dateTime) {
            tabData["$" + dataTitle + "Time"] = $time;
            tabData["$" + dataTitle + "Date"] = $date;
            tabData["$" + dataTitle + "DateTime"] = $dateTime;
        }

        function buildDateTimeContainer(containerData) {
            var $title = components.texts.titleText("<div>", containerData.title),
                $date = components.dateTime.datePickerCalendar({title: containerData.dateTitle}),
                $time = components.dateTime.timePickerClock({title: containerData.timeTitle}),
                $setDateTimeNowBtn = components.buttons.neutralButton({
                    text: "Now",
                    click: onTimeNowButtonClick
                }),
                $setDateTimeNowContainer = components.buttons.buttonsContainer("<div>", [$setDateTimeNowBtn]),
                $dateTime = components.dateTime.dateTimeReadOnly({title: containerData.savedDateTimeTitle}),
                $clearDateTimeBtn = components.buttons.neutralButton({
                    text: "Clear",
                    click: onTimeClearButtonClick
                }),
                $clearDateTimeContainer = components.buttons.buttonsContainer("<div>", [$clearDateTimeBtn])
            ;

            saveDateTimeContainerData(containerData.dataTitle, $time, $date, $dateTime);

            return buildDateTimeContainerBlock($title,
                [$date, $time, $setDateTimeNowContainer, $dateTime, $clearDateTimeContainer]
            );
        }

        function buildPublishedDateTimeContainer() {
            return buildDateTimeContainer({
                title: "Published",
                dateTitle: "Set published date",
                timeTitle: "Set published time",
                savedDateTimeTitle: "Saved publish date-time",
                dataTitle: statusRowsNames[0]
            });
        }

        function buildArchivedDateTimeContainer() {
            return buildDateTimeContainer({
                title: "Archived",
                dateTitle: "Set archived date",
                timeTitle: "Set archived time",
                savedDateTimeTitle: "Saved archived date-time",
                dataTitle: statusRowsNames[1]
            });
        }

        function buildPublishEndDateTimeContainer() {
            return buildDateTimeContainer({
                title: "Publication end",
                dateTitle: "Set publication end date",
                timeTitle: "Set publication end time",
                savedDateTimeTitle: "Saved publication end date-time",
                dataTitle: statusRowsNames[2]
            });
        }

        function buildPublisherSelectRow() {
            tabData.$publisherSelect = components.selects.imcmsSelect("<div>", {
                id: "doc-publisher",
                text: "Publisher",
                name: "publisher"
            });

            usersRestApi.read(null).done(function (users) {
                var usersDataMapped = users.map(function (user) {
                    return {
                        text: user.username,
                        "data-value": user.id
                    }
                });

                components.selects.addOptionsToSelect(usersDataMapped, tabData.$publisherSelect);
            });// todo receive users with specific role admin

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$publisherSelect}]);
        }

        function buildLanguagesContainer() {
            var $languagesTitle = components.texts.titleText("<div>", "If requested language is missing:");

            tabData.$showDefaultLang = components.radios.imcmsRadio("<div>", {
                text: "Show in default language if enabled",
                name: "langSetting",
                value: "SHOW_DEFAULT",
                checked: "checked" // default value
            });
            tabData.$doNotShow = components.radios.imcmsRadio("<div>", {
                text: "Don't show at all",
                name: "langSetting",
                value: "DO_NOT_SHOW"
            });

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [
                {"title": $languagesTitle},
                {"item": tabData.$showDefaultLang},
                {"item": tabData.$doNotShow}
            ]);
        }

        function buildCurrentVersionRow() {
            var $currentVersionRowTitle = components.texts.titleText("<div>", "Current version:"),
                $docVersionSaveDateTime = components.dateTime.dateTimeReadOnly();

            tabData.$currentVersionNumber = components.texts.textBox("<div>", {
                readonly: "readonly",
                value: "0"
            });
            tabData.$docVersionSaveDateTime = $docVersionSaveDateTime;

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [
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
            // todo implement appearance logic for this text
            var $offlineVersionInfo = components.texts.infoText("<div>", "This offline version has changes."),
                $savingVersionInfo = components.texts.infoText("<div>",
                    "Please press \"Save and publish this version\" to publish as: version 0000.", {
                        id: "save-as-new-version-message"
                    })
            ;
            return lifeCycleInnerStructureBEM.buildBlock("<div>", [
                {"item": $offlineVersionInfo},
                {"item": $savingVersionInfo}
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

        return {
            name: "life cycle",
            buildTab: function (index) {
                return linker.buildFormBlock([
                    buildDocStatusSelect(),
                    buildPublishedDateTimeContainer(),
                    buildArchivedDateTimeContainer(),
                    buildPublishEndDateTimeContainer(),
                    buildPublisherSelectRow(),
                    buildLanguagesContainer(),
                    buildCurrentVersionRow(),
                    buildDocVersionsInfoRow()
                ], index);
            },
            fillTabDataFromDocument: function (document) {
                tabData.$docStatusSelect.selectValue(document.publicationStatus);

                statusRowsNames.forEach(function (rowName) {
                    setStatusInfoRowDataFromDocument(rowName, document);
                });

                tabData.$publisherSelect.selectValue(document.published.id);

                components.radios.group(tabData.$showDefaultLang, tabData.$doNotShow)
                    .checkAmongGroup(document.disabledLanguageShowMode);

                tabData.$currentVersionNumber.setValue(document.currentVersion.id);
                tabData.$docVersionSaveDateTime.setDate(document.currentVersion.date).setTime(document.currentVersion.time);
            },
            clearTabData: function () {
                var emptyString = '';

                tabData.$docStatusSelect.selectFirst();

                statusRowsNames.forEach(function (rowName) {
                    setStatusInfoRowData(rowName, emptyString, emptyString);
                });

                tabData.$publisherSelect.selectFirst();
                tabData.$showDefaultLang.setChecked(true); //default value

                tabData.$currentVersionNumber.setValue(emptyString);
                tabData.$docVersionSaveDateTime.setDate(emptyString).setTime(emptyString);

            }
        };
    }
);
