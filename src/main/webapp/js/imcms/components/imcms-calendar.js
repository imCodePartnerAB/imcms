define("imcms-calendar", ["jquery", "imcms-date-time-validator"], function ($, dateTimeValidator) {

    function setSelectDate() {
        var $thisDay = $(this),
            curDateInput = $thisDay.parents(".imcms-date-picker").find(".imcms-current-date__input"),
            curDateInputValue = curDateInput.val(),
            curDateInputVal = curDateInput.val().split('-'),
            year = curDateInputVal[0],
            month = $thisDay.attr("data-month") || curDateInputVal[1],
            date = $thisDay.text()
        ;

        if (!curDateInputValue) {
            var d = new Date();
            year = d.getFullYear();
            month = d.getMonth() + 1;
        }

        if (month < 10) {
            month = "0" + month;
        }

        if (date < 10) {
            date = "0" + date;
        }

        if (dateTimeValidator.isPublishedDateBeforePublicationEndDate($thisDay, [year, month, date])) {

            $thisDay.parents(".imcms-calendar__body")
                .find(".imcms-day--today")
                .removeClass("imcms-day--today");

            curDateInput.val(year + "-" + month + "-" + date);
            $thisDay.addClass("imcms-day--today");
        }
    }

    function correctDayStartsFromSundayToMonday(startDay) {
        var corrected = startDay - 1;
        return corrected > -1 ? corrected : 6;
    }

    function buildCalendar(year, month, day, $calendar) {
        if (!$calendar || !$calendar.length) {
            return;
        }

        var calendarTitle = $calendar.find(".imcms-calendar__title"),
            calendarTitleVal = calendarTitle.val().split(" "),
            calendarWeek = $calendar.find(".imcms-calendar__week"),
            firstDay = new Date(year, month - 1),
            firstDate = firstDay.getDate(),
            firstDayNumber = correctDayStartsFromSundayToMonday(firstDay.getDay()),
            lastD = new Date(year, month, 0),
            lastDay = lastD.getDate(),
            prevMonthD = new Date(year, month - 1, 0),
            prevMonthDay = prevMonthD.getDate(),
            count = 0,
            monthList = [
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            ]
        ;

        calendarTitleVal[0] = monthList[month - 1];
        calendarTitleVal[1] = year;
        calendarTitle.html(calendarTitleVal.join(" "));
        count = 0;

        var previousMonthDayNumber = firstDayNumber - 1;
        var nextMonthDayNumber = 1;

        calendarWeek.find(".imcms-calendar__day").each(function () {
            var $calendarDay = $(this);

            if (count < firstDayNumber) {
                $calendarDay.removeClass("imcms-day--outer-next imcms-day--today")
                    .addClass("imcms-day--outer-prev")
                    .attr("data-month", prevMonthD.getMonth() + 1)
                    .text(prevMonthDay - previousMonthDayNumber);

                previousMonthDayNumber--;

            } else if ((count - firstDayNumber + 1) > lastDay) {
                $calendarDay.removeClass("imcms-day--outer-prev imcms-day--today")
                    .addClass("imcms-day--outer-next")
                    .attr("data-month", firstDay.getMonth() + 2)
                    .text(nextMonthDayNumber++);

            } else {
                $calendarDay.removeClass("imcms-day--outer-prev imcms-day--outer-next imcms-day--today")
                    .attr("data-month", firstDay.getMonth() + 1)
                    .text(firstDate);

                firstDate++;

                if ((count - firstDayNumber + 1) === day) {
                    $calendarDay.addClass("imcms-day--today");
                }
            }

            $calendarDay.unbind("click").click(setSelectDate);
            count++;
        });

        var lastCalendarWeekCss = ((firstDayNumber + lastDay) <= 35)
            ? {"display": "none"}
            : {"display": "block"};

        calendarWeek.last().css(lastCalendarWeekCss);
    }

    function selectDate(date, $calendar) {
        $calendar
            .find(".imcms-calendar__day")
            .filter(function () {
                return +$(this).html() === date;
            })
            .first()
            .addClass("imcms-day--today");
    }

    return {
        init: $datePicker => {
            var $curDateInput = $datePicker.find(".imcms-current-date__input"),
                currentValue = $curDateInput.val(),
                $calendar = $datePicker.find(".imcms-calendar"),
                year, month, date
            ;

            if (currentValue) {
                var curDate = $curDateInput.val().split("-");
                year = parseInt(curDate[0]);
                month = parseInt(curDate[1]);
                date = parseInt(curDate[2]);
            } else {
                var currentDate = new Date();
                year = currentDate.getFullYear();
                month = currentDate.getMonth() + 1;
                date = currentDate.getDate();
            }

            if (!$datePicker.hasClass("imcms-date-picker--active") && $calendar.length !== 0) {
                $datePicker.addClass("imcms-date-picker--active");
                buildCalendar(year, month, date, $calendar);
            }
        },
        buildCalendar: buildCalendar,
        chooseMonth: function () {
            var $btn = $(this),
                $calendar = $btn.parents(".imcms-calendar"),
                $input = $btn.parents(".imcms-date-picker")
                    .find(".imcms-current-date__input"),
                curDate = $input
                    .val()
                    .split("-"),
                year = curDate[0],
                month = curDate[1],
                date = "01"
            ;

            if ($input.hasClass("imcms-currrent-date__input--error")) {
                var currentDate = new Date();
                year = currentDate.getFullYear().toString();
                month = (currentDate.getMonth() + 1).toString();
            } else {
                month = $btn.hasClass("imcms-button--prev") ? +month - 1 : +month + 1;

                if (month > 12) {
                    year = +year + 1;
                    year = year.toString();
                    month = 1;
                } else if (month < 1) {
                    year = +year - 1;
                    year = year.toString();
                    month = 12;
                }

                month = month < 10 ? "0" + month : month.toString();
            }

            if (dateTimeValidator.isPublishedDateBeforePublicationEndDate($input, [year, month, date])) {
                $input.val(year + "-" + month + "-" + date);
                buildCalendar(year, month, date, $calendar);
                selectDate(1, $calendar);
            }
        }
    };
});
