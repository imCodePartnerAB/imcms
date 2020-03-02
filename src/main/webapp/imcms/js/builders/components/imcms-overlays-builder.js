define('imcms-overlays-builder', ['jquery', 'imcms-bem-builder'], function ($, BEM) {

    const defaultAttributes = {
        delay: {show: 400},
        placement: 'top',
    };

    /**
     * Overlay is the fundamental component for positioning and controlling tooltip.
     *
     * @example
     * overlays.bindOverlay({
     *     element: $('<div>'),
     *     overlay: overlays.tooltip('Text on tooltip'),
     *     delay: {show: 300, hide: 0},
     *     placement: 'right',
     * });
     *
     * @param {object} element - A target element for triggering an overlay.
     * @param {object} overlay - An element or text to overlay next to the target.
     * @param {number|object?} delay - A millisecond delay amount to show and hide
     *        the Overlay once triggered. Default: 400/0.
     *        Example: 200, {hide: 100}, {show: 100, hide: 200}.
     * @param {string} [placement=top] - Values: 'top'|'right'|'bottom'|'left'.
     */
    const bindOverlay = (
        {
            element,
            overlay,
            delay = defaultAttributes.delay,
            placement = defaultAttributes.placement
        }
    ) => {
        const correctDelay = getCorrectDelay(delay);
        overlay.addClass(placement);

        let onEnterTimeout, onLeaveTimeout;
        element
            .mouseenter(() => {
                clearTimeout(onLeaveTimeout);

                const $body = $('body');

                if (!$body.find(overlay).length && $body.find(element).length) {
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
                    top: elementOffset.top + elementScale.height,
                    left: elementOffset.left + elementScale.width / 2 - overlayScale.width / 2,
                };
            case 'left':
                return {
                    top: elementOffset.top + elementScale.height / 2 - overlayScale.height / 2,
                    left: elementOffset.left - overlayScale.width,
                };
            case 'top':
            default:
                return {
                    top: elementOffset.top - overlayScale.height,
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
     * Creates tooltip with white text on dark background.
     *
     * @param {object} element
     * @param {string} text
     * @param {number|object?} delay
     * @param {string?} [placement=top]
     *
     * @see bindOverlay
     */
    const buildDefaultTooltip = (element, text, {delay, placement} = defaultAttributes) => {
        const tooltip = new BEM({
            block: 'imcms-tooltip',
            elements: {
                'arrow': $('<div>'),
                'text': $('<div>').text(text),
            }
        }).buildBlockStructure('<div>');

        bindOverlay({element, delay, placement, overlay: tooltip});
    };

    return {
        defaultTooltip: buildDefaultTooltip,
    }
});