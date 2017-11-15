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
            incrementOrDecrementBtnSelector;

        if (mousewheelUp) {
            incrementOrDecrementBtnSelector = ".imcms-button--increment";
        } else {
            incrementOrDecrementBtnSelector = ".imcms-button--decrement";
        }

        $(this).find(incrementOrDecrementBtnSelector).click();

        return false;
    }

    function optionalHighlight(e) {
        var $selectedTimeUnit = $(this),
            linkIndex = $selectedTimeUnit.data("link-index"),
            $connectedTimeUnit = $selectedTimeUnit.parent().siblings().find("[data-link-index='" + linkIndex + "']"),
            isMouseLeaveEvent = e.type === "mouseleave",
            selectedTimeUnitChooseClassName = getChooseClassName($selectedTimeUnit),
            connectedTimeUnitChooseClassName = getChooseClassName($connectedTimeUnit);

        if (!isMouseLeaveEvent) {
            $selectedTimeUnit.addClass(selectedTimeUnitChooseClassName);
            $connectedTimeUnit.addClass(connectedTimeUnitChooseClassName);
        } else {
            $selectedTimeUnit.removeClass(selectedTimeUnitChooseClassName);
            $connectedTimeUnit.removeClass(connectedTimeUnitChooseClassName);
        }

        $selectedTimeUnit.siblings().removeClass(selectedTimeUnitChooseClassName);
        $connectedTimeUnit.siblings().removeClass(connectedTimeUnitChooseClassName);

        function getChooseClassName($timeUnit) {
            return $timeUnit.hasClass("imcms-time-picker__hour")
                ? "imcms-time-picker__hour--choose" : "imcms-time-picker__minute--choose";
        }
    }

    function pickTime() {
        var $selectedTimeUnit = $(this),
            linkIndex = $selectedTimeUnit.data("link-index"),
            $pickerHourOrMinute = $selectedTimeUnit.parent(),
            $connectedTimeUnit = $pickerHourOrMinute.siblings().find("[data-link-index='" + linkIndex + "']"),
            $timeInput = $pickerHourOrMinute.parent().siblings().find(".imcms-current-time__input"),
            time = [$selectedTimeUnit.text(), $connectedTimeUnit.text()];

        if ($selectedTimeUnit.hasClass("imcms-time-picker__minute")) {
            time = time.reverse();
        }

        $timeInput.val(time.join(":"));
    }

    function createCloseTimePickerFunction($timePicker, $inputTime) {
        return function (e) {
            var className = e.target.className;

            if (className
                && className.indexOf("imcms-time-picker") === -1
                && className.indexOf("imcms-current-time") === -1)
            {
                $timePicker.find(".imcms-time-picker__time").css("display", "none");
            }

            if ($inputTime.hasClass("imcms-current-time__input--error")) {
                var currentTime = getCurrentTimeObj();
                $inputTime.val(currentTime.hours + ":" + currentTime.minutes);
                $inputTime.removeClass("imcms-current-time__input--error")

            } else {
                var prevVal = $inputTime.val(),

                    correctedVal = prevVal.split(":")
                        .map(optionalAddZeroBeforeNumber)
                        .join(":");

                if (prevVal !== correctedVal) {
                    $inputTime.val(correctedVal);
                }
            }
        };
    }

    return function ($timePickerContainer) {
        var $timePicker = getTimePicker($timePickerContainer),
            $inputTime = $timePicker.find(".imcms-current-time__input"),
            $arrowButtons = $timePicker.find(".imcms-time-picker__button"),
            mousewheelEvent = (/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel";

        $inputTime.click(initTimePicker.bindArgs($timePicker, $inputTime))
            .keydown(allowNumbersAndColons)
            .on("input", initTimePicker.bindArgs($timePicker, $inputTime));

        $timePickerContainer.setTime = apiSetTime($timePickerContainer);

        $arrowButtons.click(arrowButtonsClick);

        $timePicker.find(".imcms-time-picker__hours,.imcms-time-picker__minutes")
            .bind(mousewheelEvent, minutesAndHoursContainersMouseWheel)
            .end()
            .find(".imcms-time-picker__minute,.imcms-time-picker__hour")
            .click(pickTime)
            .mouseenter(optionalHighlight)
            .mouseleave(optionalHighlight);

        $(document).click(createCloseTimePickerFunction($timePicker, $inputTime));

        return $timePickerContainer;
    };
});
