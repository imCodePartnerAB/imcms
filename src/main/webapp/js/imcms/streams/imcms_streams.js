/**
 * Streams with pub/sub principle
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.05.18
 */
Imcms.define('imcms-streams', ['imcms-displacing-array'], function (DisplacingArray) {

    var Subscriber = function (topic) {
        (this.topic = topic).addSubscriber(this);
    };

    Subscriber.prototype = {
        onPublish: function (doOnPublish) {
            this.doOnPublish = doOnPublish;
            return this;
        },
        runFromStart: function () {
            this.topic.forEachPublication(this.reactOnPublication.bind(this))
        },
        getReactionOnPublication: function (content) {
            return function () {
                this.doOnPublish(content);
            }.bind(this);
        },
        reactOnPublication: function (content) {
            setTimeout(this.getReactionOnPublication(content));
        }
    };

    var Topic = function (name) {
        this.name = name;
        this.publications = new DisplacingArray(1024);
        this.subscribers = new DisplacingArray(1024);
    };

    Topic.prototype = {
        forEachPublication: function (doOnPublication) {
            this.publications.forEach(doOnPublication);
        },
        addSubscriber: function (subscriber) {
            this.subscribers.push(subscriber);
        },
        getAllSubscribers: function () {
            return this.subscribers;
        },
        savePublication: function (content) {
            this.publications.push(content);
        },
        publish: function (content) {
            this.savePublication(content);

            this.getAllSubscribers().forEach(function (subscriber) {
                subscriber.reactOnPublication(content);
            });
        }
    };

    var namePerTopic = {};

    function getOrCreateTopic(topicName) {
        return namePerTopic[topicName] || (namePerTopic[topicName] = new Topic(topicName));
    }

    var Publisher = function (topic) {
        this.topic = topic;
    };

    Publisher.prototype = {
        publish: function (content) {
            this.topic.publish(content);
        }
    };

    return {
        createSubscriberOnTopic: function (topicName) {
            return new Subscriber(getOrCreateTopic(topicName));
        },
        subscribeFromStart: function (topicName, doOnPublish) {
            this.createSubscriberOnTopic(topicName)
                .onPublish(doOnPublish)
                .runFromStart();
        },
        createPublisherOnTopic: function (topicName) {
            return new Publisher(getOrCreateTopic(topicName));
        }
    }
});
