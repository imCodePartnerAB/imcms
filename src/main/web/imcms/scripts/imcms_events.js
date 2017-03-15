(function (Imcms) {
    /**
     * Imcms Events addon, has no dependencies and is cross-browser
     * Event can be fired in one place and catch in other
     *
     * @author Serhii Maksymchuk from Ubrainians for imCode
     */
    return Imcms.Events = {
        /**
         * All events
         */
        events: [],

        /**
         * Fires event by name
         * @param name event's name
         */
        fire: function (name) {
            var callback = Imcms.Events.getCallback(name);
            if (callback) {
                callback.call();
            }
        },

        /**
         * Set handler on event by name
         * @param name event's name
         * @param callback function that will be called when event fires
         */
        on: function (name, callback) {
            var data = {}; // data object can be added in future and use in callback as 1st_arg.data
            Imcms.Events.events.push({
                name: name,
                data: data,
                callback: callback
            })
        },

        /**
         * Returns callback of event if such exists
         * @param name event's name
         * @returns {*|callback|null}
         */
        getCallback: function (name) {
            var event = Imcms.Events.events.find(function (event) {
                return event.name == name;
            });

            if (event && event.callback) {
                return event.callback;
            }
        },

        /**
         * Returns callback of event if such exists or specified function
         * @param name event's name
         * @param orFunc - function that will be returned if there are no any handler for event
         */
        getCallbackOr: function (name, orFunc) {
            var callback = Imcms.Events.getCallback(name);

            return (callback) ? callback : orFunc;
        }
    };
})(Imcms);
