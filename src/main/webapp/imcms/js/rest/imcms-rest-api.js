const imcms = require('imcms');
const sessionTimeoutManagement = require('imcms-session-timeout-management');
const $ = require('jquery');
const logger = require('imcms-logger');

const API_PREFIX = "/api";
let counter = 0;

function logAjaxRequest(type, url, count, data) {
    const withData = (data !== null && data !== undefined);

    const groupLogger = logger.group(`${type} ${url}`);

    groupLogger.start();

    groupLogger.log(`%c AJAX ${type} call: ${url}${withData ? ' with request: ' : ''}`, 'color: blue;');
    withData && groupLogger.log(data);
    groupLogger.time(`${count} : ${url}`);
}

function logAjaxResponse(type, url, count, response) {
    const hasResponse = (response !== null) && (response !== undefined) && (response !== ''); // false and 0 is legal

    const groupLogger = logger.group(`${type} ${url}`);

    groupLogger.timeEnd(`${count} : ${url}`);
    groupLogger.log(`%c AJAX ${type} call done: ${url}${hasResponse ? ' response: ' : ''}`, 'color: green;');
    hasResponse && groupLogger.log(response);

    groupLogger.finish();
}

function ajax(data, callback) {
    const url = imcms.contextPath + API_PREFIX + this.url;
    const type = this.type;
    const contentType = (this.contentType === undefined) // exactly
        ? ('application/' + (this.json ? 'json' : 'x-www-form-urlencoded') + '; charset=UTF-8')
        : this.contentType;

    const count = ++counter;

    const ajaxObj = {
        url: url,
        type: type,
        contentType: contentType,
        processData: this.processData,
        data: this.json ? JSON.stringify(data) : data,

        success: function (response) {
            sessionTimeoutManagement.initOrUpdateSessionTimeout();
            logAjaxResponse(type, url, count, response);
            callback && callback(response);
        },

        error: response => {
            console.groupEnd();
            console.error(response);
        }
    };

    logAjaxRequest(type, url, count, data);

    return $.ajax(ajaxObj);
}

function get(path) {
    return ajax.bind({url: path, type: "GET", json: false});
}

function post(path) {
    return ajax.bind({url: path, type: "POST", json: true});
}

function postFiles(path) {
    return ajax.bind({url: path, type: "POST", json: false, contentType: false, processData: false});
}

function patch(path) {
    return ajax.bind({url: path, type: "PATCH", json: true});
}

function put(path) {
    return ajax.bind({url: path, type: "PUT", json: true});
}

function remove(path) {
    return ajax.bind({url: path, type: "DELETE", json: true});
}

class API {
    constructor(url) {
        this.create = post(url);
        this.read = get(url);
        this.update = patch(url);
        this.replace = put(url);
        this.remove = remove(url);
        this.postFiles = postFiles(url);
    }
}

module.exports = {
    ajax: ajax,
    /**
     * Always call {@code new API()}
     */
    API: API
};
