/**
 * Module that provides possibility to register events with their handlers
 * and triggering that events asynchronously.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.12.17
 */

/**
 * Event name to array of handlers
 */
const eventToHandlers = {};

/**
 * Returns callback of event if such exists
 * @param name event's name
 * @returns {*|Array}
 */
function getEventHandlers(name) {
    return (eventToHandlers[name] || []);
}

module.exports = {
    /**
     * Triggers event handlers by name
     * @param name event's name
     */
    trigger: function (name) {
        console.log("%c Imcms event " + name + " triggered.", "color: blue;");

        getEventHandlers(name).forEach(setTimeout);
    },

    /**
     * Set handler on event by name
     * @param name event's name
     * @param handler function that will be called asynchronously when event is triggered
     */
    on: function (name, handler) {

        if (!eventToHandlers[name]) {
            eventToHandlers[name] = [];
        }

        eventToHandlers[name].push(handler);
    }
};
