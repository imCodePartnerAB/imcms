define('imcms-overlays-builder', ['jquery', 'imcms-bem-builder', 'tippy.js'], function ($, BEM, tippy) {

    const defaultAttributes = {
        delay: {show: 400, hide: null},
        placement: 'top',
        followCursor: false
    };

    /**
     * Creates tooltip with white text on dark background.
     *
     * @param {object} $element - target jQuery object.
     * @param {string} text - tooltip text
     * @param {number|object?} delay - Delay in ms once a trigger event is fired before a tooltip is showed or hided.
     *        Default: show - 400, hide - default tippy.js value.
     *        Example: 200, {hide: 100}, {show: 100, hide: 200}.
     * @param {string} [placement=top]
     */
    function buildDefaultTooltip(
        $element,
        text,
        {
            delay = defaultAttributes.delay,
            placement = defaultAttributes.placement,
            followCursor = defaultAttributes.followCursor
        } = defaultAttributes
    ) {
        const correctDelay = getCorrectDelay(delay);

        tippy.default($element[0], {
            content: text,
            delay: [correctDelay.show, correctDelay.hide],
            placement: placement,
            theme: 'default',
            followCursor: followCursor,
            zIndex: 999999,
            plugins: [tippy.followCursor]
        })
    }

    function getCorrectDelay(delay) {
        if (!isNaN(delay)) {
            return {
                show: delay,
                hide: delay,
            }
        }
        if (!delay.show) {
            delay.show = null;
        }
        if (!delay.hide) {
            delay.hide = null;
        }
        return delay;
    }

    return {
        defaultTooltip: buildDefaultTooltip,
    }
});