/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-window-builder", ["imcms-window-components-builder", "jquery"], function (windowComponents, $) {

    function setBodyScrollingRule(overflowValue) {
        $("body").css("overflow", overflowValue);
    }

    var WindowBuilder = function (opts) {
        this.factory = opts.factory;
        this.loadDataStrategy = opts.loadDataStrategy;
        this.clearDataStrategy = opts.clearDataStrategy;
        this.$editor = undefined;
    };

    WindowBuilder.prototype = {
        _scrollTop: 0,
        _pageOverflow: "auto",
        _disableBackgroundPageScrolling: function () {
            var $window = $(window);
            this._scrollTop = $window.scrollTop();
            $window.scrollTop(0);

            this._pageOverflow = $("body").css("overflow") || "auto";
            setBodyScrollingRule("hidden");
        },
        _enableBackgroundPageScrolling: function () {
            setBodyScrollingRule(this._pageOverflow);
            $(window).scrollTop(this._scrollTop);
            this._scrollTop = 0;
        },
        buildWindow: function (windowInitData) {
            this._disableBackgroundPageScrolling();

            if (!this.$editor) {
                this.$editor = this.factory(windowInitData).appendTo("body");
            }

            this.loadDataStrategy && this.loadDataStrategy.applyAsync(arguments);
            this.$editor.css("display", "block");
        },
        closeWindow: function () {
            this._enableBackgroundPageScrolling();
            this.$editor.css("display", "none");

            if (this.clearDataStrategy) {
                try {
                    this.clearDataStrategy.call();

                } catch (e) {
                    console.error(e);
                }
            }
        },
        /**
         * Builds head with specified title and close button
         * @param {string} title
         * @returns {*} head as jQuery element
         */
        buildHead: function (title) {
            return windowComponents.buildHead(title, this.closeWindow.bind(this));
        },
        /**
         * Builds footer with specified buttons as it's block elements
         * @param {[]} buttons - array of jQuery elements
         * @returns {*} footer as jQuery element
         */
        buildFooter: function (buttons) {
            return windowComponents.buildFooter.apply(windowComponents, arguments);
        }
    };

    return WindowBuilder;
});
