Imcms.define("imcms-life-cycle-tab-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-users-rest-api",
        "imcms-page-info-tab-form-builder"
    ],
    function (BEM, components, usersRestApi, tabContentBuilder) {

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
                text: "Status",
                name: "status"
            }, [{
                text: "In Process",
                "data-value": "NEW"
            }, {
                text: "Disapproved",
                "data-value": "DISAPPROVED"
            }, {
                text: "Approved",
                "data-value": "APPROVED"
            }]);

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [{"select": tabData.$docStatusSelect}]);
        }

        function buildDateTimeContainerBlock($title, items) {
            var blockElements = [{"title": $title}].concat(items.map(function ($item, index) {
                return {
                    "item": $item,
                    modifiers: (items.length - 1) === index ? itemModifiers.concat("margin-l") : itemModifiers
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
                    click: function () {
                        $date.setCurrentDate();
                        $time.setCurrentTime();
                    }
                }),
                $setDateTimeNowContainer = components.buttons.buttonsContainer("<div>", [$setDateTimeNowBtn]),
                $clearDateTimeBtn = components.buttons.neutralButton({
                    text: "Clear",
                    click: function () {
                        $date.setDate('');
                        $time.setTime('');
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
                value: "SHOW_IN_DEFAULT_LANGUAGE",
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
            var $offlineVersionInfo = components.texts.infoText("<div>", "This offline version has changes.");
            var $nextVersionIndex = components.texts.infoText("<span>", "", {id: "document-next-version"});

            tabData.$savingVersionInfo = components.texts.infoText(
                "<div>", "Please press \"Save and publish this version\" to publish as: version "
            ).append($nextVersionIndex);

            return lifeCycleInnerStructureBEM.buildBlock("<div>", [
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

        return {
            name: "life cycle",
            buildTab: function (index) {
                return tabContentBuilder.buildFormBlock([
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
                /** @namespace document.currentVersion */
                /** @namespace document.published */

                tabData.$savingVersionInfo.find("#document-next-version").html(+document.currentVersion.id + 1);

                tabData.$docStatusSelect.selectValue(document.publicationStatus);

                statusRowsNames.forEach(function (rowName) {
                    setStatusInfoRowDataFromDocument(rowName, document);
                });

                tabData.$publisherSelect.selectValue(document.published.id);

                components.radios.group(tabData.$showDefaultLang, tabData.$doNotShow)
                    .checkAmongGroup(document.disabledLanguageShowMode);

                tabData.$currentVersionNumber.setValue(document.currentVersion.id);
                tabData.$docVersionSaveDateTime.setDate(document.currentVersion.date)
                    .setTime(document.currentVersion.time);
            },
            saveData: function (documentDTO) {
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

                documentDTO.disabledLanguageShowMode = components.radios
                    .group(tabData.$showDefaultLang, tabData.$doNotShow)
                    .getCheckedValue();

                return documentDTO;
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
