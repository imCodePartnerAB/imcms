/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-window-builder", ["imcms-window-components-builder", "jquery"], function (windowComponents, $) {

    function setBodyScrollingRule(overflowValue) {
        $("body").css("overflow", overflowValue);
    }

    function enableBackgroundPageScrolling(pageOverflow, scrollTop) {
        setBodyScrollingRule(pageOverflow);
        $(window).scrollTop(scrollTop);
    }

    function getScrollTopAndDisable() {
        var $window = $(window);
        var prevScrollTop = $window.scrollTop();
        $window.scrollTop(0);

        return prevScrollTop;
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
        buildWindow: function (windowInitData) {
            this._scrollTop = getScrollTopAndDisable();
            this._pageOverflow = $("body").css("overflow") || "auto";
            setBodyScrollingRule("hidden");

            if (!this.$editor) {
                this.$editor = this.factory(windowInitData).appendTo("body");
            }

            this.loadDataStrategy && this.loadDataStrategy.applyAsync(arguments);
            this.$editor.css("display", "block");
        },
        closeWindow: function () {
            enableBackgroundPageScrolling(this._pageOverflow, this._scrollTop);
            this._scrollTop = 0;
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
