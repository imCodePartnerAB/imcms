/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */

const windowComponents = require('imcms-window-components-builder');
const windowKeysController = require('imcms-window-keys-controller');
const $ = require('jquery');
const cookies = require('imcms-cookies');
const topPanelVisibilityInitiator = require('imcms-top-panel-visibility-initiator');

const windowAutoSizeClassName = 'imcms-modal-size--auto';
const windowMaximizeSizeClassName = 'imcms-modal-size--maximize';
const windowSizeCookieName = 'page-info-size';
const windowSizeCookieValues = {
    normal: 'normal',
    auto: 'auto',
    max: 'max',
};

function setBodyScrollingRule(overflowValue) {
    $("body").css("overflow", overflowValue);
}

function enableBackgroundPageScrolling(pageOverflow, scrollTop) {
    setBodyScrollingRule(pageOverflow);
}

function getScrollTopAndDisable() {
    const $window = $(window);
    const prevScrollTop = $window.scrollTop();
    $window.scrollTop(0);

    return prevScrollTop;
}

function normalizeWindowSize($window) {
    cookies.setCookie(windowSizeCookieName, windowSizeCookieValues.normal, {expires: 30});

    $window
        .removeClass(windowAutoSizeClassName)
        .removeClass(windowMaximizeSizeClassName);
}

function autoWindowSize($window) {
    normalizeWindowSize($window);

    cookies.setCookie(windowSizeCookieName, windowSizeCookieValues.auto, {expires: 30});

    $window.addClass(windowAutoSizeClassName);
}

function maximizeWindowSize($window) {
    normalizeWindowSize($window);

    cookies.setCookie(windowSizeCookieName, windowSizeCookieValues.max, {expires: 30});

    $window.addClass(windowMaximizeSizeClassName);
}

function refreshWindowSizeFromCookie($window) {
    switch (cookies.getCookie(windowSizeCookieName)) {
        case windowSizeCookieValues.auto:
            $window.addClass(windowAutoSizeClassName);
            break;
        case windowSizeCookieValues.max:
            $window.addClass(windowMaximizeSizeClassName);
            break;
    }
}

module.exports = class WindowBuilder {
    constructor(opts) {
        this._scrollTop = 0;
        this._pageOverflow = "auto";
        this.factory = opts.factory;
        this.loadDataStrategy = opts.loadDataStrategy;
        this.clearDataStrategy = opts.clearDataStrategy;

        this.onEscKeyPressed = (opts.onEscKeyPressed === "close")
            ? this.closeWindow.bind(this)
            : opts.onEscKeyPressed;

        this.onEnterKeyPressed = opts.onEnterKeyPressed;
        this.disableKeyBindings = opts.disableKeyBindings;
        this.$editor = undefined;
    }

    /**
     * Builds head with specified title
     * @param {string} title
     * @returns {*} head as jQuery element
     */
    static buildNonClosableHead(title) {
        return windowComponents.buildNonClosableHead(title);
    }

    /**
     * Builds footer with specified buttons as it's block elements
     * @param {[]?} buttons - array of jQuery elements
     * @returns {*} footer as jQuery element
     */
    static buildFooter(buttons) {
        return windowComponents.buildFooter.apply(windowComponents, arguments);
    }

    buildWindowWithShadow(windowInitData) {
        this.shadowBuilder = this.shadowBuilder || new WindowBuilder({
            disableKeyBindings: true,
            factory: () => $("<div>", {"class": "imcms-modal-layout"})
        });

        this.buildWindow.apply(this, arguments);
        this.shadowBuilder.buildWindow();
    }

    buildWindow(windowInitData) {
        try {
            this._pageOverflow = $("body").css("overflow") || "auto";
	        setBodyScrollingRule("hidden");


	        this.$editor = this.factory.apply(null, arguments).appendTo("body");

	        this.loadDataStrategy && setTimeout(() => this.loadDataStrategy.apply(null, arguments));
	        this.$editor.css("display", "block");

	        refreshWindowSizeFromCookie(this.$editor);

	        this.disableKeyBindings || windowKeysController.registerWindow(
		        this.onEscKeyPressed, this.onEnterKeyPressed
	        );
	        topPanelVisibilityInitiator.disableEventListeners();
        } catch (e) {
            console.error(e);
            alert("Error in window builder! Stacktrace in console.");

            this.$editor && this.$editor.remove();
            this.shadowBuilder && this.shadowBuilder.closeWindow();
            this.disableKeyBindings || windowKeysController.unRegister();
        }
    }

    closeWindow(opts) {
        try {
            this.disableKeyBindings || windowKeysController.unRegister();

            if (!opts || !opts.skipScrollFix) {
                enableBackgroundPageScrolling(this._pageOverflow, this._scrollTop);
                this._scrollTop = 0;
            }

            this.$editor && this.$editor.css("display", "none");

            if (this.shadowBuilder) {
                setTimeout(() => this.shadowBuilder.closeWindow({skipScrollFix: true}));
            }

            if (this.clearDataStrategy && this.clearDataStrategy.call) {
                try {
                    this.clearDataStrategy.call();

                } catch (e) {
                    console.error(e);
                }
            }
	        topPanelVisibilityInitiator.setEventListeners();
        } catch (e) {
            console.error(e);
            alert("Error in window builder! Stacktrace in console.");

            this.$editor && this.$editor.remove();
            this.shadowBuilder && this.shadowBuilder.closeWindow();
        }
    }

    /**
     * Builds head with specified title and close button
     * @param {string} title
     * @param {function?} onCloseClick
     * @returns {*} head as jQuery element
     */
    buildHead(title, onCloseClick) {
        onCloseClick = onCloseClick || this.closeWindow.bind(this);
        return windowComponents.buildHead(title, onCloseClick);
    }

    /**
     * Builds head with specified title and toolbar that contains 3 resize buttons and close button
     * @param {string} title
     * @param {function} onCloseClick
     * @returns {*} head as jQuery element
     */
    buildHeadWithResizing(title, onCloseClick) {
        return windowComponents.buildHeadWithResizing(title, {
            onWindowNormalClick: () => normalizeWindowSize(this.$editor),
            onWindowAutoClick: () => autoWindowSize(this.$editor),
            onWindowMaximizeClick: () => maximizeWindowSize(this.$editor),
            onCloseClick: onCloseClick
        });
    }
};
