Imcms.define("imcms-time-picker", ["imcms", "jquery"], function (imcms, $) {
    var TIME_PICKER_CLASS = "imcms-time-picker",
        TIME_PICKER_CLASS_SELECTOR = ".imcms-time-picker"
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
        var inputVal = $inputTime.val().split(":"),
            currentTime = getCurrentTimeObj(),
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

        if (hasErrorClass) {
            $inputTime.addClass("imcms-current-time__input--error");

        } else {
            $inputTime.removeClass("imcms-current-time__input--error");
        }

        return {
            hours: hours,
            minutes: minutes
        }
    }

    function getTimePicker($timePickerContainer) {
        return $timePickerContainer.hasClass(TIME_PICKER_CLASS)
            ? $timePickerContainer
            : $timePickerContainer.find(TIME_PICKER_CLASS_SELECTOR);
    }

    function apiSetTime($timePickerContainer) {
        return function (time) {
            return $timePickerContainer.find(".imcms-current-time__input")
                .val(time)
                .end();
        }
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

        $timePicker.find(".imcms-time-picker__time").css("display", "block");

        var hours = +parsedDate.hours,
            minutes = +parsedDate.minutes,
            hoursContainers = $timePicker.find(".imcms-time-picker__hour"),
            minutesContainers = $timePicker.find(".imcms-time-picker__minute");

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
            limit = $timeContainers.first().hasClass("imcms-time-picker__hour") ? 23 : 59;

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
            return $timeUnit.hasClass("imcms-time-picker__hour")
                ? "imcms-time-picker__hour--choose" : "imcms-time-picker__minute--choose";
        }
    }

    function pickTime() {
        var $selectedTimeUnit = $(this),
            $pickerHourOrMinute = $selectedTimeUnit.parent(),
            hoursPosition = 0,
            minutesPosition = 1,
            $timeInput = $pickerHourOrMinute.parent().siblings().find(".imcms-current-time__input");

        var time = ($timeInput.val() || "00:00").split(":");

        var changedPosition = ($selectedTimeUnit.hasClass("imcms-time-picker__minute"))
            ? minutesPosition : hoursPosition;

        time[changedPosition] = $selectedTimeUnit.text();

        $timeInput.val(time.join(":"));
    }

    function closeTimePickerFunction(e) {
        var className = e.target.className;

        if (className
            && className.indexOf("imcms-time-picker") === -1
            && className.indexOf("imcms-current-time") === -1)
        {
            $(".imcms-time-picker__time").css("display", "none");
        }

        $(".imcms-current-time__input--error").each(function () {
            var currentTime = getCurrentTimeObj();
            var $this = $(this);

            $this.val(currentTime.hours + ":" + currentTime.minutes);
            $this.removeClass("imcms-current-time__input--error")
        });
    }

    $(document).click(closeTimePickerFunction);

    return function ($timePickerContainer, withClock) {
        $timePickerContainer.setTime = apiSetTime($timePickerContainer);

        if (!withClock) {
            return $timePickerContainer;
        }

        var $timePicker = getTimePicker($timePickerContainer),
            $inputTime = $timePicker.find(".imcms-current-time__input"),
            mousewheelEvent = (/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel";

        $inputTime.click(initTimePicker.bindArgs($timePicker, $inputTime))
            .keydown(allowNumbersAndColons)
            .on("input", initTimePicker.bindArgs($timePicker, $inputTime));

        $timePicker.find(".imcms-time-picker__button")
            .click(arrowButtonsClick)
            .end()
            .find(".imcms-time-picker__hours,.imcms-time-picker__minutes")
            .bind(mousewheelEvent, minutesAndHoursContainersMouseWheel)
            .end()
            .find(".imcms-time-picker__minute,.imcms-time-picker__hour")
            .click(pickTime)
            .mouseenter(optionalHighlight)
            .mouseleave(optionalHighlight);

        return $timePickerContainer;
    };
});
