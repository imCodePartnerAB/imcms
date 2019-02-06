define("imcms-date-time-validator", [], function () {

    const PUBLISHED = "Published",
        PUBLICATION_END = "Publication end",
        defaultTime = ["00", "00"],
        dateClassSelector = ".imcms-current-date__input",
        timeClassSelector = ".imcms-current-time__input";

    function isPublishedDateBeforePublicationEndDate($from, preExistingValue) {

        const dateTimeTitle = getDateTimeTitle($from);

        if ((dateTimeTitle !== PUBLISHED && dateTimeTitle !== PUBLICATION_END)) {
            return true;
        }

        let preExistingDate,
            preExistingTime;
        const preExistingValueIsTime = preExistingValue.length === 2,
            dateOrTime = getDateOrTimeValue($from, preExistingValueIsTime);

        if (preExistingValueIsTime) {
            preExistingTime = preExistingValue;
            preExistingDate = dateOrTime;

        } else {
            preExistingTime = dateOrTime;
            preExistingDate = preExistingValue;
        }

        const dateTimeData = getDateTimeData($from, dateTimeTitle, preExistingDate, preExistingTime);

        const publishedDate = dateTimeData.publishedDate;
        let publishedTime = dateTimeData.publishedTime;
        const publicationEndDate = dateTimeData.publicationEndDate;
        let publicationEndTime = dateTimeData.publicationEndTime;

        if (!publishedDate[0] || !publicationEndDate[0]) {
            return true;
        }

        if (!publishedTime[0]) {
            publishedTime = defaultTime;
        }

        if (!publicationEndTime[0]) {
            publicationEndTime = defaultTime;
        }

        const published = new Date(
            publishedDate[0], publishedDate[1], publishedDate[2], publishedTime[0], publishedTime[1], 0, 0
        );

        const publicationEnd = new Date(
            publicationEndDate[0], publicationEndDate[1], publicationEndDate[2],
            publicationEndTime[0], publicationEndTime[1], 0, 0
        );

        return published < publicationEnd;
    }

    function getDateTimeData($from, dateTimeTitle, preExistingDate, preExistingTime) {
        let publishedDate, publishedTime, publicationEndDate, publicationEndTime;

        const $imcmsField = $from.parents(".imcms-field");

        if (dateTimeTitle === PUBLISHED) {
            publishedDate = preExistingDate;
            publishedTime = preExistingTime;

            const $imcmsFieldOfPublicationEnd = $imcmsField.next().next(),
                dateTimeOfPublicationEnd = getDateTime($imcmsFieldOfPublicationEnd);

            publicationEndDate = dateTimeOfPublicationEnd.date;
            publicationEndTime = dateTimeOfPublicationEnd.time;

        } else if (dateTimeTitle === PUBLICATION_END) {
            publicationEndDate = preExistingDate;
            publicationEndTime = preExistingTime;

            const $imcmsFieldOfPublished = $imcmsField.prev().prev(),
                dateTimeOfPublished = getDateTime($imcmsFieldOfPublished);

            publishedDate = dateTimeOfPublished.date;
            publishedTime = dateTimeOfPublished.time;
        }

        return {
            publishedDate: publishedDate,
            publishedTime: publishedTime,
            publicationEndDate: publicationEndDate,
            publicationEndTime: publicationEndTime
        }
    }

    function getDateTime($imcmsField) {
        const $dateInputSelector = $imcmsField.find(".imcms-current-date__input");

        return {
            date: $dateInputSelector.val().split("-"),
            time: getDateOrTimeValue($dateInputSelector, false)
        }
    }

    function getDateOrTimeValue($from, isTime) {
        return $from.parents(".imcms-field")
            .find(isTime ? dateClassSelector : timeClassSelector)
            .val()
            .split(isTime ? "-" : ":");
    }

    function getDateTimeTitle($from) {
        return $from.parents(".imcms-field")
            .find(".imcms-field__title")
            .text();
    }

    return {
        isPublishedDateBeforePublicationEndDate: isPublishedDateBeforePublicationEndDate
    }
});