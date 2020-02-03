define('imcms-overlays-builder', ['jquery', 'imcms-bem-builder'], function ($, BEM) {

        /**
         * Overlay is the fundamental component for positioning and controlling tooltip.
         *
         * @example
         * overlays.createOverlay({
         *     element: $('<div>'),
         *     overlay: overlays.tooltip('Text on tooltip'),
         *     delay: {show: 300, hide: 0},
         *     placement: 'right',
         *     trigger: 'hover',
         * });
         *
         * @param element {object} A target element for triggering an overlay
         * @param overlay {object} An element or text to overlay next to the target
         * @param delay {number|object?} A millisecond delay amount to show and hide the Overlay once triggered. Default: 0
         *        Example: 1, {hide: 1}, {show: 1, hide: 2}
         * @param placement {string?} Values: 'top'|'right'|'bottom'|'left'. Default: 'right'
         * @param trigger {string?} Values: 'click'|'hover'. Default: 'hover'
         */
        const createOverlay = ({element, overlay, delay = 0, placement = 'top', trigger = 'hover'}) => {

            const correctDelay = getCorrectDelay(delay);

            overlay.addClass(placement);

            let onEnterTimeout, onLeaveTimeout;

            element
                .mouseenter(() => {
                    clearTimeout(onLeaveTimeout);

                    if (!$('body').find(overlay).length) {
                        onEnterTimeout = setTimeout(() => {
                            $('body').append(overlay);

                            const position = calculatePositionOfOverlay(element, overlay, placement);

                            overlay.css('transform', `translate(${position.left}px, ${position.top}px)`);

                            setTimeout(() => overlay.addClass('show'), Number.MIN_VALUE);
                        }, correctDelay.show);
                    }
                })
                .mouseleave(() => {
                    clearTimeout(onEnterTimeout);

                    onLeaveTimeout = setTimeout(() => {
                        overlay.removeClass('show');

                        // Timeout should be equal to transition on tooltip
                        setTimeout(() => overlay.remove(), 150);
                    }, correctDelay.hide);
                });

        };

        const getCorrectDelay = delay => {
            if (!isNaN(delay)) {
                return {
                    show: delay,
                    hide: delay,
                }
            }
            if (!delay.show) {
                delay.show = 0;
            }
            if (!delay.hide) {
                delay.hide = 0;
            }
            return delay;
        };

        const calculatePositionOfOverlay = (element, overlay, placement) => {
            const elementOffset = element.offset();
            const bodyOffsetTop = parseInt($('body').css('top'));
            elementOffset.top -= bodyOffsetTop;

            switch (placement) {
                case 'right':
                    return {
                        top: elementOffset.top + element.outerHeight() / 2 - overlay.outerHeight() / 1.5,
                        left: elementOffset.left + element.outerWidth(),
                    };
                case 'bottom':
                    return {
                        top: elementOffset.top + overlay.outerHeight(),
                        left: elementOffset.left,
                    };
                case 'left':
                    return {
                        top: elementOffset.top + element.outerHeight() / 2 - overlay.outerHeight() / 1.5,
                        left: elementOffset.left - overlay.outerWidth(),
                    };
                case 'top':
                default:
                    return {
                        top: elementOffset.top - element.outerHeight(),
                        left: elementOffset.left + element.outerWidth() / 2 - overlay.outerWidth() / 2,
                    };
            }
        };

        /**
         * Universal tooltip with text and dark background
         */
        const buildTooltip = (text) => new BEM({
            block: 'imcms-tooltip',
            elements: {
                'arrow': $('<div>'),
                'text': $('<div>').text(text),
            }
        }).buildBlockStructure('<div>');

        return {
            createOverlay,
            tooltip: buildTooltip,
        }
    }
);