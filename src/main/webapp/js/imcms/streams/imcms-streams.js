/**
 * Streams with pub/sub principle
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.05.18
 */
const logger = require('imcms-logger');
const DisplacingList = require('imcms-displacing-array');

class Subscriber {
    constructor(topic) {
        (this.topic = topic).addSubscriber(this);
    }

    onPublish(doOnPublish) {
        this.doOnPublish = doOnPublish;
        return this;
    }

    runFromLast() {
        const lastPublication = this.topic.getLastPublication();
        lastPublication && this.doOnPublish(lastPublication);
    }

    runFromStart() {
        this.topic.publications.forEach(this.doOnPublish)
    }
}

class Topic {
    constructor(name) {
        this.name = name;
        this.publications = new DisplacingList(1024);
        this.subscribers = new DisplacingList(1024);
    }

    _logHowManyPublicationsSavedYed() {
        logger.log(
            `%c Topic "${this.name}" has ${this.publications.length} publications so far.`,
            "color: orange"
        );
    }

    getLastPublication() {
        return this.publications.get(this.publications.length - 1);
    }

    addSubscriber(subscriber) {
        this.subscribers.push(subscriber);
    }

    getAllSubscribers() {
        return this.subscribers;
    }

    savePublication(content) {
        this.publications.push(content);
        this._logHowManyPublicationsSavedYed();
    }

    publish(content) {
        this.savePublication(content);
        this.getAllSubscribers().forEach((subscriber) => subscriber.doOnPublish(content));
    }
}

const namePerTopic = {};

function getOrCreateTopic(topicName) {
    return namePerTopic[topicName] || (namePerTopic[topicName] = new Topic(topicName));
}

class Publisher {
    constructor(topic) {
        this.topic = topic;
    }

    publish(content) {
        this.topic.publish(content);
    }
}

module.exports = {
    createSubscriberOnTopic: function (topicName) {
        return new Subscriber(getOrCreateTopic(topicName));
    },
    subscribeFromLast: function (topicName, doOnPublish) {
        this.createSubscriberOnTopic(topicName)
            .onPublish(doOnPublish)
            .runFromLast();
    },
    subscribeFromStart: function (topicName, doOnPublish) {
        this.createSubscriberOnTopic(topicName)
            .onPublish(doOnPublish)
            .runFromStart();
    },
    createPublisherOnTopic: function (topicName) {
        return new Publisher(getOrCreateTopic(topicName));
    }
};
