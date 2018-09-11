/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 28.07.17.
 */
define("imcms-date-time-builder",
    ["imcms-bem-builder", "imcms-buttons-builder", "imcms-validator", "imcms-date-picker", "imcms-time-picker"],
    function (BEM, buttons, Validator, DatePicker, TimePicker) {
        var datePickerBEM = new BEM({
                block: "imcms-date-picker",
                elements: {
                    "current-date": "imcms-current-date",
                    "calendar": "imcms-calendar"
                }
            }),
            dateInputContainerBEM = new BEM({
                block: "imcms-current-date",
                elements: {
                    "input": ""
                }
            }),
            calendarContainerBEM = new BEM({
                block: "imcms-calendar",
                elements: {
                    "header": "",
                    "button": "",
                    "title": "",
                    "body": "",
                    "day-names": "",
                    "day-name": "",
                    "weeks": "",
                    "week": "",
                    "day": "imcms-day"
                }
            }),
            weekDays = ["mon", "tue", "wed", "thu", "fri", "sat", "sun"],
            timePickerBEM = new BEM({
                block: "imcms-time-picker",
                elements: {
                    "current-time": "imcms-current-time",
                    "time": "",
                    "button": "",
                    "hours": "",
                    "hour": "",
                    "minutes": "",
                    "minute": ""
                }
            }),
            timeInputBEM = new BEM({
                block: "imcms-current-time",
                elements: {
                    "input": ""
                }
            }),
            dateTimeBEM = new BEM({
                block: "imcms-date-time",
                elements: {
                    "date-picker": "imcms-date-picker",
                    "time-picker": "imcms-time-picker"
                }
            })
        ;

        function createEmptyDays(howManyDays) {
            var days = [];

            for (var i = 0; i < howManyDays; i++) {
                days.push(calendarContainerBEM.buildBlockElement("day", "<div>"));
            }

            return days;
        }

        function createEmptyWeeks(howManyWeeks) {
            var weeks = [];

            for (var i = 0; i < howManyWeeks; i++) {
                var $week = calendarContainerBEM.buildBlockElement("week", "<div>");
                $week.append(createEmptyDays(7));

                weeks.push($week);
            }

            return weeks;
        }

        function createCalendar() {
            var $prevMonthButton = calendarContainerBEM.makeBlockElement("button", buttons.prevButton()),
                $title = calendarContainerBEM.buildBlockElement("title", "<div>"),
                $nextMonthButton = calendarContainerBEM.makeBlockElement("button", buttons.nextButton()),
                $header = calendarContainerBEM.buildElement("header", "<div>").append(
                    $prevMonthButton,
                    $title,
                    $nextMonthButton
                ),

                dayNames = weekDays.map(function (weekDay) {
                    return calendarContainerBEM.buildBlockElement("day-name", "<div>", {
                        text: weekDay
                    });
                }),
                $dayNames = calendarContainerBEM.buildBlockElement("day-names", "<div>").append(dayNames),

                weeks = createEmptyWeeks(6),
                $weeks = calendarContainerBEM.buildBlockElement("weeks", "<div>").append(weeks),
                $body = calendarContainerBEM.buildElement("body", "<div>").append($dayNames, $weeks)
            ;

            return calendarContainerBEM.buildBlock("<div>", [
                {"header": $header},
                {"body": $body}
            ]);
        }

        function createDateBox(attributes, withCalendar) {
            attributes = attributes || {};
            attributes.placeholder = "yyyy-mm-dd";

            if (!withCalendar) {
                attributes.readonly = "readonly";
            }

            var $dateInput = dateInputContainerBEM.buildElement("input", "<input>", attributes),
                $dateInputContainer = dateInputContainerBEM.buildBlock("<div>", [
                    {"input": $dateInput}
                ]),
                datePickerElements = [{"current-date": $dateInputContainer}]
            ;

            if (withCalendar) {
                datePickerElements.push({"calendar": createCalendar()});
            }

            var dateValidator = new Validator($dateInput, isDateValid);

            validateDateInput($dateInput, dateValidator);

            var blockResult = datePickerBEM.buildBlock("<div>", datePickerElements);

            return new DatePicker(blockResult, withCalendar);
        }

        function validateDateInput($dateInput, dateValidator) {
            $dateInput
                .keydown(allowNumbersAndHyphens)
                .on('input', errorInputIfNotValid.bindArgs($dateInput, dateValidator));

            function allowNumbersAndHyphens(event) {
                var pressedButton = event.key,
                    result = true;
                if (pressedButton.length === 1) {
                    result = /^[0-9-]$/.test(pressedButton);
                }

                return result;
            }

            function errorInputIfNotValid($dateInput, dateValidator) {
                $dateInput
                    .toggleClass("imcms-currrent-date__input--error", !dateValidator.isValid());
            }
        }

        function isDateValid() {
            var $dateInput = this;
            var valueSplit = $dateInput.val()
                .split("-");

            switch (valueSplit.length) {
                case 2:
                    return isYearValid(valueSplit[0])
                        && isMonthValid(valueSplit[1]);
                case 3:
                    return isYearValid(valueSplit[0])
                        && isMonthValid(valueSplit[1])
                        && isDateValid(valueSplit[2], valueSplit[1], valueSplit[0]);
                default:
                    return false;
            }

            function isYearValid(year) {
                return /^([1-9]\d{3})$/.test(year);
            }

            function isMonthValid(month) {
                return month > 0 && month < 13;
            }

            function isDateValid(day, month, year) {
                var
                    lastDateOfMonth = new Date(+year, +month, 0).getDate(),
                    result = true;

                result &= day > 0 && day < 32;
                result &= day <= lastDateOfMonth;

                return result;
            }
        }

        function createTimePickerBlockElements(elementName, howManyElements) {
            var elements = [];

            for (var i = 0; i < howManyElements; i++) {
                elements.push(timePickerBEM.buildBlockElement(elementName, "<div>"));
            }

            return elements;
        }

        function createClock() {
            var $prevHourButton = timePickerBEM.makeBlockElement("button", buttons.incrementButton()),
                emptyHours = createTimePickerBlockElements("hour", 6),
                $nextHourButton = timePickerBEM.makeBlockElement("button", buttons.decrementButton()),
                $hours = timePickerBEM.buildBlockElement("hours", "<div>").append(
                    [$prevHourButton].concat(emptyHours, $nextHourButton)
                ),
                $prevMinuteButton = timePickerBEM.makeBlockElement("button", buttons.incrementButton()),
                emptyMinutes = createTimePickerBlockElements("minute", 6),
                $nextMinuteButton = timePickerBEM.makeBlockElement("button", buttons.decrementButton()),
                $minutes = timePickerBEM.buildBlockElement("minutes", "<div>").append(
                    [$prevMinuteButton].concat(emptyMinutes, $nextMinuteButton)
                )
            ;
            return timePickerBEM.buildBlockElement("time", "<div>").append($hours, $minutes);
        }

        function createTimeBox(attributes, withClock) {
            attributes = attributes || {};
            attributes.placeholder = "HH:mm";

            if (!withClock) {
                attributes.readonly = "readonly";
            }

            var $timeInput = timeInputBEM.buildElement("input", "<input>", attributes),
                $timeInputContainer = timeInputBEM.buildBlock("<div>", [
                    {"input": $timeInput}
                ]),
                timePickerElements = [{"current-time": $timeInputContainer}]
            ;

            if (withClock) {
                timePickerElements.push({"time": createClock()});
            }

            var blockResult = timePickerBEM.buildBlock("<div>", timePickerElements);
            return new TimePicker(blockResult, withClock);
        }

        function createDateTimeBox(attributes) {
            var $datePart = createDateBox(attributes),
                $timePart = createTimeBox(attributes)
            ;
            var blockResult = dateTimeBEM.buildBlock("<div>", [
                {"date-picker": $datePart},
                {"time-picker": $timePart}
            ]);

            return new DatePicker(new TimePicker(blockResult));
        }

        return {
            dateBoxReadOnly: function (attributes) {
                return createDateBox(attributes);
            },
            datePickerCalendar: function (attributes) {
                return createDateBox(attributes, true);
            },
            timePickerClock: function (attributes) {
                return createTimeBox(attributes, true);
            },
            timeBoxReadOnly: function (attributes) {
                return createTimeBox(attributes);
            },
            dateTimeReadOnly: function (attributes) {
                return createDateTimeBox(attributes);
            }
        };
    });
