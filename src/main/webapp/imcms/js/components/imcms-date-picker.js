define("imcms-date-picker",
    ["imcms", "imcms-calendar", "jquery", "imcms-date-time-validator"],
    function (imcms, imcmsCalendar, $, dateTimeValidator) {
        const DATE_PICKER_CLASS_SELECTOR = ".imcms-date-picker";

        function openCalendar() {
            imcmsCalendar.init($(this).parents(DATE_PICKER_CLASS_SELECTOR));
        }

        function closeCalendar(e) {
            const $target = $(e.target);
            if ($target.closest(".imcms-current-date__input").length
                || $target.hasClass("imcms-current-date__input")
                || $target.hasClass(".imcms-date-picker__current-date")
                || $target.parents(".imcms-calendar").length)
            {
                return;
            }

            $(".imcms-date-picker--active").each(function () {
                const $activeDatePicker = $(this),
                    $currentDateInput = $activeDatePicker.find(".imcms-current-date__input");

                if ($currentDateInput.hasClass("imcms-currrent-date__input--error")) {
                    let currentDate = getCurrentDate();

                    let publishedDateBeforePublicationEndDate = dateTimeValidator
                        .isPublishedDateBeforePublicationEndDate($currentDateInput, currentDate.split("-"));

                    if (!publishedDateBeforePublicationEndDate) {
                        currentDate = "";
                    }

                    $currentDateInput.val(currentDate)
                        .removeClass("imcms-currrent-date__input--error");
                }

                $activeDatePicker.removeClass("imcms-date-picker--active");
            });

            e.stopPropagation();
        }

        function getCurrentDate() {
            const currentDate = new Date(),
                year = currentDate.getFullYear();
            let month = currentDate.getMonth() + 1,
                date = currentDate.getDate()
            ;

            if (month < 10) {
                month = "0" + month;
            }
            if (date < 10) {
                date = "0" + date;
            }

            return year + "-" + month + "-" + date;
        }

        function defaultIfFalse(statement, value, defaultValue) {
            return statement ? value : defaultValue;
        }

        function rebuildCalendar() {
            const $currentDateInput = $(this),
                $calendar = $currentDateInput.parents(DATE_PICKER_CLASS_SELECTOR).find(".imcms-calendar")
            ;

            if (!$calendar.length) {
                return;
            }

            const currentDate = new Date();
            const carDate = $currentDateInput.val().split('-'),
                isValid = !$currentDateInput.hasClass("imcms-currrent-date__input--error"),
                year = defaultIfFalse(isValid, carDate[0], currentDate.getFullYear()),
                month = defaultIfFalse(isValid, carDate[1], currentDate.getMonth() + 1),
                day = defaultIfFalse(isValid && carDate[2], carDate[2], currentDate.getDate())
            ;

            imcmsCalendar.buildCalendar(year, month, day, $calendar);
            const filteredByDay = $calendar.find(".imcms-calendar__day")
                .each(function () {
                    $(this).removeClass("imcms-day--today");
                })
                .filter(function () {
                    let dayOnCalendar = $(this).html();
                    if (day.length === 2 && dayOnCalendar.length === 1) {
                        dayOnCalendar = "0" + dayOnCalendar;
                    }
                    return dayOnCalendar === day;
                });

            const lastOrFirst = day <= 20 ? filteredByDay.first() : filteredByDay.last();

            const date = [year, month, day];

            if (isValid && !dateTimeValidator.isPublishedDateBeforePublicationEndDate($currentDateInput, date)) {
                $currentDateInput.val("");
            } else {
                lastOrFirst.addClass("imcms-day--today");
            }
        }

        function bindDateSetter($dateBoxContainer) {
            return date => $dateBoxContainer.find(".imcms-current-date__input")
                .val(date)
                .end();
        }

        function bindDateGetter($dateBoxContainer) {
            return () => $dateBoxContainer.find(".imcms-current-date__input").val();
        }

        function bindCurrentDateSetter($dateBoxContainer) {
            return () => {
                bindDateSetter($dateBoxContainer)(getCurrentDate());
            };
        }

        function bindChangeEventSetter($dateBoxContainer){
            return (event) => {
                $dateBoxContainer.find(".imcms-current-date__input").on('change', event);
            };
        }

        $(document).click(closeCalendar);

        return ($dateBoxContainer, withCalendar) => {
            $dateBoxContainer.setDate = bindDateSetter($dateBoxContainer);
            $dateBoxContainer.setCurrentDate = bindCurrentDateSetter($dateBoxContainer);
            $dateBoxContainer.getDate = bindDateGetter($dateBoxContainer);
            $dateBoxContainer.setChangeEvent = bindChangeEventSetter($dateBoxContainer);

            if (!withCalendar) {
                return $dateBoxContainer;
            }

            $dateBoxContainer.find(".imcms-date-picker__current-date")
                .click(openCalendar)
                .end()
                .find(".imcms-calendar__button")
                .click(imcmsCalendar.chooseMonth)
                .end()
                .find(".imcms-current-date__input")
                .on('input', rebuildCalendar);

            return $dateBoxContainer;
        };
    });
