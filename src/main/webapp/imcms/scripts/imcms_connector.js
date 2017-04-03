(function (Imcms) {
    var publishers = [];

    /**
     * Publisher that holds subscribers and callback for them
     * @param publisherName
     * @constructor
     */
    var Publisher = function (publisherName) {
        this.name = publisherName;
    };
    Publisher.prototype = {
        subscribers: [],
        addSubscriber: function (subscriber) {
            this.subscribers.push(subscriber);
        },
        unSubscribe: function (subscriber) {
            this.subscribers.remove(subscriber);
        },
        hasSubscriber: function (subscriber) {
            return ~(this.subscribers.indexOf(subscriber));
        },
        callback: function () {
        }
    };

    /**
     * Connector module with possibility to set callback for publisher that
     * can be accessed by all subscribers.
     *
     * Created by Serhii Maksymchuk from Ubrainians for imCode
     * 09.11.16
     */
    return Imcms.CallbackConnector = {
        createPublisher: function (publisherName) {
            publishers.push(new Publisher(publisherName));
        },
        getPublisher: function (publisherName) {
            return publishers.find(function (publisher) {
                return (publisher.name === publisherName);
            })
        },
        /**
         * Set callback for all subscribers if such exists, else call it
         * @param publisherName
         * @param callback
         */
        setCallbackOrCall: function (publisherName, callback) {
            var publisher = Imcms.CallbackConnector.getPublisher(publisherName);

            (publisher && publisher.subscribers.length)
                ? publisher.callback = callback
                : callback();
        }
    }
})(Imcms);
