/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.05.18
 */
Imcms.define("imcms-window-keys-controller", ["mousetrap"], function (mousetrap) {

    function bindHotKeys() {
        mousetrap.bind('esc', handleDecline);
        mousetrap.bind('enter', handleConfirm);
    }

    function unbindHotKeys() {
        mousetrap.reset();
    }

    function unbindIfNeeds() {
        if (callbacks.length) return;
        unbindHotKeys()
    }

    function handleCallback(callbackReceiver) {
        var callback = callbackReceiver(callbacks[callbacks.length - 1]);
        unbindIfNeeds();
        callback && callback.call && callback.call();
    }

    function handleDecline() {
        handleCallback(function (callback) {
            return callback && callback.onEscKeyPressed;
        });
    }

    function handleConfirm() {
        handleCallback(function (callback) {
            return callback.onEnterKeyPressed;
        });
    }

    var KeyCallbacks = function (onEscKeyPressed, onEnterKeyPressed) {
        this.onEscKeyPressed = onEscKeyPressed;
        this.onEnterKeyPressed = onEnterKeyPressed;
    };

    KeyCallbacks.prototype = {};

    var callbacks = [];

    return {
        unRegister: function () {
            callbacks.pop();
        },
        registerWindow: function (onEscKeyPressed, onEnterKeyPressed) {
            if (!callbacks.length) bindHotKeys();

            callbacks.push(new KeyCallbacks(onEscKeyPressed, onEnterKeyPressed));
        }
    };
});
