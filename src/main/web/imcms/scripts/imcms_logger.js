/**
 * Created by Shadowgun on 24.03.2015.
 */
Imcms.Logger = {
    /**
     * @param {*} message
     * @param {...*} callback
     */
    log: function (message, callback) {
        console.info(message, Array.prototype.slice.call(arguments, 1));
        if (callback)
            callback.apply(this, Array.prototype.slice.call(arguments, 2));
    }
};
