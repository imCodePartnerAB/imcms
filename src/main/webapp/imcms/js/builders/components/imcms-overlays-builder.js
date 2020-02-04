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
         * });
         *
         * @param element {object} A target element for triggering an overlay
         * @param overlay {object} An element or text to overlay next to the target
         * @param delay {number|object?} A millisecond delay amount to show and hide the Overlay once triggered. Default: 0
         *        Example: 1, {hide: 1}, {show: 1, hide: 2}
         * @param placement {string?} Values: 'top'|'right'|'bottom'|'left'. Default: 'right'
         */
        const createOverlay = ({element, overlay, delay = 0, placement = 'top'}) => {

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
            const elementOffset = getCorrectElementOffset(element);
            const elementScale = getElementScale(element);
            const overlayScale = getElementScale(overlay);

            switch (placement) {
                case 'right':
                    return {
                        top: elementOffset.top + elementScale.height / 2 - overlayScale.height / 2,
                        left: elementOffset.left + elementScale.width,
                    };
                case 'bottom':
                    return {
                        top: elementOffset.top + overlayScale.height,
                        left: elementOffset.left,
                    };
                case 'left':
                    return {
                        top: elementOffset.top + elementScale.height / 2 - overlayScale.height / 2,
                        left: elementOffset.left - overlayScale.width,
                    };
                case 'top':
                default:
                    return {
                        top: elementOffset.top - elementScale.height,
                        left: elementOffset.left + elementScale.width / 2 - overlayScale.width / 2,
                    };
            }
        };

        const getCorrectElementOffset = element => {
            const bodyOffset = $('body').offset();
            const elementOffset = element.offset();
            return {
                top: elementOffset.top - bodyOffset.top,
                left: elementOffset.left - bodyOffset.left,
            }
        };

        const getElementScale = element => ({
            height: element.outerHeight(),
            width: element.outerWidth(),
        });

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