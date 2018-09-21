/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.05.18
 */
const mousetrap = require('mousetrap');

const onEscKeyPressedName = 'onEscKeyPressed';
const onEnterKeyPressedName = 'onEnterKeyPressed';

function bindHotKeys() {
    mousetrap.bind('esc', getNamedCallbackHandler(onEscKeyPressedName));
    mousetrap.bind('enter', getNamedCallbackHandler(onEnterKeyPressedName));
}

function unbindHotKeys() {
    mousetrap.reset();
}

function unbindIfNeeds() {
    if (callbacks.length) return;
    unbindHotKeys()
}

function handleCallback(callbackReceiver) {
    const callback = callbackReceiver(callbacks[callbacks.length - 1]);
    unbindIfNeeds();
    callback && callback.call && callback.call();

    return false; // to stop bubbling
}

function getNamedCallbackHandler(name) {
    return () => handleCallback((callback) => callback && callback[name]);
}

class KeyCallbacks {
    constructor(onEscKeyPressed, onEnterKeyPressed) {
        this[onEscKeyPressedName] = onEscKeyPressed;
        this[onEnterKeyPressedName] = onEnterKeyPressed;
    }
}

const callbacks = [];

module.exports = {
    unRegister: function () {
        callbacks.pop();
    },
    registerWindow: function (onEscKeyPressed, onEnterKeyPressed) {
        if (!callbacks.length) bindHotKeys();

        callbacks.push(new KeyCallbacks(onEscKeyPressed, onEnterKeyPressed));
    }
};
