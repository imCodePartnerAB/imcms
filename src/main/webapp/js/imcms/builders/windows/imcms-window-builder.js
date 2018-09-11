/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define(
    "imcms-window-builder",
    ["imcms-window-components-builder", "imcms-window-keys-controller", "jquery"],
    function (windowComponents, windowKeysController, $) {

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

            this.onEscKeyPressed = (opts.onEscKeyPressed === "close")
                ? this.closeWindow.bind(this)
                : opts.onEscKeyPressed;

            this.onEnterKeyPressed = opts.onEnterKeyPressed;
            this.disableKeyBindings = opts.disableKeyBindings;
            this.$editor = undefined;
        };

        WindowBuilder.prototype = {
            _scrollTop: 0,
            _pageOverflow: "auto",
            buildWindowWithShadow: function (windowInitData) {
                this.shadowBuilder = this.shadowBuilder || new WindowBuilder({
                    disableKeyBindings: true,
                    factory: function () {
                        return $("<div>", {"class": "imcms-modal-layout"});
                    }
                });

                this.buildWindow.apply(this, arguments);
                this.shadowBuilder.buildWindow();
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

                    this.disableKeyBindings || windowKeysController.registerWindow(
                        this.onEscKeyPressed, this.onEnterKeyPressed
                    );
                } catch (e) {
                    console.error(e);
                    alert("Error in window builder! Stacktrace in console.");

                    this.$editor && this.$editor.remove();
                    this.shadowBuilder && this.shadowBuilder.closeWindow();
                    this.disableKeyBindings || windowKeysController.unRegister();
                }
            },
            closeWindow: function (opts) {
                try {
                    this.disableKeyBindings || windowKeysController.unRegister();

                    if (!opts || !opts.skipScrollFix) {
                        enableBackgroundPageScrolling(this._pageOverflow, this._scrollTop);
                        this._scrollTop = 0;
                    }

                    this.$editor && this.$editor.css("display", "none");

                    if (this.shadowBuilder) {
                        setTimeout(this.shadowBuilder.closeWindow.bind(this.shadowBuilder, {skipScrollFix: true}));
                    }

                    if (this.clearDataStrategy && this.clearDataStrategy.call) {
                        try {
                            this.clearDataStrategy.call();

                        } catch (e) {
                            console.error(e);
                        }
                    }
                } catch (e) {
                    console.error(e);
                    alert("Error in window builder! Stacktrace in console.");

                    this.$editor && this.$editor.remove();
                    this.shadowBuilder && this.shadowBuilder.closeWindow();
                }
            },
            /**
             * Builds head with specified title and close button
             * @param {string} title
             * @param {function} onCloseClick
             * @returns {*} head as jQuery element
             */
            buildHead: function (title, onCloseClick) {
                onCloseClick = onCloseClick || this.closeWindow.bind(this);
                return windowComponents.buildHead(title, onCloseClick);
            },
            /**
             * Builds head with specified title
             * @param {string} title
             * @returns {*} head as jQuery element
             */
            buildNonClosableHead: function (title) {
                return windowComponents.buildNonClosableHead(title);
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
    }
);
