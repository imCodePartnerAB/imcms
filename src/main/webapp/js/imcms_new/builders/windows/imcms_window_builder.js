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
        buildWindowWithShadow: function (windowInitData) {
            this.shadowBuilder = new WindowBuilder({
                factory: function () {
                    return $("<div>", {"class": "imcms-modal-layout"});
                }
            });

            this.shadowBuilder.buildWindow();
            this.buildWindow.applyAsync(arguments, this);
        },
        buildWindow: function (windowInitData) {
            try {
                this._scrollTop = getScrollTopAndDisable();
                this._pageOverflow = $("body").css("overflow") || "auto";
                setBodyScrollingRule("hidden");

                if (!this.$editor) {
                    this.$editor = this.factory.apply(null, arguments).appendTo("body");
                }

                this.loadDataStrategy && this.loadDataStrategy.applyAsync(arguments);
                this.$editor.css("display", "block");

            } catch (e) {
                console.error(e);
                alert("Error in window builder! Stacktrace in console.");
                this.shadowBuilder.closeWindow();
                // todo: build some window with error message? hide shadow? show window anyway?
            }
        },
        closeWindow: function () {
            enableBackgroundPageScrolling(this._pageOverflow, this._scrollTop);
            this._scrollTop = 0;
            this.$editor.css("display", "none");

            this.shadowBuilder && this.shadowBuilder.closeWindow();

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
