/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 26.09.18
 */
let isDebugDisabled = process.env.NODE_ENV === 'production';

const logQueue = [];
const LOGGING_DELAY_MS = 2000;
const GROUP_NAME = 'imcms';

let schedulerId;

function doLogging() {
    let logArgs;
    const groupLogger = new GroupLogger(GROUP_NAME);

    groupLogger.start();

    while (logArgs = logQueue.shift()) {
        groupLogger.log.apply(groupLogger, logArgs);
    }

    groupLogger.finish();

    schedulerId = false;
}

function wakeUpLoggingScheduler() {
    if (schedulerId) return;

    schedulerId = setTimeout(doLogging, LOGGING_DELAY_MS)
}

class GroupLogger {
    constructor(groupName) {
        this.groupName = groupName;
        this.callers = 0;
    }

    /** Must be called before any logging */
    start() {
        if (this.callers++) return;

        console.groupCollapsed(this.groupName);
    }

    log(message, options) {
        console.log.apply(console, arguments);
    }

    /** Must be called to finish grouped logs */
    finish() {
        if (--this.callers) return;

        console.groupEnd();
    }

    time(timerName) {
        console.time(timerName);
    }

    timeEnd(timerName) {
        console.timeEnd(timerName);
    }
}

/**
 * Silent logger implementation when mode is production = logging is turned off
 */
class SilentGroupLogger extends GroupLogger {
    start() {
        // noop
    }

    log(message, options) {
        // noop
    }

    finish() {
        // noop
    }

    time(timerName) {
        // noop
    }

    timeEnd(timerName) {
        // noop
    }
}

const groupLoggers = {};

module.exports = {
    log(message, style) {
        if (isDebugDisabled) return;

        logQueue.push(arguments);
        wakeUpLoggingScheduler();
    },
    /** @return {GroupLogger} */
    group(groupName) {
        return groupLoggers[groupName] || (
            groupLoggers[groupName] = (isDebugDisabled) ? new SilentGroupLogger(groupName) : new GroupLogger(groupName)
        )
    },
    /** Enables logging for debug purposes */
    enableLogging() {
        isDebugDisabled = false;
    }
};
