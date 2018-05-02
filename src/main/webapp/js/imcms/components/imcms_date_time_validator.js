Imcms.define("imcms-date-time-validator", [], function () {

    var defaultTime = ["00", "00"];

    var dateClassSelector = ".imcms-current-date__input";
    var timeClassSelector = ".imcms-current-time__input";

    function isPublishedDateBeforePublicationEndDate($from, preExistingValue, preExistingValueIsTime) {

        var preExistingDate;
        var preExistingTime;

        var dateOrTime = getDateOrTimeValue($from, preExistingValueIsTime);

        if (preExistingValueIsTime) {
            preExistingTime = preExistingValue;
            preExistingDate = dateOrTime;
        } else {
            preExistingTime = dateOrTime;
            preExistingDate = preExistingValue;
        }

        var dateTimeData = getDateTimeData($from, preExistingDate, preExistingTime);

        var publishedDate = dateTimeData.publishedDate,
            publishedTime = dateTimeData.publishedTime,
            publicationEndDate = dateTimeData.publicationEndDate,
            publicationEndTime = dateTimeData.publicationEndTime;

        if (!publishedDate[0] || !publicationEndDate[0]) {
            return true;
        }

        if (!publishedTime[0]) {
            publishedTime = defaultTime;
        }

        if (!publicationEndTime[0]) {
            publicationEndTime = defaultTime;
        }

        var published = new Date(
            publishedDate[0], publishedDate[1], publishedDate[2], publishedTime[0], publishedTime[1], 0, 0
        );

        var publicationEnd = new Date(
            publicationEndDate[0], publicationEndDate[1], publicationEndDate[2],
            publicationEndTime[0], publicationEndTime[1], 0, 0
        );

        return published < publicationEnd;
    }

    function getDateTimeData($from, preExistingDate, preExistingTime) {
        var publishedDate, publishedTime, publicationEndDate, publicationEndTime;

        var $imcmsField = $from.parents(".imcms-field");
        var dateTimeTitle = $imcmsField.find(".imcms-field__title").text();

        if (dateTimeTitle === "Published") {
            publishedDate = preExistingDate;
            publishedTime = preExistingTime;

            var $imcmsFieldOfPublicationEnd = $imcmsField.next().next();
            var dateTimeOfPublicationEnd = getDateTime($imcmsFieldOfPublicationEnd);

            publicationEndDate = dateTimeOfPublicationEnd.date;
            publicationEndTime = dateTimeOfPublicationEnd.time;

        } else if (dateTimeTitle === "Publication end") {
            publicationEndDate = preExistingDate;
            publicationEndTime = preExistingTime;

            var $imcmsFieldOfPublished = $imcmsField.prev().prev();
            var dateTimeOfPublished = getDateTime($imcmsFieldOfPublished);

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
        var $dateInputSelector = $imcmsField.find(".imcms-current-date__input");

        var date = $dateInputSelector.val().split("-");
        var time = getDateOrTimeValue($dateInputSelector, false);

        return {
            date: date,
            time: time
        }
    }

    function getDateOrTimeValue($from, isTime) {
        return $from.parents(".imcms-field")
            .find(isTime ? dateClassSelector : timeClassSelector)
            .val()
            .split(isTime ? "-" : ":");
    }

    return {
        isPublishedDateBeforePublicationEndDate: isPublishedDateBeforePublicationEndDate
    }
});