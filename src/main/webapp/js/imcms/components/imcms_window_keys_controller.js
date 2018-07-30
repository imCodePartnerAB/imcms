/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.05.18
 */
Imcms.define("imcms-window-keys-controller", ["mousetrap"], function (mousetrap) {

    var onEscKeyPressedName = 'onEscKeyPressed';
    var onEnterKeyPressedName = 'onEnterKeyPressed';

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
        var callback = callbackReceiver(callbacks[callbacks.length - 1]);
        unbindIfNeeds();
        callback && callback.call && callback.call();

        return false; // to stop bubbling
    }

    function getNamedCallbackHandler(name) {
        return handleCallback.bind(this, function (callback) {
            return callback && callback[name];
        });
    }

    var KeyCallbacks = function (onEscKeyPressed, onEnterKeyPressed) {
        this[onEscKeyPressedName] = onEscKeyPressed;
        this[onEnterKeyPressedName] = onEnterKeyPressed;
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
