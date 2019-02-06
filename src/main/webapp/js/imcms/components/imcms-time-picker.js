define("imcms-time-picker", ["imcms", "jquery", "imcms-date-time-validator"], function (imcms, $, dateTimeValidator) {

    var TIME_PICKER__CLASS = "imcms-time-picker",
        TIME_PICKER__CLASS_$ = "." + TIME_PICKER__CLASS,
        CURRENT_TIME__INPUT__CLASS_$ = ".imcms-current-time__input",
        CURRENT_TIME__INPUT__ERROR__CLASS = "imcms-current-time__input--error",
        TIME_PICKER__TIME__CLASS_$ = ".imcms-time-picker__time",
        TIME_PICKER__HOUR__CLASS = "imcms-time-picker__hour",
        TIME_PICKER__HOUR__CLASS_$ = "." + TIME_PICKER__HOUR__CLASS,
        TIME_PICKER__MINUTE__CLASS = "imcms-time-picker__minute",
        TIME_PICKER__MINUTE__CLASS_$ = "." + TIME_PICKER__MINUTE__CLASS
    ;

    function optionalAddZeroBeforeNumber(numberStr) {
        if (numberStr === "" || numberStr === null || numberStr === undefined) { // without 0
            return numberStr;
        }

        var result = +numberStr;

        if (result < 10) {
            result = "0" + result;
        }

        return result.toString();
    }

    function getCurrentTimeObj() {
        var currentDate = new Date();
        return {
            hours: optionalAddZeroBeforeNumber(currentDate.getHours()),
            minutes: optionalAddZeroBeforeNumber(currentDate.getMinutes())
        }
    }

    function getDateObjValidated($inputTime) {
        var time = $inputTime.val(),
            currentTime = getCurrentTimeObj();

        if (!time) {
            return currentTime;
        }

        var inputVal = time.split(":"),
            currentHours = currentTime.hours.toString(),
            currentMinutes = currentTime.minutes.toString(),
            hours = inputVal[0],
            minutes = inputVal[1],
            hasErrorClass = false;

        if (!hours || +hours > 23 || hours.length > 2) {
            hours = currentHours;
            hasErrorClass = true;
        }

        if (!minutes || +minutes > 59 || minutes.length > 2) {
            minutes = currentMinutes;
            hasErrorClass = true;
        }

        $inputTime.toggleClass(CURRENT_TIME__INPUT__ERROR__CLASS, hasErrorClass);

        return {
            hours: hours,
            minutes: minutes
        }
    }

    function getTimePicker($timePickerContainer) {
        return $timePickerContainer.hasClass(TIME_PICKER__CLASS)
            ? $timePickerContainer
            : $timePickerContainer.find(TIME_PICKER__CLASS_$);
    }

    function bindSetTime($timePickerContainer) {
        return function (time) {
            return $timePickerContainer.find(CURRENT_TIME__INPUT__CLASS_$)
                .val((time ? time.split(':', 2).join(':') : ''))
                .end();
        };
    }

    function bindSetCurrentTime($timePickerContainer) {
        return () => {
            var currentTimeObj = getCurrentTimeObj();
            bindSetTime($timePickerContainer)(currentTimeObj.hours + ':' + currentTimeObj.minutes);
        };
    }

    function bindGetTime($timePickerContainer) {
        return () => $timePickerContainer.find(CURRENT_TIME__INPUT__CLASS_$).val();
    }

    function allowNumbersAndColons() {
        var pressedButton = event.key,
            result = true;

        if (pressedButton.length === 1) {
            result = /^[0-9:]$/.test(pressedButton);
        }

        return result;
    }

    function addValuesWithShift(timeValue, value, limit) {
        var result = timeValue + value;

        if (result > limit) {
            result = result - (limit + 1);
        }

        if (result < 0) {
            result = limit;
        }

        result = optionalAddZeroBeforeNumber(result);
        return result;
    }

    function initTimePicker($timePicker, $inputTime) {
        var parsedDate = getDateObjValidated($inputTime);

        $timePicker.find(TIME_PICKER__TIME__CLASS_$).css("display", "block");

        var hours = +parsedDate.hours,
            minutes = +parsedDate.minutes,
            hoursContainers = $timePicker.find(TIME_PICKER__HOUR__CLASS_$),
            minutesContainers = $timePicker.find(TIME_PICKER__MINUTE__CLASS_$);

        hoursContainers.each(function (index) {
            $(this).text(addValuesWithShift(hours, index, 23));
        });

        minutesContainers.each(function (index) {
            $(this).text(addValuesWithShift(minutes, index, 59));
        });
    }

    function arrowButtonsClick() {
        var $clickedArrow = $(this),
            addValue = $clickedArrow.hasClass("imcms-button--increment") ? -1 : 1,
            $timeContainers = $clickedArrow.parent().find("div"),
            limit = $timeContainers.first().hasClass(TIME_PICKER__HOUR__CLASS) ? 23 : 59;

        $timeContainers.each(function () {
            var $timeContainer = $(this);
            $timeContainer.text(addValuesWithShift(+$timeContainer.text(), addValue, limit));
        });
    }

    function minutesAndHoursContainersMouseWheel(e) {
        var event = window.event || e;
        event = event.originalEvent ? event.originalEvent : event;

        var delta = event.detail ? event.detail * (-40) : event.wheelDelta,
            mousewheelUp = delta > 0,
            incrementOrDecrementBtnSelector = ".imcms-button--" + ((mousewheelUp) ? "increment" : "decrement");

        $(this).find(incrementOrDecrementBtnSelector).click();

        return false;
    }

    function optionalHighlight(e) {
        var $selectedTimeUnit = $(this),
            isMouseLeaveEvent = e.type === "mouseleave",
            selectedTimeUnitChooseClassName = getChooseClassName($selectedTimeUnit);

        if (!isMouseLeaveEvent) {
            $selectedTimeUnit.addClass(selectedTimeUnitChooseClassName);

        } else {
            $selectedTimeUnit.removeClass(selectedTimeUnitChooseClassName);
        }

        $selectedTimeUnit.siblings().removeClass(selectedTimeUnitChooseClassName);

        function getChooseClassName($timeUnit) {
            return $timeUnit.hasClass(TIME_PICKER__HOUR__CLASS)
                ? "imcms-time-picker__hour--choose" : "imcms-time-picker__minute--choose";
        }
    }

    function pickTime() {
        var $selectedTimeUnit = $(this),
            $pickerHourOrMinute = $selectedTimeUnit.parent(),
            hoursPosition = 0,
            minutesPosition = 1,
            $timeInput = $pickerHourOrMinute.parent().siblings().find(CURRENT_TIME__INPUT__CLASS_$);

        var time = ($timeInput.val() || "00:00").split(":");

        var changedPosition = ($selectedTimeUnit.hasClass(TIME_PICKER__MINUTE__CLASS))
            ? minutesPosition : hoursPosition;

        time[changedPosition] = $selectedTimeUnit.text();

        if (dateTimeValidator.isPublishedDateBeforePublicationEndDate($timeInput, time)) {
            $timeInput.val(time.join(":"));
        }
    }

    function closeTimePickerFunction(e) {
        var className = e.target.className;

        if (className
            && className.indexOf(TIME_PICKER__CLASS) === -1
            && className.indexOf("imcms-current-time") === -1)
        {
            $(TIME_PICKER__TIME__CLASS_$).css("display", "none");
        }

        $(".imcms-current-time__input--error").each(function () {
            var currentTime = getCurrentTimeObj();
            var $this = $(this);

            var time = currentTime.hours + ":" + currentTime.minutes;

            var publishedDateBeforePublicationEndDate = dateTimeValidator
                .isPublishedDateBeforePublicationEndDate($this, time);

            if (!publishedDateBeforePublicationEndDate) {
                time = "";
            }

            $this.val(time);
            $this.removeClass(CURRENT_TIME__INPUT__ERROR__CLASS)
        });
    }

    function isValid(time) {
        var isValid = time.length === 2;

        time.forEach(value => {
            isValid = isValid && (value.length === 1 || value.length === 2);
        });

        return isValid;
    }

    $(document).click(closeTimePickerFunction);

    return function ($timePickerContainer, withClock) {
        $timePickerContainer.setTime = bindSetTime($timePickerContainer);
        $timePickerContainer.setCurrentTime = bindSetCurrentTime($timePickerContainer);
        $timePickerContainer.getTime = bindGetTime($timePickerContainer);

        if (!withClock) {
            return $timePickerContainer;
        }

        var $timePicker = getTimePicker($timePickerContainer),
            $inputTime = $timePicker.find(CURRENT_TIME__INPUT__CLASS_$),
            mousewheelEvent = (/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel";

        $inputTime.click(() => initTimePicker($timePicker, $inputTime))
            .keydown(allowNumbersAndColons)
            .on("input", function () {
                var $inputTime = $(this),
                    time = $inputTime.val().split(":");

                if (isValid(time)
                    && !dateTimeValidator.isPublishedDateBeforePublicationEndDate($inputTime, time))
                {
                    $inputTime.val("");
                }

                initTimePicker($timePicker, $inputTime);
            });

        $timePicker.find(".imcms-time-picker__button")
            .click(arrowButtonsClick)
            .end()
            .find(".imcms-time-picker__hours,.imcms-time-picker__minutes")
            .bind(mousewheelEvent, minutesAndHoursContainersMouseWheel)
            .end()
            .find(TIME_PICKER__MINUTE__CLASS_$ + "," + TIME_PICKER__HOUR__CLASS_$)
            .click(pickTime)
            .mouseenter(optionalHighlight)
            .mouseleave(optionalHighlight);

        return $timePickerContainer;
    };
});
